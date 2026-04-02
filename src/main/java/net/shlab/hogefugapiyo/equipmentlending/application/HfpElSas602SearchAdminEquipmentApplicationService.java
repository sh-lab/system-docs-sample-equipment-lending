package net.shlab.hogefugapiyo.equipmentlending.application;

import java.time.LocalDate;
import net.shlab.hogefugapiyo.equipmentlending.application.query.SearchAdminEquipmentQueryService;
import net.shlab.hogefugapiyo.framework.core.service.ApplicationService;

public interface HfpElSas602SearchAdminEquipmentApplicationService extends ApplicationService {

    SearchAdminEquipmentQueryService.Response search(String equipmentName, String equipmentType, String statusCode, LocalDate systemRegisteredDate);
}
