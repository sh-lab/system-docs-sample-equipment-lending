package net.shlab.hogefugapiyo.framework.core.service;
/**
 * クエリ（参照）処理用サービスのインターフェース。
 * <p>
 * すべてのクエリサービスはexecuteメソッドを実装し、
 * フレームワーク側でAOP等による横断的な処理（ログ・統計等）が可能です。
 * @param <REQ> リクエスト型
 * @param <RES> レスポンス型
 */
public interface QueryService<REQ, RES> {
    /**
     * クエリを実行します。
     * @param request クエリリクエスト
     * @return クエリ結果
     */
    RES execute(REQ request);
}
