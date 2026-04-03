package net.shlab.hogefugapiyo.equipmentlending.presentation;

import net.shlab.hogefugapiyo.equipmentlending.application.query.impl.FindAdminMypageQueryServiceImpl;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.BDDMockito.given;
import static net.shlab.hogefugapiyo.framework.security.SecurityMockMvcTestSupport.userPrincipal;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import net.shlab.hogefugapiyo.equipmentlending.application.HfpElSas201AdminMypageInitApplicationService;
import net.shlab.hogefugapiyo.equipmentlending.presentation.controller.HfpElV200AdminMypageController;
import net.shlab.hogefugapiyo.equipmentlending.presentation.route.RoutePaths;
import net.shlab.hogefugapiyo.equipmentlending.model.value.UserRole;
import net.shlab.hogefugapiyo.framework.i18n.I18nMessageResolver;
import net.shlab.hogefugapiyo.equipmentlending.infrastructure.security.config.SecurityConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(HfpElV200AdminMypageController.class)
@Import(SecurityConfiguration.class)
class HfpElV200AdminMypageControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private HfpElSas201AdminMypageInitApplicationService adminMypageInitApplicationService;

    @MockitoBean
    private I18nMessageResolver i18nMessageResolver;

    @Test
    void adminMypageRendersPendingLists() throws Exception {
        given(adminMypageInitApplicationService.initialize("ADMIN1"))
                .willReturn(new FindAdminMypageQueryServiceImpl.Response(
                        List.of(new FindAdminMypageQueryServiceImpl.RequestItem(2001L, "USER02", "長机を申請する。", "2026-01-10 09:00", "承認待ち")),
                        List.of(new FindAdminMypageQueryServiceImpl.RequestItem(2003L, "USER02", "返却したので確認を依頼する。", "2026-01-14 17:00", "返却確認待ち"))
                ));

        mockMvc.perform(get(RoutePaths.HFP_ELV200_ADMIN_MYPAGE)
                        .with(userPrincipal("ADMIN1", UserRole.ADMIN)))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("管理者マイページ")))
                .andExpect(content().string(containsString("承認待ち申請一覧")))
                .andExpect(content().string(containsString("返却確認待ち申請一覧")))
                .andExpect(content().string(containsString("長机を申請する。")))
                .andExpect(content().string(containsString(RoutePaths.HFP_ELV500_ADMIN_LENDING_REVIEW)))
                .andExpect(content().string(containsString(RoutePaths.HFP_ELV600_ADMIN_EQUIPMENT_SEARCH)));
    }
}
