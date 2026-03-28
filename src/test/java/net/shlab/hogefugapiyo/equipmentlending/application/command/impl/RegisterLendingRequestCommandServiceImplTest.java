package net.shlab.hogefugapiyo.equipmentlending.application.command.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import net.shlab.hogefugapiyo.equipmentlending.application.BusinessException;
import net.shlab.hogefugapiyo.equipmentlending.application.command.LendingRequestAvailabilityResult;
import net.shlab.hogefugapiyo.equipmentlending.application.command.RegisterLendingRequestCommandService;
import net.shlab.hogefugapiyo.equipmentlending.application.pure.CheckLendingRequestAvailabilityService;
import net.shlab.hogefugapiyo.equipmentlending.infrastructure.repository.entity.EquipmentRepository;
import net.shlab.hogefugapiyo.equipmentlending.infrastructure.repository.entity.LendingRequestRepository;
import net.shlab.hogefugapiyo.equipmentlending.infrastructure.repository.history.HistoryRepository;
import net.shlab.hogefugapiyo.equipmentlending.model.entity.Equipment;
import net.shlab.hogefugapiyo.equipmentlending.model.entity.LendingRequest;
import net.shlab.hogefugapiyo.equipmentlending.model.history.EquipmentHistory;
import net.shlab.hogefugapiyo.equipmentlending.model.history.LendingRequestDetailHistory;
import net.shlab.hogefugapiyo.equipmentlending.model.history.LendingRequestHistory;
import net.shlab.hogefugapiyo.equipmentlending.model.value.EquipmentStatus;
import net.shlab.hogefugapiyo.equipmentlending.model.value.LendingRequestStatus;
import net.shlab.hogefugapiyo.framework.core.operation.OperationContext;
import net.shlab.hogefugapiyo.framework.core.operation.OperationContextHolder;
import net.shlab.hogefugapiyo.framework.core.time.CurrentTimeProvider;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RegisterLendingRequestCommandServiceImplTest {

    @Mock
    private LendingRequestRepository lendingRequestRepository;

    @Mock
    private EquipmentRepository equipmentRepository;

    @Mock
    private CheckLendingRequestAvailabilityService checkLendingRequestAvailabilityService;

    @Mock
    private CurrentTimeProvider currentTimeProvider;

    @Mock
    private HistoryRepository historyRepository;

    @InjectMocks
    private RegisterLendingRequestCommandServiceImpl service;

    @BeforeEach
    void setUp() {
        OperationContextHolder.set(OperationContext.create());
    }

    @AfterEach
    void tearDown() {
        OperationContextHolder.clear();
    }

    @Test
    void executeRegistersLendingRequestAndUpdatesEquipmentStatus() {
        var request = new RegisterLendingRequestCommandService.Request("USER01", List.of(1001L, 1004L), "  会議で利用する。  ");
        var now = LocalDateTime.of(2026, 3, 1, 10, 30);
        var equipment1 = equipment(1001L, EquipmentStatus.AVAILABLE);
        var equipment2 = equipment(1004L, EquipmentStatus.AVAILABLE);
        when(equipmentRepository.findByIds(request.equipmentIds())).thenReturn(List.of(equipment1, equipment2));
        when(checkLendingRequestAvailabilityService.check(List.of(equipment1, equipment2)))
                .thenReturn(new LendingRequestAvailabilityResult(true, List.of(), List.of()));
        when(lendingRequestRepository.nextId()).thenReturn(3001L);
        when(currentTimeProvider.currentDateTime()).thenReturn(now);

        service.execute(request);

        ArgumentCaptor<LendingRequest> lendingRequestCaptor = ArgumentCaptor.forClass(LendingRequest.class);
        verify(lendingRequestRepository).save(lendingRequestCaptor.capture());
        LendingRequest saved = lendingRequestCaptor.getValue();
        assertThat(saved.getLendingRequestId()).isEqualTo(3001L);
        assertThat(saved.getApplicantUserId()).isEqualTo("USER01");
        assertThat(saved.getStatus()).isEqualTo(LendingRequestStatus.PENDING_APPROVAL);
        assertThat(saved.getRequestComment()).isEqualTo("会議で利用する。");
        verify(lendingRequestRepository).saveDetails(3001L, List.of(1001L, 1004L), "USER01", now);
        verify(equipmentRepository).saveAll(List.of(equipment1, equipment2));
        assertThat(equipment1.getStatus()).isEqualTo(EquipmentStatus.PENDING_LENDING);
        assertThat(equipment2.getStatus()).isEqualTo(EquipmentStatus.PENDING_LENDING);

        ArgumentCaptor<LendingRequestHistory> requestHistoryCaptor = ArgumentCaptor.forClass(LendingRequestHistory.class);
        ArgumentCaptor<List<LendingRequestDetailHistory>> detailHistoryCaptor = ArgumentCaptor.forClass(List.class);
        ArgumentCaptor<List<EquipmentHistory>> equipmentHistoryCaptor = ArgumentCaptor.forClass(List.class);
        verify(historyRepository).insertLendingRequestHistory(requestHistoryCaptor.capture());
        verify(historyRepository).insertLendingRequestDetailHistories(detailHistoryCaptor.capture());
        verify(historyRepository).insertEquipmentHistories(equipmentHistoryCaptor.capture());
        assertThat(requestHistoryCaptor.getValue().lendingRequestId()).isEqualTo(3001L);
        assertThat(detailHistoryCaptor.getValue()).hasSize(2);
        assertThat(equipmentHistoryCaptor.getValue()).hasSize(2);
    }

    @Test
    void executeThrowsBusinessExceptionWhenAvailabilityCheckFails() {
        var request = new RegisterLendingRequestCommandService.Request("USER01", List.of(1001L), null);
        var equipment = equipment(1001L, EquipmentStatus.PENDING_LENDING);
        when(equipmentRepository.findByIds(request.equipmentIds())).thenReturn(List.of(equipment));
        when(checkLendingRequestAvailabilityService.check(List.of(equipment)))
                .thenReturn(new LendingRequestAvailabilityResult(false, List.of(1001L), List.of("PENDING_LENDING")));

        assertThatThrownBy(() -> service.execute(request))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> assertThat(((BusinessException) ex).messageId()).isEqualTo("MSG_E_001"));

        verifyNoInteractions(historyRepository);
    }

    private Equipment equipment(long equipmentId, EquipmentStatus status) {
        Equipment equipment = new Equipment();
        equipment.setEquipmentId(equipmentId);
        equipment.setStatus(status);
        return equipment;
    }
}
