package net.shlab.hogefugapiyo.equipmentlending.application.impl;

import java.util.ArrayList;
import java.util.List;
import net.shlab.hogefugapiyo.equipmentlending.application.BusinessException;
import net.shlab.hogefugapiyo.equipmentlending.application.BusinessMessageIds;
import net.shlab.hogefugapiyo.equipmentlending.application.HfpElSas303StartLendingRequestApplicationService;
import net.shlab.hogefugapiyo.equipmentlending.application.pure.CheckLendingRequestAvailabilityService;
import net.shlab.hogefugapiyo.equipmentlending.infrastructure.repository.entity.EquipmentRepository;
import net.shlab.hogefugapiyo.equipmentlending.model.entity.Equipment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * {@inheritDoc}
 */
@Service
@Transactional(readOnly = true, rollbackFor = Exception.class)
public class HfpElSas303StartLendingRequestApplicationServiceImpl
        implements HfpElSas303StartLendingRequestApplicationService {

    private final EquipmentRepository equipmentRepository;
    private final CheckLendingRequestAvailabilityService checkLendingRequestAvailabilityService;

    public HfpElSas303StartLendingRequestApplicationServiceImpl(
            EquipmentRepository equipmentRepository,
            CheckLendingRequestAvailabilityService checkLendingRequestAvailabilityService
    ) {
        this.equipmentRepository = equipmentRepository;
        this.checkLendingRequestAvailabilityService = checkLendingRequestAvailabilityService;
    }

    @Override
    public List<Long> start(List<Long> equipmentIds) {
        List<Long> normalizedEquipmentIds = normalizeEquipmentIds(equipmentIds);
        List<Equipment> equipments = equipmentRepository.findByIds(normalizedEquipmentIds);
        if (equipments.size() != normalizedEquipmentIds.size()) {
            throw new BusinessException(BusinessMessageIds.EQUIPMENT_SELECTION_INVALID);
        }
        var availability = checkLendingRequestAvailabilityService.check(equipments);
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
