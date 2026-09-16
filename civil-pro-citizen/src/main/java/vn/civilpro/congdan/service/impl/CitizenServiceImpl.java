package vn.civilpro.congdan.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.civilpro.common.enums.ErrorCode;
import vn.civilpro.common.exception.DuplicateResourceException;
import vn.civilpro.common.exception.ResourceNotFoundException;
import vn.civilpro.congdan.dto.request.CreateCitizenRequest;
import vn.civilpro.congdan.dto.request.SearchCitizenRequest;
import vn.civilpro.congdan.dto.request.UpdateCitizenRequest;
import vn.civilpro.congdan.dto.response.CitizenDetailResponse;
import vn.civilpro.congdan.dto.response.CitizenSummaryResponse;
import vn.civilpro.congdan.entity.Citizen;
import vn.civilpro.congdan.event.CitizenEventPublisher;
import vn.civilpro.congdan.mapper.CitizenMapper;
import vn.civilpro.congdan.repository.CitizenRepository;
import vn.civilpro.congdan.service.CitizenService;
import vn.civilpro.congdan.util.CitizenCodeGenerator;
import vn.civilpro.congdan.util.VietnameseUtils;

import java.time.LocalDate;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CitizenServiceImpl implements CitizenService {

    private final CitizenRepository citizenRepository;
    private final CitizenMapper citizenMapper;
    private final CitizenEventPublisher eventPublisher;

    @Override
    @Transactional
    public CitizenDetailResponse create(CreateCitizenRequest request) {
        log.info("[CitizenService] Creating new citizen, ID Card: {}", request.getIdCardNumber());

        if (request.getIdCardNumber() != null &&
                citizenRepository.existsByIdCardNumber(request.getIdCardNumber())) {
            throw new DuplicateResourceException(ErrorCode.CD_CCCD_EXISTS, request.getIdCardNumber());
        }

        Citizen entity = citizenMapper.toEntity(request);
        entity.setCitizenCode(CitizenCodeGenerator.generate());
        entity.setFullNameAscii(VietnameseUtils.removeAccent(request.getFullName()));

        Citizen saved = citizenRepository.save(entity);
        log.info("[CitizenService] Created citizen, ID: {}, Code: {}", saved.getId(), saved.getCitizenCode());

        eventPublisher.publishCitizenCreated(saved);

        return citizenMapper.toDetailResponse(saved);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"citizen", "idCardLookup"}, key = "#id")
    public CitizenDetailResponse update(Long id, UpdateCitizenRequest request) {
        log.info("[CitizenService] Updating citizen ID: {}", id);

        Citizen existing = findByIdOrThrow(id);

        if (request.getIdCardNumber() != null &&
                !request.getIdCardNumber().equals(existing.getIdCardNumber()) &&
                citizenRepository.existsByIdCardNumberAndIdNot(request.getIdCardNumber(), id)) {
            throw new DuplicateResourceException(ErrorCode.CD_CCCD_EXISTS, request.getIdCardNumber());
        }

        citizenMapper.updateEntityFromRequest(request, existing);

        if (request.getFullName() != null) {
            existing.setFullNameAscii(VietnameseUtils.removeAccent(request.getFullName()));
        }

        Citizen updated = citizenRepository.save(existing);

        eventPublisher.publishCitizenUpdated(updated);

        return citizenMapper.toDetailResponse(updated);
    }

    @Override
    @Cacheable(value = "citizen", key = "#id", unless = "#result == null")
    public CitizenDetailResponse getById(Long id) {
        return citizenMapper.toDetailResponse(findByIdOrThrow(id));
    }

    @Override
    @Cacheable(value = "idCardLookup", key = "#idCardNumber", unless = "#result == null")
    public CitizenDetailResponse getByIdCardNumber(String idCardNumber) {
        Citizen citizen = citizenRepository.findByIdCardNumber(idCardNumber)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.CD_NOT_FOUND, idCardNumber));
        return citizenMapper.toDetailResponse(citizen);
    }

    @Override
    public Page<CitizenSummaryResponse> search(SearchCitizenRequest request, Pageable pageable) {
        return citizenRepository.searchCitizen(
                request.getFullName(),
                request.getAreaCode(),
                request.getCitizenType(),
                request.getStatus(),
                pageable
        ).map(citizenMapper::toSummaryResponse);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"citizen", "idCardLookup"}, key = "#id")
    public void markAsDeceased(Long id, String reason) {
        log.info("[CitizenService] Marking citizen as deceased ID: {}", id);

        Citizen citizen = findByIdOrThrow(id);
        citizen.setStatus(0);
        citizen.setDeathDate(LocalDate.now());
        citizen.setStatusReason(reason);

        citizenRepository.save(citizen);

        eventPublisher.publishCitizenDeceased(citizen);
    }

    private Citizen findByIdOrThrow(Long id) {
        return citizenRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.CD_NOT_FOUND, id));
    }
}