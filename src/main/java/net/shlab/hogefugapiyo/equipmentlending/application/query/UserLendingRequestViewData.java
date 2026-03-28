package net.shlab.hogefugapiyo.equipmentlending.application.query;

import java.util.List;

public record UserLendingRequestViewData(
        LendingRequestScreenMode mode,
        boolean actionEnabled,
        boolean backToSearch,
        boolean backToMypage,
        Long lendingRequestId,
        String statusLabel,
        String requestedAt,
        String reviewedAt,
        String returnRequestedAt,
        String requestComment,
        String returnRequestComment,
        String adminComment,
        Integer version,
        List<Long> equipmentIds,
        List<UserLendingRequestEquipmentDto> equipmentItems
) {
}
