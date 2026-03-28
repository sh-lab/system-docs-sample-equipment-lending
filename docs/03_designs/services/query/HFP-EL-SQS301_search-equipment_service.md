# クエリサービス設計書（Query Service）

## 1. サービスID・名称
- ID：`HFP-EL-SQS301_search-equipment_service`
- 名称：`備品検索情報取得サービス`

---

## 2. 役割と責務
- 本サービスは読み取り専用で、備品検索画面の一覧表示用データを取得する。
- 備品一覧と備品種別候補をまとめて返却し、表示件数上限を 100 件に制御する。
- Query Repository から取得した備品種別コードおよび状態コードを画面表示用ラベルへ変換する。
- 書き込みや状態変更は行わない。

---

## 3. 目的・スコープ
- **目的**：備品検索画面に必要な検索結果一覧と種別候補を返却する。
- **対象データ**：貸出対象備品の一覧、備品種別候補
- **利用シーン**：`備品検索画面(V300)` 初期表示および検索実行

---

## 4. 入力仕様（検索条件）

### 4.1 条件一覧
| 条件名 | 型 | 必須 | マッチ種別 | 備考 |
|--------|----|------|------------|------|
| equipmentName | string | 任意 | 部分一致 | 空文字可 |
| equipmentType | string | 任意 | 完全一致 | 空文字時は全件対象 |
| lendingStatus | string | 任意 | 完全一致 | `ALL`、`AVAILABLE`、`UNAVAILABLE` を想定 |

### 4.2 バリデーション
- 型・必須の検証は Controller 層で完了している前提とする。
- 文字列正規化や既定値補正は Application Service 側で完了している前提とする。

---

## 5. 並び替え・ページング

### 5.1 ソート
- 備品一覧はデータ取得部品の取得順を利用する。

### 5.2 ページング
- ページングは行わない。
- 表示件数は 100 件を上限とし、101 件以上取得した場合は先頭 100 件のみ返却する。

---

## 6. 出力仕様（DTO）

### 6.1 一覧 DTO（サマリ）
| 項目名 | 型 | 必須 | 説明 |
|--------|----|------|------|
| equipmentItems | `List<SearchEquipmentQueryServiceImpl.EquipmentItem>` | ○ | 備品一覧 |
| equipmentTypeOptions | `List<SearchEquipmentQueryServiceImpl.Option>` | ○ | 備品種別候補 |
| hasMoreThanLimit | boolean | ○ | 100 件超過有無 |

### 6.2 詳細 DTO（必要時のみ）
| 項目名 | 型 | 必須 | 説明 |
|--------|----|------|------|
| equipmentId | long | ○ | 備品ID |
| equipmentCode | string | ○ | 備品管理番号 |
| equipmentName | string | ○ | 備品名 |
| equipmentTypeLabel | string | ○ | 備品種別表示名 |
| storageLocation | string | ○ | 保管場所 |
| statusLabel | string | ○ | 状態表示名。`貸出可能` または `貸出不可` |
| selectable | boolean | ○ | 選択可否 |

---

## 7. 処理フロー概要（擬似コード可）
1. 検索条件を受け取る
2. 条件に一致する備品一覧を取得する
3. 備品種別候補を取得する
4. 取得した備品種別コードおよび状態コードを表示ラベルへ変換する
5. 取得件数が 100 件を超えるか判定する
6. 超過時は先頭 100 件へ切り詰める
7. 検索結果としてまとめて返却する

---

## 8. 使用するコンポーネント
- **Query Repository**：`EquipmentSearchQueryRepository`
- **補足**：`Query Repository` は備品種別コード・状態コードを返却し、表示ラベル化は本 `Query Service` が担う。

---

## 9. 例外とエラー方針
- 業務例外：件数超過は例外ではなく `hasMoreThanLimit` で通知する。
- データアクセス障害：システム例外として扱う。

参照：`02_architecture/error-handling.md`

---

## 10. 非機能要件への配慮（該当時）
- 性能：表示件数上限を 100 件に制御し、画面負荷を抑える。
- 操作性：備品種別候補を同時返却し、再検索しやすくする。
- 転送量：超過時は先頭 100 件のみに制限する。

---

## 11. 禁止事項
- 副作用を行わない。
- 備品状態の変更や申請登録を行わない。
- Command Service を呼び出さない。

---

## 12. 補足
- 関連アプリケーションサービス：`備品検索初期表示サービス(SAS301)`
- 関連アプリケーションサービス：`備品検索実行サービス(SAS302)`
- 関連画面：`備品検索画面(V300)`
- `UNAVAILABLE` は備品検索画面用の表示条件値であり、内部状態 `PENDING_LENDING`、`LENT`、`UNAVAILABLE`、`DISPOSED` をまとめて表す。
- 備品検索画面では、内部状態 `AVAILABLE` を `貸出可能`、それ以外の表示対象状態を `貸出不可` として返却する。
- 実装上のインターフェース名：`SearchEquipmentQueryService`
- 実装上の主な入出力：
  - 入力オブジェクト：`SearchEquipmentQueryServiceImpl.Request`
  - 出力オブジェクト：`SearchEquipmentQueryServiceImpl.Response`
  - データ取得部品：`EquipmentSearchQueryRepository`

---
