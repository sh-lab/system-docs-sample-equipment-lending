package net.shlab.hogefugapiyo.equipmentlending.application.query.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import net.shlab.hogefugapiyo.equipmentlending.infrastructure.repository.query.AdminMypageQueryRepository;
import net.shlab.hogefugapiyo.framework.i18n.I18nMessageResolver;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FindAdminMypageQueryServiceImplTest {

    @Mock
    private AdminMypageQueryRepository adminMypageQueryRepository;

    @Mock
    private I18nMessageResolver i18nMessageResolver;

    @InjectMocks
    private FindAdminMypageQueryServiceImpl service;

    @Test
    void executeAggregatesRepositoryResultsIntoSingleResponse() {
        var approvals = List.of(new AdminMypageQueryRepository.RequestRow(2001L, "USER02", "長机を申請する。", "2026-01-10 09:00", "PENDING_APPROVAL"));
        var returns = List.of(new AdminMypageQueryRepository.RequestRow(2003L, "USER02", "返却したので確認を依頼する。", "2026-01-14 17:00", "PENDING_RETURN_CONFIRMATION"));
        when(adminMypageQueryRepository.findPendingApprovalRequests("ADMIN1")).thenReturn(approvals);
        when(adminMypageQueryRepository.findPendingReturnRequests("ADMIN1")).thenReturn(returns);
        when(i18nMessageResolver.get("label.status.pending-approval")).thenReturn("承認待ち");
        when(i18nMessageResolver.get("label.status.pending-return-confirmation")).thenReturn("返却確認待ち");

        var actual = service.execute(new FindAdminMypageQueryServiceImpl.Request("ADMIN1"));

        assertThat(actual.pendingApprovalRequests()).containsExactly(
                new FindAdminMypageQueryServiceImpl.RequestItem(2001L, "USER02", "長机を申請する。", "2026-01-10 09:00", "承認待ち")
        );
        assertThat(actual.pendingReturnRequests()).containsExactly(
                new FindAdminMypageQueryServiceImpl.RequestItem(2003L, "USER02", "返却したので確認を依頼する。", "2026-01-14 17:00", "返却確認待ち")
        );
        verify(adminMypageQueryRepository).findPendingApprovalRequests("ADMIN1");
        verify(adminMypageQueryRepository).findPendingReturnRequests("ADMIN1");
    }
}
