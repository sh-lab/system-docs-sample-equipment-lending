package net.shlab.hogefugapiyo.equipmentlending.presentation;

import net.shlab.hogefugapiyo.equipmentlending.application.query.FindUserMypageQueryService;
import net.shlab.hogefugapiyo.equipmentlending.application.HfpElSas101UserMypageInitApplicationService;
import net.shlab.hogefugapiyo.equipmentlending.presentation.route.RoutePaths;
import net.shlab.hogefugapiyo.equipmentlending.presentation.views.Views;
import net.shlab.hogefugapiyo.framework.core.controller.AbstractBaseController;
import net.shlab.hogefugapiyo.framework.i18n.I18nMessageResolver;
import net.shlab.hogefugapiyo.framework.security.UserPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * ユーザーマイページの表示を担当する Controller。
 *
 * <p>主に HfpElSas101UserMypageInitApplicationService へ画面初期表示処理を委譲する。
 *
 * <p>設計書:
 * <ul>
 *   <li>{@code docs/03_designs/ui/HFP-EL-V100_user-mypage.md}</li>
 * </ul>
 */
@Controller
@PreAuthorize("hasRole('USER')")
public class HfpElV100UserMypageController extends AbstractBaseController {

    private static final String REJECTED_WARNING_MESSAGE_ID = "MSG_W_001";

    private final HfpElSas101UserMypageInitApplicationService userMypageInitApplicationService;
    private final I18nMessageResolver i18nMessageResolver;

    public HfpElV100UserMypageController(
            HfpElSas101UserMypageInitApplicationService userMypageInitApplicationService,
            I18nMessageResolver i18nMessageResolver
    ) {
        this.userMypageInitApplicationService = userMypageInitApplicationService;
        this.i18nMessageResolver = i18nMessageResolver;
    }

    @GetMapping(RoutePaths.HFP_ELV100_USER_MYPAGE)
    public String show(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            Model model,
            @RequestParam(value = "messageId", required = false) String messageId,
            @RequestParam(value = "errorMessageId", required = false) String errorMessageId
    ) {
        FindUserMypageQueryService.Response result = userMypageInitApplicationService.initialize(userPrincipal.userId());
        model.addAttribute("lentRequests", result.lentRequests());
        model.addAttribute("pendingRequests", result.pendingRequests());
        model.addAttribute(
                "warningMessage",
                result.hasRejectedRequest() ? i18nMessageResolver.getBusinessMessage(REJECTED_WARNING_MESSAGE_ID) : null
        );
        model.addAttribute(
                "infoMessage",
                messageId == null || messageId.isBlank() ? null : i18nMessageResolver.getBusinessMessage(messageId)
        );
        model.addAttribute(
                "errorMessage",
                errorMessageId == null || errorMessageId.isBlank() ? null : i18nMessageResolver.getBusinessMessage(errorMessageId)
        );

        return Views.HFP_ELV100_USER_MYPAGE;
    }
}
