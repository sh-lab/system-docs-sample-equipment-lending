package net.shlab.hogefugapiyo.equipmentlending.infrastructure.repository.entity.jpa;

public interface EquipmentSummaryProjection {

    Long getEquipmentId();

    String getEquipmentCode();

    String getEquipmentName();

    String getEquipmentType();

    String getEquipmentTypeName();

    String getStorageLocation();

    String getStatusCode();
}
