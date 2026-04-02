package net.shlab.hogefugapiyo.equipmentlending.presentation;

import jakarta.validation.Valid;
import net.shlab.hogefugapiyo.equipmentlending.application.query.FindAdminLendingReviewQueryService;
import net.shlab.hogefugapiyo.equipmentlending.application.BusinessException;
import net.shlab.hogefugapiyo.equipmentlending.application.BusinessMessageIds;
import net.shlab.hogefugapiyo.equipmentlending.application.HfpElSas501AdminLendingReviewInitApplicationService;
import net.shlab.hogefugapiyo.equipmentlending.application.HfpElSas502ApproveLendingRequestApplicationService;
import net.shlab.hogefugapiyo.equipmentlending.application.HfpElSas503RejectLendingRequestApplicationService;
import net.shlab.hogefugapiyo.equipmentlending.application.HfpElSas504ReturnConfirmApplicationService;
import net.shlab.hogefugapiyo.equipmentlending.application.query.AdminLendingReviewMode;
import net.shlab.hogefugapiyo.equipmentlending.presentation.form.AdminLendingApproveForm;
import net.shlab.hogefugapiyo.equipmentlending.presentation.form.AdminLendingRejectForm;
import net.shlab.hogefugapiyo.equipmentlending.presentation.form.AdminReturnConfirmForm;
import net.shlab.hogefugapiyo.equipmentlending.presentation.route.RoutePaths;
import net.shlab.hogefugapiyo.equipmentlending.presentation.views.Views;
import net.shlab.hogefugapiyo.equipmentlending.presentation.controller.AbstractBaseController;
import net.shlab.hogefugapiyo.framework.i18n.I18nMessageResolver;
import net.shlab.hogefugapiyo.equipmentlending.infrastructure.security.UserPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 管理者承認・却下・返却確認画面の表示と更新操作の受付を担当する Controller。
 *
 * <p>主に HFP-EL-SAS501〜504 の画面用アプリケーションサービスへ処理を委譲する。
 *
 * <p>設計書:
 * <ul>
 *   <li>{@code docs/03_designs/ui/HFP-EL-V500_admin-lending-review.md}</li>
 * </ul>
 */
@Controller
@PreAuthorize("hasRole('ADMIN')")
public class HfpElV500AdminLendingReviewController extends AbstractBaseController {

    private final HfpElSas501AdminLendingReviewInitApplicationService initializeApplicationService;
    private final HfpElSas502ApproveLendingRequestApplicationService approveApplicationService;
    private final HfpElSas503RejectLendingRequestApplicationService rejectApplicationService;
    private final HfpElSas504ReturnConfirmApplicationService returnConfirmApplicationService;

    public HfpElV500AdminLendingReviewController(
            HfpElSas501AdminLendingReviewInitApplicationService initializeApplicationService,
            HfpElSas502ApproveLendingRequestApplicationService approveApplicationService,
            HfpElSas503RejectLendingRequestApplicationService rejectApplicationService,
            HfpElSas504ReturnConfirmApplicationService returnConfirmApplicationService,
            I18nMessageResolver i18nMessageResolver
    ) {
        super(i18nMessageResolver);
        this.initializeApplicationService = initializeApplicationService;
        this.approveApplicationService = approveApplicationService;
        this.rejectApplicationService = rejectApplicationService;
        this.returnConfirmApplicationService = returnConfirmApplicationService;
    }

    @GetMapping(RoutePaths.HFP_ELV500_ADMIN_LENDING_REVIEW)
    public String show(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            Model model,
            @RequestParam(value = "requestId", required = false) Long requestId,
            @RequestParam(value = "messageId", required = false) String messageId,
            @RequestParam(value = "errorMessageId", required = false) String errorMessageId
    ) {
        try {
            FindAdminLendingReviewQueryService.Response viewData = initializeApplicationService.initialize(userPrincipal.userId(), requestId);
            populateModel(model, viewData, resolveMessage(messageId), resolveMessage(errorMessageId), null, null);
            return Views.HFP_ELV500_ADMIN_LENDING_REVIEW;
        } catch (BusinessException ex) {
            return redirectWithError(RoutePaths.HFP_ELV200_ADMIN_MYPAGE, ex.messageId());
        }
    }

