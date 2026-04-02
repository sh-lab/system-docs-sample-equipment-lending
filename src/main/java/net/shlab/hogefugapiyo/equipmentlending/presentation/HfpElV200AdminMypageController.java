package net.shlab.hogefugapiyo.equipmentlending.presentation;

import net.shlab.hogefugapiyo.equipmentlending.application.query.FindAdminMypageQueryService;
import net.shlab.hogefugapiyo.equipmentlending.application.HfpElSas201AdminMypageInitApplicationService;
import net.shlab.hogefugapiyo.equipmentlending.presentation.route.RoutePaths;
import net.shlab.hogefugapiyo.equipmentlending.presentation.views.Views;
import net.shlab.hogefugapiyo.equipmentlending.presentation.controller.AbstractBaseController;
import net.shlab.hogefugapiyo.framework.i18n.I18nMessageResolver;
import net.shlab.hogefugapiyo.equipmentlending.infrastructure.security.UserPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 管理者マイページの表示を担当する Controller。
 *
 * <p>主に HfpElSas201AdminMypageInitApplicationService へ画面初期表示処理を委譲する。
 *
 * <p>設計書:
 * <ul>
 *   <li>{@code docs/03_designs/ui/HFP-EL-V200_admin-mypage.md}</li>
 * </ul>
 */
@Controller
@PreAuthorize("hasRole('ADMIN')")
public class HfpElV200AdminMypageController extends AbstractBaseController {

    private final HfpElSas201AdminMypageInitApplicationService adminMypageInitApplicationService;

    public HfpElV200AdminMypageController(
            HfpElSas201AdminMypageInitApplicationService adminMypageInitApplicationService,
            I18nMessageResolver i18nMessageResolver
    ) {
        super(i18nMessageResolver);
        this.adminMypageInitApplicationService = adminMypageInitApplicationService;
    }

    @GetMapping(RoutePaths.HFP_ELV200_ADMIN_MYPAGE)
    public String show(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            Model model,
            @RequestParam(value = "messageId", required = false) String messageId,
            @RequestParam(value = "errorMessageId", required = false) String errorMessageId
    ) {
        FindAdminMypageQueryService.Response result = adminMypageInitApplicationService.initialize(userPrincipal.userId());
        model.addAttribute("pendingApprovalRequests", result.pendingApprovalRequests());
        model.addAttribute("pendingReturnRequests", result.pendingReturnRequests());
        model.addAttribute("infoMessage", resolveMessage(messageId));
        model.addAttribute("errorMessage", resolveMessage(errorMessageId));
        return Views.HFP_ELV200_ADMIN_MYPAGE;
    }
}
