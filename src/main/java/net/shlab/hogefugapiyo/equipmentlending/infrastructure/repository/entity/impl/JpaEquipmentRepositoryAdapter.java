package net.shlab.hogefugapiyo.equipmentlending.infrastructure.repository.entity.impl;

import java.util.List;
import java.util.Optional;
import net.shlab.hogefugapiyo.equipmentlending.infrastructure.repository.entity.EquipmentRepository;
import net.shlab.hogefugapiyo.equipmentlending.infrastructure.repository.entity.jpa.EquipmentSpringDataJpaRepository;
import net.shlab.hogefugapiyo.equipmentlending.model.entity.Equipment;
import org.springframework.stereotype.Repository;

/**
 * {@inheritDoc}
 */
@Repository
public class JpaEquipmentRepositoryAdapter implements EquipmentRepository {

    private final EquipmentSpringDataJpaRepository equipmentSpringDataJpaRepository;

    public JpaEquipmentRepositoryAdapter(
            EquipmentSpringDataJpaRepository equipmentSpringDataJpaRepository
    ) {
        this.equipmentSpringDataJpaRepository = equipmentSpringDataJpaRepository;
    }

    @Override
    public Optional<Equipment> findById(Long equipmentId) {
        return equipmentSpringDataJpaRepository.findById(equipmentId);
    }

    @Override
    public List<Equipment> findByIds(List<Long> equipmentIds) {
        if (equipmentIds == null || equipmentIds.isEmpty()) {
            return List.of();
        }
        return equipmentSpringDataJpaRepository.findByEquipmentIdInOrderByEquipmentCodeAsc(equipmentIds);
    }

    @Override
    public Equipment save(Equipment entity) {
        return equipmentSpringDataJpaRepository.save(entity);
    }

    @Override
    public List<Equipment> saveAll(List<Equipment> equipments) {
        if (equipments == null || equipments.isEmpty()) {
            return List.of();
        }
        return equipmentSpringDataJpaRepository.saveAll(equipments);
    }
}
