package net.shlab.hogefugapiyo.equipmentlending.model.entity;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import net.shlab.hogefugapiyo.equipmentlending.model.value.LendingRequestStatus;
import net.shlab.hogefugapiyo.framework.persistence.entity.AuditVersionEntity;

/**
 * 貸出申請を表す Entity。
 *
 * <p>設計書:
 * <ul>
 *   <li>{@code docs/03_designs/entity/HFP-EL-E003_lending-request.md}</li>
 *   <li>{@code docs/03_designs/data/T_LENDING_REQUEST.md}</li>
 *   <li>{@code docs/03_designs/data/T_LENDING_REQUEST_DETAIL.md}</li>
 * </ul>
 */
@Entity
@Table(name = "T_LENDING_REQUEST")
public class LendingRequest extends AuditVersionEntity {

    @Id
    @Column(name = "LENDING_REQUEST_ID", nullable = false)
    private Long lendingRequestId;

    @Column(name = "APPLICANT_USER_ID", nullable = false)
    private String applicantUserId;

    @Column(name = "REVIEWED_BY_USER_ID")
    private String reviewedByUserId;

    @Column(name = "RETURN_CONFIRMED_BY_USER_ID")
    private String returnConfirmedByUserId;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS_CODE", nullable = false)
    private LendingRequestStatus status;

    @Column(name = "REQUEST_COMMENT")
    private String requestComment;

    @Column(name = "REVIEW_COMMENT")
    private String reviewComment;

    @Column(name = "RETURN_REQUEST_COMMENT")
    private String returnRequestComment;

    @Column(name = "RETURN_CONFIRM_COMMENT")
    private String returnConfirmComment;

    @Column(name = "REQUESTED_AT", nullable = false)
    private LocalDateTime requestedAt;

    @Column(name = "REVIEWED_AT")
    private LocalDateTime reviewedAt;

    @Column(name = "RETURN_REQUESTED_AT")
    private LocalDateTime returnRequestedAt;

    @Column(name = "COMPLETED_AT")
    private LocalDateTime completedAt;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "T_LENDING_REQUEST_DETAIL", joinColumns = @JoinColumn(name = "LENDING_REQUEST_ID"))
    private List<LendingRequestDetail> details = new ArrayList<>();

    public LendingRequest() {
    }

    public Long getLendingRequestId() {
        return lendingRequestId;
    }

    public void setLendingRequestId(Long lendingRequestId) {
        this.lendingRequestId = lendingRequestId;
    }

    public String getApplicantUserId() {
        return applicantUserId;
    }

    public void setApplicantUserId(String applicantUserId) {
        this.applicantUserId = applicantUserId;
    }

    public String getReviewedByUserId() {
        return reviewedByUserId;
    }

    public void setReviewedByUserId(String reviewedByUserId) {
        this.reviewedByUserId = reviewedByUserId;
    }

    public String getReturnConfirmedByUserId() {
        return returnConfirmedByUserId;
    }

    public void setReturnConfirmedByUserId(String returnConfirmedByUserId) {
        this.returnConfirmedByUserId = returnConfirmedByUserId;
    }

    public LendingRequestStatus getStatus() {
        return status;
    }

    public void setStatus(LendingRequestStatus status) {
        this.status = status;
    }

    public String getRequestComment() {
        return requestComment;
    }

    public void setRequestComment(String requestComment) {
        this.requestComment = requestComment;
    }

    public String getReviewComment() {
        return reviewComment;
    }

    public void setReviewComment(String reviewComment) {
        this.reviewComment = reviewComment;
    }

    public String getReturnRequestComment() {
        return returnRequestComment;
    }

    public void setReturnRequestComment(String returnRequestComment) {
        this.returnRequestComment = returnRequestComment;
    }

    public String getReturnConfirmComment() {
        return returnConfirmComment;
    }

    public void setReturnConfirmComment(String returnConfirmComment) {
        this.returnConfirmComment = returnConfirmComment;
    }

    public LocalDateTime getRequestedAt() {
        return requestedAt;
    }

    public void setRequestedAt(LocalDateTime requestedAt) {
        this.requestedAt = requestedAt;
    }

    public LocalDateTime getReviewedAt() {
        return reviewedAt;
    }

    public void setReviewedAt(LocalDateTime reviewedAt) {
        this.reviewedAt = reviewedAt;
    }

    public LocalDateTime getReturnRequestedAt() {
        return returnRequestedAt;
    }

    public void setReturnRequestedAt(LocalDateTime returnRequestedAt) {
        this.returnRequestedAt = returnRequestedAt;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }

    public List<LendingRequestDetail> getDetails() {
        return details;
    }

    public void setDetails(List<LendingRequestDetail> details) {
        this.details = details == null ? new ArrayList<>() : new ArrayList<>(details);
    }

    public void replaceDetails(List<LendingRequestDetail> details) {
        this.details.clear();
        if (details != null) {
            this.details.addAll(details);
        }
    }

    public long lendingRequestId() {
        return lendingRequestId;
    }

    public String applicantUserId() {
        return applicantUserId;
    }

    public String reviewedByUserId() {
        return reviewedByUserId;
    }

    public String returnConfirmedByUserId() {
        return returnConfirmedByUserId;
    }

    public String statusCode() {
        return status == null ? null : status.code();
    }

    public String requestComment() {
        return requestComment;
    }

    public String reviewComment() {
        return reviewComment;
    }

    public String returnRequestComment() {
        return returnRequestComment;
    }

    public String returnConfirmComment() {
        return returnConfirmComment;
    }

    public LocalDateTime requestedAt() {
        return requestedAt;
    }

    public LocalDateTime reviewedAt() {
        return reviewedAt;
    }

    public LocalDateTime returnRequestedAt() {
        return returnRequestedAt;
    }

    public LocalDateTime completedAt() {
        return completedAt;
    }

    public List<LendingRequestDetail> details() {
        return List.copyOf(details);
    }

    public int version() {
        return getVersion() == null ? 0 : getVersion();
    }
}
