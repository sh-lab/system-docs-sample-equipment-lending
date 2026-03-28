package net.shlab.hogefugapiyo.equipmentlending.application;

import net.shlab.hogefugapiyo.framework.core.service.ApplicationService;

/**
 * 返却確認ユースケースを提供する画面用アプリケーションサービス。
 *
 * <p>設計書:
 * <ul>
 *   <li>{@code docs/03_designs/services/application/HFP-EL-SAS504_return-confirm_service.md}</li>
 * </ul>
 */
public interface HfpElSas504ReturnConfirmApplicationService extends ApplicationService {

    void confirm(String adminUserId, long lendingRequestId, String returnConfirmComment, int version);
}
