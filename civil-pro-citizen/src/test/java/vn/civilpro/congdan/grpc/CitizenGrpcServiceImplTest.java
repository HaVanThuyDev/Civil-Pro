package vn.civilpro.congdan.grpc;

import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import vn.civil.grpc.citizen.*;
import vn.civilpro.congdan.model.entity.Citizen;
import vn.civilpro.congdan.repository.CitizenRepository;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CitizenGrpcServiceImpl Unit Tests - Model <-> gRPC Mapping")
class CitizenGrpcServiceImplTest {

    @Mock
    private CitizenRepository citizenRepository;

    @Mock
    private StreamObserver<GetCitizenResponse> getCitizenObserver;

    @Mock
    private StreamObserver<GetCitizenListResponse> getCitizenListObserver;

    @Mock
    private StreamObserver<CheckExistsResponse> checkExistsObserver;

    @InjectMocks
    private CitizenGrpcServiceImpl citizenGrpcService;

    private Citizen sampleCitizen;

    @BeforeEach
    void setUp() {
        sampleCitizen = Citizen.builder()
                .id(1L)
                .citizenCode("CTZ20260001")
                .fullName("Nguyen Van A")
                .gender(1)
                .dateOfBirth(LocalDate.of(1995, 5, 20))
                .idCardNumber("012345678901")
                .idCardExpiryDate(LocalDate.of(2035, 5, 20))
                .permanentAreaCode("HN-001")
                .permanentAddress("123 Ba Dinh, Hanoi")
                .occupation("Software Engineer")
                .citizenType("THUONG_TRU")
                .status(1)
                .isHouseholdHead(1)
                .householdId(100L)
                .build();
    }

    @Test
    @DisplayName("getById - Map thành công đầy đủ các trường từ Model sang gRPC CitizenInfo")
    void getById_success_mapsAllFields() {
        when(citizenRepository.findById(1L)).thenReturn(Optional.of(sampleCitizen));

        GetCitizenByIdRequest request = GetCitizenByIdRequest.newBuilder().setId(1L).build();
        citizenGrpcService.getById(request, getCitizenObserver);

        ArgumentCaptor<GetCitizenResponse> captor = ArgumentCaptor.forClass(GetCitizenResponse.class);
        verify(getCitizenObserver).onNext(captor.capture());
        verify(getCitizenObserver).onCompleted();

        GetCitizenResponse response = captor.getValue();
        assertThat(response.getMeta().getSuccess()).isTrue();
        assertThat(response.getMeta().getCode()).isEqualTo(200);

        CitizenInfo data = response.getData();
        assertThat(data.getId()).isEqualTo(1L);
        assertThat(data.getCitizenCode()).isEqualTo("CTZ20260001");
        assertThat(data.getFullName()).isEqualTo("Nguyen Van A");
        assertThat(data.getGender()).isEqualTo(1);
        assertThat(data.getDateOfBirth()).isEqualTo("1995-05-20");
        assertThat(data.getNationalId()).isEqualTo("012345678901");
        assertThat(data.getNationalIdExpiryDate()).isEqualTo("2035-05-20");
        assertThat(data.getPermanentAddressCode()).isEqualTo("HN-001");
        assertThat(data.getPermanentAddress()).isEqualTo("123 Ba Dinh, Hanoi");
        assertThat(data.getOccupation()).isEqualTo("Software Engineer");
        assertThat(data.getCitizenType()).isEqualTo("THUONG_TRU");
        assertThat(data.getStatus()).isEqualTo(1);
        assertThat(data.getIsHouseholdHead()).isTrue();
        assertThat(data.getHouseholdId()).isEqualTo(100L);

        int expectedAge = Period.between(sampleCitizen.getDateOfBirth(), LocalDate.now()).getYears();
        assertThat(data.getAge()).isEqualTo(expectedAge);
    }

