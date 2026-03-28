package net.shlab.hogefugapiyo.equipmentlending.application;

import net.shlab.hogefugapiyo.equipmentlending.application.query.FindAdminLendingReviewQueryService;
import net.shlab.hogefugapiyo.framework.core.service.ApplicationService;

/**
 * 管理者承認・却下・返却確認画面の初期表示ユースケースを提供する画面用アプリケーションサービス。
 *
 * <p>設計書:
 * <ul>
 *   <li>{@code docs/03_designs/services/application/HFP-EL-SAS501_admin-lending-review-init_service.md}</li>
 * </ul>
 */
public interface HfpElSas501AdminLendingReviewInitApplicationService extends ApplicationService {

    FindAdminLendingReviewQueryService.Response initialize(String adminUserId, Long lendingRequestId);
}
