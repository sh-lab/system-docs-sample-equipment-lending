package net.shlab.hogefugapiyo.equipmentlending.infrastructure.repository.entity.impl;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;

@DataJpaTest
@Import(JpaLendingRequestRepositoryAdapter.class)
class JpaLendingRequestRepositoryAdapterDataJpaTest {

    @Autowired
    private JpaLendingRequestRepositoryAdapter lendingRequestRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void nextIdAllocatesSequentialIdsWithoutPersistingEntity() {
        Long maxId = jdbcTemplate.queryForObject(
                "SELECT COALESCE(MAX(LENDING_REQUEST_ID), 0) FROM T_LENDING_REQUEST",
                Long.class
        );

        long first = lendingRequestRepository.nextId();
        long second = lendingRequestRepository.nextId();

        assertThat(first).isGreaterThan(maxId);
        assertThat(second).isEqualTo(first + 1);
    }
}
