package net.shlab.hogefugapiyo.framework.core.service;

/**
 * コマンド（書き込み）処理用サービスのインターフェース。
 * <p>
 * すべてのコマンドサービスはexecuteメソッドを実装し、
 * フレームワーク側でAOP等による横断的な処理（ログ・統計等）が可能です。
 * @param <REQ> リクエスト型
 */
public interface CommandService<REQ> extends BaseCommandService {
    /**
     * コマンドを実行します。
     * @param request コマンドリクエスト
     */
    void execute(REQ request);
}
