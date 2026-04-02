# アプリケーションサービス設計書（Application Service）

## 1. サービスID・名称
- ID：`HFP-EL-SAS601_admin-equipment-search-init_service`
- 名称：`管理者備品検索初期表示サービス`

## 2. 役割と責務
- `管理者備品検索画面(V600)` の初期表示ユースケース境界を担う。
- 条件未指定の既定条件を適用し、管理者向け備品一覧と検索候補を取得する。
- UI から Query Service を直接呼び出させず、初期表示の入口を一本化する。

## 3. 目的・スコープ
- 目的：管理者備品検索画面の初期表示に必要な検索結果と候補を返却する。
- スコープ：
  - 対象画面：`管理者備品検索画面(V600)`
  - 対象データ：備品一覧、備品種別マスタ候補、備品状態候補
  - 業務範囲：初期表示用参照

## 4. 前提条件・事後条件

### 4.1 前提条件
- 認証済みの管理者であること。
- 初期表示時は画面入力値を持たないことを前提とする。

### 4.2 事後条件
- 正常終了時：
  - 管理者備品検索画面の初期表示に必要な一覧データを返却する。
- 異常終了時：
  - 更新処理は行われない。
  - UI へエラーが通知される。

## 5. 処理フロー概要
1. 初期表示要求を受け取る。
2. 既定条件として `equipmentName = ""`、`equipmentType = ""`、`equipmentStatus = "ALL"`、`systemRegisteredDate = null` を適用する。
3. 管理者備品検索用の取得処理を呼び出す。
4. 検索結果を UI へ返却する。

## 6. 内部で使用するサービス
- Query Service：
  - `管理者備品検索情報取得サービス(SQS601)`
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
| equipmentItems | `List<AdminEquipmentSearchQueryServiceImpl.EquipmentItem>` | ○ | 一覧表示用備品 |
| equipmentTypeOptions | `List<AdminEquipmentSearchQueryServiceImpl.Option>` | ○ | 備品種別マスタから取得した検索条件プルダウン候補 |
| equipmentStatusOptions | `List<AdminEquipmentSearchQueryServiceImpl.Option>` | ○ | 検索条件プルダウンに表示する備品状態候補 |
| hasMoreThanLimit | boolean | ○ | 件数上限超過有無 |

## 8. 例外マッピング方針
- 業務例外：既定条件での表示では業務例外を想定しない。
- システム例外：汎用エラーとして通知する。
- 参照：`02_architecture/error-handling.md`

## 9. トランザクション・整合性
- 参照専用サービスであり、状態変更は行わない。
- 初期表示と検索実行で同一の Query Service を利用し、検索結果仕様を統一する。

## 10. 補足
- 関連画面：`管理者備品検索画面(V600)`
- 関連ユースケース：`UC-007`
- 関連機能要件：`FR-008`
- 実装上のインターフェース名：`HfpElSas601AdminEquipmentSearchInitApplicationService`

---
