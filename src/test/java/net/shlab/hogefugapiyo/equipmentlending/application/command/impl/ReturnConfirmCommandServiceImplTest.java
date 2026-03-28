package net.shlab.hogefugapiyo.equipmentlending.application.command.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import net.shlab.hogefugapiyo.equipmentlending.application.BusinessException;
import net.shlab.hogefugapiyo.equipmentlending.application.command.ReturnConfirmCommandService;
import net.shlab.hogefugapiyo.equipmentlending.infrastructure.repository.entity.EquipmentRepository;
import net.shlab.hogefugapiyo.equipmentlending.infrastructure.repository.entity.LendingRequestRepository;
import net.shlab.hogefugapiyo.equipmentlending.infrastructure.repository.history.HistoryRepository;
import net.shlab.hogefugapiyo.equipmentlending.model.entity.Equipment;
import net.shlab.hogefugapiyo.equipmentlending.model.entity.LendingRequest;
import net.shlab.hogefugapiyo.equipmentlending.model.history.EquipmentHistory;
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
class ReturnConfirmCommandServiceImplTest {

    @Mock
    private LendingRequestRepository lendingRequestRepository;

    @Mock
    private EquipmentRepository equipmentRepository;

    @Mock
    private CurrentTimeProvider currentTimeProvider;

    @Mock
    private HistoryRepository historyRepository;

    @InjectMocks
    private ReturnConfirmCommandServiceImpl service;

    @BeforeEach
    void setUp() {
        OperationContextHolder.set(OperationContext.create());
    }

    @AfterEach
    void tearDown() {
        OperationContextHolder.clear();
    }

    @Test
    void executeCompletesReturnAndRestoresEquipmentAvailability() {
        var request = new ReturnConfirmCommandService.Request("ADMIN1", 2003L, " 返却を確認した。 ", 2);
        var now = LocalDateTime.of(2026, 3, 6, 16, 0);
        LendingRequest lendingRequest = lendingRequest(2003L, LendingRequestStatus.PENDING_RETURN_CONFIRMATION, 2);
        Equipment equipment = equipment(1003L, EquipmentStatus.LENT);
        when(lendingRequestRepository.findEquipmentIdsByLendingRequestId(2003L)).thenReturn(List.of(1003L));
        when(lendingRequestRepository.findById(2003L)).thenReturn(Optional.of(lendingRequest));
        when(equipmentRepository.findByIds(List.of(1003L))).thenReturn(List.of(equipment));
        when(currentTimeProvider.currentDateTime()).thenReturn(now);

        service.execute(request);

        assertThat(lendingRequest.getStatus()).isEqualTo(LendingRequestStatus.COMPLETED);
        assertThat(lendingRequest.getReturnConfirmedByUserId()).isEqualTo("ADMIN1");
        assertThat(lendingRequest.getReturnConfirmComment()).isEqualTo("返却を確認した。");
        assertThat(equipment.getStatus()).isEqualTo(EquipmentStatus.AVAILABLE);
        verify(lendingRequestRepository).save(lendingRequest);
        verify(equipmentRepository).saveAll(List.of(equipment));

        ArgumentCaptor<LendingRequestHistory> requestHistoryCaptor = ArgumentCaptor.forClass(LendingRequestHistory.class);
        ArgumentCaptor<List<EquipmentHistory>> equipmentHistoryCaptor = ArgumentCaptor.forClass(List.class);
        verify(historyRepository).insertLendingRequestHistory(requestHistoryCaptor.capture());
        verify(historyRepository).insertEquipmentHistories(equipmentHistoryCaptor.capture());
        assertThat(requestHistoryCaptor.getValue().lendingRequestId()).isEqualTo(2003L);
        assertThat(equipmentHistoryCaptor.getValue()).hasSize(1);
    }

    @Test
    void executeThrowsBusinessExceptionWhenRequestIsNotPendingReturnConfirmation() {
        var request = new ReturnConfirmCommandService.Request("ADMIN1", 2003L, null, 2);
        LendingRequest lendingRequest = lendingRequest(2003L, LendingRequestStatus.LENT, 2);
        when(lendingRequestRepository.findEquipmentIdsByLendingRequestId(2003L)).thenReturn(List.of(1003L));
        when(lendingRequestRepository.findById(2003L)).thenReturn(Optional.of(lendingRequest));

        assertThatThrownBy(() -> service.execute(request))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> assertThat(((BusinessException) ex).messageId()).isEqualTo("MSG_E_007"));

        verifyNoInteractions(historyRepository);
    }

    private LendingRequest lendingRequest(long lendingRequestId, LendingRequestStatus status, int version) {
        LendingRequest lendingRequest = new LendingRequest();
        lendingRequest.setLendingRequestId(lendingRequestId);
        lendingRequest.setStatus(status);
        lendingRequest.setVersion(version);
        return lendingRequest;
    }

    private Equipment equipment(long equipmentId, EquipmentStatus status) {
        Equipment equipment = new Equipment();
        equipment.setEquipmentId(equipmentId);
        equipment.setStatus(status);
        return equipment;
    }
}
