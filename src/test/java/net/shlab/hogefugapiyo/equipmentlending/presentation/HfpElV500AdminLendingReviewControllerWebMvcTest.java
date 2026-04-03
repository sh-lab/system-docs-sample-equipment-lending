package net.shlab.hogefugapiyo.equipmentlending.presentation;

import net.shlab.hogefugapiyo.equipmentlending.application.query.impl.FindAdminLendingReviewQueryServiceImpl;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.mockito.BDDMockito.given;
import static net.shlab.hogefugapiyo.framework.security.SecurityMockMvcTestSupport.userPrincipal;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import net.shlab.hogefugapiyo.equipmentlending.application.HfpElSas501AdminLendingReviewInitApplicationService;
import net.shlab.hogefugapiyo.equipmentlending.application.HfpElSas502ApproveLendingRequestApplicationService;
import net.shlab.hogefugapiyo.equipmentlending.application.HfpElSas503RejectLendingRequestApplicationService;
import net.shlab.hogefugapiyo.equipmentlending.application.HfpElSas504ReturnConfirmApplicationService;
import net.shlab.hogefugapiyo.equipmentlending.application.query.AdminLendingReviewMode;
import net.shlab.hogefugapiyo.equipmentlending.presentation.controller.HfpElV500AdminLendingReviewController;
import net.shlab.hogefugapiyo.equipmentlending.presentation.route.RoutePaths;
import net.shlab.hogefugapiyo.equipmentlending.model.value.UserRole;
import net.shlab.hogefugapiyo.framework.i18n.I18nMessageResolver;
import net.shlab.hogefugapiyo.equipmentlending.presentation.config.PresentationWebMvcConfiguration;
import net.shlab.hogefugapiyo.equipmentlending.presentation.token.OneTimeTokenScopes;
import net.shlab.hogefugapiyo.framework.web.OneTimeTokenSupport;
import net.shlab.hogefugapiyo.equipmentlending.infrastructure.security.config.SecurityConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.doNothing;

@WebMvcTest(HfpElV500AdminLendingReviewController.class)
@Import({SecurityConfiguration.class, PresentationWebMvcConfiguration.class})
class HfpElV500AdminLendingReviewControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private HfpElSas501AdminLendingReviewInitApplicationService initializeApplicationService;

    @MockitoBean
    private HfpElSas502ApproveLendingRequestApplicationService approveApplicationService;

    @MockitoBean
    private HfpElSas503RejectLendingRequestApplicationService rejectApplicationService;

    @MockitoBean
    private HfpElSas504ReturnConfirmApplicationService returnConfirmApplicationService;

    @MockitoBean
    private I18nMessageResolver i18nMessageResolver;

    @Test
    void showApprovalModeRendersSelectedRequestOnly() throws Exception {
        given(initializeApplicationService.initialize("ADMIN1", 2001L)).willReturn(approvalViewData());

        mockMvc.perform(get(RoutePaths.HFP_ELV500_ADMIN_LENDING_REVIEW)
                        .param("requestId", "2001")
                        .with(userPrincipal("ADMIN1", UserRole.ADMIN)))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("管理者承認・却下・返却確認画面")))
                .andExpect(content().string(containsString("承認・却下")))
                .andExpect(content().string(containsString("EQ-0001")))
                .andExpect(content().string(not(containsString("承認待ち申請一覧"))));
    }

    @Test
    void approveRedirectsToAdminMypageOnSuccess() throws Exception {
        doNothing().when(approveApplicationService).approve("ADMIN1", 2001L, "承認する。", 0);
        MockHttpSession session = sessionWithToken(OneTimeTokenScopes.V500_REVIEW);

        mockMvc.perform(post(RoutePaths.HFP_ELV500_ADMIN_LENDING_REVIEW_APPROVE)
                        .session(session)
                        .with(csrf())
                        .param("requestId", "2001")
                        .param("version", "0")
                        .param("oneTimeToken", token(session, OneTimeTokenScopes.V500_REVIEW))
                        .param("reviewComment", "承認する。")
                        .with(userPrincipal("ADMIN1", UserRole.ADMIN)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(RoutePaths.HFP_ELV200_ADMIN_MYPAGE + "?messageId=MSG_I_005"));
    }

    @Test
    void approveWithTooLongCommentRedisplaysSamePage() throws Exception {
        given(initializeApplicationService.initialize("ADMIN1", 2001L)).willReturn(approvalViewData());
        MockHttpSession session = sessionWithToken(OneTimeTokenScopes.V500_REVIEW);

        mockMvc.perform(post(RoutePaths.HFP_ELV500_ADMIN_LENDING_REVIEW_APPROVE)
                        .session(session)
                        .with(csrf())
                        .with(userPrincipal("ADMIN1", UserRole.ADMIN))
                        .param("requestId", "2001")
                        .param("version", "0")
                        .param("oneTimeToken", token(session, OneTimeTokenScopes.V500_REVIEW))
                        .param("reviewComment", "a".repeat(501)))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("管理者コメントは500文字以内で入力してください。")))
                .andExpect(content().string(containsString("管理者承認・却下・返却確認画面")));
    }

    private MockHttpSession sessionWithToken(String scope) {
        MockHttpSession session = new MockHttpSession();
        OneTimeTokenSupport.issueToken(session, scope);
        return session;
    }

    private String token(MockHttpSession session, String scope) {
        return OneTimeTokenSupport.issueToken(session, scope);
    }

    private FindAdminLendingReviewQueryServiceImpl.Response approvalViewData() {
        return new FindAdminLendingReviewQueryServiceImpl.Response(
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
    }
}
