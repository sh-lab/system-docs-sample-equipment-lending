package net.shlab.hogefugapiyo.equipmentlending.presentation.form;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.List;

public class UserLendingRegisterForm {

    @NotEmpty(message = "{validation.user-lending.equipment-ids.required}")
    private List<Long> equipmentIds;

    @Size(max = 500, message = "{validation.user-lending.request-comment.size}")
    private String requestComment;

    public List<Long> getEquipmentIds() {
        return equipmentIds;
    }

    public void setEquipmentIds(List<Long> equipmentIds) {
        this.equipmentIds = equipmentIds;
    }

    public String getRequestComment() {
        return requestComment;
    }

    public void setRequestComment(String requestComment) {
        this.requestComment = requestComment;
    }
}
