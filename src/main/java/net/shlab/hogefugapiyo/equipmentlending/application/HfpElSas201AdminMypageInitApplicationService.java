package net.shlab.hogefugapiyo.equipmentlending.application;

import net.shlab.hogefugapiyo.equipmentlending.application.query.FindAdminMypageQueryService;
import net.shlab.hogefugapiyo.framework.core.service.ApplicationService;

/**
 * 管理者マイページ初期表示ユースケースを提供する画面用アプリケーションサービス。
 *
 * <p>設計書:
 * <ul>
 *   <li>{@code docs/03_designs/services/application/HFP-EL-SAS201_admin-mypage-init_service.md}</li>
 * </ul>
 */
public interface HfpElSas201AdminMypageInitApplicationService extends ApplicationService {

    FindAdminMypageQueryService.Response initialize(String adminUserId);
}
