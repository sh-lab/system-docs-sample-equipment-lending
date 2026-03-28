package net.shlab.hogefugapiyo.equipmentlending.presentation;

import net.shlab.hogefugapiyo.equipmentlending.application.query.FindLoginUserQueryService;
import java.util.Optional;
import net.shlab.hogefugapiyo.equipmentlending.application.LoginApplicationService;
import net.shlab.hogefugapiyo.equipmentlending.presentation.route.RoutePaths;
import net.shlab.hogefugapiyo.equipmentlending.presentation.views.Views;
import net.shlab.hogefugapiyo.framework.core.controller.AbstractBaseController;
import net.shlab.hogefugapiyo.framework.i18n.I18nMessageResolver;
import net.shlab.hogefugapiyo.framework.security.SecurityRouteResolver;
import net.shlab.hogefugapiyo.framework.security.UserPrincipal;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
    public String login(@RequestParam("userId") String userId,
                        @RequestParam("password") String password,
                        Model model,
                        HttpServletRequest request,
                        HttpServletResponse response) {
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
