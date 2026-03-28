package net.shlab.hogefugapiyo.equipmentlending.presentation;

import java.util.ArrayList;
import java.util.List;
import net.shlab.hogefugapiyo.equipmentlending.application.BusinessException;
import net.shlab.hogefugapiyo.equipmentlending.application.BusinessMessageIds;
import net.shlab.hogefugapiyo.equipmentlending.application.HfpElSas401UserLendingRequestInitializeApplicationService;
import net.shlab.hogefugapiyo.equipmentlending.application.HfpElSas402LendingRequestApplicationService;
import net.shlab.hogefugapiyo.equipmentlending.application.HfpElSas403ReturnRequestApplicationService;
import net.shlab.hogefugapiyo.equipmentlending.application.HfpElSas404RejectedRequestConfirmApplicationService;
import net.shlab.hogefugapiyo.equipmentlending.application.query.LendingRequestScreenMode;
import net.shlab.hogefugapiyo.equipmentlending.application.query.UserLendingRequestViewData;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 貸出申請画面の表示と申請関連操作の受付を担当する Controller。
 *
 * <p>主に HFP-EL-SAS401〜404 の画面用アプリケーションサービスへ処理を委譲する。
 *
 * <p>設計書:
 * <ul>
 *   <li>{@code docs/03_designs/ui/HFP-EL-V400_user-lending-request.md}</li>
 * </ul>
 */
@Controller
@PreAuthorize("hasRole('USER')")
public class HfpElV400UserLendingRequestController extends AbstractBaseController {

    private static final String FROM_EQUIPMENT_SEARCH = "V300";
    private static final String FROM_USER_MYPAGE = "V100";

    private final HfpElSas401UserLendingRequestInitializeApplicationService initializeApplicationService;
    private final HfpElSas402LendingRequestApplicationService lendingRequestApplicationService;
    private final HfpElSas403ReturnRequestApplicationService returnRequestApplicationService;
    private final HfpElSas404RejectedRequestConfirmApplicationService rejectedRequestConfirmApplicationService;
    private final I18nMessageResolver i18nMessageResolver;

    public HfpElV400UserLendingRequestController(
            HfpElSas401UserLendingRequestInitializeApplicationService initializeApplicationService,
            HfpElSas402LendingRequestApplicationService lendingRequestApplicationService,
            HfpElSas403ReturnRequestApplicationService returnRequestApplicationService,
            HfpElSas404RejectedRequestConfirmApplicationService rejectedRequestConfirmApplicationService,
            I18nMessageResolver i18nMessageResolver
    ) {
        this.initializeApplicationService = initializeApplicationService;
        this.lendingRequestApplicationService = lendingRequestApplicationService;
        this.returnRequestApplicationService = returnRequestApplicationService;
        this.rejectedRequestConfirmApplicationService = rejectedRequestConfirmApplicationService;
        this.i18nMessageResolver = i18nMessageResolver;
    }

    @GetMapping(RoutePaths.HFP_ELV400_USER_LENDING_REQUEST)
    public String show(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            Model model,
            @RequestParam(value = "from", required = false) String from,
            @RequestParam(value = "requestId", required = false) Long requestId,
            @RequestParam(value = "equipmentIds", required = false) List<Long> equipmentIds,
            @RequestParam(value = "messageId", required = false) String messageId,
            @RequestParam(value = "errorMessageId", required = false) String errorMessageId
    ) {
        String userId = userPrincipal.userId();
        try {
            UserLendingRequestViewData viewData = initializeApplicationService.initialize(userId, normalizeFrom(from), requestId, normalizeEquipmentIds(equipmentIds));
            populateModel(model, viewData, resolveMessage(messageId), resolveMessage(errorMessageId));
            return Views.HFP_ELV400_USER_LENDING_REQUEST;
        } catch (BusinessException ex) {
            return redirectBySource(normalizeFrom(from), ex.messageId());
        }
    }

    @PostMapping(RoutePaths.HFP_ELV400_USER_LENDING_REQUEST_LENDING)
    public String registerLending(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            Model model,
            @RequestParam("equipmentIds") List<Long> equipmentIds,
            @RequestParam(value = "requestComment", required = false) String requestComment
    ) {
        String userId = userPrincipal.userId();
        try {
            lendingRequestApplicationService.register(userId, normalizeEquipmentIds(equipmentIds), requestComment);
            return redirectWithMessage(RoutePaths.HFP_ELV100_USER_MYPAGE, BusinessMessageIds.LENDING_REQUEST_ACCEPTED);
        } catch (BusinessException ex) {
            return renderLendingError(model, userId, normalizeEquipmentIds(equipmentIds), requestComment, ex.messageId());
        }
    }

