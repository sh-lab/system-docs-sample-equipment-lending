package net.shlab.hogefugapiyo.equipmentlending.model.history;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 貸出申請操作履歴を表す record。
 *
 * <p>設計書:
 * <ul>
 *   <li>{@code docs/03_designs/data/H_LENDING_REQUEST_HISTORY.md}</li>
 * </ul>
 */
public record LendingRequestHistory(
        UUID operationId,
        long lendingRequestId,
        String commandServiceId,
        LocalDateTime operatedAt
) {
}
