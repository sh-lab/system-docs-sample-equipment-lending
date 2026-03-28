package net.shlab.hogefugapiyo.equipmentlending.application;

import java.util.Optional;
import net.shlab.hogefugapiyo.equipmentlending.application.query.FindLoginUserQueryService;
import net.shlab.hogefugapiyo.framework.core.service.ApplicationService;

/**
 * ログイン後の遷移先判定に必要な利用者情報を取得する画面用アプリケーションサービス。
 *
 * <p>設計書:
 * <ul>
 *   <li>{@code docs/03_designs/ui/screen-list.md}</li>
 * </ul>
 */
public interface LoginApplicationService extends ApplicationService {

    Optional<FindLoginUserQueryService.Response> findLoginUser(String userId);
}
