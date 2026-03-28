package net.shlab.hogefugapiyo.equipmentlending.application;

import net.shlab.hogefugapiyo.framework.core.service.ApplicationService;

/**
 * 貸出申請却下ユースケースを提供する画面用アプリケーションサービス。
 *
 * <p>設計書:
 * <ul>
 *   <li>{@code docs/03_designs/services/application/HFP-EL-SAS503_reject-lending-request_service.md}</li>
 * </ul>
 */
public interface HfpElSas503RejectLendingRequestApplicationService extends ApplicationService {

    void reject(String adminUserId, long lendingRequestId, String reviewComment, int version);
}
