package net.shlab.hogefugapiyo.equipmentlending.application.command.impl;

import net.shlab.hogefugapiyo.equipmentlending.application.BusinessException;
import net.shlab.hogefugapiyo.equipmentlending.application.BusinessMessageIds;
import net.shlab.hogefugapiyo.equipmentlending.application.command.ConfirmRejectedRequestCommandService;
import net.shlab.hogefugapiyo.equipmentlending.infrastructure.repository.history.HistoryRepository;
import net.shlab.hogefugapiyo.equipmentlending.infrastructure.repository.entity.LendingRequestRepository;
import net.shlab.hogefugapiyo.equipmentlending.model.history.LendingRequestHistory;
import net.shlab.hogefugapiyo.equipmentlending.model.value.LendingRequestStatus;
import net.shlab.hogefugapiyo.framework.core.time.CurrentTimeProvider;
import net.shlab.hogefugapiyo.framework.service.CommandBaseService;
import org.springframework.stereotype.Service;

/**
 * {@inheritDoc}
 */
@Service
public class ConfirmRejectedRequestCommandServiceImpl
        extends CommandBaseService<ConfirmRejectedRequestCommandService.Request, ConfirmRejectedRequestCommandService.HistoryResponse>
        implements ConfirmRejectedRequestCommandService {

    private static final String COMMAND_ID = "HFP-EL-SCS003_confirm-rejected-request_service";

    private final LendingRequestRepository lendingRequestRepository;
    private final CurrentTimeProvider currentTimeProvider;
    private final HistoryRepository historyRepository;

    public ConfirmRejectedRequestCommandServiceImpl(
            LendingRequestRepository lendingRequestRepository,
            CurrentTimeProvider currentTimeProvider,
            HistoryRepository historyRepository
    ) {
        this.lendingRequestRepository = lendingRequestRepository;
        this.currentTimeProvider = currentTimeProvider;
        this.historyRepository = historyRepository;
    }

    @Override
    protected String commandId() {
        return COMMAND_ID;
    }

    @Override
    protected ConfirmRejectedRequestCommandService.HistoryResponse doExecute(ConfirmRejectedRequestCommandService.Request request) {
        var lendingRequest = lendingRequestRepository.findById(request.lendingRequestId())
                .orElseThrow(() -> new BusinessException(BusinessMessageIds.REJECTED_CONFIRM_INVALID));
        if (lendingRequest.version() != request.version()
                || lendingRequest.getStatus() != LendingRequestStatus.REJECTED
                || !request.userId().equals(lendingRequest.getApplicantUserId())) {
            throw new BusinessException(BusinessMessageIds.REJECTED_CONFIRM_INVALID);
        }
        var now = currentTimeProvider.currentDateTime();
        lendingRequest.setStatus(LendingRequestStatus.COMPLETED);
        lendingRequest.setCompletedAt(now);
        lendingRequest.setUpdatedAt(now.toInstant(java.time.ZoneOffset.UTC));
        lendingRequest.setUpdatedBy(request.userId());
        lendingRequestRepository.save(lendingRequest);
        return new ConfirmRejectedRequestCommandService.HistoryResponse(commandId(), request.lendingRequestId());
    }

    @Override
    protected void recordHistory(ConfirmRejectedRequestCommandService.HistoryResponse result) {
        historyRepository.insertLendingRequestHistory(
                new LendingRequestHistory(
                        currentOperationId(),
                        result.lendingRequestId(),
                        result.commandId(),
                        currentTimeProvider.currentDateTime()
                )
        );
    }
}
