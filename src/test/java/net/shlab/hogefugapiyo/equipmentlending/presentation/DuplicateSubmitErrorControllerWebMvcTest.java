package net.shlab.hogefugapiyo.equipmentlending.presentation;

import static net.shlab.hogefugapiyo.framework.security.SecurityMockMvcTestSupport.userPrincipal;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import net.shlab.hogefugapiyo.equipmentlending.infrastructure.security.config.SecurityConfiguration;
import net.shlab.hogefugapiyo.equipmentlending.model.value.UserRole;
import net.shlab.hogefugapiyo.equipmentlending.presentation.controller.DuplicateSubmitErrorController;
import net.shlab.hogefugapiyo.equipmentlending.presentation.route.RoutePaths;
import net.shlab.hogefugapiyo.framework.i18n.I18nMessageResolver;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(DuplicateSubmitErrorController.class)
@Import(SecurityConfiguration.class)
class DuplicateSubmitErrorControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private I18nMessageResolver i18nMessageResolver;

    @Test
    void redirectsToLoginWhenSessionIsMissing() throws Exception {
        mockMvc.perform(get(RoutePaths.DUPLICATE_SUBMIT_ERROR))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost" + RoutePaths.LOGIN));
    }

    @Test
    void userViewShowsLinkToUserMypage() throws Exception {
        given(i18nMessageResolver.getBusinessMessage("MSG_E_011"))
                .willReturn("[MSG_E_011]画面を再表示してからやり直してください。");

        mockMvc.perform(get(RoutePaths.DUPLICATE_SUBMIT_ERROR)
                        .with(userPrincipal("USER01", UserRole.USER)))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("[MSG_E_011]画面を再表示してからやり直してください。")))
                .andExpect(content().string(containsString("action=\"" + RoutePaths.HFP_ELV100_USER_MYPAGE + "\"")));
    }

    @Test
    void adminViewShowsLinkToAdminMypage() throws Exception {
        given(i18nMessageResolver.getBusinessMessage("MSG_E_011"))
                .willReturn("[MSG_E_011]画面を再表示してからやり直してください。");

        mockMvc.perform(get(RoutePaths.DUPLICATE_SUBMIT_ERROR)
                        .with(userPrincipal("ADMIN1", UserRole.ADMIN)))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("action=\"" + RoutePaths.HFP_ELV200_ADMIN_MYPAGE + "\"")));
    }
}
