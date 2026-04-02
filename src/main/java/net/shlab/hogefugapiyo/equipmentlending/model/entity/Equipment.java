package net.shlab.hogefugapiyo.equipmentlending.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import net.shlab.hogefugapiyo.equipmentlending.model.value.EquipmentStatus;
import net.shlab.hogefugapiyo.framework.persistence.entity.AuditVersionEntity;

/**
 * 備品を表す Entity。
 *
 * <p>設計書:
 * <ul>
 *   <li>{@code docs/03_designs/entity/HFP-EL-E001_equipment.md}</li>
 *   <li>{@code docs/03_designs/data/M_EQUIPMENT.md}</li>
 * </ul>
 */
@Entity
@Table(name = "M_EQUIPMENT")
public class Equipment extends AuditVersionEntity {

    @Id
    @Column(name = "EQUIPMENT_ID", nullable = false)
    private Long equipmentId;

    @Column(name = "EQUIPMENT_CODE", nullable = false)
    private String equipmentCode;

    @Column(name = "EQUIPMENT_NAME", nullable = false)
    private String equipmentName;

    @Column(name = "EQUIPMENT_TYPE", nullable = false)
    private String equipmentType;

    @Column(name = "STORAGE_LOCATION", nullable = false)
    private String storageLocation;

    @Column(name = "SYSTEM_REGISTERED_DATE", nullable = false)
    private LocalDate systemRegisteredDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS_CODE", nullable = false)
    private EquipmentStatus status;

    @Column(name = "REMARKS")
    private String remarks;

    public Equipment() {
    }

    public Long getEquipmentId() {
        return equipmentId;
    }

    public void setEquipmentId(Long equipmentId) {
        this.equipmentId = equipmentId;
    }

    public String getEquipmentCode() {
        return equipmentCode;
    }

    public void setEquipmentCode(String equipmentCode) {
        this.equipmentCode = equipmentCode;
    }

    public String getEquipmentName() {
        return equipmentName;
    }

    public void setEquipmentName(String equipmentName) {
        this.equipmentName = equipmentName;
    }

    public String getEquipmentType() {
        return equipmentType;
    }

    public void setEquipmentType(String equipmentType) {
        this.equipmentType = equipmentType;
    }

    public String getStorageLocation() {
        return storageLocation;
    }

    public void setStorageLocation(String storageLocation) {
        this.storageLocation = storageLocation;
    }

    public EquipmentStatus getStatus() {
        return status;
    }

    public void setStatus(EquipmentStatus status) {
        this.status = status;
    }

    public LocalDate getSystemRegisteredDate() {
        return systemRegisteredDate;
    }

    public void setSystemRegisteredDate(LocalDate systemRegisteredDate) {
        this.systemRegisteredDate = systemRegisteredDate;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public long equipmentId() {
        return equipmentId;
    }

    public String equipmentCode() {
        return equipmentCode;
    }

    public String equipmentName() {
        return equipmentName;
    }

    public String equipmentType() {
        return equipmentType;
    }

    public String storageLocation() {
        return storageLocation;
    }

    public LocalDate systemRegisteredDate() {
        return systemRegisteredDate;
    }

    public String statusCode() {
        return status == null ? null : status.code();
    }

    public int version() {
        return getVersion() == null ? 0 : getVersion();
    }
}
