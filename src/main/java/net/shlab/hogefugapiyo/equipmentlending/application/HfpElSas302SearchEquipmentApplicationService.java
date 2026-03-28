package net.shlab.hogefugapiyo.equipmentlending.application;

import net.shlab.hogefugapiyo.equipmentlending.application.query.SearchEquipmentQueryService;
import net.shlab.hogefugapiyo.framework.core.service.ApplicationService;

/**
 * 備品検索ユースケースを提供する画面用アプリケーションサービス。
 *
 * <p>主に SearchEquipmentQueryService を組み合わせて検索結果を返す。
 *
 * <p>設計書:
 * <ul>
 *   <li>{@code docs/03_designs/services/application/HFP-EL-SAS302_search-equipment_service.md}</li>
 * </ul>
 */
public interface HfpElSas302SearchEquipmentApplicationService extends ApplicationService {

    SearchEquipmentQueryService.Response search(String equipmentName, String equipmentType, String lendingStatus);
}
