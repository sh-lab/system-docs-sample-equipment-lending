package net.shlab.hogefugapiyo.equipmentlending.presentation.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class AdminEquipmentUpdateForm {

    @NotNull
    private Long equipmentId;

    @NotBlank(message = "{validation.admin-equipment.equipment-name.required}")
    @Size(max = 100, message = "{validation.admin-equipment.equipment-name.size}")
    private String equipmentName;

    @NotBlank(message = "{validation.admin-equipment.status-code.required}")
    private String statusCode;

    @Size(max = 500, message = "{validation.admin-equipment.remarks.size}")
    private String remarks;

    @NotNull
    private Integer version;

    public Long getEquipmentId() {
        return equipmentId;
    }

    public void setEquipmentId(Long equipmentId) {
        this.equipmentId = equipmentId;
    }

    public String getEquipmentName() {
        return equipmentName;
    }

    public void setEquipmentName(String equipmentName) {
        this.equipmentName = equipmentName;
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

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }
}
