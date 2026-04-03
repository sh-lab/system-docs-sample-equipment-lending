# クエリサービス設計書（Query Service）

## 1. サービスID・名称
- ID：`HFP-EL-SQS201_find-admin-mypage_service`
- 名称：`管理者マイページ情報取得サービス`

---

## 2. 役割と責務
- 本サービスは読み取り専用で、管理者マイページ(V200) の一覧表示用データを取得する。
- 承認待ち申請一覧と返却確認待ち申請一覧を表示用結果としてまとめる。
- Query Repository から取得した状態コードを画面表示用ラベルへ変換する。
- 書き込みや状態変更は行わない。

---

## 3. 目的・スコープ
- **目的**：管理者マイページに必要な申請一覧情報を返却する。
- **対象データ**：承認待ち申請一覧、返却確認待ち申請一覧
- **利用シーン**：`管理者マイページ(V200)` 初期表示

---

## 4. 入力仕様（検索条件）

### 4.1 条件一覧
| 条件名 | 型 | 必須 | マッチ種別 | 備考 |
|--------|----|------|------------|------|
| adminUserId | string | ○ | 完全一致 | 認証文脈上の管理者ユーザーID |

### 4.2 バリデーション
- 型・必須の検証は Controller 層で完了している前提とする。
- 実際の絞り込み条件は申請状態を主とし、管理者個人の所有物としては扱わない。

---

## 5. 並び替え・ページング

### 5.1 ソート
- 承認待ち申請一覧は `REQUESTED_AT` 昇順で返却する。
- 返却確認待ち申請一覧は `RETURN_REQUESTED_AT` 昇順で返却する。

### 5.2 ページング
- ページングは行わない。

---

## 6. 出力仕様（DTO）

### 6.1 一覧 DTO
| 項目名 | 型 | 必須 | 説明 |
|--------|----|------|------|
| pendingApprovalRequests | `List<FindAdminMypageQueryService.RequestItem>` | ○ | 承認待ち申請一覧 |
| pendingReturnRequests | `List<FindAdminMypageQueryService.RequestItem>` | ○ | 返却確認待ち申請一覧 |

### 6.2 明細 DTO
| 項目名 | 型 | 必須 | 説明 |
|--------|----|------|------|
| lendingRequestId | long | ○ | 貸出申請ID |
| applicantUserId | string | ○ | 申請者ユーザーID |
| requestComment | string | 任意 | 申請コメント |
| returnRequestComment | string | 任意 | 返却申請コメント |
| requestedAt | datetime | 任意 | 申請日時 |
| returnRequestedAt | datetime | 任意 | 返却申請日時 |
| statusLabel | string | ○ | 状態表示ラベル |

---

## 7. 処理フロー概要（擬似コード可）
1. 管理者ユーザーIDを受け取る。
2. 状態 `PENDING_APPROVAL` の申請一覧を取得する。
3. 状態 `PENDING_RETURN_CONFIRMATION` の申請一覧を取得する。
4. 取得した状態コードを表示ラベルへ変換する。
5. 一覧表示用 DTO に整形して返却する。

---

## 8. 使用するコンポーネント
- **Query Repository**：`AdminMypageQueryRepository`
- **補足**：`Query Repository` は状態コードを返却し、表示ラベル化は本 `Query Service` が担う。

---

## 9. 例外とエラー方針
- 業務例外：一覧対象が 0 件でも例外とせず、空一覧で返却する。
- データアクセス障害：システム例外として扱う。

参照：`02_architecture/error-handling.md`

---

## 10. 非機能要件への配慮（該当時）
- 性能：一覧表示に必要なサマリ項目へ限定して取得する。
- 操作性：2 種類の一覧を同時返却し、管理者が処理対象を切り替えやすくする。

---

## 11. 禁止事項
- 副作用を行わない。
- 申請状態の変更や更新を行わない。
- Command Service を呼び出さない。

---

## 12. 補足
- 関連アプリケーションサービス：`管理者マイページ初期表示サービス(SAS201)`
- 関連画面：`管理者マイページ(V200)`
- 実装上のインターフェース名：`FindAdminMypageQueryService`

---
