package net.shlab.hogefugapiyo.equipmentlending.application.query;

import java.util.List;
import net.shlab.hogefugapiyo.framework.core.service.QueryService;

/**
 * 備品検索結果を取得する Query Service。
 *
 * <p>設計書:
 * <ul>
 *   <li>{@code docs/03_designs/services/query/HFP-EL-SQS301_search-equipment_service.md}</li>
 * </ul>
 */
public interface SearchEquipmentQueryService
        extends QueryService<SearchEquipmentQueryService.Request, SearchEquipmentQueryService.Response> {

    record Request(String equipmentName, String equipmentType, String lendingStatus) {
    }

    record EquipmentItem(long equipmentId, String equipmentCode, String equipmentName,
                         String equipmentTypeLabel, String storageLocation, String statusLabel,
                         boolean selectable) {
    }

    record Option(String equipmentTypeCode, String equipmentTypeLabel) {
    }

    record Response(List<EquipmentItem> equipmentItems, List<Option> equipmentTypeOptions,
                    boolean hasMoreThanLimit) {
    }
}
