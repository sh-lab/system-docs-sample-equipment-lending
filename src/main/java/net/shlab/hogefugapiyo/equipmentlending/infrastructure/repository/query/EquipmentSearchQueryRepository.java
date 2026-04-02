package net.shlab.hogefugapiyo.equipmentlending.infrastructure.repository.query;

import java.util.List;
import net.shlab.hogefugapiyo.framework.core.repository.QueryRepository;

public interface EquipmentSearchQueryRepository extends QueryRepository {
    record Criteria(String equipmentName, String equipmentType, String lendingStatus) {
    }

    record EquipmentRow(long equipmentId, String equipmentCode, String equipmentName,
                        String equipmentTypeCode, String equipmentTypeName, String storageLocation, String statusCode,
                        boolean selectable) {
    }

    record EquipmentTypeOptionRow(String equipmentTypeCode, String equipmentTypeName) {
    }

    List<EquipmentRow> findEquipmentByCriteria(Criteria criteria);
    List<EquipmentTypeOptionRow> findEquipmentTypeOptions();
}
