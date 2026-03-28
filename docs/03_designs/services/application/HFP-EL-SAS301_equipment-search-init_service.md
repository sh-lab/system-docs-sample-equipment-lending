# アプリケーションサービス設計書（Application Service）

## 1. サービスID・名称
- ID：`HFP-EL-SAS301_equipment-search-init_service`
- 名称：`備品検索初期表示サービス`

## 2. 役割と責務
- 備品検索画面の初期表示ユースケース境界を担う。
- 既定の貸出状態 `AVAILABLE` を適用し、初期検索結果と検索条件候補を取得する。
- 初期表示時の検索条件既定化を UI から分離する。

## 3. 目的・スコープ
- 目的：備品検索画面の初期表示で、貸出申請可能な備品一覧と種別候補を返却する。
- スコープ：
  - 対象画面：`備品検索画面(V300)`
  - 対象データ：貸出可能備品一覧、備品種別候補
  - 業務範囲：初期表示用参照

## 4. 前提条件・事後条件

### 4.1 前提条件
- 利用者としてログイン済みであること。
- 初期表示時は画面入力値を持たないことを前提とする。

### 4.2 事後条件
- 正常終了時：
  - 初期表示に必要な検索結果を返却し、貸出可能備品の一覧を表示できる。
- 異常終了時：
  - 更新処理は行われない。
  - UI へエラーが通知される。

## 5. 処理フロー概要
1. 初期表示要求を受け取る
2. 既定条件として `equipmentName = ""`、`equipmentType = ""`、`lendingStatus = "AVAILABLE"` を適用する
3. 備品検索用の取得処理を呼び出す
4. 検索結果を UI へ返却する

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
| なし | - | - | 初期表示専用 |

### 7.2 出力DTO
| 項目 | 型 | 必須 | 説明 |
|-----|----|-----|-----|
| equipmentItems | `List<SearchEquipmentQueryServiceImpl.EquipmentItem>` | ○ | 一覧表示用備品 |
| equipmentTypeOptions | `List<SearchEquipmentQueryServiceImpl.Option>` | ○ | 備品種別候補 |
| hasMoreThanLimit | boolean | ○ | 件数上限超過有無 |

## 8. 例外マッピング方針
- 業務例外：検索条件既定値で矛盾は発生しない想定である。
- システム例外：汎用エラーとして通知する。
- 参照：`02_architecture/error-handling.md`

## 9. トランザクション・整合性
- 参照専用サービスであり、状態変更は行わない。
- 初期表示と検索処理で同一の Query Service を利用し、検索結果仕様を統一する。

## 10. 補足
- 関連画面：`備品検索画面(V300)`
- 関連ユースケース：`UC-001`、`UC-002`
- 関連機能要件：`FR-001`、`FR-002`
- 実装上のインターフェース名：`HfpElSas301EquipmentSearchInitApplicationService`
- 実装上の主な出力オブジェクト：
  - `SearchEquipmentQueryServiceImpl.Response`

---
