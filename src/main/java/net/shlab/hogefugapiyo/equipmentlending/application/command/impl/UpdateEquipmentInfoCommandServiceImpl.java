package net.shlab.hogefugapiyo.equipmentlending.application.command.impl;

import net.shlab.hogefugapiyo.equipmentlending.application.BusinessException;
import net.shlab.hogefugapiyo.equipmentlending.application.BusinessMessageIds;
import net.shlab.hogefugapiyo.equipmentlending.application.command.UpdateEquipmentInfoCommandService;
import net.shlab.hogefugapiyo.equipmentlending.infrastructure.repository.entity.EquipmentRepository;
import net.shlab.hogefugapiyo.equipmentlending.infrastructure.repository.history.HistoryRepository;
import net.shlab.hogefugapiyo.equipmentlending.model.history.EquipmentHistory;
import net.shlab.hogefugapiyo.equipmentlending.model.value.EquipmentStatus;
import net.shlab.hogefugapiyo.framework.core.time.CurrentTimeProvider;
import net.shlab.hogefugapiyo.framework.service.CommandBaseService;
import org.springframework.stereotype.Service;

@Service
public class UpdateEquipmentInfoCommandServiceImpl
        extends CommandBaseService<UpdateEquipmentInfoCommandService.Request, UpdateEquipmentInfoCommandService.HistoryResponse>
        implements UpdateEquipmentInfoCommandService {

    private static final String COMMAND_ID = "HFP-EL-SCS014_update-equipment-info_service";

    private final EquipmentRepository equipmentRepository;
    private final CurrentTimeProvider currentTimeProvider;
    private final HistoryRepository historyRepository;

    public UpdateEquipmentInfoCommandServiceImpl(
            EquipmentRepository equipmentRepository,
            CurrentTimeProvider currentTimeProvider,
            HistoryRepository historyRepository
    ) {
        this.equipmentRepository = equipmentRepository;
        this.currentTimeProvider = currentTimeProvider;
        this.historyRepository = historyRepository;
    }

    @Override
    protected String commandId() {
        return COMMAND_ID;
    }

    @Override
    protected HistoryResponse doExecute(Request request) {
        var equipment = equipmentRepository.findById(request.equipmentId())
                .orElseThrow(() -> new BusinessException(BusinessMessageIds.EQUIPMENT_UPDATE_INVALID));
        if (equipment.version() != request.version()) {
            throw new BusinessException(BusinessMessageIds.EQUIPMENT_UPDATE_INVALID);
        }
        if (request.equipmentName() == null || request.equipmentName().isBlank()) {
            throw new BusinessException(BusinessMessageIds.EQUIPMENT_UPDATE_INVALID);
        }
        EquipmentStatus nextStatus = toUpdatableStatus(request.statusCode());
        if (equipment.getStatus() == EquipmentStatus.PENDING_LENDING || equipment.getStatus() == EquipmentStatus.LENT) {
            throw new BusinessException(BusinessMessageIds.EQUIPMENT_STATUS_UPDATE_RESTRICTED);
        }
        if (!isTransitionAllowed(equipment.getStatus(), nextStatus)) {
            throw new BusinessException(BusinessMessageIds.EQUIPMENT_UPDATE_INVALID);
        }
        var now = currentTimeProvider.currentDateTime();
        equipment.setEquipmentName(request.equipmentName().trim());
        equipment.setStatus(nextStatus);
        equipment.setRemarks(normalize(request.remarks()));
        equipment.setUpdatedAt(now.toInstant(java.time.ZoneOffset.UTC));
        equipment.setUpdatedBy(request.adminUserId());
        equipmentRepository.save(equipment);
        return new HistoryResponse(commandId(), equipment.equipmentId());
    }

    @Override
    protected void recordHistory(HistoryResponse result) {
        historyRepository.insertEquipmentHistories(java.util.List.of(
                new EquipmentHistory(currentOperationId(), result.equipmentId(), result.commandId(), currentTimeProvider.currentDateTime())
        ));
    }

    private EquipmentStatus toUpdatableStatus(String statusCode) {
        return switch (statusCode) {
            case "AVAILABLE" -> EquipmentStatus.AVAILABLE;
            case "UNAVAILABLE" -> EquipmentStatus.UNAVAILABLE;
            case "DISPOSED" -> EquipmentStatus.DISPOSED;
            default -> throw new BusinessException(BusinessMessageIds.EQUIPMENT_UPDATE_INVALID);
        };
    }

    private boolean isTransitionAllowed(EquipmentStatus current, EquipmentStatus next) {
        if (current == EquipmentStatus.DISPOSED) {
            return false;
        }
        if (current == next) {
            return true;
        }
        return current == EquipmentStatus.AVAILABLE || current == EquipmentStatus.UNAVAILABLE;
    }

    private String normalize(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
