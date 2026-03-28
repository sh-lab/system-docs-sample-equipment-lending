# テーブル設計書（Table Design）

## 0. 本設計書の記載ルール（命名規則）
- テーブル名およびカラム名の物理名は、**すべて大文字のスネークケース**とする。
- 本書では、DB製品の識別子正規化ルールを考慮し、実体に最も近い表記として大文字で記載する。

---

## 1. テーブルID・名称
- テーブルID：`M_EQUIPMENT`
- 論理名：`備品マスタ`
- 物理名：`M_EQUIPMENT`

---

## 2. 目的・概要
- 本テーブルは、`HFP-EL-E001_equipment` の情報を永続化するためのテーブルである。
- 業務的な意味、不変条件、状態遷移は `docs/03_designs/entity/HFP-EL-E001_equipment.md` を参照する。

---

## 3. 対応エンティティ・関連
- 対応エンティティ：`HFP-EL-E001_equipment`
- 関連エンティティ：
  - `HFP-EL-E003_lending-request：1:N`
  - `H_EQUIPMENT_HISTORY：1:N`

---

## 4. カラム定義

| 論理名 | 物理名 | データ型 | NOT NULL | 主キー | 初期値 | 説明 |
|--------|--------|----------|----------|--------|--------|------|
| 備品ID | EQUIPMENT_ID | BIGINT | ○ | PK | - | 備品を一意に識別する内部ID |
| 備品管理番号 | EQUIPMENT_CODE | VARCHAR(30) | ○ | - | - | 運用上の管理番号 |
| 備品名 | EQUIPMENT_NAME | VARCHAR(100) | ○ | - | - | 利用者向け表示名 |
| 備品種別 | EQUIPMENT_TYPE | VARCHAR(40) | ○ | - | - | 検索条件に利用する粗い分類。数量差は `EQUIPMENT_NAME` で表現する |
| 保管場所 | STORAGE_LOCATION | VARCHAR(100) | ○ | - | - | 通常保管場所 |
| 備品状態コード | STATUS_CODE | VARCHAR(20) | ○ | - | `AVAILABLE` | `AVAILABLE`、`PENDING_LENDING`、`LENT`、`UNAVAILABLE`、`DISPOSED` のいずれかを表すコード値 |
| 備考 | REMARKS | VARCHAR(500) | - | - | - | 補足事項 |

※ 物理名は大文字スネークケースで記載する。

---

## 5. 主キー・一意制約
- 主キー：
  - `PK_M_EQUIPMENT`（`EQUIPMENT_ID`）
- 一意制約：
  - `UK_M_EQUIPMENT_01`（`EQUIPMENT_CODE`）

---

## 6. 外部キー制約（必要な場合）

現時点では定義しない。

関連マスタを分割しない方針であるため、備品種別・保管場所・状態コードは本テーブル内に保持する。

---

## 7. インデックス定義

| インデックス名 | カラム | ユニーク | 目的 |
|----------------|--------|----------|------|
| IDX_M_EQUIPMENT_01 | EQUIPMENT_NAME | × | 備品名検索の性能向上 |
| IDX_M_EQUIPMENT_02 | EQUIPMENT_TYPE, STATUS_CODE | × | 備品種別・貸出可否条件による検索の性能向上 |

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
- 初回設計では単一の備品マスタのみを対象とするため、備品種別・保管場所・状態コードは別マスタへ分割しない。
- enum 相当の値はコード値で保持し、表示名はアプリケーション側で解決する。
- `EQUIPMENT_TYPE` は `DESK`、`PIPE_CHAIR`、`PROJECTOR` のような粗い分類を保持し、`長机 2台` / `長机 3台` のような数量差は `EQUIPMENT_NAME` で表現する。
- `STATUS_CODE` は `AVAILABLE`、`PENDING_LENDING`、`LENT`、`UNAVAILABLE`、`DISPOSED` を許可値とする。
- `UNAVAILABLE` および `DISPOSED` は次期開発用の予約値として保持する。
- 状態変更の追跡に必要な `operationId`、`commandServiceId`、`operatedAt` は `H_EQUIPMENT_HISTORY` に分離し、本テーブルへは保持しない。

---

## 11. マイグレーション・変更管理
- DDL 変更は履歴管理されたマイグレーション手順で行う。
- 既存データへの影響確認を必須とする。
- 関連マスタ分割を行う場合は、既存コード値との移行方針を別途設計する。

---

## 12. 禁止事項
- 業務ルールや状態遷移の記述を含めない。
- 画面都合の項目や表示順序を持ち込まない。
- トランザクション境界や例外処理方針を記載しない。
- Repository / Application Service の実装詳細を書かない。
