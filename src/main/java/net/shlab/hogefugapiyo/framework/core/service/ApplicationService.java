package net.shlab.hogefugapiyo.framework.core.service;

/**
 * アプリケーションサービス（ユースケース境界）を示すマーカーインターフェース。
 * <p>
 * - 1サービス1機能の原則で、各ユースケースごとにIFを定義する。
 * - トランザクション境界は実装クラス側に付与する（例：@Transactional）。
 * - 詳細な業務ロジックは Command / Query / Pure Service に委譲し、本層はオーケストレーションに徹する。
 * - フレームワーク側でAOP等による横断処理（ログ・計測・トレース）を適用可能。
 */
public interface ApplicationService {
    // マーカー（メソッド定義は各ユースケースIF側に置く）
}