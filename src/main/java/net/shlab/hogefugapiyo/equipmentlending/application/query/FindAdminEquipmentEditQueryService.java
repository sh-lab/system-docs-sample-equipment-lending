package net.shlab.hogefugapiyo.equipmentlending.application.query;

import java.time.LocalDate;
import java.util.List;
import net.shlab.hogefugapiyo.framework.core.service.QueryService;

public interface FindAdminEquipmentEditQueryService extends QueryService<FindAdminEquipmentEditQueryService.Request, FindAdminEquipmentEditQueryService.Response> {

    record Request(String mode, Long equipmentId) {
    }

    record Option(String value, String label) {
    }

    record EquipmentDetail(Long equipmentId, String equipmentCode, String equipmentName,
                           String equipmentType, String equipmentTypeLabel, String storageLocation,
                           LocalDate systemRegisteredDate, String remarks, String currentStatusCode,
                           String currentStatusLabel, int version) {
    }

    record Response(String mode, LocalDate displaySystemRegisteredDate, EquipmentDetail equipmentDetail,
                    List<Option> equipmentTypeOptions, List<Option> statusOptions) {
    }
}
