package net.shlab.hogefugapiyo.equipmentlending.infrastructure.repository.query.impl;

import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.List;
import net.shlab.hogefugapiyo.equipmentlending.infrastructure.repository.query.AdminMypageQueryRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

/**
 * {@inheritDoc}
 */
@Repository
public class AdminMypageQueryRepositoryImpl implements AdminMypageQueryRepository {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final String NO_COMMENT = "";

    private final JdbcTemplate jdbcTemplate;

    public AdminMypageQueryRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<RequestRow> findPendingApprovalRequests(String adminUserId) {
        return jdbcTemplate.query(
                """
                SELECT LENDING_REQUEST_ID, APPLICANT_USER_ID, REQUEST_COMMENT, REQUESTED_AT, STATUS_CODE
                FROM T_LENDING_REQUEST
                WHERE STATUS_CODE = 'PENDING_APPROVAL'
                ORDER BY REQUESTED_AT ASC, LENDING_REQUEST_ID ASC
                """,
                (rs, rowNum) -> new RequestRow(
                        rs.getLong("LENDING_REQUEST_ID"),
                        rs.getString("APPLICANT_USER_ID"),
                        defaultString(rs.getString("REQUEST_COMMENT")),
                        formatDateTime(rs.getTimestamp("REQUESTED_AT")),
                        rs.getString("STATUS_CODE")
                )
        );
    }

    @Override
    public List<RequestRow> findPendingReturnRequests(String adminUserId) {
        return jdbcTemplate.query(
                """
                SELECT LENDING_REQUEST_ID, APPLICANT_USER_ID, RETURN_REQUEST_COMMENT, RETURN_REQUESTED_AT, STATUS_CODE
                FROM T_LENDING_REQUEST
                WHERE STATUS_CODE = 'PENDING_RETURN_CONFIRMATION'
                ORDER BY RETURN_REQUESTED_AT ASC, LENDING_REQUEST_ID ASC
                """,
                (rs, rowNum) -> new RequestRow(
                        rs.getLong("LENDING_REQUEST_ID"),
                        rs.getString("APPLICANT_USER_ID"),
                        defaultString(rs.getString("RETURN_REQUEST_COMMENT")),
                        formatDateTime(rs.getTimestamp("RETURN_REQUESTED_AT")),
                        rs.getString("STATUS_CODE")
                )
        );
    }

    private String formatDateTime(Timestamp timestamp) {
        return timestamp == null ? null : timestamp.toLocalDateTime().format(DATE_TIME_FORMATTER);
    }

    private String defaultString(String value) {
        return value == null ? NO_COMMENT : value;
    }
}
