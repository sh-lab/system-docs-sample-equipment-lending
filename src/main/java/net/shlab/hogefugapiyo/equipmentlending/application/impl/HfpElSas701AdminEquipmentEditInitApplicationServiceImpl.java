package net.shlab.hogefugapiyo.equipmentlending.application.impl;

import net.shlab.hogefugapiyo.equipmentlending.application.HfpElSas701AdminEquipmentEditInitApplicationService;
import net.shlab.hogefugapiyo.equipmentlending.application.query.FindAdminEquipmentEditQueryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class)
public class HfpElSas701AdminEquipmentEditInitApplicationServiceImpl implements HfpElSas701AdminEquipmentEditInitApplicationService {

    private final FindAdminEquipmentEditQueryService findAdminEquipmentEditQueryService;

    public HfpElSas701AdminEquipmentEditInitApplicationServiceImpl(FindAdminEquipmentEditQueryService findAdminEquipmentEditQueryService) {
        this.findAdminEquipmentEditQueryService = findAdminEquipmentEditQueryService;
    }

    @Override
    public FindAdminEquipmentEditQueryService.Response initialize(String mode, Long equipmentId) {
        return findAdminEquipmentEditQueryService.execute(new FindAdminEquipmentEditQueryService.Request(mode, equipmentId));
    }
}
