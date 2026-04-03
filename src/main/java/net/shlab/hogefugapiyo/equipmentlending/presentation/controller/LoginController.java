package net.shlab.hogefugapiyo.equipmentlending.presentation.controller;

import jakarta.validation.Valid;
import net.shlab.hogefugapiyo.equipmentlending.application.query.FindLoginUserQueryService;
import java.util.Optional;
import net.shlab.hogefugapiyo.equipmentlending.application.LoginApplicationService;
import net.shlab.hogefugapiyo.equipmentlending.presentation.form.LoginForm;
import net.shlab.hogefugapiyo.equipmentlending.presentation.route.RoutePaths;
import net.shlab.hogefugapiyo.equipmentlending.presentation.views.Views;
import net.shlab.hogefugapiyo.equipmentlending.presentation.controller.AbstractBaseController;
import net.shlab.hogefugapiyo.framework.i18n.I18nMessageResolver;
import net.shlab.hogefugapiyo.equipmentlending.infrastructure.security.SecurityRouteResolver;
import net.shlab.hogefugapiyo.equipmentlending.infrastructure.security.UserPrincipal;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * ログイン画面の表示とログイン要求の受付を担当する Controller。
 *
 * <p>認証結果に応じて利用者を各マイページへ遷移させる。
 *
 * <p>設計書:
 * <ul>
 *   <li>{@code docs/03_designs/ui/screen-list.md}</li>
 * </ul>
 */
@Controller
public class LoginController extends AbstractBaseController {

    private static final String PASSWORD = "pass";
    private static final String INVALID_CREDENTIALS_MESSAGE_KEY = "login.error.invalid-credentials";
    private final LoginApplicationService loginApplicationService;
    private final I18nMessageResolver i18nMessageResolver;
    private final SecurityContextRepository securityContextRepository;
    private final SecurityRouteResolver securityRouteResolver;

    public LoginController(
            LoginApplicationService loginApplicationService,
            I18nMessageResolver i18nMessageResolver,
            SecurityContextRepository securityContextRepository,
            SecurityRouteResolver securityRouteResolver
    ) {
        super(i18nMessageResolver);
        this.loginApplicationService = loginApplicationService;
        this.i18nMessageResolver = i18nMessageResolver;
        this.securityContextRepository = securityContextRepository;
        this.securityRouteResolver = securityRouteResolver;
    }

    @GetMapping(RoutePaths.ROOT)
    public String redirectRootToLogin(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        return userPrincipal == null ? redirectTo(Views.LOGIN) : redirectToHome(userPrincipal);
    }

    @GetMapping(RoutePaths.LOGIN)
    public String show(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        if (userPrincipal != null) {
            return redirectToHome(userPrincipal);
        }
        return Views.LOGIN;
    }

    @PostMapping(RoutePaths.LOGIN)
    public String login(@Valid @ModelAttribute("loginForm") LoginForm form,
                        BindingResult bindingResult,
                        Model model,
                        HttpServletRequest request,
                        HttpServletResponse response) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("userId", form.getUserId());
            model.addAttribute("errorMessage", null);
            ValidationErrorSupport.populate(model, bindingResult);
            return Views.LOGIN;
        }
        String userId = form.getUserId();
        String password = form.getPassword();
        Optional<FindLoginUserQueryService.Response> loginUser = loginApplicationService.findLoginUser(userId);
        if (!PASSWORD.equals(password) || loginUser.isEmpty()) {
            clearAuthentication(request, response);
            model.addAttribute("userId", userId);
            model.addAttribute("errorMessage", i18nMessageResolver.get(INVALID_CREDENTIALS_MESSAGE_KEY));
            return Views.LOGIN;
        }

        UserPrincipal userPrincipal = new UserPrincipal(loginUser.get().userId(), loginUser.get().roleCode());
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(UsernamePasswordAuthenticationToken.authenticated(
                userPrincipal,
                null,
                userPrincipal.getAuthorities()
        ));
        SecurityContextHolder.setContext(context);
        securityContextRepository.saveContext(context, request, response);
        return "redirect:" + securityRouteResolver.resolveHomePath(userPrincipal);
    }

    private String redirectToHome(UserPrincipal userPrincipal) {
        return "redirect:" + securityRouteResolver.resolveHomePath(userPrincipal);
    }

    private void clearAuthentication(HttpServletRequest request, HttpServletResponse response) {
        SecurityContextHolder.clearContext();
        securityContextRepository.saveContext(SecurityContextHolder.createEmptyContext(), request, response);
    }

}
