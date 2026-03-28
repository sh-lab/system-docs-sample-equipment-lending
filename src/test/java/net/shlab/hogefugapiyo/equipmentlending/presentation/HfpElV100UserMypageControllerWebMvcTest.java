package net.shlab.hogefugapiyo.equipmentlending.presentation;

import net.shlab.hogefugapiyo.equipmentlending.application.query.impl.FindUserMypageQueryServiceImpl;
import java.util.List;
import net.shlab.hogefugapiyo.equipmentlending.application.HfpElSas101UserMypageInitApplicationService;
import net.shlab.hogefugapiyo.equipmentlending.presentation.route.RoutePaths;
import net.shlab.hogefugapiyo.equipmentlending.model.value.UserRole;
import net.shlab.hogefugapiyo.framework.security.config.SecurityConfiguration;
import net.shlab.hogefugapiyo.framework.i18n.I18nMessageResolver;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.mockito.BDDMockito.given;
import static net.shlab.hogefugapiyo.framework.security.SecurityMockMvcTestSupport.userPrincipal;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(HfpElV100UserMypageController.class)
@Import(SecurityConfiguration.class)
class HfpElV100UserMypageControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private HfpElSas101UserMypageInitApplicationService userMypageInitApplicationService;

    @MockitoBean
    private I18nMessageResolver i18nMessageResolver;

    @Test
    void redirectsToLoginWhenSessionIsMissing() throws Exception {
        mockMvc.perform(get(RoutePaths.HFP_ELV100_USER_MYPAGE))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost" + RoutePaths.LOGIN));
    }

    @Test
    void redirectsAdminToAdminMypage() throws Exception {
        mockMvc.perform(get(RoutePaths.HFP_ELV100_USER_MYPAGE)
                        .with(userPrincipal("ADMIN1", UserRole.ADMIN)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(RoutePaths.HFP_ELV200_ADMIN_MYPAGE));
    }

    @Test
    void mypageRendersWithRequestsAndWarning() throws Exception {
        given(userMypageInitApplicationService.initialize("USER02"))
                .willReturn(new FindUserMypageQueryServiceImpl.Response(
                        List.of(new FindUserMypageQueryServiceImpl.RequestItem(2002L, "長机を貸し出してほしい。", "", "貸出中")),
                        List.of(
                                new FindUserMypageQueryServiceImpl.RequestItem(2001L, "長机を申請する。", "", "承認待ち"),
                                new FindUserMypageQueryServiceImpl.RequestItem(2003L, "椅子を貸し出してほしい。", "承認済み。", "返却確認待ち"),
                                new FindUserMypageQueryServiceImpl.RequestItem(2004L, "プロジェクターを申請する。", "利用目的が不足しているため却下。", "却下")
                        ),
                        true
                ));
        given(i18nMessageResolver.getBusinessMessage("MSG_W_001"))
                .willReturn("[MSG_W_001]却下された申請があります。");

        mockMvc.perform(get(RoutePaths.HFP_ELV100_USER_MYPAGE)
                        .with(userPrincipal("USER02", UserRole.USER)))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("HFP備品貸出システム:利用者マイページ")))
                .andExpect(content().string(containsString("[MSG_W_001]却下された申請があります。")))
                .andExpect(content().string(containsString("長机を貸し出してほしい。")))
                .andExpect(content().string(containsString("長机を申請する。")))
                .andExpect(content().string(containsString("椅子を貸し出してほしい。")))
                .andExpect(content().string(containsString("プロジェクターを申請する。")))
                .andExpect(content().string(containsString("利用目的が不足しているため却下。")))
                .andExpect(content().string(containsString("貸出中")))
                .andExpect(content().string(containsString("承認待ち")))
                .andExpect(content().string(containsString("返却確認待ち")))
                .andExpect(content().string(containsString("却下")))
                .andExpect(content().string(containsString(RoutePaths.HFP_ELV300_EQUIPMENT_SEARCH)))
                .andExpect(content().string(containsString(RoutePaths.HFP_ELV400_USER_LENDING_REQUEST)))
                .andExpect(content().string(containsString("2026 Hoge Fuga Piyo EquipmentLending System")));
    }

    @Test
    void mypageShowsEmptyMessagesWhenUserHasNoRequests() throws Exception {
        given(userMypageInitApplicationService.initialize("USER01"))
                .willReturn(new FindUserMypageQueryServiceImpl.Response(List.of(), List.of(), false));

        mockMvc.perform(get(RoutePaths.HFP_ELV100_USER_MYPAGE)
                        .with(userPrincipal("USER01", UserRole.USER)))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("利用者マイページ")))
                .andExpect(content().string(containsString("表示できる申請はありません。")))
                .andExpect(content().string(not(containsString("[MSG_W_001]却下された申請があります。"))));
    }
}
