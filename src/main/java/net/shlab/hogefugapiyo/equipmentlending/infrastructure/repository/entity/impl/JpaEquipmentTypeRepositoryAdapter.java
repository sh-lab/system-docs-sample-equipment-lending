package net.shlab.hogefugapiyo.equipmentlending.infrastructure.repository.entity.impl;

import java.util.List;
import java.util.Optional;
import net.shlab.hogefugapiyo.equipmentlending.infrastructure.repository.entity.EquipmentTypeRepository;
import net.shlab.hogefugapiyo.equipmentlending.infrastructure.repository.entity.jpa.EquipmentTypeSpringDataJpaRepository;
import net.shlab.hogefugapiyo.equipmentlending.model.entity.EquipmentType;
import org.springframework.stereotype.Repository;

@Repository
public class JpaEquipmentTypeRepositoryAdapter implements EquipmentTypeRepository {

    private final EquipmentTypeSpringDataJpaRepository repository;

    public JpaEquipmentTypeRepositoryAdapter(EquipmentTypeSpringDataJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<EquipmentType> findActiveOrderByDisplayOrder() {
        return repository.findByActiveFlagTrueOrderByDisplayOrderAscEquipmentTypeCodeAsc();
    }

    @Override
    public Optional<EquipmentType> findActiveByCode(String equipmentTypeCode) {
        return repository.findByEquipmentTypeCodeAndActiveFlagTrue(equipmentTypeCode);
    }

    @Override
    public Optional<EquipmentType> findById(String id) {
        return repository.findById(id);
    }

    @Override
    public EquipmentType save(EquipmentType entity) {
        return repository.save(entity);
    }
}
