package net.shlab.hogefugapiyo.equipmentlending.application.command.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;
import net.shlab.hogefugapiyo.equipmentlending.application.BusinessException;
import net.shlab.hogefugapiyo.equipmentlending.application.command.ConfirmRejectedRequestCommandService;
import net.shlab.hogefugapiyo.equipmentlending.infrastructure.repository.entity.LendingRequestRepository;
import net.shlab.hogefugapiyo.equipmentlending.infrastructure.repository.history.HistoryRepository;
import net.shlab.hogefugapiyo.equipmentlending.model.entity.LendingRequest;
import net.shlab.hogefugapiyo.equipmentlending.model.history.LendingRequestHistory;
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
class ConfirmRejectedRequestCommandServiceImplTest {

    @Mock
    private LendingRequestRepository lendingRequestRepository;

    @Mock
    private CurrentTimeProvider currentTimeProvider;

    @Mock
    private HistoryRepository historyRepository;

    @InjectMocks
    private ConfirmRejectedRequestCommandServiceImpl service;

    @BeforeEach
    void setUp() {
        OperationContextHolder.set(OperationContext.create());
    }

    @AfterEach
    void tearDown() {
        OperationContextHolder.clear();
    }

    @Test
    void executeCompletesRejectedRequest() {
        var request = new ConfirmRejectedRequestCommandService.Request("USER02", 2004L, 1);
        var now = LocalDateTime.of(2026, 3, 3, 11, 0);
        LendingRequest lendingRequest = lendingRequest(2004L, "USER02", LendingRequestStatus.REJECTED, 1);
        when(lendingRequestRepository.findById(2004L)).thenReturn(Optional.of(lendingRequest));
        when(currentTimeProvider.currentDateTime()).thenReturn(now);

        service.execute(request);

        assertThat(lendingRequest.getStatus()).isEqualTo(LendingRequestStatus.COMPLETED);
        assertThat(lendingRequest.getCompletedAt()).isEqualTo(now);
        verify(lendingRequestRepository).save(lendingRequest);
        ArgumentCaptor<LendingRequestHistory> historyCaptor = ArgumentCaptor.forClass(LendingRequestHistory.class);
        verify(historyRepository).insertLendingRequestHistory(historyCaptor.capture());
        assertThat(historyCaptor.getValue().lendingRequestId()).isEqualTo(2004L);
    }

    @Test
    void executeThrowsBusinessExceptionWhenRequestIsNotRejected() {
        var request = new ConfirmRejectedRequestCommandService.Request("USER02", 2004L, 1);
        LendingRequest lendingRequest = lendingRequest(2004L, "USER02", LendingRequestStatus.LENT, 1);
        when(lendingRequestRepository.findById(2004L)).thenReturn(Optional.of(lendingRequest));

        assertThatThrownBy(() -> service.execute(request))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> assertThat(((BusinessException) ex).messageId()).isEqualTo("MSG_E_003"));

        verifyNoInteractions(historyRepository);
    }

    private LendingRequest lendingRequest(long lendingRequestId, String applicantUserId, LendingRequestStatus status, int version) {
        LendingRequest lendingRequest = new LendingRequest();
        lendingRequest.setLendingRequestId(lendingRequestId);
        lendingRequest.setApplicantUserId(applicantUserId);
        lendingRequest.setStatus(status);
        lendingRequest.setVersion(version);
        return lendingRequest;
    }
}
