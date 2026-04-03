package net.shlab.hogefugapiyo.equipmentlending.presentation.controller;

import net.shlab.hogefugapiyo.equipmentlending.application.BusinessMessageIds;
import net.shlab.hogefugapiyo.equipmentlending.infrastructure.security.SecurityRouteResolver;
import net.shlab.hogefugapiyo.equipmentlending.infrastructure.security.UserPrincipal;
import net.shlab.hogefugapiyo.equipmentlending.presentation.controller.AbstractBaseController;
import net.shlab.hogefugapiyo.equipmentlending.presentation.route.RoutePaths;
import net.shlab.hogefugapiyo.equipmentlending.presentation.views.Views;
import net.shlab.hogefugapiyo.framework.i18n.I18nMessageResolver;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * ワンタイムトークン不正時の専用エラー画面を表示する Controller。
 */
@Controller
@PreAuthorize("isAuthenticated()")
public class DuplicateSubmitErrorController extends AbstractBaseController {

    private final SecurityRouteResolver securityRouteResolver;

    public DuplicateSubmitErrorController(
            I18nMessageResolver i18nMessageResolver,
            SecurityRouteResolver securityRouteResolver
    ) {
        super(i18nMessageResolver);
        this.securityRouteResolver = securityRouteResolver;
    }

    @GetMapping(RoutePaths.DUPLICATE_SUBMIT_ERROR)
    public String show(@AuthenticationPrincipal UserPrincipal userPrincipal, Model model) {
        model.addAttribute("errorMessage", resolveMessage(BusinessMessageIds.DUPLICATE_SUBMIT_INVALID));
        model.addAttribute("homePath", securityRouteResolver.resolveHomePath(userPrincipal));
        return Views.DUPLICATE_SUBMIT_ERROR;
    }
}
