package net.shlab.hogefugapiyo.equipmentlending.infrastructure.repository.entity.jpa;

import java.util.List;
import net.shlab.hogefugapiyo.equipmentlending.model.entity.Equipment;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

public interface EquipmentSpringDataJpaRepository extends JpaRepository<Equipment, Long> {

    List<Equipment> findByEquipmentIdInOrderByEquipmentCodeAsc(List<Long> equipmentIds);

    @Query(value = "SELECT COALESCE(MAX(EQUIPMENT_ID), 1000) + 1 FROM M_EQUIPMENT", nativeQuery = true)
    Long nextId();

    @Query(
            """
            select e.equipmentId as equipmentId,
                   e.equipmentCode as equipmentCode,
                   e.equipmentName as equipmentName,
                   e.equipmentType as equipmentType,
                   t.equipmentTypeName as equipmentTypeName,
                   e.storageLocation as storageLocation,
                   CAST(e.status AS string) as statusCode
            from Equipment e
            join EquipmentType t on t.equipmentTypeCode = e.equipmentType
            where e.equipmentId in :equipmentIds
            order by e.equipmentCode asc
            """
    )
    List<EquipmentSummaryProjection> findSummaryByEquipmentIdIn(@Param("equipmentIds") List<Long> equipmentIds);
}
