package net.shlab.hogefugapiyo.equipmentlending.model.history;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 備品操作履歴を表す record。
 *
 * <p>設計書:
 * <ul>
 *   <li>{@code docs/03_designs/data/H_EQUIPMENT_HISTORY.md}</li>
 * </ul>
 */
public record EquipmentHistory(
        UUID operationId,
        long equipmentId,
        String commandServiceId,
        LocalDateTime operatedAt
) {
}
