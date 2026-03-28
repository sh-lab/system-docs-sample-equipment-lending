package net.shlab.hogefugapiyo.equipmentlending.application.command.impl;

import java.util.List;
import net.shlab.hogefugapiyo.equipmentlending.application.BusinessException;
import net.shlab.hogefugapiyo.equipmentlending.application.BusinessMessageIds;
import net.shlab.hogefugapiyo.equipmentlending.application.command.ReturnConfirmCommandService;
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
public class ReturnConfirmCommandServiceImpl
        extends CommandBaseService<ReturnConfirmCommandService.Request, ReturnConfirmCommandService.HistoryResponse>
        implements ReturnConfirmCommandService {

    private static final String COMMAND_ID = "HFP-EL-SCS012_return-confirm_service";

    private final LendingRequestRepository lendingRequestRepository;
    private final EquipmentRepository equipmentRepository;
    private final CurrentTimeProvider currentTimeProvider;
    private final HistoryRepository historyRepository;

    public ReturnConfirmCommandServiceImpl(
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
    protected ReturnConfirmCommandService.HistoryResponse doExecute(ReturnConfirmCommandService.Request request) {
        List<Long> equipmentIds = lendingRequestRepository.findEquipmentIdsByLendingRequestId(request.lendingRequestId());
        var lendingRequest = lendingRequestRepository.findById(request.lendingRequestId())
                .orElseThrow(() -> new BusinessException(BusinessMessageIds.RETURN_CONFIRM_INVALID));
        if (lendingRequest.version() != request.version()
                || lendingRequest.getStatus() != LendingRequestStatus.PENDING_RETURN_CONFIRMATION) {
            throw new BusinessException(BusinessMessageIds.RETURN_CONFIRM_INVALID);
        }
        List<net.shlab.hogefugapiyo.equipmentlending.model.entity.Equipment> equipments =
                equipmentIds.isEmpty() ? List.of() : equipmentRepository.findByIds(equipmentIds);
        if (equipments.size() != equipmentIds.size()) {
            throw new BusinessException(BusinessMessageIds.RETURN_CONFIRM_INVALID);
        }
        var now = currentTimeProvider.currentDateTime();
        lendingRequest.setStatus(LendingRequestStatus.COMPLETED);
        lendingRequest.setReturnConfirmedByUserId(request.adminUserId());
        lendingRequest.setReturnConfirmComment(normalizeComment(request.returnConfirmComment()));
        lendingRequest.setCompletedAt(now);
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
        return new ReturnConfirmCommandService.HistoryResponse(commandId(), request.lendingRequestId(), equipmentIds);
    }

    @Override
    protected void recordHistory(ReturnConfirmCommandService.HistoryResponse result) {
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

    private String normalizeComment(String returnConfirmComment) {
        return returnConfirmComment == null || returnConfirmComment.isBlank() ? null : returnConfirmComment.trim();
    }
}
