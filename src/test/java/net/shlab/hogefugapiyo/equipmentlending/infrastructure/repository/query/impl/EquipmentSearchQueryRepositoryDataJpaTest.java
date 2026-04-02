package net.shlab.hogefugapiyo.equipmentlending.infrastructure.repository.query.impl;

import net.shlab.hogefugapiyo.equipmentlending.infrastructure.repository.query.EquipmentSearchQueryRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({RepositoryTestConfig.class, EquipmentSearchQueryRepositoryImpl.class})
class EquipmentSearchQueryRepositoryDataJpaTest {

    @Autowired
    private EquipmentSearchQueryRepositoryImpl equipmentSearchQueryRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void findEquipmentByCriteriaFiltersByKeywordAndStatus() {
        var items = equipmentSearchQueryRepository.findEquipmentByCriteria(
                new EquipmentSearchQueryRepository.Criteria("プロジェクター", "", "ALL")
        );

        assertThat(items)
                .extracting(item -> item.equipmentCode())
                .containsExactly("EQ-0021", "EQ-0022");
        assertThat(items)
                .extracting(EquipmentSearchQueryRepository.EquipmentRow::equipmentTypeCode)
                .containsOnly("PROJECTOR");
        assertThat(items)
                .allSatisfy(item -> assertThat(item.statusCode()).isEqualTo("AVAILABLE"));
    }

    @Test
    void findEquipmentTypeOptionsReturnsCodes() {
        var options = equipmentSearchQueryRepository.findEquipmentTypeOptions();

        assertThat(options)
                .extracting(EquipmentSearchQueryRepository.EquipmentTypeOptionRow::equipmentTypeCode)
                .contains("DESK", "PROJECTOR");
    }

    @Test
    void findEquipmentByCriteriaAppliesSentinelLimit() {
        for (int equipmentId = 2000; equipmentId <= 2105; equipmentId++) {
            jdbcTemplate.update(
                    """
                    INSERT INTO M_EQUIPMENT (
                        EQUIPMENT_ID, EQUIPMENT_CODE, EQUIPMENT_NAME, EQUIPMENT_TYPE, STORAGE_LOCATION,
                        SYSTEM_REGISTERED_DATE, STATUS_CODE, REMARKS, CREATED_AT, CREATED_BY, UPDATED_AT, UPDATED_BY, VERSION
                    ) VALUES (?, ?, ?, ?, ?, DATE '2026-01-01', 'AVAILABLE', NULL, TIMESTAMP '2026-01-01 09:00:00', 'SYSTEM',
                              TIMESTAMP '2026-01-01 09:00:00', 'SYSTEM', 0)
                    """,
                    equipmentId,
                    "EQ-X" + equipmentId,
                    "追加備品" + equipmentId,
                    "PROJECTOR",
                    "追加倉庫"
            );
        }

        var items = equipmentSearchQueryRepository.findEquipmentByCriteria(
                new EquipmentSearchQueryRepository.Criteria("", "", "AVAILABLE")
        );

        assertThat(items).hasSize(101);
        assertThat(items.getFirst().equipmentCode()).isEqualTo("EQ-0009");
    }

    @Test
    void findEquipmentByCriteriaCanFilterUnavailableStatuses() {
        jdbcTemplate.update(
                """
                INSERT INTO M_EQUIPMENT (
                    EQUIPMENT_ID, EQUIPMENT_CODE, EQUIPMENT_NAME, EQUIPMENT_TYPE, STORAGE_LOCATION,
                    SYSTEM_REGISTERED_DATE, STATUS_CODE, REMARKS, CREATED_AT, CREATED_BY, UPDATED_AT, UPDATED_BY, VERSION
                ) VALUES (?, ?, ?, ?, ?, DATE '2026-01-01', ?, NULL, TIMESTAMP '2026-01-01 09:00:00', 'SYSTEM',
                          TIMESTAMP '2026-01-01 09:00:00', 'SYSTEM', 0)
                """,
                3001,
                "EQ-3001",
                "状態確認用備品",
                "PROJECTOR",
                "検証倉庫",
                "UNAVAILABLE"
        );

        var items = equipmentSearchQueryRepository.findEquipmentByCriteria(
                new EquipmentSearchQueryRepository.Criteria("", "", "NOT_AVAILABLE")
        );

        assertThat(items)
                .extracting(EquipmentSearchQueryRepository.EquipmentRow::statusCode)
                .doesNotContain("AVAILABLE")
                .contains("PENDING_LENDING", "UNAVAILABLE");
        assertThat(items)
                .filteredOn(item -> item.equipmentCode().equals("EQ-3001"))
                .singleElement()
                .satisfies(item -> assertThat(item.selectable()).isFalse());
    }
}