    @Test
    @DisplayName("getById - Trả về 404 khi không tìm thấy Citizen")
    void getById_notFound_returns404() {
        when(citizenRepository.findById(999L)).thenReturn(Optional.empty());

        GetCitizenByIdRequest request = GetCitizenByIdRequest.newBuilder().setId(999L).build();
        citizenGrpcService.getById(request, getCitizenObserver);

        ArgumentCaptor<GetCitizenResponse> captor = ArgumentCaptor.forClass(GetCitizenResponse.class);
        verify(getCitizenObserver).onNext(captor.capture());
        verify(getCitizenObserver).onCompleted();

        GetCitizenResponse response = captor.getValue();
        assertThat(response.getMeta().getSuccess()).isFalse();
        assertThat(response.getMeta().getCode()).isEqualTo(404);
    }

    @Test
    @DisplayName("getByNationalId - Map thành công theo số CCCD")
    void getByNationalId_success() {
        when(citizenRepository.findByIdCardNumber("012345678901")).thenReturn(Optional.of(sampleCitizen));

        GetCitizenByNationalIdRequest request = GetCitizenByNationalIdRequest.newBuilder()
                .setNationalId("012345678901")
                .build();
        citizenGrpcService.getByNationalId(request, getCitizenObserver);

        ArgumentCaptor<GetCitizenResponse> captor = ArgumentCaptor.forClass(GetCitizenResponse.class);
        verify(getCitizenObserver).onNext(captor.capture());
        verify(getCitizenObserver).onCompleted();

        GetCitizenResponse response = captor.getValue();
        assertThat(response.getMeta().getSuccess()).isTrue();
        assertThat(response.getData().getFullName()).isEqualTo("Nguyen Van A");
    }

    @Test
    @DisplayName("getListByIds - Map danh sách Citizen sang CitizenSummary proto")
    void getListByIds_success() {
        Citizen citizen2 = Citizen.builder()
                .id(2L)
                .citizenCode("CTZ20260002")
                .fullName("Tran Thi B")
                .gender(2)
                .dateOfBirth(LocalDate.of(1998, 8, 15))
                .idCardNumber("012345678902")
                .permanentAreaCode("HN-002")
                .status(1)
                .build();

        when(citizenRepository.findByIdIn(List.of(1L, 2L))).thenReturn(List.of(sampleCitizen, citizen2));

        GetCitizenListByIdsRequest request = GetCitizenListByIdsRequest.newBuilder()
                .addAllIds(List.of(1L, 2L))
                .build();
        citizenGrpcService.getListByIds(request, getCitizenListObserver);

        ArgumentCaptor<GetCitizenListResponse> captor = ArgumentCaptor.forClass(GetCitizenListResponse.class);
        verify(getCitizenListObserver).onNext(captor.capture());
        verify(getCitizenListObserver).onCompleted();

        GetCitizenListResponse response = captor.getValue();
        assertThat(response.getMeta().getSuccess()).isTrue();
        assertThat(response.getDataList()).hasSize(2);
        assertThat(response.getData(0).getFullName()).isEqualTo("Nguyen Van A");
        assertThat(response.getData(1).getFullName()).isEqualTo("Tran Thi B");
    }

    @Test
    @DisplayName("checkNationalIdExists - Kiểm tra sự tồn tại của số CCCD")
    void checkNationalIdExists_success() {
        when(citizenRepository.existsByIdCardNumber("012345678901")).thenReturn(true);

        CheckNationalIdExistsRequest request = CheckNationalIdExistsRequest.newBuilder()
                .setNationalId("012345678901")
                .build();
        citizenGrpcService.checkNationalIdExists(request, checkExistsObserver);

        ArgumentCaptor<CheckExistsResponse> captor = ArgumentCaptor.forClass(CheckExistsResponse.class);
        verify(checkExistsObserver).onNext(captor.capture());
        verify(checkExistsObserver).onCompleted();

        CheckExistsResponse response = captor.getValue();
        assertThat(response.getMeta().getSuccess()).isTrue();
        assertThat(response.getExists()).isTrue();
    }
}
