package net.shlab.hogefugapiyo.framework.core.marker;

/**
 * 楽観ロック用のバージョン値を保持するエンティティが実装するマーカーIFである。
 */
public interface Versioned {
    Integer getVersion();
}