    @PostMapping(RoutePaths.HFP_ELV500_ADMIN_LENDING_REVIEW_APPROVE)
    public String approve(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            Model model,
            @Valid @ModelAttribute("approveForm") AdminLendingApproveForm form,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            if (form.getRequestId() == null) {
                return redirectWithError(RoutePaths.HFP_ELV200_ADMIN_MYPAGE, BusinessMessageIds.REQUEST_DISPLAY_INVALID);
            }
            return renderValidationError(model, userPrincipal.userId(), form.getRequestId(), form.getReviewComment(), null, bindingResult);
        }
        try {
            approveApplicationService.approve(userPrincipal.userId(), form.getRequestId(), form.getReviewComment(), form.getVersion());
            return redirectWithMessage(RoutePaths.HFP_ELV200_ADMIN_MYPAGE, BusinessMessageIds.APPROVE_REQUEST_COMPLETED);
        } catch (BusinessException ex) {
            return renderRequestError(model, userPrincipal.userId(), form.getRequestId(), ex.messageId(), form.getReviewComment(), null);
        }
    }

    @PostMapping(RoutePaths.HFP_ELV500_ADMIN_LENDING_REVIEW_REJECT)
    public String reject(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            Model model,
            @Valid @ModelAttribute("rejectForm") AdminLendingRejectForm form,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            if (form.getRequestId() == null) {
                return redirectWithError(RoutePaths.HFP_ELV200_ADMIN_MYPAGE, BusinessMessageIds.REQUEST_DISPLAY_INVALID);
            }
            return renderValidationError(model, userPrincipal.userId(), form.getRequestId(), form.getReviewComment(), null, bindingResult);
        }
        try {
            rejectApplicationService.reject(userPrincipal.userId(), form.getRequestId(), form.getReviewComment(), form.getVersion());
            return redirectWithMessage(RoutePaths.HFP_ELV200_ADMIN_MYPAGE, BusinessMessageIds.REJECT_REQUEST_COMPLETED);
        } catch (BusinessException ex) {
            return renderRequestError(model, userPrincipal.userId(), form.getRequestId(), ex.messageId(), form.getReviewComment(), null);
        }
    }

    @PostMapping(RoutePaths.HFP_ELV500_ADMIN_LENDING_REVIEW_RETURN_CONFIRM)
    public String returnConfirm(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            Model model,
            @Valid @ModelAttribute("returnConfirmForm") AdminReturnConfirmForm form,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            if (form.getRequestId() == null) {
                return redirectWithError(RoutePaths.HFP_ELV200_ADMIN_MYPAGE, BusinessMessageIds.REQUEST_DISPLAY_INVALID);
            }
            return renderValidationError(model, userPrincipal.userId(), form.getRequestId(), null, form.getReturnConfirmComment(), bindingResult);
        }
        try {
            returnConfirmApplicationService.confirm(
                    userPrincipal.userId(),
                    form.getRequestId(),
                    form.getReturnConfirmComment(),
                    form.getVersion()
            );
            return redirectWithMessage(RoutePaths.HFP_ELV200_ADMIN_MYPAGE, BusinessMessageIds.RETURN_CONFIRM_COMPLETED);
        } catch (BusinessException ex) {
            return renderRequestError(model, userPrincipal.userId(), form.getRequestId(), ex.messageId(), null, form.getReturnConfirmComment());
        }
    }

    private String renderRequestError(
            Model model,
            String adminUserId,
            long requestId,
            String errorMessageId,
            String reviewComment,
            String returnConfirmComment
    ) {
        try {
            FindAdminLendingReviewQueryService.Response viewData = initializeApplicationService.initialize(adminUserId, requestId);
            populateModel(model, viewData, null, resolveMessage(errorMessageId), reviewComment, returnConfirmComment);
            return Views.HFP_ELV500_ADMIN_LENDING_REVIEW;
        } catch (BusinessException ex) {
            return redirectWithError(RoutePaths.HFP_ELV200_ADMIN_MYPAGE, ex.messageId());
        }
    }

    private String renderValidationError(
            Model model,
            String adminUserId,
            long requestId,
            String reviewComment,
            String returnConfirmComment,
            BindingResult bindingResult
    ) {
        try {
            FindAdminLendingReviewQueryService.Response viewData = initializeApplicationService.initialize(adminUserId, requestId);
            populateModel(model, viewData, null, null, reviewComment, returnConfirmComment);
            ValidationErrorSupport.populate(model, bindingResult);
            return Views.HFP_ELV500_ADMIN_LENDING_REVIEW;
        } catch (BusinessException ex) {
            return redirectWithError(RoutePaths.HFP_ELV200_ADMIN_MYPAGE, ex.messageId());
        }
    }

    private void populateModel(
            Model model,
            FindAdminLendingReviewQueryService.Response viewData,
            String infoMessage,
            String errorMessage,
            String reviewComment,
            String returnConfirmComment
    ) {
        model.addAttribute("viewData", viewData);
        model.addAttribute("infoMessage", infoMessage);
        model.addAttribute("errorMessage", errorMessage);
        model.addAttribute("hasSelection", viewData.selectedRequest() != null);
        model.addAttribute("isApprovalReviewMode", viewData.mode() == AdminLendingReviewMode.APPROVAL_REVIEW);
        model.addAttribute("isReturnConfirmMode", viewData.mode() == AdminLendingReviewMode.RETURN_CONFIRM);
        model.addAttribute(
                "reviewComment",
                reviewComment != null ? reviewComment : defaultDetailValue(viewData, true)
        );
        model.addAttribute(
                "returnConfirmComment",
                returnConfirmComment != null ? returnConfirmComment : defaultDetailValue(viewData, false)
        );
    }

    private String defaultDetailValue(FindAdminLendingReviewQueryService.Response viewData, boolean review) {
        if (viewData.selectedRequest() == null) {
            return "";
        }
        return review ? viewData.selectedRequest().reviewComment() : viewData.selectedRequest().returnConfirmComment();
    }
}
