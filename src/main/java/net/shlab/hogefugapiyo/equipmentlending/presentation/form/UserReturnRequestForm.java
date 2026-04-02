package net.shlab.hogefugapiyo.equipmentlending.presentation.form;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class UserReturnRequestForm {

    @NotNull(message = "{validation.user-lending.request-id.required}")
    private Long requestId;

    @NotNull(message = "{validation.user-lending.version.required}")
    private Integer version;

    @Size(max = 500, message = "{validation.user-lending.return-request-comment.size}")
    private String returnRequestComment;

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

    public String getReturnRequestComment() {
        return returnRequestComment;
    }

    public void setReturnRequestComment(String returnRequestComment) {
        this.returnRequestComment = returnRequestComment;
    }
}
