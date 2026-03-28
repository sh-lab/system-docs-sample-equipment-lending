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
import net.shlab.hogefugapiyo.equipmentlending.application.command.RejectLendingRequestCommandService;
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
class RejectLendingRequestCommandServiceImplTest {

    @Mock
    private LendingRequestRepository lendingRequestRepository;

    @Mock
    private EquipmentRepository equipmentRepository;

    @Mock
    private CurrentTimeProvider currentTimeProvider;

    @Mock
    private HistoryRepository historyRepository;

    @InjectMocks
    private RejectLendingRequestCommandServiceImpl service;

    @BeforeEach
    void setUp() {
        OperationContextHolder.set(OperationContext.create());
    }

    @AfterEach
    void tearDown() {
        OperationContextHolder.clear();
    }

    @Test
    void executeRejectsRequestAndRestoresEquipmentAvailability() {
        var request = new RejectLendingRequestCommandService.Request("ADMIN1", 2001L, " 今回は却下する。 ", 0);
        var now = LocalDateTime.of(2026, 3, 5, 15, 30);
        LendingRequest lendingRequest = lendingRequest(2001L, LendingRequestStatus.PENDING_APPROVAL, 0);
        Equipment equipment1 = equipment(1001L, EquipmentStatus.PENDING_LENDING);
        Equipment equipment2 = equipment(1004L, EquipmentStatus.PENDING_LENDING);
        when(lendingRequestRepository.findEquipmentIdsByLendingRequestId(2001L)).thenReturn(List.of(1001L, 1004L));
        when(lendingRequestRepository.findById(2001L)).thenReturn(Optional.of(lendingRequest));
        when(equipmentRepository.findByIds(List.of(1001L, 1004L))).thenReturn(List.of(equipment1, equipment2));
        when(currentTimeProvider.currentDateTime()).thenReturn(now);

        service.execute(request);

        assertThat(lendingRequest.getStatus()).isEqualTo(LendingRequestStatus.REJECTED);
        assertThat(lendingRequest.getReviewComment()).isEqualTo("今回は却下する。");
        assertThat(equipment1.getStatus()).isEqualTo(EquipmentStatus.AVAILABLE);
        assertThat(equipment2.getStatus()).isEqualTo(EquipmentStatus.AVAILABLE);
        verify(lendingRequestRepository).save(lendingRequest);
        verify(equipmentRepository).saveAll(List.of(equipment1, equipment2));

        ArgumentCaptor<LendingRequestHistory> requestHistoryCaptor = ArgumentCaptor.forClass(LendingRequestHistory.class);
        ArgumentCaptor<List<EquipmentHistory>> equipmentHistoryCaptor = ArgumentCaptor.forClass(List.class);
        verify(historyRepository).insertLendingRequestHistory(requestHistoryCaptor.capture());
        verify(historyRepository).insertEquipmentHistories(equipmentHistoryCaptor.capture());
        assertThat(requestHistoryCaptor.getValue().lendingRequestId()).isEqualTo(2001L);
        assertThat(equipmentHistoryCaptor.getValue()).hasSize(2);
    }

    @Test
    void executeThrowsBusinessExceptionWhenRequestIsNotPendingApproval() {
        var request = new RejectLendingRequestCommandService.Request("ADMIN1", 2001L, null, 0);
        LendingRequest lendingRequest = lendingRequest(2001L, LendingRequestStatus.LENT, 0);
        when(lendingRequestRepository.findEquipmentIdsByLendingRequestId(2001L)).thenReturn(List.of(1001L));
        when(lendingRequestRepository.findById(2001L)).thenReturn(Optional.of(lendingRequest));

        assertThatThrownBy(() -> service.execute(request))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> assertThat(((BusinessException) ex).messageId()).isEqualTo("MSG_E_006"));

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
