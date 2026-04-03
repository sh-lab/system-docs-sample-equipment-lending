# アプリケーションサービス設計書（Application Service）

## 1. サービスID・名称
- ID：`HFP-EL-SAS701_admin-equipment-edit-init_service`
- 名称：`管理者備品編集画面初期表示サービス`

## 2. 役割と責務
- `管理者備品編集画面(V700)` の初期表示ユースケース境界を担う。
- 画面表示に必要な備品情報、備品種別マスタ候補、状態候補を取得して UI へ返却する。

## 3. 目的・スコープ
- 目的：管理者が備品登録または備品情報更新を開始できる表示データを返す。
- スコープ：
  - 対象画面：`管理者備品編集画面(V700)`
  - 対象データ：選択中備品、備品種別マスタ候補、備品状態候補、表示用システム登録日
  - 業務範囲：初期表示用参照処理の呼び出し

## 4. 前提条件・事後条件

### 4.1 前提条件
- 管理者としてログイン済みであること。
- 画面モードは `create` または `edit` のいずれかであること。
- `編集モード` の場合は対象備品IDが指定されていること。

### 4.2 事後条件
- 正常終了時：
  - 画面モードに応じた初期表示データを返却する。
- 異常終了時：
  - 更新処理は行われない。
  - UI へエラーが通知される。

## 5. 処理フロー概要
1. 画面モードと対象備品IDを受け取る。
2. `管理者備品編集情報取得サービス(SQS701)` を呼び出し、モードに応じた表示情報を取得する。
3. 取得結果をそのまま UI 表示用 DTO として返却する。

## 6. 内部で使用するサービス
- Query Service：
  - `管理者備品編集情報取得サービス(SQS701)`
- Command Service：
  - なし
- Pure Service：
  - なし

## 7. 入出力DTO

### 7.1 入力DTO
| 項目 | 型 | 必須 | 備考 |
|-----|----|-----|-----|
| mode | string | ○ | `create` または `edit` |
| equipmentId | long | 条件付き必須 | `edit` の場合に指定する |

### 7.2 出力DTO
| 項目 | 型 | 必須 | 説明 |
|-----|----|-----|-----|
| mode | string | ○ | `create` または `edit` |
| displaySystemRegisteredDate | date | ○ | `create` 時は当日、`edit` 時は既存値 |
| equipmentDetail | `FindAdminEquipmentEditQueryService.EquipmentDetail` | 任意 | `edit` 時の表示対象備品 |
| equipmentTypeOptions | `List<FindAdminEquipmentEditQueryService.Option>` | ○ | 備品種別マスタから取得した登録時候補 |
| statusOptions | `List<FindAdminEquipmentEditQueryService.Option>` | ○ | 状態選択肢 |

## 8. 例外マッピング方針
- 業務例外：対象備品不存在、参照不可を通知する。
- システム例外：データアクセス障害などは汎用エラーとして通知する。
- 参照：`02_architecture/error-handling.md`

## 9. トランザクション・整合性
- 参照専用サービスであり、状態変更は行わない。
- 参照データ取得の詳細は Query Service に委譲し、本サービスはユースケース境界として結果を返却する。

## 10. 補足
- 関連画面：`管理者備品編集画面(V700)`
- 関連ユースケース：`UC-008`、`UC-009`
- 関連機能要件：`FR-009`、`FR-010`
- 実装上のインターフェース名：`HfpElSas701AdminEquipmentEditInitApplicationService`

---
