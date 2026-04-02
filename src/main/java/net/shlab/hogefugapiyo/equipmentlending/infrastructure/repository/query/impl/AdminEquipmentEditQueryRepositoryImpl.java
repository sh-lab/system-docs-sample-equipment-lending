package net.shlab.hogefugapiyo.equipmentlending.infrastructure.repository.query.impl;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import net.shlab.hogefugapiyo.equipmentlending.infrastructure.repository.query.AdminEquipmentEditQueryRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class AdminEquipmentEditQueryRepositoryImpl implements AdminEquipmentEditQueryRepository {

    private final JdbcTemplate jdbcTemplate;

    public AdminEquipmentEditQueryRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<EquipmentDetailRow> findEquipmentDetail(long equipmentId) {
        List<EquipmentDetailRow> rows = jdbcTemplate.query(
                """
                SELECT e.EQUIPMENT_ID, e.EQUIPMENT_CODE, e.EQUIPMENT_NAME, e.EQUIPMENT_TYPE,
                       t.EQUIPMENT_TYPE_NAME, e.STORAGE_LOCATION, e.SYSTEM_REGISTERED_DATE,
                       e.REMARKS, e.STATUS_CODE, e.VERSION
                  FROM M_EQUIPMENT e
                  JOIN M_EQUIPMENT_TYPE t ON t.EQUIPMENT_TYPE_CODE = e.EQUIPMENT_TYPE
                 WHERE e.EQUIPMENT_ID = ?
                """,
                (rs, rowNum) -> new EquipmentDetailRow(
                        rs.getLong("EQUIPMENT_ID"),
                        rs.getString("EQUIPMENT_CODE"),
                        rs.getString("EQUIPMENT_NAME"),
                        rs.getString("EQUIPMENT_TYPE"),
                        rs.getString("EQUIPMENT_TYPE_NAME"),
                        rs.getString("STORAGE_LOCATION"),
                        rs.getObject("SYSTEM_REGISTERED_DATE", LocalDate.class),
                        rs.getString("REMARKS") == null ? "" : rs.getString("REMARKS"),
                        rs.getString("STATUS_CODE"),
                        rs.getInt("VERSION")
                ),
                equipmentId
        );
        return rows.stream().findFirst();
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
}
