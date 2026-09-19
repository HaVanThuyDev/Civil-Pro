package vn.civilpro.congdan.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import vn.civilpro.congdan.model.enums.ErrorCode;
import vn.civilpro.congdan.exception.DuplicateResourceException;
import vn.civilpro.congdan.exception.ResourceNotFoundException;
import vn.civilpro.congdan.model.dto.request.CreateCitizenRequest;
import vn.civilpro.congdan.model.dto.response.CitizenDetailResponse;
import vn.civilpro.congdan.model.entity.Citizen;
import org.springframework.context.ApplicationEventPublisher;
import vn.civilpro.congdan.mapper.CitizenMapper;
import vn.civilpro.congdan.repository.CitizenRepository;
import vn.civilpro.congdan.service.impl.CitizenServiceImpl;
import vn.civilpro.congdan.util.CitizenCodeGenerator;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CitizenService Unit Tests")
class CitizenServiceImplTest {

    @Mock
    private CitizenRepository citizenRepository;

    @Mock
    private CitizenMapper citizenMapper;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private CitizenCodeGenerator citizenCodeGenerator;

    @InjectMocks
    private CitizenServiceImpl citizenService;

    private CreateCitizenRequest validRequest;
    private Citizen savedEntity;
    private CitizenDetailResponse detailResponse;

    @BeforeEach
    void setUp() {
        // Mock mã định danh tự động sinh ra
        lenient().when(citizenCodeGenerator.generate()).thenReturn("CTZ202608070001");

        validRequest = new CreateCitizenRequest();
        validRequest.setFullName("Nguyen Van A");
        validRequest.setGender(1);
        validRequest.setDateOfBirth(LocalDate.of(1990, 5, 15));
        validRequest.setIdCardNumber("038090012345");
        validRequest.setPermanentAreaCode("TP-002");
        validRequest.setPermanentAddress("No. 12, Street 3/2, Ward 1");

        savedEntity = Citizen.builder()
                .id(1L)
                .citizenCode("CTZ202608070001")
                .fullName("Nguyen Van A")
                .gender(1)
                .dateOfBirth(LocalDate.of(1990, 5, 15))
                .idCardNumber("038090012345")
                .permanentAreaCode("TP-002")
                .status(1)
                .build();

        detailResponse = CitizenDetailResponse.builder()
                .id(1L)
                .citizenCode("CTZ202608070001")
                .fullName("Nguyen Van A")
                .status(1)
                .build();
    }

    @Nested
    @DisplayName("Create Citizen")
    class CreateTests {

        @Test
        @DisplayName("Create successfully with valid request")
        void create_success_whenValidRequest() {
            when(citizenRepository.existsByIdCardNumber("038090012345")).thenReturn(false);
            when(citizenMapper.toEntity(any())).thenReturn(savedEntity);
            when(citizenRepository.save(any())).thenReturn(savedEntity);
            when(citizenMapper.toDetailResponse(any())).thenReturn(detailResponse);
            CitizenDetailResponse result = citizenService.create(validRequest);

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getCitizenCode()).isEqualTo("CTZ202608070001");

            verify(citizenRepository).existsByIdCardNumber("038090012345");
            verify(citizenRepository).save(any(Citizen.class));
            verify(eventPublisher).publishEvent(any(Object.class));
            verify(citizenCodeGenerator).generate();
        }

        @Test
        @DisplayName("Throw DuplicateResourceException when ID card number already exists")
        void create_throwsDuplicateException_whenIdCardExists() {
            when(citizenRepository.existsByIdCardNumber("038090012345")).thenReturn(true);

            assertThatThrownBy(() -> citizenService.create(validRequest))
                    .isInstanceOf(DuplicateResourceException.class)
                    .extracting(ex -> ((DuplicateResourceException) ex).getErrorCode())
                    .isEqualTo(ErrorCode.CD_CCCD_EXISTS);

            verify(citizenRepository, never()).save(any());
            verify(eventPublisher, never()).publishEvent(any());
        }

        @Test
        @DisplayName("Skip ID card check when request does not contain ID card number")
        void create_skipIdCardCheck_whenNoIdCardNumber() {
            validRequest.setIdCardNumber(null);
            when(citizenMapper.toEntity(any())).thenReturn(savedEntity);
            when(citizenRepository.save(any())).thenReturn(savedEntity);
            when(citizenMapper.toDetailResponse(any())).thenReturn(detailResponse);

            citizenService.create(validRequest);

            verify(citizenRepository, never()).existsByIdCardNumber(any());
        }
    }

    @Nested
    @DisplayName("Get Citizen By ID")
    class GetByIdTests {

        @Test
        @DisplayName("Return response when citizen is found")
        void getById_returnsResponse_whenFound() {
            when(citizenRepository.findById(1L)).thenReturn(Optional.of(savedEntity));
            when(citizenMapper.toDetailResponse(savedEntity)).thenReturn(detailResponse);

            CitizenDetailResponse result = citizenService.getById(1L);

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
        }

        @Test
        @DisplayName("Throw ResourceNotFoundException when citizen is not found")
        void getById_throwsNotFoundException_whenNotFound() {
            when(citizenRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> citizenService.getById(999L))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .extracting(ex -> ((ResourceNotFoundException) ex).getErrorCode())
                    .isEqualTo(ErrorCode.CD_NOT_FOUND);
        }
    }
}