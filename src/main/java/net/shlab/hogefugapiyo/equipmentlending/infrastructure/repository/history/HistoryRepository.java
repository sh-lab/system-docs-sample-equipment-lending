package net.shlab.hogefugapiyo.equipmentlending.infrastructure.repository.history;

import java.util.List;
import net.shlab.hogefugapiyo.equipmentlending.model.history.EquipmentHistory;
import net.shlab.hogefugapiyo.equipmentlending.model.history.LendingRequestDetailHistory;
import net.shlab.hogefugapiyo.equipmentlending.model.history.LendingRequestHistory;

/**
 * 履歴テーブルへの追記を担当する History Repository。
 *
 * <p>設計書:
 * <ul>
 *   <li>{@code docs/03_designs/data/H_LENDING_REQUEST_HISTORY.md}</li>
 *   <li>{@code docs/03_designs/data/H_LENDING_REQUEST_DETAIL_HISTORY.md}</li>
 *   <li>{@code docs/03_designs/data/H_EQUIPMENT_HISTORY.md}</li>
 * </ul>
 */
public interface HistoryRepository {

    void insertLendingRequestHistory(LendingRequestHistory history);

    void insertLendingRequestDetailHistories(List<LendingRequestDetailHistory> histories);

    void insertEquipmentHistories(List<EquipmentHistory> histories);
}
