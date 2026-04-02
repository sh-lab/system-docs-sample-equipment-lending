package net.shlab.hogefugapiyo.equipmentlending.application.query;

import java.time.LocalDateTime;
import java.util.List;
import net.shlab.hogefugapiyo.framework.core.service.QueryService;

/**
 * 貸出申請IDから貸出申請情報を取得する Query Service。
 *
 * <p>設計書:
 * <ul>
 *   <li>{@code docs/03_designs/services/query/HFP-EL-SQS002_find-lending-request-by-id_service.md}</li>
 * </ul>
 */
public interface FindLendingRequestByIdQueryService
        extends QueryService<FindLendingRequestByIdQueryService.Request, FindLendingRequestByIdQueryService.Response> {

    record Request(long lendingRequestId) {
    }

    record Response(
            long lendingRequestId,
            String applicantUserId,
            String statusCode,
            LocalDateTime requestedAt,
            LocalDateTime reviewedAt,
            LocalDateTime returnRequestedAt,
            String requestComment,
            String returnRequestComment,
            String reviewComment,
            int version,
            List<Long> equipmentIds
    ) {
    }
}
