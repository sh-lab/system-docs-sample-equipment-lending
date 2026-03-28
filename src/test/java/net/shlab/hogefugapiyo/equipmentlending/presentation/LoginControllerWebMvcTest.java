package net.shlab.hogefugapiyo.equipmentlending.presentation;

import net.shlab.hogefugapiyo.equipmentlending.application.query.impl.FindLoginUserQueryServiceImpl;
import java.util.Optional;
import net.shlab.hogefugapiyo.equipmentlending.application.LoginApplicationService;
import net.shlab.hogefugapiyo.equipmentlending.presentation.route.RoutePaths;
import net.shlab.hogefugapiyo.equipmentlending.model.value.UserRole;
import net.shlab.hogefugapiyo.framework.i18n.I18nMessageResolver;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.BDDMockito.given;
import static net.shlab.hogefugapiyo.framework.security.SecurityMockMvcTestSupport.userPrincipal;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import net.shlab.hogefugapiyo.framework.security.config.SecurityConfiguration;

@WebMvcTest(LoginController.class)
@Import(SecurityConfiguration.class)
class LoginControllerWebMvcTest {

    private static final String INVALID_CREDENTIALS_MESSAGE = "ユーザーIDまたはパスワードが正しくありません。";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LoginApplicationService loginApplicationService;

    @MockitoBean
    private I18nMessageResolver i18nMessageResolver;

    @Test
    void rootRedirectsToLogin() throws Exception {
        mockMvc.perform(get(RoutePaths.ROOT))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(RoutePaths.LOGIN));
    }

    @Test
    void rootRedirectsAuthenticatedUserToOwnMypage() throws Exception {
        mockMvc.perform(get(RoutePaths.ROOT)
                        .with(userPrincipal("ADMIN1", UserRole.ADMIN)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(RoutePaths.HFP_ELV200_ADMIN_MYPAGE));
    }

    @Test
    void loginPageRenders() throws Exception {
        mockMvc.perform(get(RoutePaths.LOGIN))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("ログイン")))
                .andExpect(content().string(containsString("ユーザーID")))
                .andExpect(content().string(containsString("試験用利用者一覧")))
                .andExpect(content().string(containsString("USER01")))
                .andExpect(content().string(containsString("ADMIN1")))
                .andExpect(content().string(containsString("_csrf")));
    }

    @Test
    void loginPageRedirectsAuthenticatedUserToOwnMypage() throws Exception {
        mockMvc.perform(get(RoutePaths.LOGIN)
                        .with(userPrincipal("USER01", UserRole.USER)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(RoutePaths.HFP_ELV100_USER_MYPAGE));
    }

    @Test
    void loginPostWithoutCsrfIsForbidden() throws Exception {
        mockMvc.perform(post(RoutePaths.LOGIN)
                        .param("userId", "USER99")
                        .param("password", "pass"))
                .andExpect(status().isForbidden());
    }

    @Test
    void unknownUserRedisplaysLoginPage() throws Exception {
        given(loginApplicationService.findLoginUser("USER99")).willReturn(Optional.empty());
        given(i18nMessageResolver.get("login.error.invalid-credentials")).willReturn(INVALID_CREDENTIALS_MESSAGE);

        mockMvc.perform(post(RoutePaths.LOGIN)
                        .with(csrf())
                        .param("userId", "USER99")
                        .param("password", "pass"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(INVALID_CREDENTIALS_MESSAGE)));
    }

    @Test
    void invalidPasswordRedisplaysLoginPage() throws Exception {
        given(loginApplicationService.findLoginUser("USER01"))
                .willReturn(Optional.of(new FindLoginUserQueryServiceImpl.Response("USER01", UserRole.USER)));
        given(i18nMessageResolver.get("login.error.invalid-credentials")).willReturn(INVALID_CREDENTIALS_MESSAGE);

        mockMvc.perform(post(RoutePaths.LOGIN)
                        .with(csrf())
                        .param("userId", "USER01")
                        .param("password", "wrong"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(INVALID_CREDENTIALS_MESSAGE)))
                .andExpect(content().string(containsString("ログイン")));
    }

    @Test
    void validAdminLoginRedirectsToAdminMypage() throws Exception {
        given(loginApplicationService.findLoginUser("ADMIN1"))
                .willReturn(Optional.of(new FindLoginUserQueryServiceImpl.Response("ADMIN1", UserRole.ADMIN)));

        mockMvc.perform(post(RoutePaths.LOGIN)
                        .with(csrf())
                        .param("userId", "ADMIN1")
                        .param("password", "pass"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(RoutePaths.HFP_ELV200_ADMIN_MYPAGE));
    }
}
