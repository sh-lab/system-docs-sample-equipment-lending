package net.shlab.hogefugapiyo.equipmentlending.infrastructure.repository.entity;

import java.util.List;
import java.util.Optional;
import net.shlab.hogefugapiyo.equipmentlending.model.entity.EquipmentType;
import net.shlab.hogefugapiyo.framework.core.repository.EntityRepository;

public interface EquipmentTypeRepository extends EntityRepository<EquipmentType, String> {

    List<EquipmentType> findActiveOrderByDisplayOrder();

    Optional<EquipmentType> findActiveByCode(String equipmentTypeCode);
}
