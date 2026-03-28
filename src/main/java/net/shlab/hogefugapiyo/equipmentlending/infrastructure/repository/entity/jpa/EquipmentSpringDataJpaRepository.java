package net.shlab.hogefugapiyo.equipmentlending.infrastructure.repository.entity.jpa;

import java.util.List;
import net.shlab.hogefugapiyo.equipmentlending.model.entity.Equipment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EquipmentSpringDataJpaRepository extends JpaRepository<Equipment, Long> {

    List<Equipment> findByEquipmentIdInOrderByEquipmentCodeAsc(List<Long> equipmentIds);
}
