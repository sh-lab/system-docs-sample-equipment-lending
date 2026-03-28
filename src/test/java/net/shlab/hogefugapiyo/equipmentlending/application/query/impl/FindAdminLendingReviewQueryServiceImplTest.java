package net.shlab.hogefugapiyo.equipmentlending.application.query.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import net.shlab.hogefugapiyo.equipmentlending.application.BusinessException;
import net.shlab.hogefugapiyo.equipmentlending.application.query.AdminLendingReviewMode;
import net.shlab.hogefugapiyo.equipmentlending.infrastructure.repository.query.AdminLendingReviewQueryRepository;
import net.shlab.hogefugapiyo.framework.i18n.I18nMessageResolver;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FindAdminLendingReviewQueryServiceImplTest {

    @Mock
    private AdminLendingReviewQueryRepository adminLendingReviewQueryRepository;

    @Mock
    private I18nMessageResolver i18nMessageResolver;

    @InjectMocks
    private FindAdminLendingReviewQueryServiceImpl service;

    @Test
    void executeReturnsSelectedRequestWhenRequestIdIsValid() {
        var detail = new AdminLendingReviewQueryRepository.DetailRow(
                2001L,
                "USER02",
                "PENDING_APPROVAL",
                "長机を申請する。",
                "",
                "",
                "",
                "2026-01-10 09:00",
                null,
                null,
                0,
                List.of(new AdminLendingReviewQueryRepository.EquipmentRow(1001L, "EQ-0001", "長机 2台", "DESK", "第1倉庫"))
        );
        when(adminLendingReviewQueryRepository.findRequestDetail(2001L)).thenReturn(Optional.of(detail));
        when(i18nMessageResolver.get("label.status.pending-approval")).thenReturn("承認待ち");
        when(i18nMessageResolver.get("label.equipment-type.desk")).thenReturn("長机");

        var actual = service.execute(new FindAdminLendingReviewQueryServiceImpl.Request("ADMIN1", 2001L));

        assertThat(actual.selectedRequest()).isEqualTo(new FindAdminLendingReviewQueryServiceImpl.Detail(
                2001L,
                "USER02",
                "PENDING_APPROVAL",
                "承認待ち",
                "長机を申請する。",
                "",
                "",
                "",
                "2026-01-10 09:00",
                null,
                null,
                0,
                List.of(new FindAdminLendingReviewQueryServiceImpl.EquipmentItem(1001L, "EQ-0001", "長机 2台", "長机", "第1倉庫"))
        ));
        assertThat(actual.mode()).isEqualTo(AdminLendingReviewMode.APPROVAL_REVIEW);
        verify(adminLendingReviewQueryRepository).findRequestDetail(2001L);
    }

    @Test
    void executeRejectsMissingRequestId() {
        assertThatThrownBy(() -> service.execute(new FindAdminLendingReviewQueryServiceImpl.Request("ADMIN1", null)))
                .isInstanceOf(BusinessException.class);
    }
}
