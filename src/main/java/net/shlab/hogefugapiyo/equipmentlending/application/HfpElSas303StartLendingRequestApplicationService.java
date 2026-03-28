package net.shlab.hogefugapiyo.equipmentlending.application;

import java.util.List;
import net.shlab.hogefugapiyo.framework.core.service.ApplicationService;

/**
 * 備品検索画面から貸出申請開始遷移を行う画面用アプリケーションサービス。
 *
 * <p>設計書:
 * <ul>
 *   <li>{@code docs/03_designs/services/application/HFP-EL-SAS303_start-lending-request_service.md}</li>
 * </ul>
 */
public interface HfpElSas303StartLendingRequestApplicationService extends ApplicationService {

    List<Long> start(List<Long> equipmentIds);
}
