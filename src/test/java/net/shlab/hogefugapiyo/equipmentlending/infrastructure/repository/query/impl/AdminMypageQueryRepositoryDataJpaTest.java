package net.shlab.hogefugapiyo.equipmentlending.infrastructure.repository.query.impl;

import static org.assertj.core.api.Assertions.assertThat;

import net.shlab.hogefugapiyo.equipmentlending.infrastructure.repository.query.AdminMypageQueryRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@Import({RepositoryTestConfig.class, AdminMypageQueryRepositoryImpl.class})
class AdminMypageQueryRepositoryDataJpaTest {

    @Autowired
    private AdminMypageQueryRepositoryImpl adminMypageQueryRepository;

    @Test
    void findPendingRequestsReturnsStatusCodesAndAscendingOrder() {
        assertThat(adminMypageQueryRepository.findPendingApprovalRequests("ADMIN1"))
                .extracting(AdminMypageQueryRepository.RequestRow::lendingRequestId, AdminMypageQueryRepository.RequestRow::statusCode)
                .containsExactly(
                        org.assertj.core.groups.Tuple.tuple(2001L, "PENDING_APPROVAL"),
                        org.assertj.core.groups.Tuple.tuple(2006L, "PENDING_APPROVAL"),
                        org.assertj.core.groups.Tuple.tuple(2011L, "PENDING_APPROVAL")
                );

        assertThat(adminMypageQueryRepository.findPendingReturnRequests("ADMIN1"))
                .extracting(AdminMypageQueryRepository.RequestRow::lendingRequestId, AdminMypageQueryRepository.RequestRow::statusCode)
                .containsExactly(
                        org.assertj.core.groups.Tuple.tuple(2003L, "PENDING_RETURN_CONFIRMATION"),
                        org.assertj.core.groups.Tuple.tuple(2008L, "PENDING_RETURN_CONFIRMATION"),
                        org.assertj.core.groups.Tuple.tuple(2013L, "PENDING_RETURN_CONFIRMATION")
                );
    }
}