    @PostMapping(RoutePaths.HFP_ELV400_USER_LENDING_REQUEST_RETURN)
    public String registerReturn(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            Model model,
            @RequestParam("requestId") long requestId,
            @RequestParam("version") int version,
            @RequestParam(value = "returnRequestComment", required = false) String returnRequestComment
    ) {
        String userId = userPrincipal.userId();
        try {
            returnRequestApplicationService.register(userId, requestId, returnRequestComment, version);
            return redirectWithMessage(RoutePaths.HFP_ELV100_USER_MYPAGE, BusinessMessageIds.RETURN_REQUEST_ACCEPTED);
        } catch (BusinessException ex) {
            return renderRequestError(model, userId, requestId, returnRequestComment, ex.messageId());
        }
    }

    @PostMapping(RoutePaths.HFP_ELV400_USER_LENDING_REQUEST_REJECTED_CONFIRM)
    public String confirmRejected(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            Model model,
            @RequestParam("requestId") long requestId,
            @RequestParam("version") int version
    ) {
        String userId = userPrincipal.userId();
        try {
            rejectedRequestConfirmApplicationService.confirm(userId, requestId, version);
            return redirectWithMessage(RoutePaths.HFP_ELV100_USER_MYPAGE, BusinessMessageIds.REJECTED_CONFIRM_COMPLETED);
        } catch (BusinessException ex) {
            return renderRequestError(model, userId, requestId, null, ex.messageId());
        }
    }

    private String renderLendingError(Model model, String userId, List<Long> equipmentIds, String requestComment, String errorMessageId) {
        try {
            UserLendingRequestViewData viewData = initializeApplicationService.initialize(userId, FROM_EQUIPMENT_SEARCH, null, equipmentIds);
            populateModel(model, viewData, null, resolveMessage(errorMessageId));
            model.addAttribute("requestComment", requestComment == null ? "" : requestComment);
            return Views.HFP_ELV400_USER_LENDING_REQUEST;
        } catch (BusinessException ex) {
            return redirectWithError(RoutePaths.HFP_ELV300_EQUIPMENT_SEARCH, ex.messageId());
        }
    }

    private String renderRequestError(Model model, String userId, long requestId, String returnRequestComment, String errorMessageId) {
        try {
            UserLendingRequestViewData viewData = initializeApplicationService.initialize(userId, FROM_USER_MYPAGE, requestId, List.of());
            populateModel(model, viewData, null, resolveMessage(errorMessageId));
            if (returnRequestComment != null) {
                model.addAttribute("returnRequestComment", returnRequestComment);
            }
            return Views.HFP_ELV400_USER_LENDING_REQUEST;
        } catch (BusinessException ex) {
            return redirectWithError(RoutePaths.HFP_ELV100_USER_MYPAGE, ex.messageId());
        }
    }

    private void populateModel(Model model, UserLendingRequestViewData viewData, String infoMessage, String errorMessage) {
        model.addAttribute("viewData", viewData);
        model.addAttribute("isLendingMode", viewData.mode() == LendingRequestScreenMode.LENDING);
        model.addAttribute("isReturnMode", viewData.mode() == LendingRequestScreenMode.RETURN);
        model.addAttribute("isRejectedConfirmMode", viewData.mode() == LendingRequestScreenMode.REJECTED_CONFIRM);
        model.addAttribute("infoMessage", infoMessage);
        model.addAttribute("errorMessage", errorMessage);
        model.addAttribute("requestComment", viewData.requestComment());
        model.addAttribute("returnRequestComment", viewData.returnRequestComment());
    }

    private String normalizeFrom(String from) {
        return FROM_USER_MYPAGE.equals(from) ? FROM_USER_MYPAGE : FROM_EQUIPMENT_SEARCH;
    }

    private List<Long> normalizeEquipmentIds(List<Long> equipmentIds) {
        return equipmentIds == null ? List.of() : new ArrayList<>(equipmentIds);
    }

    private String resolveMessage(String messageId) {
        return messageId == null || messageId.isBlank() ? null : i18nMessageResolver.getBusinessMessage(messageId);
    }

    private String redirectBySource(String from, String messageId) {
        return redirectWithError(FROM_USER_MYPAGE.equals(from) ? RoutePaths.HFP_ELV100_USER_MYPAGE : RoutePaths.HFP_ELV300_EQUIPMENT_SEARCH, messageId);
    }

    private String redirectWithMessage(String routePath, String messageId) {
        return "redirect:" + routePath + "?messageId=" + messageId;
    }

    private String redirectWithError(String routePath, String messageId) {
        return "redirect:" + routePath + "?errorMessageId=" + messageId;
    }
}
