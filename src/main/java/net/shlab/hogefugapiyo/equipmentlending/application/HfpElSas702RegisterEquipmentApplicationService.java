package net.shlab.hogefugapiyo.equipmentlending.application;

import net.shlab.hogefugapiyo.framework.core.service.ApplicationService;

public interface HfpElSas702RegisterEquipmentApplicationService extends ApplicationService {

    void register(String adminUserId, String equipmentName, String equipmentType, String storageLocation, String statusCode, String remarks);
}
