package net.shlab.hogefugapiyo.equipmentlending.presentation;

import java.util.List;
import net.shlab.hogefugapiyo.equipmentlending.application.BusinessException;
import net.shlab.hogefugapiyo.equipmentlending.application.HfpElSas401UserLendingRequestInitializeApplicationService;
import net.shlab.hogefugapiyo.equipmentlending.application.HfpElSas402LendingRequestApplicationService;
import net.shlab.hogefugapiyo.equipmentlending.application.HfpElSas403ReturnRequestApplicationService;
import net.shlab.hogefugapiyo.equipmentlending.application.HfpElSas404RejectedRequestConfirmApplicationService;
import net.shlab.hogefugapiyo.equipmentlending.application.query.LendingRequestScreenMode;
import net.shlab.hogefugapiyo.equipmentlending.application.query.UserLendingRequestEquipmentDto;
import net.shlab.hogefugapiyo.equipmentlending.application.query.UserLendingRequestViewData;
import net.shlab.hogefugapiyo.equipmentlending.presentation.controller.HfpElV400UserLendingRequestController;
import net.shlab.hogefugapiyo.equipmentlending.presentation.route.RoutePaths;
import net.shlab.hogefugapiyo.equipmentlending.model.value.UserRole;
import net.shlab.hogefugapiyo.equipmentlending.infrastructure.security.config.SecurityConfiguration;
import net.shlab.hogefugapiyo.equipmentlending.presentation.config.PresentationWebMvcConfiguration;
import net.shlab.hogefugapiyo.equipmentlending.presentation.token.OneTimeTokenScopes;
import net.shlab.hogefugapiyo.framework.web.OneTimeTokenSupport;
import net.shlab.hogefugapiyo.framework.i18n.I18nMessageResolver;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.mock.web.MockHttpSession;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static net.shlab.hogefugapiyo.framework.security.SecurityMockMvcTestSupport.userPrincipal;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(HfpElV400UserLendingRequestController.class)
@Import({SecurityConfiguration.class, PresentationWebMvcConfiguration.class})
class HfpElV400UserLendingRequestControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private HfpElSas401UserLendingRequestInitializeApplicationService initializeApplicationService;

    @MockitoBean
    private HfpElSas402LendingRequestApplicationService lendingRequestApplicationService;

    @MockitoBean
    private HfpElSas403ReturnRequestApplicationService returnRequestApplicationService;

    @MockitoBean
    private HfpElSas404RejectedRequestConfirmApplicationService rejectedRequestConfirmApplicationService;

    @MockitoBean
    private I18nMessageResolver i18nMessageResolver;

    @Test
    void showLendingModeFromEquipmentSearch() throws Exception {
        given(initializeApplicationService.initialize("USER01", "V300", null, List.of(1001L, 1004L)))
                .willReturn(lendingViewData());

        mockMvc.perform(get(RoutePaths.HFP_ELV400_USER_LENDING_REQUEST)
                        .param("from", "V300")
                        .param("equipmentIds", "1001", "1004")
                        .with(userPrincipal("USER01", UserRole.USER)))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("利用者貸出申請・返却画面")))
                .andExpect(content().string(containsString("貸出申請")))
                .andExpect(content().string(containsString("EQ-0001")))
                .andExpect(content().string(containsString(RoutePaths.HFP_ELV400_USER_LENDING_REQUEST_LENDING)));
    }

    @Test
    void lendingPostRedirectsToMypageOnSuccess() throws Exception {
        doNothing().when(lendingRequestApplicationService).register("USER01", List.of(1001L, 1004L), "会議で利用する。");
        MockHttpSession session = sessionWithToken(OneTimeTokenScopes.V400_LENDING);

        mockMvc.perform(post(RoutePaths.HFP_ELV400_USER_LENDING_REQUEST_LENDING)
                        .session(session)
                        .with(csrf())
                        .with(userPrincipal("USER01", UserRole.USER))
                        .param("equipmentIds", "1001", "1004")
                        .param("oneTimeToken", token(session, OneTimeTokenScopes.V400_LENDING))
                        .param("requestComment", "会議で利用する。"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(RoutePaths.HFP_ELV100_USER_MYPAGE + "?messageId=MSG_I_002"));
    }

    @Test
    void returnPostRendersErrorMessageWhenBusinessErrorOccurs() throws Exception {
        doThrow(new BusinessException("MSG_E_002"))
                .when(returnRequestApplicationService)
                .register("USER02", 2002L, "返却しました。", 1);
        given(initializeApplicationService.initialize("USER02", "V100", 2002L, List.of()))
                .willReturn(returnViewData());
        given(i18nMessageResolver.getBusinessMessage("MSG_E_002"))
                .willReturn("[MSG_E_002]返却申請を更新できませんでした。最新の状態を確認してください。");
        MockHttpSession session = sessionWithToken(OneTimeTokenScopes.V400_RETURN);

        mockMvc.perform(post(RoutePaths.HFP_ELV400_USER_LENDING_REQUEST_RETURN)
                        .session(session)
                        .with(csrf())
                        .with(userPrincipal("USER02", UserRole.USER))
                        .param("requestId", "2002")
                        .param("version", "1")
                        .param("oneTimeToken", token(session, OneTimeTokenScopes.V400_RETURN))
                        .param("returnRequestComment", "返却しました。"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("[MSG_E_002]返却申請を更新できませんでした。最新の状態を確認してください。")))
                .andExpect(content().string(containsString("返却申請")))
                .andExpect(content().string(containsString("返却しました。")));
    }

    @Test
    void lendingPostWithTooLongCommentRedisplaysSamePage() throws Exception {
        given(initializeApplicationService.initialize("USER01", "V300", null, List.of(1001L, 1004L)))
                .willReturn(lendingViewData());
        MockHttpSession session = sessionWithToken(OneTimeTokenScopes.V400_LENDING);

        mockMvc.perform(post(RoutePaths.HFP_ELV400_USER_LENDING_REQUEST_LENDING)
                        .session(session)
                        .with(csrf())
                        .with(userPrincipal("USER01", UserRole.USER))
                        .param("equipmentIds", "1001", "1004")
                        .param("oneTimeToken", token(session, OneTimeTokenScopes.V400_LENDING))
                        .param("requestComment", "a".repeat(501)))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("申請コメントは500文字以内で入力してください。")))
                .andExpect(content().string(containsString("利用者貸出申請・返却画面")));
    }

    @Test
    void returnPostWithoutRequestIdRedirectsToMypage() throws Exception {
        MockHttpSession session = sessionWithToken(OneTimeTokenScopes.V400_RETURN);

        mockMvc.perform(post(RoutePaths.HFP_ELV400_USER_LENDING_REQUEST_RETURN)
                        .session(session)
                        .with(csrf())
                        .with(userPrincipal("USER02", UserRole.USER))
                        .param("version", "1")
                        .param("oneTimeToken", token(session, OneTimeTokenScopes.V400_RETURN))
                        .param("returnRequestComment", "返却しました。"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(RoutePaths.HFP_ELV100_USER_MYPAGE + "?errorMessageId=MSG_E_004"));
    }

    private MockHttpSession sessionWithToken(String scope) {
        MockHttpSession session = new MockHttpSession();
        OneTimeTokenSupport.issueToken(session, scope);
        return session;
    }

    private String token(MockHttpSession session, String scope) {
        return OneTimeTokenSupport.issueToken(session, scope);
    }

    private UserLendingRequestViewData lendingViewData() {
        return new UserLendingRequestViewData(
                LendingRequestScreenMode.LENDING,
                true,
                true,
                false,
                null,
                null,
                null,
                null,
                null,
                "",
                "",
                "",
                null,
                List.of(1001L, 1004L),
                List.of(
                        new UserLendingRequestEquipmentDto(1001L, "EQ-0001", "長机 2台", "長机", "第1倉庫"),
                        new UserLendingRequestEquipmentDto(1004L, "EQ-0004", "長机 2台", "長机", "第1倉庫")
                )
        );
    }

    private UserLendingRequestViewData returnViewData() {
        return new UserLendingRequestViewData(
                LendingRequestScreenMode.RETURN,
                true,
                false,
                true,
                2002L,
                "貸出中",
                "2026-01-11 09:00",
                "2026-01-11 10:00",
                null,
                "長机を貸し出してほしい。",
                "",
                "承認済み。",
                1,
                List.of(1002L),
                List.of(new UserLendingRequestEquipmentDto(1002L, "EQ-0002", "長机 2台", "長机", "第1倉庫"))
        );
    }
}
