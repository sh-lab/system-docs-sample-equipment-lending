package net.shlab.hogefugapiyo.equipmentlending.presentation.controller;

import jakarta.servlet.http.HttpSession;
import net.shlab.hogefugapiyo.equipmentlending.presentation.route.RoutePaths;
import net.shlab.hogefugapiyo.framework.core.controller.BaseController;
import net.shlab.hogefugapiyo.framework.i18n.I18nMessageResolver;
import net.shlab.hogefugapiyo.framework.web.OneTimeTokenSupport;
import org.springframework.ui.Model;

/**
 * コントローラの共通基底クラス。
 * メッセージ解決・リダイレクト生成などの共通処理を提供する。
 */
public abstract class AbstractBaseController implements BaseController {

    private final I18nMessageResolver i18nMessageResolver;

    protected AbstractBaseController(I18nMessageResolver i18nMessageResolver) {
        this.i18nMessageResolver = i18nMessageResolver;
    }

    protected I18nMessageResolver i18nMessageResolver() {
        return i18nMessageResolver;
    }

    protected String redirectTo(String viewName) {
        return "redirect:" + RoutePaths.fromView(viewName);
    }

    protected String resolveMessage(String messageId) {
        return messageId == null || messageId.isBlank() ? null : i18nMessageResolver.getBusinessMessage(messageId);
    }

    protected String redirectWithMessage(String routePath, String messageId) {
        return "redirect:" + routePath + "?messageId=" + messageId;
    }

    protected String redirectWithError(String routePath, String messageId) {
        return "redirect:" + routePath + "?errorMessageId=" + messageId;
    }

    protected void issueOneTimeToken(Model model, HttpSession session, String attributeName, String scope) {
        model.addAttribute(attributeName, OneTimeTokenSupport.issueToken(session, scope));
    }

}
