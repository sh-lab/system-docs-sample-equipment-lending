package net.shlab.hogefugapiyo.equipmentlending.application;

import net.shlab.hogefugapiyo.equipmentlending.application.query.FindUserMypageQueryService;
import net.shlab.hogefugapiyo.framework.core.service.ApplicationService;

/**
 * ユーザーマイページ初期表示ユースケースを提供する画面用アプリケーションサービス。
 *
 * <p>主に FindUserMypageQueryService を組み合わせて表示データを整える。
 *
 * <p>設計書:
 * <ul>
 *   <li>{@code docs/03_designs/services/application/HFP-EL-SAS101_user-mypage-init_service.md}</li>
 * </ul>
 */
public interface HfpElSas101UserMypageInitApplicationService extends ApplicationService {

    FindUserMypageQueryService.Response initialize(String userId);
}
