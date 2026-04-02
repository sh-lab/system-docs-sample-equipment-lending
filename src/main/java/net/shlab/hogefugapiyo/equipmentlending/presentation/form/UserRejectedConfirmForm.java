package net.shlab.hogefugapiyo.equipmentlending.presentation.form;

import jakarta.validation.constraints.NotNull;

public class UserRejectedConfirmForm {

    @NotNull(message = "{validation.user-lending.request-id.required}")
    private Long requestId;

    @NotNull(message = "{validation.user-lending.version.required}")
    private Integer version;

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
}
