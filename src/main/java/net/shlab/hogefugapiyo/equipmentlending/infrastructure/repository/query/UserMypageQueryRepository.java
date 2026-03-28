package net.shlab.hogefugapiyo.equipmentlending.infrastructure.repository.query;

import java.util.List;
import net.shlab.hogefugapiyo.framework.core.repository.QueryRepository;

public interface UserMypageQueryRepository extends QueryRepository {
    record RequestRow(long lendingRequestId, String requestComment, String reviewComment, String statusCode) {
    }

    List<RequestRow> findLentRequestsByApplicantUserId(String userId);
    List<RequestRow> findPendingRequestsByApplicantUserId(String userId);
    boolean existsRejectedRequestByApplicantUserId(String userId);
}
