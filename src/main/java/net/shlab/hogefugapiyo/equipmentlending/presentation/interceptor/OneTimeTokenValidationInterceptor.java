package net.shlab.hogefugapiyo.equipmentlending.presentation.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.LinkedHashMap;
import java.util.Map;
import net.shlab.hogefugapiyo.equipmentlending.presentation.route.RoutePaths;
import net.shlab.hogefugapiyo.equipmentlending.presentation.token.OneTimeTokenScopes;
import net.shlab.hogefugapiyo.framework.web.OneTimeTokenSupport;
import org.springframework.web.servlet.HandlerInterceptor;

public class OneTimeTokenValidationInterceptor implements HandlerInterceptor {

    private static final String TOKEN_PARAMETER = "oneTimeToken";

    private final Map<String, TokenValidationRule> rules = new LinkedHashMap<>();

    public OneTimeTokenValidationInterceptor() {
        rules.put(
                RoutePaths.HFP_ELV400_USER_LENDING_REQUEST_LENDING,
                new TokenValidationRule(OneTimeTokenScopes.V400_LENDING)
        );
        rules.put(
                RoutePaths.HFP_ELV400_USER_LENDING_REQUEST_RETURN,
                new TokenValidationRule(OneTimeTokenScopes.V400_RETURN)
        );
        rules.put(
                RoutePaths.HFP_ELV400_USER_LENDING_REQUEST_REJECTED_CONFIRM,
                new TokenValidationRule(OneTimeTokenScopes.V400_REJECTED_CONFIRM)
        );
        rules.put(
                RoutePaths.HFP_ELV500_ADMIN_LENDING_REVIEW_APPROVE,
                new TokenValidationRule(OneTimeTokenScopes.V500_REVIEW)
        );
        rules.put(
                RoutePaths.HFP_ELV500_ADMIN_LENDING_REVIEW_REJECT,
                new TokenValidationRule(OneTimeTokenScopes.V500_REVIEW)
        );
        rules.put(
                RoutePaths.HFP_ELV500_ADMIN_LENDING_REVIEW_RETURN_CONFIRM,
                new TokenValidationRule(OneTimeTokenScopes.V500_RETURN_CONFIRM)
        );
        rules.put(
                RoutePaths.HFP_ELV700_ADMIN_EQUIPMENT_EDIT_REGISTER,
                new TokenValidationRule(OneTimeTokenScopes.V700_REGISTER)
        );
        rules.put(
                RoutePaths.HFP_ELV700_ADMIN_EQUIPMENT_EDIT_UPDATE,
                new TokenValidationRule(OneTimeTokenScopes.V700_UPDATE)
        );
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!"POST".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        TokenValidationRule rule = rules.get(requestPath(request));
        if (rule == null) {
            return true;
        }
        HttpSession session = request.getSession(false);
        if (session != null && OneTimeTokenSupport.consumeToken(session, rule.scope(), request.getParameter(TOKEN_PARAMETER))) {
            return true;
        }
        response.sendRedirect(request.getContextPath() + RoutePaths.DUPLICATE_SUBMIT_ERROR);
        return false;
    }

    private String requestPath(HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        String contextPath = request.getContextPath();
        if (contextPath != null && !contextPath.isEmpty() && requestUri.startsWith(contextPath)) {
            return requestUri.substring(contextPath.length());
        }
        return requestUri;
    }

    private record TokenValidationRule(String scope) {
    }
}
