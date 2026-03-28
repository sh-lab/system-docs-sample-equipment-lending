package net.shlab.hogefugapiyo.equipmentlending.application.pure.impl;

import java.util.List;
import net.shlab.hogefugapiyo.equipmentlending.application.BusinessException;
import net.shlab.hogefugapiyo.equipmentlending.application.BusinessMessageIds;
import net.shlab.hogefugapiyo.equipmentlending.application.command.LendingRequestAvailabilityResult;
import net.shlab.hogefugapiyo.equipmentlending.application.pure.CheckLendingRequestAvailabilityService;
import net.shlab.hogefugapiyo.equipmentlending.model.entity.Equipment;
import net.shlab.hogefugapiyo.equipmentlending.model.value.EquipmentStatus;
import org.springframework.stereotype.Service;

/**
 * {@inheritDoc}
 */
@Service
public class CheckLendingRequestAvailabilityServiceImpl implements CheckLendingRequestAvailabilityService {

    @Override
    public LendingRequestAvailabilityResult check(List<Equipment> equipments) {
        if (equipments == null || equipments.isEmpty()) {
            throw new BusinessException(BusinessMessageIds.EQUIPMENT_SELECTION_INVALID);
        }
        List<Long> unavailableIds = equipments.stream()
                .filter(e -> e.getStatus() != EquipmentStatus.AVAILABLE)
                .map(Equipment::equipmentId)
                .toList();
        List<String> invalidStatusCodes = equipments.stream()
                .filter(e -> e.getStatus() != EquipmentStatus.AVAILABLE)
                .map(Equipment::statusCode)
                .distinct()
                .toList();
        return new LendingRequestAvailabilityResult(unavailableIds.isEmpty(), unavailableIds, invalidStatusCodes);
    }
}
