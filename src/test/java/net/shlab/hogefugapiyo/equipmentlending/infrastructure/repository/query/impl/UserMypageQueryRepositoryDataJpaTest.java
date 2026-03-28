package net.shlab.hogefugapiyo.equipmentlending.infrastructure.repository.query.impl;

import java.util.List;
import net.shlab.hogefugapiyo.equipmentlending.infrastructure.repository.query.UserMypageQueryRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({RepositoryTestConfig.class, UserMypageQueryRepositoryImpl.class})
class UserMypageQueryRepositoryDataJpaTest {

    @Autowired
    private UserMypageQueryRepositoryImpl userMypageQueryRepository;

    @Test
    void findRequestsByApplicantReturnsStatusCodes() {
        List<UserMypageQueryRepository.RequestRow> lentRequests = userMypageQueryRepository.findLentRequestsByApplicantUserId("USER02");
        List<UserMypageQueryRepository.RequestRow> pendingRequests = userMypageQueryRepository.findPendingRequestsByApplicantUserId("USER02");

        assertThat(lentRequests)
                .singleElement()
                .satisfies(request -> {
                    assertThat(request.requestComment()).isEqualTo("長机を貸し出してほしい。");
                    assertThat(request.statusCode()).isEqualTo("LENT");
                });

        assertThat(pendingRequests)
                .extracting(UserMypageQueryRepository.RequestRow::statusCode)
                .containsExactly("REJECTED", "PENDING_RETURN_CONFIRMATION", "PENDING_APPROVAL");
        assertThat(pendingRequests)
                .extracting(UserMypageQueryRepository.RequestRow::reviewComment)
                .contains("", "承認済み。", "利用目的が不足しているため却下。");
    }

    @Test
    void existsRejectedRequestByApplicantUserIdDetectsRejectedData() {
        assertThat(userMypageQueryRepository.existsRejectedRequestByApplicantUserId("USER02")).isTrue();
        assertThat(userMypageQueryRepository.existsRejectedRequestByApplicantUserId("USER01")).isFalse();
    }

    @Test
    void findRequestsByApplicantReturnsEmptyForUserWithoutRequests() {
        assertThat(userMypageQueryRepository.findLentRequestsByApplicantUserId("USER01")).isEmpty();
        assertThat(userMypageQueryRepository.findPendingRequestsByApplicantUserId("USER01")).isEmpty();
    }
}
