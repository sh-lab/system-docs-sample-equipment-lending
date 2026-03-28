package net.shlab.hogefugapiyo.equipmentlending.infrastructure.repository.query.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import net.shlab.hogefugapiyo.equipmentlending.infrastructure.repository.query.UserMypageQueryRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

/**
 * {@inheritDoc}
 */
@Repository
public class UserMypageQueryRepositoryImpl implements UserMypageQueryRepository {

    private static final String NO_REVIEW_COMMENT = "";
    private final JdbcTemplate jdbcTemplate;

    public UserMypageQueryRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<RequestRow> findLentRequestsByApplicantUserId(String userId) {
        return jdbcTemplate.query(
                """
                SELECT LENDING_REQUEST_ID, REQUEST_COMMENT, REVIEW_COMMENT, STATUS_CODE
                FROM T_LENDING_REQUEST
                WHERE APPLICANT_USER_ID = ?
                  AND STATUS_CODE = 'LENT'
                ORDER BY REQUESTED_AT DESC, LENDING_REQUEST_ID DESC
                """,
                (rs, rowNum) -> toUserMypageRequestDto(rs),
                userId
        );
    }

    @Override
    public List<RequestRow> findPendingRequestsByApplicantUserId(String userId) {
        return jdbcTemplate.query(
                """
                SELECT LENDING_REQUEST_ID, REQUEST_COMMENT, REVIEW_COMMENT, STATUS_CODE
                FROM T_LENDING_REQUEST
                WHERE APPLICANT_USER_ID = ?
                  AND STATUS_CODE IN ('PENDING_APPROVAL', 'PENDING_RETURN_CONFIRMATION', 'REJECTED')
                ORDER BY REQUESTED_AT DESC, LENDING_REQUEST_ID DESC
                """,
                (rs, rowNum) -> toUserMypageRequestDto(rs),
                userId
        );
    }

    @Override
    public boolean existsRejectedRequestByApplicantUserId(String userId) {
        Integer count = jdbcTemplate.queryForObject(
                """
                SELECT COUNT(*)
                FROM T_LENDING_REQUEST
                WHERE APPLICANT_USER_ID = ?
                  AND STATUS_CODE = 'REJECTED'
                """,
                Integer.class,
                userId
        );
        return count != null && count > 0;
    }

    private RequestRow toUserMypageRequestDto(ResultSet resultSet) throws SQLException {
        return new RequestRow(
                resultSet.getLong("LENDING_REQUEST_ID"),
                resultSet.getString("REQUEST_COMMENT"),
                normalizeReviewComment(resultSet.getString("REVIEW_COMMENT")),
                resultSet.getString("STATUS_CODE")
        );
    }

    private String normalizeReviewComment(String reviewComment) {
        return reviewComment == null ? NO_REVIEW_COMMENT : reviewComment;
    }
}
