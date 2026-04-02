package net.shlab.hogefugapiyo.equipmentlending.presentation.form;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public class EquipmentLendingStartForm {

    @NotEmpty(message = "{validation.equipment-search-start.equipment-ids.required}")
    private List<Long> equipmentIds;

    private String equipmentName;

    private String equipmentType;

    private String lendingStatus;

    public List<Long> getEquipmentIds() {
        return equipmentIds;
    }

    public void setEquipmentIds(List<Long> equipmentIds) {
        this.equipmentIds = equipmentIds;
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

    public String getLendingStatus() {
        return lendingStatus;
    }

    public void setLendingStatus(String lendingStatus) {
        this.lendingStatus = lendingStatus;
    }
}
