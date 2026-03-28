package net.shlab.hogefugapiyo.equipmentlending.application.query.impl;

import java.util.List;
import net.shlab.hogefugapiyo.equipmentlending.application.query.FindUserMypageQueryService;
import net.shlab.hogefugapiyo.equipmentlending.infrastructure.repository.query.UserMypageQueryRepository;
import net.shlab.hogefugapiyo.framework.i18n.I18nMessageResolver;
import org.springframework.stereotype.Service;

/**
 * {@inheritDoc}
 */
@Service
public class FindUserMypageQueryServiceImpl implements FindUserMypageQueryService {

    private final UserMypageQueryRepository userMypageQueryRepository;
    private final I18nMessageResolver i18nMessageResolver;

    public FindUserMypageQueryServiceImpl(
            UserMypageQueryRepository userMypageQueryRepository,
            I18nMessageResolver i18nMessageResolver
    ) {
        this.userMypageQueryRepository = userMypageQueryRepository;
        this.i18nMessageResolver = i18nMessageResolver;
    }

    @Override
    public FindUserMypageQueryService.Response execute(FindUserMypageQueryService.Request request) {
        List<FindUserMypageQueryService.RequestItem> lentRequests = userMypageQueryRepository
                .findLentRequestsByApplicantUserId(request.userId())
                .stream()
                .map(this::toRequestItem)
                .toList();
        List<FindUserMypageQueryService.RequestItem> pendingRequests = userMypageQueryRepository
                .findPendingRequestsByApplicantUserId(request.userId())
                .stream()
                .map(this::toRequestItem)
                .toList();
        boolean hasRejectedRequest =
                userMypageQueryRepository.existsRejectedRequestByApplicantUserId(request.userId());

        return new FindUserMypageQueryService.Response(lentRequests, pendingRequests, hasRejectedRequest);
    }

    private FindUserMypageQueryService.RequestItem toRequestItem(UserMypageQueryRepository.RequestRow requestRow) {
        return new FindUserMypageQueryService.RequestItem(
                requestRow.lendingRequestId(),
                requestRow.requestComment(),
                requestRow.reviewComment(),
                toStatusLabel(requestRow.statusCode())
        );
    }

    private String toStatusLabel(String statusCode) {
        return switch (statusCode) {
            case "LENT" -> i18nMessageResolver.get("label.status.lent");
            case "PENDING_APPROVAL" -> i18nMessageResolver.get("label.status.pending-approval");
            case "PENDING_RETURN_CONFIRMATION" -> i18nMessageResolver.get("label.status.pending-return-confirmation");
            case "REJECTED" -> i18nMessageResolver.get("label.status.rejected");
            default -> statusCode;
        };
    }
}
