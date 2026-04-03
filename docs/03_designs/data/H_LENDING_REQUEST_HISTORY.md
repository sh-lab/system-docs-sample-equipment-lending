# テーブル設計書（Table Design）

## 0. 本設計書の記載ルール（命名規則）
- テーブル名およびカラム名の物理名は、**すべて大文字のスネークケース**とする。
- 本書では、DB製品の識別子正規化ルールを考慮し、実体に最も近い表記として大文字で記載する。

---

## 1. テーブルID・名称
- テーブルID：`H_LENDING_REQUEST_HISTORY`
- 論理名：`貸出申請操作履歴`
- 物理名：`H_LENDING_REQUEST_HISTORY`

---

## 2. 目的・概要
- 本テーブルは、`T_LENDING_REQUEST` に対するコマンド実行の履歴を `operationId` 単位で追跡するための履歴テーブルである。
- 1 回のコマンドサービス実行で `T_LENDING_REQUEST` に変更が確定した場合、対象貸出申請 1 件につき 1 行を追加する。
- 業務的な意味、不変条件、状態遷移は `docs/03_designs/entity/HFP-EL-E003_lending-request.md` を参照する。

---

## 3. 対応エンティティ・関連
- 対応エンティティ：`HFP-EL-E003_lending-request`
- 関連テーブル：
  - `T_LENDING_REQUEST：N:1`

---

## 4. カラム定義

| 論理名 | 物理名 | データ型 | NOT NULL | 主キー | 初期値 | 説明 |
|--------|--------|----------|----------|--------|--------|------|
| 操作ID | OPERATION_ID | CHAR(36) | ○ | PK | - | Application Service 呼び出し単位で framework 共通部が設定する UUID |
| 貸出申請ID | LENDING_REQUEST_ID | BIGINT | ○ | PK | - | 変更が確定した貸出申請の主キー |
| コマンドサービスID | COMMAND_SERVICE_ID | VARCHAR(64) | ○ | - | - | 履歴登録を行ったコマンドサービスの設計ID |
| 操作時刻 | OPERATED_AT | TIMESTAMP | ○ | - | - | 当該 `operationId` の履歴登録処理で使用した時刻 |

※ 物理名は大文字スネークケースで記載する。

---

## 5. 主キー・一意制約
- 主キー：
  - `PK_H_LENDING_REQUEST_HISTORY`（`OPERATION_ID`, `LENDING_REQUEST_ID`）
- 一意制約：
  - なし

---

## 6. 外部キー制約（必要な場合）

現時点では定義しない。

貸出申請本体との関連は設計上の参照関係として扱い、テーブル制約としての外部キーは設けない。

---

## 7. インデックス定義

| インデックス名 | カラム | ユニーク | 目的 |
|----------------|--------|----------|------|
| IDX_H_LENDING_REQUEST_HISTORY_01 | LENDING_REQUEST_ID, OPERATED_AT | × | 貸出申請単位での履歴参照効率向上 |
| IDX_H_LENDING_REQUEST_HISTORY_02 | COMMAND_SERVICE_ID, OPERATED_AT | × | コマンドサービス単位での追跡効率向上 |

---

## 8. 監査カラム（方針）

- 本テーブルは append-only の履歴テーブルであるため、共通の監査カラムは追加しない。
- 操作時刻は `OPERATED_AT` で管理する。

| 論理名 | 物理名 | データ型 | NOT NULL | 説明 |
|--------|--------|----------|----------|------|
| なし | - | - | - | `OPERATED_AT` を履歴登録時刻として用いる |

---

## 9. 排他制御・バージョニング

- 本テーブルは追記専用であり、バージョン管理用カラムを持たない。
- 排他制御は元テーブル側の `VERSION` により実施する。

| 論理名 | 物理名 | データ型 | NOT NULL | 説明 |
|--------|--------|----------|----------|------|
| なし | - | - | - | 元テーブル側で制御する |

---

## 10. データ正規化・注意事項
- 履歴は `operationId` と貸出申請主キーの組合せで一意に管理する。
- 同一業務操作で複数の貸出申請を更新する場合は、同一 `operationId` で対象件数分の行を追加する。
- 同一業務操作内で同一貸出申請を複数回更新した場合でも、履歴登録は変更確定後の貸出申請単位で 1 行とする。
- `COMMAND_SERVICE_ID` には `HFP-EL-SCS001_register-lending-request_service`、`HFP-EL-SCS002_register-return-request_service`、`HFP-EL-SCS003_confirm-rejected-request_service`、`HFP-EL-SCS010_approve-lending-request_service`、`HFP-EL-SCS011_reject-lending-request_service`、`HFP-EL-SCS012_return-confirm_service` のいずれかの設計IDを保持する。
- 本テーブルは変更差分やコメント本文の全文保存を目的とせず、どのコマンドサービスがどの貸出申請をいつ更新したかを追跡するために用いる。

---

## 11. マイグレーション・変更管理
- DDL 変更は履歴管理されたマイグレーション手順で行う。
- 既存データへの影響確認を必須とする。
- 履歴対象の拡張や記録項目の追加を行う場合は、既存 `operationId` との整合性を前提に移行方針を設計する。

---

## 12. 禁止事項
- 元テーブルの正データを代替する用途に用いない。
- 画面都合の表示用項目や検索専用の集計結果を持ち込まない。
- トランザクション境界や例外処理方針を記載しない。
- Repository / Application Service の実装詳細を書かない。
