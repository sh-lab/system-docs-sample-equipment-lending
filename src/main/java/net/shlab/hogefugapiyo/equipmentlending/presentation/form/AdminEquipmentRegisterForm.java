package net.shlab.hogefugapiyo.equipmentlending.presentation.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AdminEquipmentRegisterForm {

    @NotBlank(message = "{validation.admin-equipment.equipment-name.required}")
    @Size(max = 100, message = "{validation.admin-equipment.equipment-name.size}")
    private String equipmentName;

    @NotBlank(message = "{validation.admin-equipment.equipment-type.required}")
    private String equipmentType;

    @NotBlank(message = "{validation.admin-equipment.storage-location.required}")
    @Size(max = 100, message = "{validation.admin-equipment.storage-location.size}")
    private String storageLocation;

    @NotBlank(message = "{validation.admin-equipment.status-code.required}")
    private String statusCode;

    @Size(max = 500, message = "{validation.admin-equipment.remarks.size}")
    private String remarks;

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

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
