package net.shlab.hogefugapiyo.equipmentlending.application;

import java.util.List;
import net.shlab.hogefugapiyo.equipmentlending.application.query.UserLendingRequestViewData;
import net.shlab.hogefugapiyo.framework.core.service.ApplicationService;

/**
 * 貸出申請画面の初期表示ユースケースを提供する画面用アプリケーションサービス。
 *
 * <p>設計書:
 * <ul>
 *   <li>{@code docs/03_designs/services/application/HFP-EL-SAS401_user-lending-request-initialize_service.md}</li>
 * </ul>
 */
public interface HfpElSas401UserLendingRequestInitializeApplicationService extends ApplicationService {

    UserLendingRequestViewData initialize(String userId, String from, Long requestId, List<Long> equipmentIds);
}
