package net.shlab.hogefugapiyo.equipmentlending.application.impl;

import net.shlab.hogefugapiyo.equipmentlending.application.query.impl.FindAdminLendingReviewQueryServiceImpl;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import net.shlab.hogefugapiyo.equipmentlending.application.query.AdminLendingReviewMode;
import net.shlab.hogefugapiyo.equipmentlending.application.query.FindAdminLendingReviewQueryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class HfpElSas501AdminLendingReviewInitApplicationServiceImplTest {

    @Mock
    private FindAdminLendingReviewQueryService findAdminLendingReviewQueryService;

    @InjectMocks
    private HfpElSas501AdminLendingReviewInitApplicationServiceImpl applicationService;

    @Test
    void initializeDelegatesQueryServiceWithAdminUserIdAndRequestId() {
        var expected = new FindAdminLendingReviewQueryServiceImpl.Response(
                new FindAdminLendingReviewQueryServiceImpl.Detail(
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
                ),
                AdminLendingReviewMode.APPROVAL_REVIEW
        );
        when(findAdminLendingReviewQueryService.execute(eq(new FindAdminLendingReviewQueryServiceImpl.Request("ADMIN1", 2001L)))).thenReturn(expected);

        var actual = applicationService.initialize("ADMIN1", 2001L);

        assertThat(actual).isEqualTo(expected);
        verify(findAdminLendingReviewQueryService).execute(new FindAdminLendingReviewQueryServiceImpl.Request("ADMIN1", 2001L));
    }
}
