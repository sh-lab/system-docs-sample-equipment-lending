package net.shlab.hogefugapiyo.equipmentlending.infrastructure.repository.query.impl;

import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import net.shlab.hogefugapiyo.equipmentlending.infrastructure.repository.query.AdminLendingReviewQueryRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

/**
 * {@inheritDoc}
 */
@Repository
public class AdminLendingReviewQueryRepositoryImpl implements AdminLendingReviewQueryRepository {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final String NO_COMMENT = "";

    private final JdbcTemplate jdbcTemplate;

    public AdminLendingReviewQueryRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<DetailRow> findRequestDetail(long lendingRequestId) {
        List<DetailRow> requests = jdbcTemplate.query(
                """
                SELECT LENDING_REQUEST_ID, APPLICANT_USER_ID, STATUS_CODE, REQUEST_COMMENT, REVIEW_COMMENT,
                       RETURN_REQUEST_COMMENT, RETURN_CONFIRM_COMMENT, REQUESTED_AT, REVIEWED_AT,
                       RETURN_REQUESTED_AT, VERSION
                FROM T_LENDING_REQUEST
                WHERE LENDING_REQUEST_ID = ?
                  AND STATUS_CODE IN ('PENDING_APPROVAL', 'PENDING_RETURN_CONFIRMATION')
                """,
                (rs, rowNum) -> new DetailRow(
                        rs.getLong("LENDING_REQUEST_ID"),
                        rs.getString("APPLICANT_USER_ID"),
                        rs.getString("STATUS_CODE"),
                        defaultString(rs.getString("REQUEST_COMMENT")),
                        defaultString(rs.getString("REVIEW_COMMENT")),
                        defaultString(rs.getString("RETURN_REQUEST_COMMENT")),
                        defaultString(rs.getString("RETURN_CONFIRM_COMMENT")),
                        formatDateTime(rs.getTimestamp("REQUESTED_AT")),
                        formatDateTime(rs.getTimestamp("REVIEWED_AT")),
                        formatDateTime(rs.getTimestamp("RETURN_REQUESTED_AT")),
                        rs.getInt("VERSION"),
                        findEquipmentItems(lendingRequestId)
                ),
                lendingRequestId
        );
        return requests.stream().findFirst();
    }

    private List<EquipmentRow> findEquipmentItems(long lendingRequestId) {
        return jdbcTemplate.query(
                """
                SELECT e.EQUIPMENT_ID, e.EQUIPMENT_CODE, e.EQUIPMENT_NAME, e.EQUIPMENT_TYPE,
                       t.EQUIPMENT_TYPE_NAME, e.STORAGE_LOCATION
                FROM T_LENDING_REQUEST_DETAIL d
                JOIN M_EQUIPMENT e ON e.EQUIPMENT_ID = d.EQUIPMENT_ID
                JOIN M_EQUIPMENT_TYPE t ON t.EQUIPMENT_TYPE_CODE = e.EQUIPMENT_TYPE
                WHERE d.LENDING_REQUEST_ID = ?
                ORDER BY e.EQUIPMENT_CODE ASC
                """,
                (rs, rowNum) -> new EquipmentRow(
                        rs.getLong("EQUIPMENT_ID"),
                        rs.getString("EQUIPMENT_CODE"),
                        rs.getString("EQUIPMENT_NAME"),
                        rs.getString("EQUIPMENT_TYPE"),
                        rs.getString("EQUIPMENT_TYPE_NAME"),
                        rs.getString("STORAGE_LOCATION")
                ),
                lendingRequestId
        );
    }

    private String formatDateTime(Timestamp timestamp) {
        return timestamp == null ? null : timestamp.toLocalDateTime().format(DATE_TIME_FORMATTER);
    }

    private String defaultString(String value) {
        return value == null ? NO_COMMENT : value;
    }
}
