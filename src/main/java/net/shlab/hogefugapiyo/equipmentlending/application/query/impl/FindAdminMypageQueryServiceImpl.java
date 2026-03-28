package net.shlab.hogefugapiyo.equipmentlending.application.query.impl;

import java.util.List;
import net.shlab.hogefugapiyo.equipmentlending.application.query.FindAdminMypageQueryService;
import net.shlab.hogefugapiyo.equipmentlending.infrastructure.repository.query.AdminMypageQueryRepository;
import net.shlab.hogefugapiyo.framework.i18n.I18nMessageResolver;
import org.springframework.stereotype.Service;

/**
 * {@inheritDoc}
 */
@Service
public class FindAdminMypageQueryServiceImpl implements FindAdminMypageQueryService {

    private final AdminMypageQueryRepository adminMypageQueryRepository;
    private final I18nMessageResolver i18nMessageResolver;

    public FindAdminMypageQueryServiceImpl(
            AdminMypageQueryRepository adminMypageQueryRepository,
            I18nMessageResolver i18nMessageResolver
    ) {
        this.adminMypageQueryRepository = adminMypageQueryRepository;
        this.i18nMessageResolver = i18nMessageResolver;
    }

    @Override
    public FindAdminMypageQueryService.Response execute(FindAdminMypageQueryService.Request request) {
        List<FindAdminMypageQueryService.RequestItem> pendingApprovalRequests = adminMypageQueryRepository
                .findPendingApprovalRequests(request.adminUserId())
                .stream()
                .map(this::toRequestItem)
                .toList();
        List<FindAdminMypageQueryService.RequestItem> pendingReturnRequests = adminMypageQueryRepository
                .findPendingReturnRequests(request.adminUserId())
                .stream()
                .map(this::toRequestItem)
                .toList();
        return new FindAdminMypageQueryService.Response(
                pendingApprovalRequests,
                pendingReturnRequests
        );
    }

    private FindAdminMypageQueryService.RequestItem toRequestItem(AdminMypageQueryRepository.RequestRow requestRow) {
        return new FindAdminMypageQueryService.RequestItem(
                requestRow.lendingRequestId(),
                requestRow.applicantUserId(),
                requestRow.comment(),
                requestRow.dateTime(),
                toStatusLabel(requestRow.statusCode())
        );
    }

    private String toStatusLabel(String statusCode) {
        return switch (statusCode) {
            case "PENDING_APPROVAL" -> i18nMessageResolver.get("label.status.pending-approval");
            case "PENDING_RETURN_CONFIRMATION" -> i18nMessageResolver.get("label.status.pending-return-confirmation");
            default -> statusCode;
        };
    }
}
