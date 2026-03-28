package net.shlab.hogefugapiyo.framework.core.service;
/**
 * 副作用を持たない業務ロジック（計算・判定・ポリシー）を示すマーカー。
 * - 永続化や状態変更を行わないこと。
 * - 同一入力に対し同一出力を期待（pure/参照透明）。
 */
public interface PureService {
    // マーカー（メソッド定義は各ユースケースIF側に置く）
}