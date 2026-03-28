package net.shlab.hogefugapiyo.equipmentlending.model.history;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 貸出申請明細操作履歴を表す record。
 *
 * <p>設計書:
 * <ul>
 *   <li>{@code docs/03_designs/data/H_LENDING_REQUEST_DETAIL_HISTORY.md}</li>
 * </ul>
 */
public record LendingRequestDetailHistory(
        UUID operationId,
        long lendingRequestId,
        long equipmentId,
        String commandServiceId,
        LocalDateTime operatedAt
) {
}
