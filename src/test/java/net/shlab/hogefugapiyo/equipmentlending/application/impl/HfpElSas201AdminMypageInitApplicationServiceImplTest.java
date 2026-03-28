package net.shlab.hogefugapiyo.equipmentlending.application.impl;

import net.shlab.hogefugapiyo.equipmentlending.application.query.impl.FindAdminMypageQueryServiceImpl;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import net.shlab.hogefugapiyo.equipmentlending.application.query.FindAdminMypageQueryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class HfpElSas201AdminMypageInitApplicationServiceImplTest {

    @Mock
    private FindAdminMypageQueryService findAdminMypageQueryService;

    @InjectMocks
    private HfpElSas201AdminMypageInitApplicationServiceImpl applicationService;

    @Test
    void initializeDelegatesQueryServiceWithAdminUserId() {
        var expected = new FindAdminMypageQueryServiceImpl.Response(
                List.of(new FindAdminMypageQueryServiceImpl.RequestItem(2001L, "USER02", "長机を申請する。", "2026-01-10 09:00", "承認待ち")),
                List.of()
        );
        when(findAdminMypageQueryService.execute(eq(new FindAdminMypageQueryServiceImpl.Request("ADMIN1")))).thenReturn(expected);

        var actual = applicationService.initialize("ADMIN1");

        assertThat(actual).isEqualTo(expected);
        verify(findAdminMypageQueryService).execute(new FindAdminMypageQueryServiceImpl.Request("ADMIN1"));
    }
}
