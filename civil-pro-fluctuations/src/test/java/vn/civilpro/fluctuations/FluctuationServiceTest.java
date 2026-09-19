package vn.civilpro.fluctuations;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import vn.civilpro.fluctuations.model.entity.PopulationFluctuation;
import vn.civilpro.fluctuations.event.CitizenKafkaEvent;
import vn.civilpro.fluctuations.event.FluctuationKafkaConsumer;
import vn.civilpro.fluctuations.repository.PopulationFluctuationRepository;
import vn.civilpro.fluctuations.service.impl.PopulationFluctuationServiceImpl;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FluctuationServiceTest {

    @Mock
    private PopulationFluctuationRepository fluctuationRepository;

    @InjectMocks
    private PopulationFluctuationServiceImpl fluctuationService;

    @InjectMocks
    private FluctuationKafkaConsumer kafkaConsumer;

    private PopulationFluctuation sampleRecord;

    @BeforeEach
    void setUp() {
        sampleRecord = PopulationFluctuation.builder()
                .id(1L)
                .fluctuationCode("FL-BIRTH-2026-001")
                .fluctuationType("BIRTH")
                .citizenId(100L)
                .fullName("Baby Nguyen")
                .areaCode("01001")
                .fluctuationDate(LocalDate.now())
                .month(LocalDate.now().getMonthValue())
                .year(LocalDate.now().getYear())
                .build();
    }

    @Test
    void testRecordFluctuation() {
        when(fluctuationRepository.save(any(PopulationFluctuation.class))).thenReturn(sampleRecord);

        PopulationFluctuation result = fluctuationService.recordFluctuation(sampleRecord);
        assertNotNull(result);
        assertEquals("BIRTH", result.getFluctuationType());
        verify(fluctuationRepository, times(1)).save(any(PopulationFluctuation.class));
    }

    @Test
    void testGetMonthlySummary() {
        List<Object[]> mockSummary = List.of(
                new Object[]{1, "BIRTH", 15L},
                new Object[]{1, "DEATH", 5L}
        );
        when(fluctuationRepository.aggregateMonthlySummary("01001", 2026, 1, 12)).thenReturn(mockSummary);

        List<Object[]> result = fluctuationService.getMonthlySummary("01001", 2026, 1, 12);
        assertEquals(2, result.size());
    }

    @Test
    void testKafkaConsumer_OnCitizenDeceased() {
        CitizenKafkaEvent event = CitizenKafkaEvent.builder()
                .eventType("CITIZEN_DECEASED")
                .citizenId(200L)
                .fullName("Elderly Citizen")
                .areaCode("01001")
                .status(0)
                .build();

        kafkaConsumer.onCitizenDeceased(event);

        verify(fluctuationRepository, times(1)).save(argThat(rec ->
                "DEATH".equals(rec.getFluctuationType()) && Long.valueOf(200L).equals(rec.getCitizenId())
        ));
    }

    @Test
    void testKafkaConsumer_OnCitizenCreated() {
        CitizenKafkaEvent event = CitizenKafkaEvent.builder()
                .eventType("CITIZEN_CREATED")
                .citizenId(300L)
                .fullName("New Baby")
                .areaCode("01001")
                .status(1)
                .build();

        kafkaConsumer.onCitizenCreated(event);

        verify(fluctuationRepository, times(1)).save(argThat(rec ->
                "BIRTH".equals(rec.getFluctuationType()) && Long.valueOf(300L).equals(rec.getCitizenId())
        ));
    }
}
