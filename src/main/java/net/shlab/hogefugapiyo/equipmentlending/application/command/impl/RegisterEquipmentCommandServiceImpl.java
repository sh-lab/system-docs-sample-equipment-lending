package net.shlab.hogefugapiyo.equipmentlending.application.command.impl;

import java.time.ZoneOffset;
import net.shlab.hogefugapiyo.equipmentlending.application.BusinessException;
import net.shlab.hogefugapiyo.equipmentlending.application.BusinessMessageIds;
import net.shlab.hogefugapiyo.equipmentlending.application.command.RegisterEquipmentCommandService;
import net.shlab.hogefugapiyo.equipmentlending.infrastructure.repository.entity.EquipmentRepository;
import net.shlab.hogefugapiyo.equipmentlending.infrastructure.repository.entity.EquipmentTypeRepository;
import net.shlab.hogefugapiyo.equipmentlending.infrastructure.repository.history.HistoryRepository;
import net.shlab.hogefugapiyo.equipmentlending.model.entity.Equipment;
import net.shlab.hogefugapiyo.equipmentlending.model.history.EquipmentHistory;
import net.shlab.hogefugapiyo.equipmentlending.model.value.EquipmentStatus;
import net.shlab.hogefugapiyo.framework.core.time.CurrentTimeProvider;
import net.shlab.hogefugapiyo.framework.service.CommandBaseService;
import org.springframework.stereotype.Service;

@Service
public class RegisterEquipmentCommandServiceImpl
        extends CommandBaseService<RegisterEquipmentCommandService.Request, RegisterEquipmentCommandService.HistoryResponse>
        implements RegisterEquipmentCommandService {

    private static final String COMMAND_ID = "HFP-EL-SCS013_register-equipment_service";

    private final EquipmentRepository equipmentRepository;
    private final EquipmentTypeRepository equipmentTypeRepository;
    private final CurrentTimeProvider currentTimeProvider;
    private final HistoryRepository historyRepository;

    public RegisterEquipmentCommandServiceImpl(
            EquipmentRepository equipmentRepository,
            EquipmentTypeRepository equipmentTypeRepository,
            CurrentTimeProvider currentTimeProvider,
            HistoryRepository historyRepository
    ) {
        this.equipmentRepository = equipmentRepository;
        this.equipmentTypeRepository = equipmentTypeRepository;
        this.currentTimeProvider = currentTimeProvider;
        this.historyRepository = historyRepository;
    }

    @Override
    protected String commandId() {
        return COMMAND_ID;
    }

    @Override
    protected HistoryResponse doExecute(Request request) {
        if (request.equipmentName() == null || request.equipmentName().isBlank()
                || request.storageLocation() == null || request.storageLocation().isBlank()) {
            throw new BusinessException(BusinessMessageIds.EQUIPMENT_UPDATE_INVALID);
        }
        equipmentTypeRepository.findActiveByCode(request.equipmentType())
                .orElseThrow(() -> new BusinessException(BusinessMessageIds.EQUIPMENT_UPDATE_INVALID));
        EquipmentStatus initialStatus = toCreatableStatus(request.statusCode());
        long equipmentId = equipmentRepository.nextId();
        var now = currentTimeProvider.currentDateTime();
        Equipment equipment = new Equipment();
        equipment.setEquipmentId(equipmentId);
        equipment.setEquipmentCode("EQ-%04d".formatted(equipmentId - 1000));
        equipment.setEquipmentName(request.equipmentName().trim());
        equipment.setEquipmentType(request.equipmentType());
        equipment.setStorageLocation(request.storageLocation().trim());
        equipment.setSystemRegisteredDate(now.toLocalDate());
        equipment.setStatus(initialStatus);
        equipment.setRemarks(normalize(request.remarks()));
        equipment.setCreatedAt(now.toInstant(ZoneOffset.UTC));
        equipment.setCreatedBy(request.adminUserId());
        equipment.setUpdatedAt(now.toInstant(ZoneOffset.UTC));
        equipment.setUpdatedBy(request.adminUserId());
        equipmentRepository.save(equipment);
        return new HistoryResponse(commandId(), equipmentId);
    }

    @Override
    protected void recordHistory(HistoryResponse result) {
        historyRepository.insertEquipmentHistories(java.util.List.of(
                new EquipmentHistory(currentOperationId(), result.equipmentId(), result.commandId(), currentTimeProvider.currentDateTime())
        ));
    }

    private EquipmentStatus toCreatableStatus(String statusCode) {
        return switch (statusCode) {
            case "AVAILABLE" -> EquipmentStatus.AVAILABLE;
            case "UNAVAILABLE" -> EquipmentStatus.UNAVAILABLE;
            case "DISPOSED" -> EquipmentStatus.DISPOSED;
            default -> throw new BusinessException(BusinessMessageIds.EQUIPMENT_UPDATE_INVALID);
        };
    }

    private String normalize(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
