package vn.civilpro.congdan.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.civilpro.congdan.event.CitizenCreatedEvent;
import vn.civilpro.congdan.event.CitizenDeceasedEvent;
import vn.civilpro.congdan.event.CitizenUpdatedEvent;
import vn.civilpro.congdan.model.dto.response.PagedResult;
import vn.civilpro.congdan.model.enums.ErrorCode;
import vn.civilpro.congdan.exception.DuplicateResourceException;
import vn.civilpro.congdan.exception.ResourceNotFoundException;
import vn.civilpro.congdan.model.dto.request.CreateCitizenRequest;
import vn.civilpro.congdan.model.dto.request.SearchCitizenRequest;
import vn.civilpro.congdan.model.dto.request.UpdateCitizenRequest;
import vn.civilpro.congdan.model.dto.response.CitizenDetailResponse;
import vn.civilpro.congdan.model.dto.response.CitizenSummaryResponse;
import vn.civilpro.congdan.model.entity.Citizen;
import vn.civilpro.congdan.mapper.CitizenMapper;
import vn.civilpro.congdan.repository.CitizenRepository;
import vn.civilpro.congdan.service.CitizenService;
import vn.civilpro.congdan.util.CitizenCodeGenerator;
import vn.civilpro.congdan.util.VietnameseUtils;

import java.time.LocalDate;

/**
 * Lưu ý về Kafka event: service này KHÔNG gọi trực tiếp Kafka publisher.
 * Thay vào đó nó publish domain event nội bộ qua ApplicationEventPublisher
 * (CitizenCreatedEvent / CitizenUpdatedEvent / CitizenDeceasedEvent).
 * CitizenEventPublisher (trong package `event`) lắng nghe các event này bằng
 * @TransactionalEventListener(phase = AFTER_COMMIT), tức là message chỉ thực
 * sự được đẩy lên Kafka SAU KHI transaction DB commit thành công — tránh
 * tình trạng dual-write (Kafka có event nhưng DB rollback).
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CitizenServiceImpl implements CitizenService {

    private final CitizenRepository citizenRepository;
    private final CitizenMapper citizenMapper;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final CitizenCodeGenerator citizenCodeGenerator;

    @Override
    @Transactional
    public CitizenDetailResponse create(CreateCitizenRequest request) {
        log.info("[CitizenService] Creating new citizen, ID Card: {}", request.getIdCardNumber());

        if (request.getIdCardNumber() != null &&
                citizenRepository.existsByIdCardNumber(request.getIdCardNumber())) {
            throw new DuplicateResourceException(ErrorCode.CD_CCCD_EXISTS, request.getIdCardNumber());
        }

        Citizen entity = citizenMapper.toEntity(request);
        entity.setCitizenCode(citizenCodeGenerator.generate());
        entity.setFullNameAscii(VietnameseUtils.removeAccent(request.getFullName()));

        Citizen saved = citizenRepository.save(entity);
        log.info("[CitizenService] Created citizen, ID: {}, Code: {}", saved.getId(), saved.getCitizenCode());

        applicationEventPublisher.publishEvent(new CitizenCreatedEvent(saved));

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

        applicationEventPublisher.publishEvent(new CitizenUpdatedEvent(updated));

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
    public PagedResult<CitizenSummaryResponse> getAll(int page, int size) {
        Page<Citizen> result = citizenRepository.findAll(PageRequest.of(page, size));
        Page<CitizenSummaryResponse> mapped = result.map(citizenMapper::toSummaryResponse);

        return new PagedResult<>(mapped.getContent(), mapped.getTotalElements(), page, size);
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

        applicationEventPublisher.publishEvent(new CitizenDeceasedEvent(citizen));
    }

    private Citizen findByIdOrThrow(Long id) {
        return citizenRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.CD_NOT_FOUND, id));
    }
}