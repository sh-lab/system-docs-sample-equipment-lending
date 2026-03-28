package net.shlab.hogefugapiyo.equipmentlending.application.query;

import java.util.List;
import net.shlab.hogefugapiyo.framework.core.service.QueryService;

/**
 * 管理者承認・却下・返却確認画面表示用データを取得する Query Service。
 *
 * <p>設計書:
 * <ul>
 *   <li>{@code docs/03_designs/services/query/HFP-EL-SQS501_admin-lending-review_service.md}</li>
 * </ul>
 */
public interface FindAdminLendingReviewQueryService
        extends QueryService<FindAdminLendingReviewQueryService.Request, FindAdminLendingReviewQueryService.Response> {

    record Request(String adminUserId, Long lendingRequestId) {
    }

    record EquipmentItem(long equipmentId, String equipmentCode, String equipmentName,
                         String equipmentTypeLabel, String storageLocation) {
    }

    record Detail(long lendingRequestId, String applicantUserId, String statusCode,
                  String statusLabel, String requestComment, String reviewComment,
                  String returnRequestComment, String returnConfirmComment, String requestedAt,
                  String reviewedAt, String returnRequestedAt, int version,
                  List<EquipmentItem> equipmentItems) {
    }

    record Response(Detail selectedRequest, AdminLendingReviewMode mode) {
    }
}
