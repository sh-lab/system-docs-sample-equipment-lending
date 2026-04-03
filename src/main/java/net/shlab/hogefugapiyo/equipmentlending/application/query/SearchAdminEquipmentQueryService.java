package net.shlab.hogefugapiyo.equipmentlending.application.query;

import java.time.LocalDate;
import java.util.List;
import net.shlab.hogefugapiyo.framework.core.service.QueryService;

public interface SearchAdminEquipmentQueryService extends QueryService<SearchAdminEquipmentQueryService.Request, SearchAdminEquipmentQueryService.Response> {

    record Request(
            String equipmentName,
            String equipmentType,
            String statusCode,
            LocalDate systemRegisteredDateFrom,
            LocalDate systemRegisteredDateTo
    ) {
    }

    record EquipmentItem(long equipmentId, String equipmentCode, String equipmentName,
                         String equipmentTypeCode, String equipmentTypeLabel, LocalDate systemRegisteredDate,
                         String storageLocation, String statusCode, String statusLabel, int version) {
    }

    record Option(String value, String label) {
    }

    record Response(List<EquipmentItem> equipmentItems, List<Option> equipmentTypeOptions,
                    List<Option> statusOptions, boolean hasMoreThanLimit) {
    }
}
