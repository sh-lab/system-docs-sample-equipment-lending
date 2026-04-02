package net.shlab.hogefugapiyo.equipmentlending.application.query.impl;

import java.util.List;
import net.shlab.hogefugapiyo.equipmentlending.application.BusinessException;
import net.shlab.hogefugapiyo.equipmentlending.application.BusinessMessageIds;
import net.shlab.hogefugapiyo.equipmentlending.application.query.FindEquipmentByIdsQueryService;
import net.shlab.hogefugapiyo.equipmentlending.infrastructure.repository.entity.EquipmentRepository;
import org.springframework.stereotype.Service;

/**
 * {@inheritDoc}
 */
@Service
public class FindEquipmentByIdsQueryServiceImpl implements FindEquipmentByIdsQueryService {

    private final EquipmentRepository equipmentRepository;

    public FindEquipmentByIdsQueryServiceImpl(EquipmentRepository equipmentRepository) {
        this.equipmentRepository = equipmentRepository;
    }

    @Override
    public FindEquipmentByIdsQueryService.Response execute(FindEquipmentByIdsQueryService.Request request) {
        if (request.equipmentIds() == null || request.equipmentIds().isEmpty()) {
            throw new BusinessException(BusinessMessageIds.EQUIPMENT_SELECTION_INVALID);
        }
        List<FindEquipmentByIdsQueryService.EquipmentItem> items = equipmentRepository.findSummaryByIds(request.equipmentIds()).stream()
                .map(summary -> new FindEquipmentByIdsQueryService.EquipmentItem(
                        summary.equipmentId(),
                        summary.equipmentCode(),
                        summary.equipmentName(),
                        summary.equipmentType(),
                        summary.equipmentTypeName(),
                        summary.storageLocation(),
                        summary.statusCode()
                ))
                .toList();
        return new FindEquipmentByIdsQueryService.Response(items);
    }
}
