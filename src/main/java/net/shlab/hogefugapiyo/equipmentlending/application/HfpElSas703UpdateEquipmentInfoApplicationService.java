package net.shlab.hogefugapiyo.equipmentlending.application;

import net.shlab.hogefugapiyo.framework.core.service.ApplicationService;

public interface HfpElSas703UpdateEquipmentInfoApplicationService extends ApplicationService {

    void update(String adminUserId, long equipmentId, String equipmentName, String statusCode, String remarks, int version);
}
