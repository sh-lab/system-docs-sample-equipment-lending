package net.shlab.hogefugapiyo.equipmentlending.application.command.impl;

import net.shlab.hogefugapiyo.equipmentlending.application.BusinessException;
import net.shlab.hogefugapiyo.equipmentlending.application.BusinessMessageIds;
import net.shlab.hogefugapiyo.equipmentlending.application.command.RegisterReturnRequestCommandService;
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
public class RegisterReturnRequestCommandServiceImpl
        extends CommandBaseService<RegisterReturnRequestCommandService.Request, RegisterReturnRequestCommandService.HistoryResponse>
        implements RegisterReturnRequestCommandService {

    private static final String COMMAND_ID = "HFP-EL-SCS002_register-return-request_service";

    private final LendingRequestRepository lendingRequestRepository;
    private final CurrentTimeProvider currentTimeProvider;
    private final HistoryRepository historyRepository;

    public RegisterReturnRequestCommandServiceImpl(
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
    protected RegisterReturnRequestCommandService.HistoryResponse doExecute(RegisterReturnRequestCommandService.Request request) {
        var lendingRequest = lendingRequestRepository.findById(request.lendingRequestId())
                .orElseThrow(() -> new BusinessException(BusinessMessageIds.RETURN_REQUEST_INVALID));
        if (lendingRequest.version() != request.version()
                || lendingRequest.getStatus() != LendingRequestStatus.LENT
                || !request.userId().equals(lendingRequest.getApplicantUserId())) {
            throw new BusinessException(BusinessMessageIds.RETURN_REQUEST_INVALID);
        }
        var now = currentTimeProvider.currentDateTime();
        lendingRequest.setStatus(LendingRequestStatus.PENDING_RETURN_CONFIRMATION);
        lendingRequest.setReturnRequestComment(normalizeComment(request.returnRequestComment()));
        lendingRequest.setReturnRequestedAt(now);
        lendingRequest.setUpdatedAt(now.toInstant(java.time.ZoneOffset.UTC));
        lendingRequest.setUpdatedBy(request.userId());
        lendingRequestRepository.save(lendingRequest);
        return new RegisterReturnRequestCommandService.HistoryResponse(commandId(), request.lendingRequestId());
    }

    @Override
    protected void recordHistory(RegisterReturnRequestCommandService.HistoryResponse result) {
        historyRepository.insertLendingRequestHistory(
                new LendingRequestHistory(
                        currentOperationId(),
                        result.lendingRequestId(),
                        result.commandId(),
                        currentTimeProvider.currentDateTime()
                )
        );
    }

    private String normalizeComment(String returnRequestComment) {
        return returnRequestComment == null || returnRequestComment.isBlank() ? null : returnRequestComment.trim();
    }
}
