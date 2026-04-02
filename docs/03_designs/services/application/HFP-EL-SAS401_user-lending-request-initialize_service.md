# アプリケーションサービス設計書（Application Service）

## 1. サービスID・名称
- ID：`HFP-EL-SAS401_user-lending-request-initialize_service`
- 名称：`利用者貸出申請・返却画面初期表示サービス`

## 2. 役割と責務
- 「利用者貸出申請・返却画面(V400)」初期表示時に、遷移元や申請状態に応じて画面モード・申請概要・対象備品一覧・入力可能項目を組み立てて返却する。
- トランザクション境界の管理と例外の分類・変換を行う。
- UIは本サービスのみを呼び出す。

## 3. 目的・スコープ
- 目的：利用者が貸出申請・返却申請・却下確認を行う画面の初期表示に必要な情報を提供する。
- スコープ：
  - 対象画面：`利用者貸出申請・返却画面(V400)`
  - 対象データ：貸出申請、貸出申請明細、備品
  - 業務範囲：遷移元・申請状態に応じた画面モード判定、申請概要・備品一覧・コメント等の取得と表示用 DTO の組み立て

## 4. 前提条件・事後条件

### 4.1 前提条件
- 認証済みの利用者本人であること。
- 入力値の型・必須・形式検証は Controller 層で完了している前提とする。

### 4.2 事後条件
- 正常終了時：
  - 画面モードが以下のいずれかに判定され、対応する表示用 DTO が返却される。
    - `LENDING`：備品検索画面(V300) からの遷移。選択備品の一覧を表示する。
    - `RETURN`：貸出中(`LENT`) または 返却確認待ち(`PENDING_RETURN_CONFIRMATION`) の申請に対し、返却申請フォームを表示する。
    - `REJECTED_CONFIRM`：却下済み(`REJECTED`) の申請に対し、却下確認画面を表示する。
  - 画面モードに応じて操作ボタンの活性状態（`actionEnabled`）が制御される。
- 異常終了時：
  - 申請 ID 未指定、申請者不一致、備品の欠損等は業務例外（`MSG_E_004`）として通知する。
  - 貸出申請時に備品 ID の欠損がある場合は業務例外（`MSG_E_001`）として通知する。

## 5. 処理フロー概要

### 5.1 貸出申請モード（遷移元 = V300）
1. 備品 ID リストを受け取る。
2. `備品ID指定取得サービス(SQS001)` から備品情報を取得する。
3. 取得件数と入力件数が一致しない場合は `MSG_E_001` を送出する。
4. 画面モード `LENDING` で表示用 DTO を組み立てて返却する。

### 5.2 既存申請モード（遷移元 = V100 等）
1. 申請 ID を受け取る。申請 ID が未指定の場合は `MSG_E_004` を送出する。
2. `貸出申請ID検索サービス(SQS002)` から申請情報と備品 ID 一覧を取得する。
3. 認証利用者と申請者が一致しない場合は `MSG_E_004` を送出する。
4. `備品ID指定取得サービス(SQS001)` から備品情報を取得する。
5. 取得件数と申請明細の件数が一致しない場合は `MSG_E_004` を送出する。
6. 申請ステータスに応じて画面モード（`RETURN` / `REJECTED_CONFIRM` / `LENDING`）と操作ボタンの活性状態を判定する。
7. ステータスラベルを国際化メッセージから解決する。
8. 表示用 DTO を組み立てて返却する。

## 6. 内部で使用するサービス
- Query Service：
  - `備品ID指定取得サービス(SQS001)` — 備品情報取得
  - `貸出申請ID検索サービス(SQS002)` — 申請情報・備品ID一覧取得
- Command Service：
  - なし
- Pure Service：
  - なし（画面モード判定・表示用データ組み立ては本サービス内の private メソッドで実装）

## 7. 入出力

### 7.1 入力
| 項目 | 型 | 必須 | 備考 |
|-----|----|-----|-----|
| userId | `String` | ○ | 認証利用者ID |
| from | `String` | ○ | 遷移元画面（`"V300"`: 備品検索、`"V100"`: マイページ等） |
| requestId | `Long` | △ | 貸出申請ID（既存申請モード時に必須） |
| equipmentIds | `List<Long>` | △ | 備品IDリスト（貸出申請モード時に必須） |

### 7.2 出力（`UserLendingRequestViewData`）
| 項目 | 型 | 説明 |
|-----|----|------|
| mode | `LendingRequestScreenMode` | 画面モード（`LENDING` / `RETURN` / `REJECTED_CONFIRM`） |
| actionEnabled | `boolean` | 操作ボタンの活性状態 |
| backToSearch | `boolean` | 備品検索画面への戻りリンク表示 |
| backToMypage | `boolean` | マイページへの戻りリンク表示 |
| lendingRequestId | `Long` | 貸出申請ID（貸出申請モード時は `null`） |
| statusLabel | `String` | ステータス表示ラベル（国際化済み。貸出申請モード時は `null`） |
| requestedAt | `String` | 申請日時（`yyyy-MM-dd HH:mm` 形式。貸出申請モード時は `null`） |
| reviewedAt | `String` | 審査日時（同上） |
| returnRequestedAt | `String` | 返却申請日時（同上） |
| requestComment | `String` | 申請コメント |
| returnRequestComment | `String` | 返却申請コメント |
| adminComment | `String` | 管理者コメント（審査コメント） |
| version | `Integer` | 楽観ロック用バージョン（貸出申請モード時は `null`） |
| equipmentIds | `List<Long>` | 対象備品ID一覧 |
| equipmentItems | `List<UserLendingRequestEquipmentDto>` | 対象備品の表示用一覧（下表参照） |

**`UserLendingRequestEquipmentDto`**
| 項目 | 型 | 説明 |
|-----|----|------|
| equipmentId | `Long` | 備品ID |
| equipmentCode | `String` | 備品コード |
| equipmentName | `String` | 備品名称 |
| equipmentTypeLabel | `String` | 備品種別ラベル |
| storageLocation | `String` | 保管場所 |

## 8. 例外マッピング方針
- 業務例外：
  - 貸出申請モードで備品の欠損がある場合は `MSG_E_001` を通知対象とする。
  - 既存申請モードで申請ID未指定・申請者不一致・備品欠損の場合は `MSG_E_004` を通知対象とする。
- システム例外：永続化失敗や想定外障害は汎用エラーとして通知する。
- 参照：`02_architecture/error-handling.md`

## 9. トランザクション・整合性
- トランザクション境界は Application Service とする。
- 本サービスは読み取り専用であり、データの更新は行わない。
- 分離レベルは Read Committed とする。

## 10. 補足
- 関連画面：`利用者貸出申請・返却画面(V400)`
- 関連ユースケース：`UC-002`、`UC-004`、`UC-006`
- 関連機能要件：`FR-002`、`FR-004`、`FR-006`
- 関連エンティティ：
  - `HFP-EL-E003_lending-request`
  - `HFP-EL-E001_equipment`
- 関連内部サービス：
  - `備品ID指定取得サービス(SQS001)`
  - `貸出申請ID検索サービス(SQS002)`
- 実装上のインターフェース名：`HfpElSas401UserLendingRequestInitializeApplicationService`
- 実装上の主な入出力：
  - 入力：メソッド引数（`String userId`, `String from`, `Long requestId`, `List<Long> equipmentIds`）
  - 出力：`UserLendingRequestViewData`

---
