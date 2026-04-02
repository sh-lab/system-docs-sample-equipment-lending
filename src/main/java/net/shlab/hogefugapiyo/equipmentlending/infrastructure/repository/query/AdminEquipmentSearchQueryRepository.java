package net.shlab.hogefugapiyo.equipmentlending.infrastructure.repository.query;

import java.time.LocalDate;
import java.util.List;
import net.shlab.hogefugapiyo.framework.core.repository.QueryRepository;

public interface AdminEquipmentSearchQueryRepository extends QueryRepository {

    record Criteria(String equipmentName, String equipmentType, String statusCode, LocalDate systemRegisteredDate) {
    }

    record EquipmentRow(long equipmentId, String equipmentCode, String equipmentName,
                        String equipmentTypeCode, String equipmentTypeName, LocalDate systemRegisteredDate,
                        String storageLocation, String statusCode, int version) {
    }

    record EquipmentTypeOptionRow(String equipmentTypeCode, String equipmentTypeName) {
    }

    List<EquipmentRow> findByCriteria(Criteria criteria);

    List<EquipmentTypeOptionRow> findEquipmentTypeOptions();
}
