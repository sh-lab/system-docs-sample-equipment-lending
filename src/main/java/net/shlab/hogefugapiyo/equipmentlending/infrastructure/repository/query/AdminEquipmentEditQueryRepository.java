package net.shlab.hogefugapiyo.equipmentlending.infrastructure.repository.query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import net.shlab.hogefugapiyo.framework.core.repository.QueryRepository;

public interface AdminEquipmentEditQueryRepository extends QueryRepository {

    record EquipmentDetailRow(long equipmentId, String equipmentCode, String equipmentName,
                              String equipmentTypeCode, String equipmentTypeName, String storageLocation,
                              LocalDate systemRegisteredDate, String remarks, String statusCode, int version) {
    }

    record EquipmentTypeOptionRow(String equipmentTypeCode, String equipmentTypeName) {
    }

    Optional<EquipmentDetailRow> findEquipmentDetail(long equipmentId);

    List<EquipmentTypeOptionRow> findEquipmentTypeOptions();
}
