package net.shlab.hogefugapiyo.equipmentlending.presentation.form;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class AdminLendingApproveForm {

    @NotNull(message = "{validation.admin-lending.request-id.required}")
    private Long requestId;

    @NotNull(message = "{validation.admin-lending.version.required}")
    private Integer version;

    @Size(max = 500, message = "{validation.admin-lending.review-comment.size}")
    private String reviewComment;

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

    public String getReviewComment() {
        return reviewComment;
    }

    public void setReviewComment(String reviewComment) {
        this.reviewComment = reviewComment;
    }
}
