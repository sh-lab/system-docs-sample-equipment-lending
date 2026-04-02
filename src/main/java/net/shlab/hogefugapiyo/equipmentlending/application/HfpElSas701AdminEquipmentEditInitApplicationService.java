package net.shlab.hogefugapiyo.equipmentlending.application;

import net.shlab.hogefugapiyo.equipmentlending.application.query.FindAdminEquipmentEditQueryService;
import net.shlab.hogefugapiyo.framework.core.service.ApplicationService;

public interface HfpElSas701AdminEquipmentEditInitApplicationService extends ApplicationService {

    FindAdminEquipmentEditQueryService.Response initialize(String mode, Long equipmentId);
}
