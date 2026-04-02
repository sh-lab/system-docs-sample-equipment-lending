package net.shlab.hogefugapiyo.equipmentlending.presentation.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import net.shlab.hogefugapiyo.equipmentlending.application.BusinessException;
import net.shlab.hogefugapiyo.equipmentlending.application.SystemException;
import net.shlab.hogefugapiyo.equipmentlending.infrastructure.security.SecurityRouteResolver;
import net.shlab.hogefugapiyo.equipmentlending.infrastructure.security.UserPrincipal;
import net.shlab.hogefugapiyo.equipmentlending.model.value.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.web.method.annotation.AuthenticationPrincipalArgumentResolver;
import org.springframework.stereotype.Controller;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * {@link GlobalExceptionHandler} の単体テスト。
 *
 * <p>テスト用コントローラから各種例外をスローし、
 * {@code @ControllerAdvice} による一元的なハンドリングを検証する。
 */
@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    private MockMvc mockMvc;

    @Mock
    private SecurityRouteResolver securityRouteResolver;

    @Controller
    static class TestController {

        @GetMapping("/test/business-exception")
        public String throwBusinessException() {
            throw new BusinessException("MSG_E_001");
        }

        @GetMapping("/test/system-exception")
        public String throwSystemException() {
            throw new SystemException("DB connection failed");
        }

        @GetMapping("/test/unexpected-exception")
        public String throwUnexpectedException() {
            throw new RuntimeException("Something unexpected");
        }

        @GetMapping("/test/access-denied")
        public String throwAccessDeniedException() {
            throw new AccessDeniedException("Forbidden");
        }
    }

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new TestController())
                .setControllerAdvice(new GlobalExceptionHandler(securityRouteResolver))
                .setCustomArgumentResolvers(new AuthenticationPrincipalArgumentResolver())
                .build();
    }

    @Test
    void businessExceptionRedirectsToUserHome() throws Exception {
        setAuthentication("USER01", UserRole.USER);
        given(securityRouteResolver.resolveHomePath(any(UserPrincipal.class)))
                .willReturn("/hfp-el-v100-user-mypage");

        mockMvc.perform(get("/test/business-exception"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/hfp-el-v100-user-mypage?errorMessageId=MSG_E_001"));
    }

    @Test
    void businessExceptionRedirectsToAdminHome() throws Exception {
        setAuthentication("ADMIN01", UserRole.ADMIN);
        given(securityRouteResolver.resolveHomePath(any(UserPrincipal.class)))
                .willReturn("/hfp-el-v200-admin-mypage");

        mockMvc.perform(get("/test/business-exception"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/hfp-el-v200-admin-mypage?errorMessageId=MSG_E_001"));
    }

    @Test
    void businessExceptionFallsBackToLoginWhenUnauthenticated() throws Exception {
        SecurityContextHolder.clearContext();

        mockMvc.perform(get("/test/business-exception"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?errorMessageId=MSG_E_001"));
    }

    @Test
    void systemExceptionReturnsErrorView() throws Exception {
        mockMvc.perform(get("/test/system-exception"))
                .andExpect(status().isOk())
                .andExpect(view().name("error"));
    }

    @Test
    void unexpectedExceptionReturnsErrorView() throws Exception {
        mockMvc.perform(get("/test/unexpected-exception"))
                .andExpect(status().isOk())
                .andExpect(view().name("error"));
    }

    @Test
    void accessDeniedExceptionIsRethrown() {
        org.assertj.core.api.Assertions.assertThatThrownBy(() ->
                mockMvc.perform(get("/test/access-denied"))
        ).rootCause().isInstanceOf(AccessDeniedException.class);
    }

    private void setAuthentication(String userId, UserRole role) {
        UserPrincipal principal = new UserPrincipal(userId, role);
        var auth = UsernamePasswordAuthenticationToken.authenticated(
                principal, null, principal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }
}
