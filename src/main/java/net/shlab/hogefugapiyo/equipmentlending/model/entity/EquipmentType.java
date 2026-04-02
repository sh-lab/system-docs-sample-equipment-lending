package net.shlab.hogefugapiyo.equipmentlending.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import net.shlab.hogefugapiyo.framework.persistence.entity.AuditVersionEntity;

@Entity
@Table(name = "M_EQUIPMENT_TYPE")
public class EquipmentType extends AuditVersionEntity {

    @Id
    @Column(name = "EQUIPMENT_TYPE_CODE", nullable = false)
    private String equipmentTypeCode;

    @Column(name = "EQUIPMENT_TYPE_NAME", nullable = false)
    private String equipmentTypeName;

    @Column(name = "DISPLAY_ORDER", nullable = false)
    private int displayOrder;

    @Column(name = "ACTIVE_FLAG", nullable = false)
    private boolean activeFlag;

    public String getEquipmentTypeCode() {
        return equipmentTypeCode;
    }

    public void setEquipmentTypeCode(String equipmentTypeCode) {
        this.equipmentTypeCode = equipmentTypeCode;
    }

    public String getEquipmentTypeName() {
        return equipmentTypeName;
    }

    public void setEquipmentTypeName(String equipmentTypeName) {
        this.equipmentTypeName = equipmentTypeName;
    }

    public int getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(int displayOrder) {
        this.displayOrder = displayOrder;
    }

    public boolean isActiveFlag() {
        return activeFlag;
    }

    public void setActiveFlag(boolean activeFlag) {
        this.activeFlag = activeFlag;
    }

    public String equipmentTypeCode() {
        return equipmentTypeCode;
    }

    public String equipmentTypeName() {
        return equipmentTypeName;
    }

    public int displayOrder() {
        return displayOrder;
    }

    public boolean active() {
        return activeFlag;
    }
}
