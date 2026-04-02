package net.shlab.hogefugapiyo.equipmentlending.application;

import net.shlab.hogefugapiyo.equipmentlending.application.query.SearchAdminEquipmentQueryService;
import net.shlab.hogefugapiyo.framework.core.service.ApplicationService;

public interface HfpElSas601AdminEquipmentSearchInitApplicationService extends ApplicationService {

    SearchAdminEquipmentQueryService.Response initialize();
}
