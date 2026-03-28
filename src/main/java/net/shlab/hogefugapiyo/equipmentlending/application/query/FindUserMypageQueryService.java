package net.shlab.hogefugapiyo.equipmentlending.application.query;

import java.util.List;
import net.shlab.hogefugapiyo.framework.core.service.QueryService;

/**
 * ユーザーマイページ表示用データを取得する Query Service。
 *
 * <p>設計書:
 * <ul>
 *   <li>{@code docs/03_designs/services/query/HFP-EL-SQS101_find-user-mypage_service.md}</li>
 * </ul>
 */
public interface FindUserMypageQueryService
        extends QueryService<FindUserMypageQueryService.Request, FindUserMypageQueryService.Response> {

    record Request(String userId) {
    }

    record RequestItem(long lendingRequestId, String requestComment, String reviewComment,
                       String statusLabel) {
    }

    record Response(List<RequestItem> lentRequests, List<RequestItem> pendingRequests,
                    boolean hasRejectedRequest) {
    }
}
