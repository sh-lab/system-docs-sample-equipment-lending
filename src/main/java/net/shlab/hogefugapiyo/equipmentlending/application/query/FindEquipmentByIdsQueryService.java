package net.shlab.hogefugapiyo.equipmentlending.application.query;

import java.util.List;
import net.shlab.hogefugapiyo.framework.core.service.QueryService;

/**
 * 備品ID一覧から対象備品を取得する Query Service。
 *
 * <p>設計書:
 * <ul>
 *   <li>{@code docs/03_designs/services/query/HFP-EL-SQS001_find-equipment-by-ids_service.md}</li>
 * </ul>
 */
public interface FindEquipmentByIdsQueryService
        extends QueryService<FindEquipmentByIdsQueryService.Request, FindEquipmentByIdsQueryService.Response> {

    record Request(List<Long> equipmentIds) {
    }

    record EquipmentItem(long equipmentId, String equipmentCode, String equipmentName,
                         String equipmentType, String storageLocation) {
    }

    record Response(List<EquipmentItem> items) {
    }
}
