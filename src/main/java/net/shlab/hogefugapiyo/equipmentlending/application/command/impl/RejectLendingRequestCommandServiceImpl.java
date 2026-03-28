package net.shlab.hogefugapiyo.equipmentlending.application.command.impl;

import java.util.List;
import net.shlab.hogefugapiyo.equipmentlending.application.BusinessException;
import net.shlab.hogefugapiyo.equipmentlending.application.BusinessMessageIds;
import net.shlab.hogefugapiyo.equipmentlending.application.command.RejectLendingRequestCommandService;
import net.shlab.hogefugapiyo.equipmentlending.infrastructure.repository.entity.EquipmentRepository;
import net.shlab.hogefugapiyo.equipmentlending.infrastructure.repository.history.HistoryRepository;
import net.shlab.hogefugapiyo.equipmentlending.infrastructure.repository.entity.LendingRequestRepository;
import net.shlab.hogefugapiyo.equipmentlending.model.history.EquipmentHistory;
import net.shlab.hogefugapiyo.equipmentlending.model.history.LendingRequestHistory;
import net.shlab.hogefugapiyo.equipmentlending.model.value.EquipmentStatus;
import net.shlab.hogefugapiyo.equipmentlending.model.value.LendingRequestStatus;
import net.shlab.hogefugapiyo.framework.core.time.CurrentTimeProvider;
import net.shlab.hogefugapiyo.framework.service.CommandBaseService;
import org.springframework.stereotype.Service;

/**
 * {@inheritDoc}
 */
@Service
public class RejectLendingRequestCommandServiceImpl
        extends CommandBaseService<RejectLendingRequestCommandService.Request, RejectLendingRequestCommandService.HistoryResponse>
        implements RejectLendingRequestCommandService {

    private static final String COMMAND_ID = "HFP-EL-SCS011_reject-lending-request_service";

    private final LendingRequestRepository lendingRequestRepository;
    private final EquipmentRepository equipmentRepository;
    private final CurrentTimeProvider currentTimeProvider;
    private final HistoryRepository historyRepository;

    public RejectLendingRequestCommandServiceImpl(
            LendingRequestRepository lendingRequestRepository,
            EquipmentRepository equipmentRepository,
            CurrentTimeProvider currentTimeProvider,
            HistoryRepository historyRepository
    ) {
        this.lendingRequestRepository = lendingRequestRepository;
        this.equipmentRepository = equipmentRepository;
        this.currentTimeProvider = currentTimeProvider;
        this.historyRepository = historyRepository;
    }

    @Override
    protected String commandId() {
        return COMMAND_ID;
    }

    @Override
    protected RejectLendingRequestCommandService.HistoryResponse doExecute(RejectLendingRequestCommandService.Request request) {
        List<Long> equipmentIds = lendingRequestRepository.findEquipmentIdsByLendingRequestId(request.lendingRequestId());
        var lendingRequest = lendingRequestRepository.findById(request.lendingRequestId())
                .orElseThrow(() -> new BusinessException(BusinessMessageIds.REJECT_REQUEST_INVALID));
        if (lendingRequest.version() != request.version()
                || lendingRequest.getStatus() != LendingRequestStatus.PENDING_APPROVAL) {
            throw new BusinessException(BusinessMessageIds.REJECT_REQUEST_INVALID);
        }
        List<net.shlab.hogefugapiyo.equipmentlending.model.entity.Equipment> equipments =
                equipmentIds.isEmpty() ? List.of() : equipmentRepository.findByIds(equipmentIds);
        if (equipments.size() != equipmentIds.size()) {
            throw new BusinessException(BusinessMessageIds.REJECT_REQUEST_INVALID);
        }
        var now = currentTimeProvider.currentDateTime();
        lendingRequest.setStatus(LendingRequestStatus.REJECTED);
        lendingRequest.setReviewedByUserId(request.adminUserId());
        lendingRequest.setReviewComment(normalizeComment(request.reviewComment()));
        lendingRequest.setReviewedAt(now);
        lendingRequest.setUpdatedAt(now.toInstant(java.time.ZoneOffset.UTC));
        lendingRequest.setUpdatedBy(request.adminUserId());
        lendingRequestRepository.save(lendingRequest);
        equipments.forEach(equipment -> {
            equipment.setStatus(EquipmentStatus.AVAILABLE);
            equipment.setUpdatedAt(now.toInstant(java.time.ZoneOffset.UTC));
            equipment.setUpdatedBy(request.adminUserId());
        });
        if (!equipments.isEmpty()) {
            equipmentRepository.saveAll(equipments);
        }
        return new RejectLendingRequestCommandService.HistoryResponse(commandId(), request.lendingRequestId(), equipmentIds);
    }

    @Override
    protected void recordHistory(RejectLendingRequestCommandService.HistoryResponse result) {
        var operationId = currentOperationId();
        var operatedAt = currentTimeProvider.currentDateTime();
        historyRepository.insertLendingRequestHistory(
                new LendingRequestHistory(operationId, result.lendingRequestId(), result.commandId(), operatedAt)
        );
        historyRepository.insertEquipmentHistories(
                result.equipmentIds().stream()
                        .map(equipmentId -> new EquipmentHistory(
                                operationId,
                                equipmentId,
                                result.commandId(),
                                operatedAt
                        ))
                        .toList()
        );
    }

    private String normalizeComment(String reviewComment) {
        return reviewComment == null || reviewComment.isBlank() ? null : reviewComment.trim();
    }
}
