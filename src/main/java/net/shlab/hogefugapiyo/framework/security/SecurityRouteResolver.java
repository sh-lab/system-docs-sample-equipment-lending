package net.shlab.hogefugapiyo.framework.security;

import net.shlab.hogefugapiyo.equipmentlending.presentation.route.RoutePaths;
import net.shlab.hogefugapiyo.equipmentlending.model.value.UserRole;

/**
 * ロールに応じた遷移先を解決する。
 */
public class SecurityRouteResolver {

    public String resolveHomePath(UserRole roleCode) {
        return roleCode.isAdmin() ? RoutePaths.HFP_ELV200_ADMIN_MYPAGE : RoutePaths.HFP_ELV100_USER_MYPAGE;
    }

    public String resolveHomePath(UserPrincipal userPrincipal) {
        return resolveHomePath(userPrincipal.roleCode());
    }
}
