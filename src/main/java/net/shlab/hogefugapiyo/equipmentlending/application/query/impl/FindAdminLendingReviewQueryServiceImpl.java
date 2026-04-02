package net.shlab.hogefugapiyo.equipmentlending.application.query.impl;

import net.shlab.hogefugapiyo.equipmentlending.application.BusinessException;
import net.shlab.hogefugapiyo.equipmentlending.application.BusinessMessageIds;
import net.shlab.hogefugapiyo.equipmentlending.application.query.AdminLendingReviewMode;
import net.shlab.hogefugapiyo.equipmentlending.application.query.FindAdminLendingReviewQueryService;
import net.shlab.hogefugapiyo.equipmentlending.infrastructure.repository.query.AdminLendingReviewQueryRepository;
import net.shlab.hogefugapiyo.framework.i18n.I18nMessageResolver;
import org.springframework.stereotype.Service;

/**
 * {@inheritDoc}
 */
@Service
public class FindAdminLendingReviewQueryServiceImpl implements FindAdminLendingReviewQueryService {

    private final AdminLendingReviewQueryRepository adminLendingReviewQueryRepository;
    private final I18nMessageResolver i18nMessageResolver;

    public FindAdminLendingReviewQueryServiceImpl(
            AdminLendingReviewQueryRepository adminLendingReviewQueryRepository,
            I18nMessageResolver i18nMessageResolver
    ) {
        this.adminLendingReviewQueryRepository = adminLendingReviewQueryRepository;
        this.i18nMessageResolver = i18nMessageResolver;
    }

    @Override
    public FindAdminLendingReviewQueryService.Response execute(FindAdminLendingReviewQueryService.Request request) {
        if (request.lendingRequestId() == null) {
            throw new BusinessException(BusinessMessageIds.REQUEST_DISPLAY_INVALID);
        }
        AdminLendingReviewQueryRepository.DetailRow selectedRequestRow = adminLendingReviewQueryRepository
                .findRequestDetail(request.lendingRequestId())
                .orElseThrow(() -> new BusinessException(BusinessMessageIds.REQUEST_DISPLAY_INVALID));
        FindAdminLendingReviewQueryService.Detail selectedRequest = toDetail(selectedRequestRow);
        AdminLendingReviewMode mode = resolveMode(selectedRequest.statusCode());
        return new FindAdminLendingReviewQueryService.Response(selectedRequest, mode);
    }

    private FindAdminLendingReviewQueryService.Detail toDetail(AdminLendingReviewQueryRepository.DetailRow detailRow) {
        return new FindAdminLendingReviewQueryService.Detail(
                detailRow.lendingRequestId(),
                detailRow.applicantUserId(),
                detailRow.statusCode(),
                toStatusLabel(detailRow.statusCode()),
                detailRow.requestComment(),
                detailRow.reviewComment(),
                detailRow.returnRequestComment(),
                detailRow.returnConfirmComment(),
                detailRow.requestedAt(),
                detailRow.reviewedAt(),
                detailRow.returnRequestedAt(),
                detailRow.version(),
                detailRow.equipmentRows().stream()
                        .map(this::toEquipmentItem)
                        .toList()
        );
    }

    private FindAdminLendingReviewQueryService.EquipmentItem toEquipmentItem(AdminLendingReviewQueryRepository.EquipmentRow equipmentRow) {
        return new FindAdminLendingReviewQueryService.EquipmentItem(
                equipmentRow.equipmentId(),
                equipmentRow.equipmentCode(),
                equipmentRow.equipmentName(),
                equipmentRow.equipmentTypeName(),
                equipmentRow.storageLocation()
        );
    }

    private AdminLendingReviewMode resolveMode(String statusCode) {
        return switch (statusCode) {
            case "PENDING_APPROVAL" -> AdminLendingReviewMode.APPROVAL_REVIEW;
            case "PENDING_RETURN_CONFIRMATION" -> AdminLendingReviewMode.RETURN_CONFIRM;
            default -> throw new BusinessException(BusinessMessageIds.REQUEST_DISPLAY_INVALID);
        };
    }

    private String toStatusLabel(String statusCode) {
        return switch (statusCode) {
            case "PENDING_APPROVAL" -> i18nMessageResolver.get("label.status.pending-approval");
            case "PENDING_RETURN_CONFIRMATION" -> i18nMessageResolver.get("label.status.pending-return-confirmation");
            case "LENT" -> i18nMessageResolver.get("label.status.lent");
            case "REJECTED" -> i18nMessageResolver.get("label.status.rejected");
            case "COMPLETED" -> i18nMessageResolver.get("label.status.completed");
            default -> statusCode;
        };
    }

}
