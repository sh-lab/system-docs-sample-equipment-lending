package net.shlab.hogefugapiyo.equipmentlending.infrastructure.repository.history.impl;

import java.sql.Timestamp;
import java.util.List;
import net.shlab.hogefugapiyo.equipmentlending.infrastructure.repository.history.HistoryRepository;
import net.shlab.hogefugapiyo.equipmentlending.model.history.EquipmentHistory;
import net.shlab.hogefugapiyo.equipmentlending.model.history.LendingRequestDetailHistory;
import net.shlab.hogefugapiyo.equipmentlending.model.history.LendingRequestHistory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

/**
 * {@inheritDoc}
 */
@Repository
public class HistoryRepositoryImpl implements HistoryRepository {

    private final JdbcTemplate jdbcTemplate;

    public HistoryRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void insertLendingRequestHistory(LendingRequestHistory history) {
        jdbcTemplate.update(
                """
                INSERT INTO H_LENDING_REQUEST_HISTORY (
                    OPERATION_ID, LENDING_REQUEST_ID, COMMAND_SERVICE_ID, OPERATED_AT
                ) VALUES (?, ?, ?, ?)
                """,
                history.operationId().toString(),
                history.lendingRequestId(),
                history.commandServiceId(),
                Timestamp.valueOf(history.operatedAt())
        );
    }

    @Override
    public void insertLendingRequestDetailHistories(List<LendingRequestDetailHistory> histories) {
        if (histories == null || histories.isEmpty()) {
            return;
        }
        jdbcTemplate.batchUpdate(
                """
                INSERT INTO H_LENDING_REQUEST_DETAIL_HISTORY (
                    OPERATION_ID, LENDING_REQUEST_ID, EQUIPMENT_ID, COMMAND_SERVICE_ID, OPERATED_AT
                ) VALUES (?, ?, ?, ?, ?)
                """,
                histories,
                histories.size(),
                (ps, history) -> {
                    ps.setString(1, history.operationId().toString());
                    ps.setLong(2, history.lendingRequestId());
                    ps.setLong(3, history.equipmentId());
                    ps.setString(4, history.commandServiceId());
                    ps.setTimestamp(5, Timestamp.valueOf(history.operatedAt()));
                }
        );
    }

    @Override
    public void insertEquipmentHistories(List<EquipmentHistory> histories) {
        if (histories == null || histories.isEmpty()) {
            return;
        }
        jdbcTemplate.batchUpdate(
                """
                INSERT INTO H_EQUIPMENT_HISTORY (
                    OPERATION_ID, EQUIPMENT_ID, COMMAND_SERVICE_ID, OPERATED_AT
                ) VALUES (?, ?, ?, ?)
                """,
                histories,
                histories.size(),
                (ps, history) -> {
                    ps.setString(1, history.operationId().toString());
                    ps.setLong(2, history.equipmentId());
                    ps.setString(3, history.commandServiceId());
                    ps.setTimestamp(4, Timestamp.valueOf(history.operatedAt()));
                }
        );
    }
}
