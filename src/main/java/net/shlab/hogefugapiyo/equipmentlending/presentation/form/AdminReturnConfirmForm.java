package net.shlab.hogefugapiyo.equipmentlending.presentation.form;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class AdminReturnConfirmForm {

    @NotNull(message = "{validation.admin-lending.request-id.required}")
    private Long requestId;

    @NotNull(message = "{validation.admin-lending.version.required}")
    private Integer version;

    @Size(max = 500, message = "{validation.admin-lending.return-confirm-comment.size}")
    private String returnConfirmComment;

    public Long getRequestId() {
        return requestId;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getReturnConfirmComment() {
        return returnConfirmComment;
    }

    public void setReturnConfirmComment(String returnConfirmComment) {
        this.returnConfirmComment = returnConfirmComment;
    }
}
