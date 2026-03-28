package net.shlab.hogefugapiyo.equipmentlending.application.query.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import net.shlab.hogefugapiyo.equipmentlending.infrastructure.repository.query.UserMypageQueryRepository;
import net.shlab.hogefugapiyo.framework.i18n.I18nMessageResolver;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FindUserMypageQueryServiceImplTest {

    @Mock
    private UserMypageQueryRepository userMypageQueryRepository;

    @Mock
    private I18nMessageResolver i18nMessageResolver;

    @InjectMocks
    private FindUserMypageQueryServiceImpl service;

    @Test
    void executeAggregatesRepositoryResultsIntoSingleResponse() {
        var lentRequests = List.of(new UserMypageQueryRepository.RequestRow(1L, "長机", "", "LENT"));
        var pendingRequests = List.of(new UserMypageQueryRepository.RequestRow(2L, "プロジェクター", "", "PENDING_APPROVAL"));
        when(userMypageQueryRepository.findLentRequestsByApplicantUserId("USER02")).thenReturn(lentRequests);
        when(userMypageQueryRepository.findPendingRequestsByApplicantUserId("USER02")).thenReturn(pendingRequests);
        when(userMypageQueryRepository.existsRejectedRequestByApplicantUserId("USER02")).thenReturn(true);
        when(i18nMessageResolver.get("label.status.lent")).thenReturn("貸出中");
        when(i18nMessageResolver.get("label.status.pending-approval")).thenReturn("承認待ち");

        var actual = service.execute(new FindUserMypageQueryServiceImpl.Request("USER02"));

        assertThat(actual.lentRequests())
                .containsExactly(new FindUserMypageQueryServiceImpl.RequestItem(1L, "長机", "", "貸出中"));
        assertThat(actual.pendingRequests())
                .containsExactly(new FindUserMypageQueryServiceImpl.RequestItem(2L, "プロジェクター", "", "承認待ち"));
        assertThat(actual.hasRejectedRequest()).isTrue();
        verify(userMypageQueryRepository).findLentRequestsByApplicantUserId("USER02");
        verify(userMypageQueryRepository).findPendingRequestsByApplicantUserId("USER02");
        verify(userMypageQueryRepository).existsRejectedRequestByApplicantUserId("USER02");
    }
}
