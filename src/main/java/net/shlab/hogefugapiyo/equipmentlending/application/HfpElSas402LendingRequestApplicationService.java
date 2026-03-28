package net.shlab.hogefugapiyo.equipmentlending.application;

import java.util.List;
import net.shlab.hogefugapiyo.framework.core.service.ApplicationService;

/**
 * 貸出申請登録ユースケースを提供する画面用アプリケーションサービス。
 *
 * <p>コマンドサービスと参照サービスを組み合わせて申請結果を画面へ返す。
 *
 * <p>設計書:
 * <ul>
 *   <li>{@code docs/03_designs/services/application/HFP-EL-SAS402_lending-request_service.md}</li>
 * </ul>
 */
public interface HfpElSas402LendingRequestApplicationService extends ApplicationService {

    void register(String userId, List<Long> equipmentIds, String requestComment);
}
