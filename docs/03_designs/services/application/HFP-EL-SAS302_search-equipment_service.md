# アプリケーションサービス設計書（Application Service）

## 1. サービスID・名称
- ID：`HFP-EL-SAS302_search-equipment_service`
- 名称：`備品検索実行サービス`

## 2. 役割と責務
- 備品検索画面の検索実行ユースケース境界を担う。
- 画面入力された検索条件を正規化し、Query Service へ委譲する。
- 貸出状態の許可値を `ALL`、`AVAILABLE`、`NOT_AVAILABLE` に制御し、不正値は既定値 `AVAILABLE` に補正する。

## 3. 目的・スコープ
- 目的：利用者が入力した条件で備品を検索し、表示用の結果一覧を返す。
- スコープ：
  - 対象画面：`備品検索画面(V300)`
  - 対象データ：備品一覧、種別候補
  - 業務範囲：検索条件の正規化と参照処理呼び出し

## 4. 前提条件・事後条件

### 4.1 前提条件
- 利用者としてログイン済みであること。
- 入力値の型・必須検証は Controller 層で完了している前提とする。

### 4.2 事後条件
- 正常終了時：
  - 正規化済み条件に基づく検索結果を返却する。
- 異常終了時：
  - 更新処理は行われない。
  - UI へエラーが通知される。

## 5. 処理フロー概要
1. 備品名、備品種別、貸出状態を受け取る
2. 文字列条件の前後空白を除去し、未指定値は空文字へ正規化する
3. `lendingStatus` が `ALL`、`AVAILABLE`、`NOT_AVAILABLE` 以外の場合は `AVAILABLE` を適用する
4. 検索条件を検索処理へ引き渡せる形に整える
5. 備品検索用の取得処理を呼び出す
6. 検索結果を UI へ返却する

## 6. 内部で使用するサービス
- Query Service：
  - `備品検索情報取得サービス(SQS301)`
- Command Service：
  - なし
- Pure Service：
  - なし

## 7. 入出力DTO

### 7.1 入力DTO
| 項目 | 型 | 必須 | 備考 |
|-----|----|-----|-----|
| equipmentName | string | 任意 | 前後空白を除去したうえで部分一致条件へ利用する |
| equipmentType | string | 任意 | 前後空白を除去したうえで完全一致条件へ利用する |
| lendingStatus | string | 任意 | `ALL`、`AVAILABLE`、`NOT_AVAILABLE` のみ許可する |

### 7.2 出力DTO
| 項目 | 型 | 必須 | 説明 |
|-----|----|-----|-----|
| equipmentItems | `List<SearchEquipmentQueryServiceImpl.EquipmentItem>` | ○ | 一覧表示用備品 |
| equipmentTypeOptions | `List<SearchEquipmentQueryServiceImpl.Option>` | ○ | 備品種別マスタから取得した候補 |
| hasMoreThanLimit | boolean | ○ | 件数上限超過有無 |

## 8. 例外マッピング方針
- 業務例外：貸出状態の不正値は例外とせず `AVAILABLE` へ補正する。
- システム例外：データアクセス障害などは汎用エラーとして通知する。
- 参照：`02_architecture/error-handling.md`

## 9. トランザクション・整合性
- 参照専用サービスであり、状態変更は行わない。
- 正規化ルールは Application Service に集約し、Query Service では検索実行に専念させる。

## 10. 補足
- 関連画面：`備品検索画面(V300)`
- 関連ユースケース：`UC-001`、`UC-002`
- 関連機能要件：`FR-001`、`FR-002`
- `NOT_AVAILABLE` は備品検索画面用の集約条件値であり、内部状態 `PENDING_LENDING`、`LENT`、`UNAVAILABLE`、`DISPOSED` をまとめて「AVAILABLE 以外すべて」を意味する。ドメイン層の `EquipmentStatus.UNAVAILABLE`（故障等による利用停止）とは異なる概念であるため混同しないこと。
- 実装上のインターフェース名：`HfpElSas302SearchEquipmentApplicationService`
- 実装上の主な入出力：
  - 入力オブジェクト：`SearchEquipmentQueryServiceImpl.Request`
  - 出力オブジェクト：`SearchEquipmentQueryServiceImpl.Response`

---
