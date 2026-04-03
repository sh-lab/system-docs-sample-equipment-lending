# クエリサービス設計書（Query Service）

## 1. サービスID・名称
- ID：`HFP-EL-SQS601_admin-equipment-search_service`
- 名称：`管理者備品検索情報取得サービス`

---

## 2. 役割と責務
- 本サービスは読み取り専用で、`管理者備品検索画面(V600)` の一覧表示用データを取得する。
- 備品一覧、備品種別マスタ候補、備品状態候補をまとめて返却し、表示件数上限を 100 件に制御する。
- Query Repository から取得した備品種別コードおよび状態コードを、備品種別マスタおよび状態定義に基づく画面表示用ラベルへ変換する。
- 書き込みや状態変更は行わない。

---

## 3. 目的・スコープ
- **目的**：管理者備品検索画面に必要な検索結果一覧と候補値を返却する。
- **対象データ**：登録済み備品の一覧、備品種別マスタ候補、備品状態候補
- **利用シーン**：`管理者備品検索画面(V600)` 初期表示および検索実行

---

## 4. 入力仕様（検索条件）

### 4.1 条件一覧
| 条件名 | 型 | 必須 | マッチ種別 | 備考 |
|--------|----|------|------------|------|
| equipmentName | string | 任意 | 部分一致 | 空文字可 |
| equipmentType | string | 任意 | 完全一致 | 空文字時は全件対象 |
| statusCode | string | 任意 | 完全一致 | `ALL`、`AVAILABLE`、`PENDING_LENDING`、`LENT`、`UNAVAILABLE`、`DISPOSED` を想定 |
| systemRegisteredDateFrom | date | 任意 | 以上 | `null` 時は下限条件なし |
| systemRegisteredDateTo | date | 任意 | 以下 | `null` 時は上限条件なし |

### 4.2 バリデーション
- 型・必須の検証は Controller 層で完了している前提とする。
- 文字列正規化や既定値補正は Application Service 側で完了している前提とする。

---

## 5. 並び替え・ページング

### 5.1 ソート
- 備品一覧は `EQUIPMENT_CODE` 昇順で返却する。

### 5.2 ページング
- ページングは行わない。
- 表示件数は 100 件を上限とし、101 件以上取得した場合は先頭 100 件のみ返却する。

---

## 6. 出力仕様（DTO）

### 6.1 一覧 DTO
| 項目名 | 型 | 必須 | 説明 |
|--------|----|------|------|
| equipmentItems | `List<SearchAdminEquipmentQueryService.EquipmentItem>` | ○ | 備品一覧 |
| equipmentTypeOptions | `List<SearchAdminEquipmentQueryService.Option>` | ○ | 備品種別マスタから取得した検索条件プルダウン候補 |
| statusOptions | `List<SearchAdminEquipmentQueryService.Option>` | ○ | 検索条件プルダウンに表示する備品状態候補 |
| hasMoreThanLimit | boolean | ○ | 100 件超過有無 |

### 6.2 明細 DTO
| 項目名 | 型 | 必須 | 説明 |
|--------|----|------|------|
| equipmentId | long | ○ | 備品ID |
| equipmentCode | string | ○ | 備品管理番号 |
| equipmentName | string | ○ | 備品名 |
| equipmentTypeCode | string | ○ | 備品種別コード |
| equipmentTypeLabel | string | ○ | 備品種別マスタに基づく表示名 |
| systemRegisteredDate | date | ○ | システム登録日 |
| storageLocation | string | ○ | 保管場所 |
| statusCode | string | ○ | 備品状態コード |
| statusLabel | string | ○ | 状態表示名。`貸出可能`、`貸出申請中`、`貸出中`、`貸出不可`、`廃棄` のいずれか |
| version | int | ○ | 楽観ロック用バージョン |

---

## 7. 処理フロー概要（擬似コード可）
1. 検索条件を受け取る。
2. 条件に一致する備品一覧を取得する。
3. 備品種別マスタから有効な候補を取得する。
4. 備品状態候補を取得する。
5. 取得した備品種別コードおよび状態コードを表示ラベルへ変換する。
6. 取得件数が 100 件を超えるか判定する。
7. 超過時は先頭 100 件へ切り詰める。
8. 検索結果としてまとめて返却する。

---

## 8. 使用するコンポーネント
- **Query Repository**：`AdminEquipmentSearchQueryRepository`
- **補足**：`Query Repository` は備品種別コード・状態コードを返却し、表示ラベル化および備品種別候補取得は本 `Query Service` が `M_EQUIPMENT_TYPE` を参照して担う。

---

## 9. 例外とエラー方針
- 業務例外：件数超過は例外ではなく `hasMoreThanLimit` で通知する。
- データアクセス障害：システム例外として扱う。

参照：`02_architecture/error-handling.md`

---

## 10. 非機能要件への配慮（該当時）
- 性能：表示件数上限を 100 件に制御し、画面負荷を抑える。
- 操作性：備品種別マスタ候補と備品状態候補を同時返却し、管理者が再検索しやすくする。
- 転送量：超過時は先頭 100 件のみに制限する。

---

## 11. 禁止事項
- 副作用を行わない。
- 備品状態の変更や申請登録を行わない。
- Command Service を呼び出さない。

---

## 12. 補足
- 関連アプリケーションサービス：`管理者備品検索初期表示サービス(SAS601)`
- 関連アプリケーションサービス：`管理者備品検索実行サービス(SAS602)`
- 関連画面：`管理者備品検索画面(V600)`
- 実装上のインターフェース名：`SearchAdminEquipmentQueryService`

---
