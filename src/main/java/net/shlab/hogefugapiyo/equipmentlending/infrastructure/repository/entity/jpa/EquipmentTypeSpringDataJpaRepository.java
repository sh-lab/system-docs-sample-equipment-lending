package net.shlab.hogefugapiyo.equipmentlending.infrastructure.repository.entity.jpa;

import java.util.List;
import java.util.Optional;
import net.shlab.hogefugapiyo.equipmentlending.model.entity.EquipmentType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EquipmentTypeSpringDataJpaRepository extends JpaRepository<EquipmentType, String> {

    List<EquipmentType> findByActiveFlagTrueOrderByDisplayOrderAscEquipmentTypeCodeAsc();

    Optional<EquipmentType> findByEquipmentTypeCodeAndActiveFlagTrue(String equipmentTypeCode);
}
