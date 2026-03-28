package net.shlab.hogefugapiyo.equipmentlending.infrastructure.repository.query;

import java.util.List;
import net.shlab.hogefugapiyo.framework.core.repository.QueryRepository;

public interface AdminMypageQueryRepository extends QueryRepository {
    record RequestRow(long lendingRequestId, String applicantUserId, String comment,
                      String dateTime, String statusCode) {
    }

    List<RequestRow> findPendingApprovalRequests(String adminUserId);
    List<RequestRow> findPendingReturnRequests(String adminUserId);
}
