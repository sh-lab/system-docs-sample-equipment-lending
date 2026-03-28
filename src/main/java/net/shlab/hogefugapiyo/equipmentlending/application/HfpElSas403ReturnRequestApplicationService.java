package net.shlab.hogefugapiyo.equipmentlending.application;

import net.shlab.hogefugapiyo.framework.core.service.ApplicationService;

/**
 * 返却申請登録ユースケースを提供する画面用アプリケーションサービス。
 *
 * <p>設計書:
 * <ul>
 *   <li>{@code docs/03_designs/services/application/HFP-EL-SAS403_return-request_service.md}</li>
 * </ul>
 */
public interface HfpElSas403ReturnRequestApplicationService extends ApplicationService {

    void register(String userId, long lendingRequestId, String returnRequestComment, int version);
}
