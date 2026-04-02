package net.shlab.hogefugapiyo.equipmentlending.application.impl;

import net.shlab.hogefugapiyo.equipmentlending.application.HfpElSas703UpdateEquipmentInfoApplicationService;
import net.shlab.hogefugapiyo.equipmentlending.application.command.UpdateEquipmentInfoCommandService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class)
public class HfpElSas703UpdateEquipmentInfoApplicationServiceImpl implements HfpElSas703UpdateEquipmentInfoApplicationService {

    private final UpdateEquipmentInfoCommandService updateEquipmentStatusCommandService;

    public HfpElSas703UpdateEquipmentInfoApplicationServiceImpl(UpdateEquipmentInfoCommandService updateEquipmentStatusCommandService) {
        this.updateEquipmentStatusCommandService = updateEquipmentStatusCommandService;
    }

    @Override
    public void update(String adminUserId, long equipmentId, String equipmentName, String statusCode, String remarks, int version) {
        updateEquipmentStatusCommandService.execute(new UpdateEquipmentInfoCommandService.Request(
                adminUserId,
                equipmentId,
                equipmentName,
                statusCode,
                remarks,
                version
        ));
    }
}
