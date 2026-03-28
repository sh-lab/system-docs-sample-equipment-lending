# テーブル設計書（Table Design）

## 0. 本設計書の記載ルール（命名規則）
- テーブル名およびカラム名の物理名は、**すべて大文字のスネークケース**とする。
- 本書では、DB製品の識別子正規化ルールを考慮し、実体に最も近い表記として大文字で記載する。

---

## 1. テーブルID・名称
- テーブルID：`T_LENDING_REQUEST`
- 論理名：`貸出申請`
- 物理名：`T_LENDING_REQUEST`

---

## 2. 目的・概要
- 本テーブルは、`HFP-EL-E003_lending-request` の本体情報を永続化するためのテーブルである。
- 業務的な意味、不変条件、状態遷移は `docs/03_designs/entity/HFP-EL-E003_lending-request.md` を参照する。

---

## 3. 対応エンティティ・関連
- 対応エンティティ：`HFP-EL-E003_lending-request`
- 関連エンティティ：
  - `HFP-EL-E002_user：N:1`
  - `T_LENDING_REQUEST_DETAIL：1:N`
  - `H_LENDING_REQUEST_HISTORY：1:N`

---

## 4. カラム定義

| 論理名 | 物理名 | データ型 | NOT NULL | 主キー | 初期値 | 説明 |
|--------|--------|----------|----------|--------|--------|------|
| 貸出申請ID | LENDING_REQUEST_ID | BIGINT | ○ | PK | シーケンス採番 | 貸出申請を一意に識別する内部ID |
| 申請者ユーザーID | APPLICANT_USER_ID | CHAR(6) | ○ | - | - | 申請を行った利用者のユーザーID |
| 承認または却下者ユーザーID | REVIEWED_BY_USER_ID | CHAR(6) | - | - | - | 承認または却下を行った管理者のユーザーID |
| 返却確認者ユーザーID | RETURN_CONFIRMED_BY_USER_ID | CHAR(6) | - | - | - | 返却確認を行った管理者のユーザーID |
| 申請状態コード | STATUS_CODE | VARCHAR(30) | ○ | - | `PENDING_APPROVAL` | 申請の現在状態を表すコード値 |
| 申請コメント | REQUEST_COMMENT | VARCHAR(500) | - | - | - | 利用者が申請時に入力するコメント |
| 承認または却下コメント | REVIEW_COMMENT | VARCHAR(500) | - | - | - | 管理者が承認または却下時に入力するコメント |
| 返却申請コメント | RETURN_REQUEST_COMMENT | VARCHAR(500) | - | - | - | 利用者が返却申請時に入力するコメント |
| 返却確認コメント | RETURN_CONFIRM_COMMENT | VARCHAR(500) | - | - | - | 管理者が返却確認時に入力するコメント |
| 申請日時 | REQUESTED_AT | TIMESTAMP | ○ | - | - | 貸出申請を登録した日時 |
| 承認または却下日時 | REVIEWED_AT | TIMESTAMP | - | - | - | 承認または却下を確定した日時 |
| 返却申請日時 | RETURN_REQUESTED_AT | TIMESTAMP | - | - | - | 利用者が返却申請を行った日時 |
| 完了日時 | COMPLETED_AT | TIMESTAMP | - | - | - | 却下確認または返却確認により完了した日時 |

※ 物理名は大文字スネークケースで記載する。

---

## 5. 主キー・一意制約
- 主キー：
  - `PK_T_LENDING_REQUEST`（`LENDING_REQUEST_ID`）
- 一意制約：
  - なし
- 採番方式：
  - `LENDING_REQUEST_ID` は `SEQ_T_LENDING_REQUEST_ID` で採番する。

---

## 6. 外部キー制約（必要な場合）

現時点では定義しない。

ユーザーおよび備品との関連は設計上の参照関係として扱い、テーブル制約としての外部キーは設けない。

---

## 7. インデックス定義

| インデックス名 | カラム | ユニーク | 目的 |
|----------------|--------|----------|------|
| IDX_T_LENDING_REQUEST_01 | APPLICANT_USER_ID, STATUS_CODE | × | 利用者マイページの申請一覧検索 |
| IDX_T_LENDING_REQUEST_02 | STATUS_CODE | × | 承認待ち・返却確認待ち一覧の抽出 |
| IDX_T_LENDING_REQUEST_03 | REVIEWED_BY_USER_ID | × | 管理者処理履歴の参照効率向上 |

---

## 8. 監査カラム（方針）

> 監査カラムの値はシステム側で自動設定する。
> ユーザー入力は禁止する。

| 論理名 | 物理名 | データ型 | NOT NULL | 説明 |
|--------|--------|----------|----------|------|
| 作成日時 | CREATED_AT | TIMESTAMP | ○ | 登録日時 |
| 作成者 | CREATED_BY | VARCHAR(64) | ○ | 登録ユーザー |
| 更新日時 | UPDATED_AT | TIMESTAMP | ○ | 更新日時 |
| 更新者 | UPDATED_BY | VARCHAR(64) | ○ | 更新ユーザー |

---

## 9. 排他制御・バージョニング

- 併存更新制御として **楽観ロック** を採用する。
- バージョン管理用カラムを設ける。

| 論理名 | 物理名 | データ型 | NOT NULL | 説明 |
|--------|--------|----------|----------|------|
| バージョン | VERSION | INT | ○ | 更新回数 |

※ 更新時の不一致は業務例外として扱う。

---

## 10. データ正規化・注意事項
- 正規化レベルは第3正規形を原則とする。
- 明細は `T_LENDING_REQUEST_DETAIL` に分離し、申請本体に繰り返し項目を持ち込まない。
- enum 相当の値はコード値で保持し、表示名はアプリケーション側で解決する。
- `STATUS_CODE` は `PENDING_APPROVAL`、`LENT`、`PENDING_RETURN_CONFIRMATION`、`REJECTED`、`COMPLETED` を許可値とする。
- `LENDING_REQUEST_ID` はアプリケーション側で連番計算せず、データベースシーケンスから取得する。
- 備品一覧やユーザー表示名などの画面都合データは本テーブルに持ち込まない。
- 操作履歴の追跡に必要な `operationId`、`commandServiceId`、`operatedAt` は `H_LENDING_REQUEST_HISTORY` に分離し、本テーブルへは保持しない。

---

## 11. マイグレーション・変更管理
- DDL 変更は履歴管理されたマイグレーション手順で行う。
- 既存データへの影響確認を必須とする。
- 申請状態体系を変更する場合は、既存コード値との移行方針を別途設計する。

---

## 12. 禁止事項
- 業務ルールや状態遷移の記述を含めない。
- 画面都合の項目や表示順序を持ち込まない。
- トランザクション境界や例外処理方針を記載しない。
- Repository / Application Service の実装詳細を書かない。
