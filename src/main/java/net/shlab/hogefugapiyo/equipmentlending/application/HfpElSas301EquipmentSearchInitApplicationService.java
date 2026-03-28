package net.shlab.hogefugapiyo.equipmentlending.application;

import net.shlab.hogefugapiyo.equipmentlending.application.query.SearchEquipmentQueryService;
import net.shlab.hogefugapiyo.framework.core.service.ApplicationService;

/**
 * 備品検索画面の初期表示ユースケースを提供する画面用アプリケーションサービス。
 *
 * <p>設計書:
 * <ul>
 *   <li>{@code docs/03_designs/services/application/HFP-EL-SAS301_equipment-search-init_service.md}</li>
 * </ul>
 */
public interface HfpElSas301EquipmentSearchInitApplicationService extends ApplicationService {

    SearchEquipmentQueryService.Response initialize();
}
