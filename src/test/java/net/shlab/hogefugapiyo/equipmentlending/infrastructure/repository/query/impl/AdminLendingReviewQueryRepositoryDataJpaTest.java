package net.shlab.hogefugapiyo.equipmentlending.infrastructure.repository.query.impl;

import static org.assertj.core.api.Assertions.assertThat;

import net.shlab.hogefugapiyo.equipmentlending.infrastructure.repository.query.AdminLendingReviewQueryRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@Import({RepositoryTestConfig.class, AdminLendingReviewQueryRepositoryImpl.class})
class AdminLendingReviewQueryRepositoryDataJpaTest {

    @Autowired
    private AdminLendingReviewQueryRepositoryImpl adminLendingReviewQueryRepository;

    @Test
    void findRequestDetailReturnsEquipmentAndCodes() {
        AdminLendingReviewQueryRepository.DetailRow detail = adminLendingReviewQueryRepository.findRequestDetail(2003L).orElseThrow();

        assertThat(detail.statusCode()).isEqualTo("PENDING_RETURN_CONFIRMATION");
        assertThat(detail.returnRequestComment()).isEqualTo("返却したので確認を依頼する。");
        assertThat(detail.equipmentRows())
                .singleElement()
                .satisfies(item -> {
                    assertThat(item.equipmentCode()).isEqualTo("EQ-0003");
                    assertThat(item.equipmentTypeCode()).isEqualTo("DESK");
                });
    }

    @Test
    void findRequestDetailReturnsEmptyWhenRequestIsNotDisplayTarget() {
        assertThat(adminLendingReviewQueryRepository.findRequestDetail(2002L)).isEmpty();
    }
}
