# クエリサービス設計書（Query Service）

## 1. サービスID・名称
- ID：`HFP-EL-SQS002_find-lending-request-by-id_service`
- 名称：`貸出申請ID検索サービス`

---

## 2. 役割と責務
- 本サービスは、指定された貸出申請IDに対応する貸出申請情報と対象備品ID一覧を読み取り専用で取得する。
- 利用者貸出申請・返却画面(V400) の初期表示に必要な申請概要情報の取得を担う。
- 書き込みや状態変更は行わない。
- トランザクション境界は管理しない。

---

## 3. 目的・スコープ
- 目的：貸出申請IDから、申請概要と対象備品ID一覧を DTO として取得する。
- 利用元：
  - `利用者貸出申請・返却画面初期表示サービス(SAS401)`
- 対象業務概念：貸出申請の参照

---

## 4. 入力・出力仕様

### 4.1 入力
| 項目名 | 型 | 必須 | 備考 |
|--------|----|------|------|
| lendingRequestId | `long` | ○ | 対象貸出申請ID |

### 4.2 出力
| 項目名 | 型 | 必須 | 説明 |
|--------|----|------|------|
| lendingRequestId | `long` | ○ | 貸出申請ID |
| applicantUserId | `String` | ○ | 申請者ユーザーID |
| statusCode | `String` | ○ | 申請ステータスコード |
| requestedAt | `LocalDateTime` | ○ | 申請日時 |
| reviewedAt | `LocalDateTime` | △ | 承認・却下日時 |
| returnRequestedAt | `LocalDateTime` | △ | 返却申請日時 |
| requestComment | `String` | △ | 申請コメント |
| returnRequestComment | `String` | △ | 返却申請コメント |
| reviewComment | `String` | △ | 管理者コメント |
| version | `int` | ○ | 楽観ロック用バージョン |
| equipmentIds | `List<Long>` | ○ | 対象備品ID一覧 |

---

## 5. 処理内容概要
1. 指定された貸出申請IDで貸出申請エンティティを検索する。
2. 該当する申請が存在しない場合は業務例外を送出する。
3. 同じ貸出申請IDに紐づく備品ID一覧を取得する。
4. エンティティの各フィールドと備品ID一覧を DTO へ変換して返却する。

---

## 6. 使用するコンポーネント
- **Entity Repository**：
  - `LendingRequestRepository`：貸出申請エンティティの取得、備品ID一覧の取得

---

## 7. 例外とエラー方針
- 業務例外：指定IDの申請が存在しない場合は `MSG_E_002` を送出する。
- システム例外：データアクセス障害や想定外障害はシステム例外として扱う。
- 例外の捕捉・変換・通知は呼び出し元のアプリケーションサービスの責務とする。

参照：`02_architecture/error-handling.md`

---

## 8. 不変条件
- 同一の入力に対して、同一時点の永続状態に基づく申請情報を返すこと。
- 入力値を変更しないこと。
- 副作用を持たないこと。

---

## 9. 禁止事項
- 申請状態の更新を行わない。
- 業務ルール（権限チェック、状態遷移判定等）を実装しない。
- Command Service を呼び出さない。
- トランザクションを開始・制御しない。

---

## 10. 補足
- 本サービスは申請情報の参照のみを担当し、権限チェックや状態判定は利用元の Application Service で行う。
- 関連アプリケーションサービス：`利用者貸出申請・返却画面初期表示サービス(SAS401)`
- 関連エンティティ：`HFP-EL-E003_lending-request`
- 実装上のインターフェース名：`FindLendingRequestByIdQueryService`

---
