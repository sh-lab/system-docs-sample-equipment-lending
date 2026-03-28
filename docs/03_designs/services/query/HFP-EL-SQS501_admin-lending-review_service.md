# クエリサービス設計書（Query Service）

## 1. サービスID・名称
- ID：`HFP-EL-SQS501_admin-lending-review_service`
- 名称：`管理者承認・却下・返却確認情報取得サービス`

---

## 2. 役割と責務
- 本サービスは読み取り専用で、管理者承認・却下・返却確認画面(V500) の表示用データを取得する。
- `V200` で選択された貸出申請 1 件の詳細と対象備品一覧を返却する。
- Query Repository から取得した状態コードおよび備品種別コードを画面表示用ラベルへ変換する。
- 書き込みや状態変更は行わない。

---

## 3. 目的・スコープ
- **目的**：`V500` に必要な選択中申請詳細を返却する。
- **対象データ**：選択中申請詳細、対象備品一覧
- **利用シーン**：`管理者承認・却下・返却確認画面(V500)` 初期表示

---

## 4. 入力仕様（検索条件）

### 4.1 条件一覧
| 条件名 | 型 | 必須 | マッチ種別 | 備考 |
|--------|----|------|------------|------|
| adminUserId | string | ○ | 完全一致 | 認証文脈上の管理者ユーザーID |
| lendingRequestId | long | ○ | 完全一致 | `V200` で選択された貸出申請ID |

### 4.2 バリデーション
- 型・必須の検証は Controller 層で完了している前提とする。
- `lendingRequestId` は必須であり、既定選択は行わない。

---

## 5. 並び替え・ページング

### 5.1 ソート
- 選択中申請の対象備品一覧は `EQUIPMENT_CODE` 昇順で返却する。

### 5.2 ページング
- ページングは行わない。

---

## 6. 出力仕様（DTO）

### 6.1 画面 DTO
| 項目名 | 型 | 必須 | 説明 |
|--------|----|------|------|
| selectedRequest | `FindAdminLendingReviewQueryServiceImpl.Detail` | ○ | 選択中申請詳細 |
| mode | string | ○ | `APPROVAL_REVIEW` または `RETURN_CONFIRM` |

### 6.2 申請詳細 DTO
| 項目名 | 型 | 必須 | 説明 |
|--------|----|------|------|
| lendingRequestId | long | ○ | 貸出申請ID |
| applicantUserId | string | ○ | 申請者ユーザーID |
| statusCode | string | ○ | `PENDING_APPROVAL` または `PENDING_RETURN_CONFIRMATION` |
| statusLabel | string | ○ | 状態表示ラベル |
| requestComment | string | 任意 | 申請コメント |
| reviewComment | string | 任意 | 承認または却下コメント |
| returnRequestComment | string | 任意 | 返却申請コメント |
| returnConfirmComment | string | 任意 | 返却確認コメント |
| requestedAt | datetime | 任意 | 申請日時 |
| reviewedAt | datetime | 任意 | 承認または却下日時 |
| returnRequestedAt | datetime | 任意 | 返却申請日時 |
| version | long | ○ | 楽観ロック用バージョン |
| equipmentItems | `List<EquipmentSummaryDto>` | ○ | 対象備品一覧 |

---

## 7. 処理フロー概要（擬似コード可）
1. 管理者ユーザーIDと選択対象申請IDを受け取る。
2. 指定申請IDの申請詳細を取得する。
3. 対象申請の現在状態が `PENDING_APPROVAL` または `PENDING_RETURN_CONFIRMATION` であることを確認する。
4. 対象備品一覧を取得する。
5. 取得した状態コードおよび備品種別コードを表示ラベルへ変換する。
6. 画面表示用 DTO に整形して返却する。

---

## 8. 使用するコンポーネント
- **Query Repository**：`AdminLendingReviewQueryRepository`
- **補足**：`Query Repository` は状態コード・備品種別コードを返却し、表示ラベル化は本 `Query Service` が担う。

---

## 9. 例外とエラー方針
- 業務例外：指定申請IDが存在しない場合は表示不可として通知する。
- 業務例外：選択申請の状態が `PENDING_APPROVAL` または `PENDING_RETURN_CONFIRMATION` でない場合は表示不可として通知する。
- データアクセス障害：システム例外として扱う。

参照：`02_architecture/error-handling.md`

---

## 10. 非機能要件への配慮（該当時）
- 性能：単一申請の詳細と対象備品一覧に限定して取得する。
- 一貫性：`V200` で選択された申請に対する詳細のみを返却し、画面責務を明確化する。

---

## 11. 禁止事項
- 副作用を行わない。
- 申請状態や備品状態の変更を行わない。
- Command Service を呼び出さない。

---

## 12. 補足
- 関連アプリケーションサービス：`管理者承認・却下・返却確認画面初期表示サービス(SAS501)`
- 関連画面：`管理者承認・却下・返却確認画面(V500)`
- 実装上のインターフェース名：`FindAdminLendingReviewQueryService`

---
