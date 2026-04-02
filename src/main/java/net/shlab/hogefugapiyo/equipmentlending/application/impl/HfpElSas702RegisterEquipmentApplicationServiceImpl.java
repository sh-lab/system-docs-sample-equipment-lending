package net.shlab.hogefugapiyo.equipmentlending.application.impl;

import net.shlab.hogefugapiyo.equipmentlending.application.HfpElSas702RegisterEquipmentApplicationService;
import net.shlab.hogefugapiyo.equipmentlending.application.command.RegisterEquipmentCommandService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class)
public class HfpElSas702RegisterEquipmentApplicationServiceImpl implements HfpElSas702RegisterEquipmentApplicationService {

    private final RegisterEquipmentCommandService registerEquipmentCommandService;

    public HfpElSas702RegisterEquipmentApplicationServiceImpl(RegisterEquipmentCommandService registerEquipmentCommandService) {
        this.registerEquipmentCommandService = registerEquipmentCommandService;
    }

    @Override
    public void register(String adminUserId, String equipmentName, String equipmentType, String storageLocation, String statusCode, String remarks) {
        registerEquipmentCommandService.execute(new RegisterEquipmentCommandService.Request(
                adminUserId,
                equipmentName,
                equipmentType,
                storageLocation,
                statusCode,
                remarks
        ));
    }
}
