package net.shlab.hogefugapiyo.equipmentlending.application.query;

import java.util.Optional;
import net.shlab.hogefugapiyo.equipmentlending.model.value.UserRole;
import net.shlab.hogefugapiyo.framework.core.service.QueryService;

/**
 * ログイン利用者の取得処理を提供する Query Service。
 *
 * <p>設計書:
 * <ul>
 *   <li>{@code docs/03_designs/ui/screen-list.md}</li>
 * </ul>
 */
public interface FindLoginUserQueryService
        extends QueryService<FindLoginUserQueryService.Request, Optional<FindLoginUserQueryService.Response>> {

    record Request(String userId) {
    }

    record Response(String userId, UserRole roleCode) {
    }
}
