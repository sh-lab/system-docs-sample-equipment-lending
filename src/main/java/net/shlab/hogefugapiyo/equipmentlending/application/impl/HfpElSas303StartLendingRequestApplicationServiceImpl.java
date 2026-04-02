package net.shlab.hogefugapiyo.equipmentlending.application.impl;

import java.util.ArrayList;
import java.util.List;
import net.shlab.hogefugapiyo.equipmentlending.application.BusinessException;
import net.shlab.hogefugapiyo.equipmentlending.application.BusinessMessageIds;
import net.shlab.hogefugapiyo.equipmentlending.application.HfpElSas303StartLendingRequestApplicationService;
import net.shlab.hogefugapiyo.equipmentlending.application.pure.CheckLendingRequestAvailabilityService;
import net.shlab.hogefugapiyo.equipmentlending.application.pure.EquipmentAvailabilityInput;
import net.shlab.hogefugapiyo.equipmentlending.application.query.FindEquipmentByIdsQueryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * {@inheritDoc}
 */
@Service
@Transactional(readOnly = true, rollbackFor = Exception.class)
public class HfpElSas303StartLendingRequestApplicationServiceImpl
        implements HfpElSas303StartLendingRequestApplicationService {

    private final FindEquipmentByIdsQueryService findEquipmentByIdsQueryService;
    private final CheckLendingRequestAvailabilityService checkLendingRequestAvailabilityService;

    public HfpElSas303StartLendingRequestApplicationServiceImpl(
            FindEquipmentByIdsQueryService findEquipmentByIdsQueryService,
            CheckLendingRequestAvailabilityService checkLendingRequestAvailabilityService
    ) {
        this.findEquipmentByIdsQueryService = findEquipmentByIdsQueryService;
        this.checkLendingRequestAvailabilityService = checkLendingRequestAvailabilityService;
    }

    @Override
    public List<Long> start(List<Long> equipmentIds) {
        List<Long> normalizedEquipmentIds = normalizeEquipmentIds(equipmentIds);
        var response = findEquipmentByIdsQueryService.execute(
                new FindEquipmentByIdsQueryService.Request(normalizedEquipmentIds));
        if (response.items().size() != normalizedEquipmentIds.size()) {
            throw new BusinessException(BusinessMessageIds.EQUIPMENT_SELECTION_INVALID);
        }
        List<EquipmentAvailabilityInput> inputs = response.items().stream()
                .map(item -> new EquipmentAvailabilityInput(item.equipmentId(), item.statusCode()))
                .toList();
        var availability = checkLendingRequestAvailabilityService.check(inputs);
        if (!availability.valid()) {
            throw new BusinessException(BusinessMessageIds.EQUIPMENT_SELECTION_INVALID);
        }
        return normalizedEquipmentIds;
    }

    private List<Long> normalizeEquipmentIds(List<Long> equipmentIds) {
        if (equipmentIds == null || equipmentIds.isEmpty()) {
            throw new BusinessException(BusinessMessageIds.EQUIPMENT_SELECTION_INVALID);
        }
        List<Long> normalizedEquipmentIds = new ArrayList<>(equipmentIds);
        if (normalizedEquipmentIds.stream().distinct().count() != normalizedEquipmentIds.size()) {
            throw new BusinessException(BusinessMessageIds.EQUIPMENT_SELECTION_INVALID);
        }
        return normalizedEquipmentIds;
    }
}
