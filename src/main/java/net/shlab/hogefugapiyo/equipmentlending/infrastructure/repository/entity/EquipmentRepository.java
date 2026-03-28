package net.shlab.hogefugapiyo.equipmentlending.infrastructure.repository.entity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import net.shlab.hogefugapiyo.equipmentlending.model.entity.Equipment;
import net.shlab.hogefugapiyo.framework.core.repository.EntityRepository;

/**
 * 備品 Entity の永続化を担当する Entity Repository。
 *
 * <p>設計書:
 * <ul>
 *   <li>{@code docs/03_designs/entity/HFP-EL-E001_equipment.md}</li>
 *   <li>{@code docs/03_designs/data/M_EQUIPMENT.md}</li>
 * </ul>
 */
public interface EquipmentRepository extends EntityRepository<Equipment, Long> {

    List<Equipment> findByIds(List<Long> equipmentIds);

    List<Equipment> saveAll(List<Equipment> equipments);
}
