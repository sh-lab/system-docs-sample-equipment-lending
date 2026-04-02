package net.shlab.hogefugapiyo.equipmentlending.application;

/**
 * システム例外の基底クラス。
 *
 * <p>DB障害・シーケンス払い出し失敗・外部サービス通信障害など、
 * システム基盤の異常を表す非検査例外である。
 *
 * <p>原因例外（cause）をチェーンすることで、
 * グローバル例外ハンドラでのログ出力時に根本原因を追跡可能とする。
 *
 * <p>設計書:
 * <ul>
 *   <li>{@code docs/02_architecture/error-handling.md} §5</li>
 * </ul>
 *
 * @see BusinessException
 */
public class SystemException extends RuntimeException {

    public SystemException(String message) {
        super(message);
    }

    public SystemException(String message, Throwable cause) {
        super(message, cause);
    }
}
