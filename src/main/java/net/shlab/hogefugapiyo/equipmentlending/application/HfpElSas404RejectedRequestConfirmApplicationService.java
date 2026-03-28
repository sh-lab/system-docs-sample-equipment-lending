package net.shlab.hogefugapiyo.equipmentlending.application;

import net.shlab.hogefugapiyo.framework.core.service.ApplicationService;

/**
 * 却下済み申請確認ユースケースを提供する画面用アプリケーションサービス。
 *
 * <p>設計書:
 * <ul>
 *   <li>{@code docs/03_designs/services/application/HFP-EL-SAS404_rejected-request-confirm_service.md}</li>
 * </ul>
 */
public interface HfpElSas404RejectedRequestConfirmApplicationService extends ApplicationService {

    void confirm(String userId, long lendingRequestId, int version);
}
