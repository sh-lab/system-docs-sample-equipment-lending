# アプリケーションサービス設計書（Application Service）

## 1. サービスID・名称
- ID：`HFP-EL-SAS602_admin-equipment-search_service`
- 名称：`管理者備品検索実行サービス`

## 2. 役割と責務
- `管理者備品検索画面(V600)` の検索実行ユースケース境界を担う。
- 画面入力された検索条件を正規化し、Query Service へ委譲する。
- 備品状態の許可値を制御し、不正値は `ALL` に補正する。

## 3. 目的・スコープ
- 目的：管理者が入力した条件で備品を検索し、表示用の結果一覧を返す。
- スコープ：
  - 対象画面：`管理者備品検索画面(V600)`
  - 対象データ：備品一覧、備品種別マスタ候補、備品状態候補
  - 業務範囲：検索条件の正規化と参照処理呼び出し

## 4. 前提条件・事後条件

### 4.1 前提条件
- 管理者としてログイン済みであること。
- 入力値の型・必須検証は Controller 層で完了している前提とする。

### 4.2 事後条件
- 正常終了時：
  - 正規化済み条件に基づく検索結果を返却する。
- 異常終了時：
  - 更新処理は行われない。
  - UI へエラーが通知される。

## 5. 処理フロー概要
1. 備品名、備品種別、備品状態、システム登録日を受け取る。
2. 文字列条件の前後空白を除去し、未指定値は空文字または `null` へ正規化する。
3. `equipmentStatus` が `ALL`、`AVAILABLE`、`PENDING_LENDING`、`LENT`、`UNAVAILABLE`、`DISPOSED` 以外の場合は `ALL` を適用する。
4. 検索条件を検索処理へ引き渡せる形に整える。
5. 管理者備品検索用の取得処理を呼び出す。
6. 検索結果を UI へ返却する。

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
| equipmentName | string | 任意 | 前後空白を除去したうえで部分一致条件へ利用する |
| equipmentType | string | 任意 | 前後空白を除去したうえで完全一致条件へ利用する |
| equipmentStatus | string | 任意 | `ALL`、`AVAILABLE`、`PENDING_LENDING`、`LENT`、`UNAVAILABLE`、`DISPOSED` を許可する |
| systemRegisteredDate | date | 任意 | 完全一致条件へ利用する |

### 7.2 出力DTO
| 項目 | 型 | 必須 | 説明 |
|-----|----|-----|-----|
| equipmentItems | `List<AdminEquipmentSearchQueryServiceImpl.EquipmentItem>` | ○ | 一覧表示用備品 |
| equipmentTypeOptions | `List<AdminEquipmentSearchQueryServiceImpl.Option>` | ○ | 備品種別マスタから取得した検索条件プルダウン候補 |
| equipmentStatusOptions | `List<AdminEquipmentSearchQueryServiceImpl.Option>` | ○ | 検索条件プルダウンに表示する備品状態候補 |
| hasMoreThanLimit | boolean | ○ | 件数上限超過有無 |

## 8. 例外マッピング方針
- 業務例外：備品状態の不正値は例外とせず `ALL` へ補正する。
- システム例外：データアクセス障害などは汎用エラーとして通知する。
- 参照：`02_architecture/error-handling.md`

## 9. トランザクション・整合性
- 参照専用サービスであり、状態変更は行わない。
- 正規化ルールは Application Service に集約し、Query Service では検索実行に専念させる。

## 10. 補足
- 関連画面：`管理者備品検索画面(V600)`
- 関連ユースケース：`UC-007`
- 関連機能要件：`FR-008`
- 実装上のインターフェース名：`HfpElSas602AdminEquipmentSearchApplicationService`

---
