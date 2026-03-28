package net.shlab.hogefugapiyo.equipmentlending.infrastructure.repository.query.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import net.shlab.hogefugapiyo.equipmentlending.infrastructure.repository.query.EquipmentSearchQueryRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

/**
 * {@inheritDoc}
 */
@Repository
public class EquipmentSearchQueryRepositoryImpl implements EquipmentSearchQueryRepository {

    private static final int FETCH_LIMIT_WITH_SENTINEL = 101;
    private final JdbcTemplate jdbcTemplate;

    public EquipmentSearchQueryRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<EquipmentRow> findEquipmentByCriteria(Criteria criteria) {
        String equipmentNameLike = "%" + escapeLike(criteria.equipmentName()) + "%";
        boolean hasEquipmentType = !criteria.equipmentType().isEmpty();
        boolean filterAvailableOnly = "AVAILABLE".equals(criteria.lendingStatus());
        boolean filterUnavailableOnly = "UNAVAILABLE".equals(criteria.lendingStatus());

        return jdbcTemplate.query(
                """
                SELECT EQUIPMENT_ID, EQUIPMENT_CODE, EQUIPMENT_NAME, EQUIPMENT_TYPE, STORAGE_LOCATION, STATUS_CODE
                FROM M_EQUIPMENT
                WHERE UPPER(EQUIPMENT_NAME) LIKE UPPER(?) ESCAPE '\\'
                  AND (? = FALSE OR EQUIPMENT_TYPE = ?)
                  AND (? = FALSE OR STATUS_CODE = 'AVAILABLE')
                  AND (? = FALSE OR STATUS_CODE <> 'AVAILABLE')
                ORDER BY EQUIPMENT_CODE ASC
                LIMIT ?
                """,
                (rs, rowNum) -> toEquipmentSearchItemDto(rs),
                equipmentNameLike,
                hasEquipmentType,
                criteria.equipmentType(),
                filterAvailableOnly,
                filterUnavailableOnly,
                FETCH_LIMIT_WITH_SENTINEL
        );
    }

    @Override
    public List<EquipmentTypeOptionRow> findEquipmentTypeOptions() {
        return jdbcTemplate.query(
                """
                SELECT DISTINCT EQUIPMENT_TYPE
                FROM M_EQUIPMENT
                ORDER BY EQUIPMENT_TYPE ASC
                """,
                (rs, rowNum) -> toEquipmentTypeOptionDto(rs)
        );
    }

    private EquipmentRow toEquipmentSearchItemDto(ResultSet resultSet) throws SQLException {
        String statusCode = resultSet.getString("STATUS_CODE");
        return new EquipmentRow(
                resultSet.getLong("EQUIPMENT_ID"),
                resultSet.getString("EQUIPMENT_CODE"),
                resultSet.getString("EQUIPMENT_NAME"),
                resultSet.getString("EQUIPMENT_TYPE"),
                resultSet.getString("STORAGE_LOCATION"),
                statusCode,
                "AVAILABLE".equals(statusCode)
        );
    }

    private EquipmentTypeOptionRow toEquipmentTypeOptionDto(ResultSet resultSet) throws SQLException {
        return new EquipmentTypeOptionRow(resultSet.getString("EQUIPMENT_TYPE"));
    }

    private String escapeLike(String value) {
        return value
                .replace("\\", "\\\\")
                .replace("%", "\\%")
                .replace("_", "\\_");
    }
}
