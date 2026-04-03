package net.shlab.hogefugapiyo.equipmentlending.infrastructure.repository.query.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import net.shlab.hogefugapiyo.equipmentlending.infrastructure.repository.query.AdminEquipmentSearchQueryRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class AdminEquipmentSearchQueryRepositoryImpl implements AdminEquipmentSearchQueryRepository {

    private static final int FETCH_LIMIT_WITH_SENTINEL = 101;
    private final JdbcTemplate jdbcTemplate;

    public AdminEquipmentSearchQueryRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<EquipmentRow> findByCriteria(Criteria criteria) {
        String equipmentNameLike = "%" + escapeLike(criteria.equipmentName()) + "%";
        boolean hasType = criteria.equipmentType() != null && !criteria.equipmentType().isBlank();
        boolean hasStatus = criteria.statusCode() != null && !criteria.statusCode().isBlank() && !"ALL".equals(criteria.statusCode());
        boolean hasDateFrom = criteria.systemRegisteredDateFrom() != null;
        boolean hasDateTo = criteria.systemRegisteredDateTo() != null;
        return jdbcTemplate.query(
                """
                SELECT e.EQUIPMENT_ID, e.EQUIPMENT_CODE, e.EQUIPMENT_NAME, e.EQUIPMENT_TYPE,
                       t.EQUIPMENT_TYPE_NAME, e.SYSTEM_REGISTERED_DATE, e.STORAGE_LOCATION,
                       e.STATUS_CODE, e.VERSION
                  FROM M_EQUIPMENT e
                  JOIN M_EQUIPMENT_TYPE t ON t.EQUIPMENT_TYPE_CODE = e.EQUIPMENT_TYPE
                  WHERE UPPER(e.EQUIPMENT_NAME) LIKE UPPER(?) ESCAPE '\\'
                    AND (? = FALSE OR e.EQUIPMENT_TYPE = ?)
                    AND (? = FALSE OR e.STATUS_CODE = ?)
                    AND (? = FALSE OR e.SYSTEM_REGISTERED_DATE >= ?)
                    AND (? = FALSE OR e.SYSTEM_REGISTERED_DATE <= ?)
                  ORDER BY e.EQUIPMENT_CODE ASC
                  LIMIT ?
                """,
                (rs, rowNum) -> toRow(rs),
                equipmentNameLike,
                hasType,
                criteria.equipmentType(),
                hasStatus,
                criteria.statusCode(),
                hasDateFrom,
                criteria.systemRegisteredDateFrom(),
                hasDateTo,
                criteria.systemRegisteredDateTo(),
                FETCH_LIMIT_WITH_SENTINEL
        );
    }

    @Override
    public List<EquipmentTypeOptionRow> findEquipmentTypeOptions() {
        return jdbcTemplate.query(
                """
                SELECT EQUIPMENT_TYPE_CODE, EQUIPMENT_TYPE_NAME
                  FROM M_EQUIPMENT_TYPE
                 WHERE ACTIVE_FLAG = TRUE
                 ORDER BY DISPLAY_ORDER ASC, EQUIPMENT_TYPE_CODE ASC
                """,
                (rs, rowNum) -> new EquipmentTypeOptionRow(rs.getString("EQUIPMENT_TYPE_CODE"), rs.getString("EQUIPMENT_TYPE_NAME"))
        );
    }

    private EquipmentRow toRow(ResultSet rs) throws SQLException {
        return new EquipmentRow(
                rs.getLong("EQUIPMENT_ID"),
                rs.getString("EQUIPMENT_CODE"),
                rs.getString("EQUIPMENT_NAME"),
                rs.getString("EQUIPMENT_TYPE"),
                rs.getString("EQUIPMENT_TYPE_NAME"),
                rs.getObject("SYSTEM_REGISTERED_DATE", LocalDate.class),
                rs.getString("STORAGE_LOCATION"),
                rs.getString("STATUS_CODE"),
                rs.getInt("VERSION")
        );
    }

    private String escapeLike(String value) {
        return value
                .replace("\\", "\\\\")
                .replace("%", "\\%")
                .replace("_", "\\_");
    }
}
