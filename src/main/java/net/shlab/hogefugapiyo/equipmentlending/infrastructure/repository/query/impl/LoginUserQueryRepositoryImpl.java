package net.shlab.hogefugapiyo.equipmentlending.infrastructure.repository.query.impl;

import net.shlab.hogefugapiyo.equipmentlending.application.query.FindLoginUserQueryService;
import java.util.List;
import java.util.Optional;
import net.shlab.hogefugapiyo.equipmentlending.infrastructure.repository.query.LoginUserQueryRepository;
import net.shlab.hogefugapiyo.equipmentlending.model.value.UserRole;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

/**
 * {@inheritDoc}
 */
@Repository
public class LoginUserQueryRepositoryImpl implements LoginUserQueryRepository {

    private final JdbcTemplate jdbcTemplate;

    public LoginUserQueryRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<FindLoginUserQueryService.Response> findByUserId(String userId) {
        List<FindLoginUserQueryService.Response> results = jdbcTemplate.query(
                """
                SELECT USER_ID, ROLE_CODE
                FROM M_USER
                WHERE USER_ID = ?
                """,
                (rs, rowNum) -> new FindLoginUserQueryService.Response(
                        rs.getString("USER_ID"),
                        UserRole.fromCode(rs.getString("ROLE_CODE"))
                ),
                userId
        );
        return results.stream().findFirst();
    }
}
