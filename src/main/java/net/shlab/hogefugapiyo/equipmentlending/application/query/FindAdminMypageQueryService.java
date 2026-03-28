package net.shlab.hogefugapiyo.equipmentlending.application.query;

import java.util.List;
import net.shlab.hogefugapiyo.framework.core.service.QueryService;

/**
 * 管理者マイページ表示用データを取得する Query Service。
 *
 * <p>設計書:
 * <ul>
 *   <li>{@code docs/03_designs/services/query/HFP-EL-SQS201_find-admin-mypage_service.md}</li>
 * </ul>
 */
public interface FindAdminMypageQueryService
        extends QueryService<FindAdminMypageQueryService.Request, FindAdminMypageQueryService.Response> {

    record Request(String adminUserId) {
    }

    record RequestItem(long lendingRequestId, String applicantUserId, String comment,
                       String dateTime, String statusLabel) {
    }

    record Response(List<RequestItem> pendingApprovalRequests, List<RequestItem> pendingReturnRequests) {
    }
}
