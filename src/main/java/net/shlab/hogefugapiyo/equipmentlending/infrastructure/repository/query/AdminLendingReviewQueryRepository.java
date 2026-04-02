package net.shlab.hogefugapiyo.equipmentlending.infrastructure.repository.query;

import java.util.List;
import java.util.Optional;
import net.shlab.hogefugapiyo.framework.core.repository.QueryRepository;

public interface AdminLendingReviewQueryRepository extends QueryRepository {
    record EquipmentRow(long equipmentId, String equipmentCode, String equipmentName,
                        String equipmentTypeCode, String equipmentTypeName, String storageLocation) {
    }

    record DetailRow(long lendingRequestId, String applicantUserId, String statusCode,
                     String requestComment, String reviewComment, String returnRequestComment,
                     String returnConfirmComment, String requestedAt, String reviewedAt,
                     String returnRequestedAt, int version, List<EquipmentRow> equipmentRows) {
    }

    Optional<DetailRow> findRequestDetail(long lendingRequestId);
}
