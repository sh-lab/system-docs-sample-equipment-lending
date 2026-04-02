# クエリサービス設計書（Query Service）

## 1. サービスID・名称
- ID：`HFP-EL-SQS701_admin-equipment-edit_service`
- 名称：`管理者備品編集情報取得サービス`

## 2. 役割と責務
- `管理者備品編集画面(V700)` の初期表示に必要な参照情報を取得する。
- `新規登録モード` では備品種別マスタ候補および状態候補を返す。
- `編集モード` では対象備品の現在情報と候補値を返す。既存の備品名および備考も返し、画面で編集できるようにする。

## 3. 目的・スコープ
- 目的：管理者備品編集画面の表示に必要な参照データを取得する。
- 対象画面：`管理者備品編集画面(V700)`
- 対象データ：備品、備品種別マスタ候補、備品状態候補
- 操作種別：参照

## 4. 前提条件・事後条件

### 4.1 前提条件
- 入力DTOの型・必須・形式検証は上位層で完了している前提とする。
- `編集モード` の場合は対象備品IDが指定されていること。

### 4.2 事後条件
- 正常終了時：
  - 表示対象備品と選択候補を返却する。
- 異常終了時：
  - 例外が送出され、上位で変換される。

## 5. 処理内容概要
1. `新規登録モード` または `編集モード` を受け取る。
2. 備品種別マスタから有効な候補一覧を取得する。
3. 状態候補として `AVAILABLE`、`UNAVAILABLE`、`DISPOSED` を取得する。
4. `編集モード` の場合は、対象備品IDに対応する備品情報を取得する。
5. 取得結果を `EquipmentItem` と候補一覧へ詰め替えて返却する。

## 6. 使用するコンポーネント
- **Repository / Data Access**：
  - `EquipmentRepository` または同等の参照手段
  - `EquipmentTypeRepository` または同等の参照手段
- **補助コンポーネント**：
  - なし

## 7. 入出力仕様

### 7.1 入力
| 項目 | 型 | 必須 | 備考 |
|------|----|------|------|
| screenMode | string | ○ | `create` または `edit` |
| equipmentId | long | 条件付き必須 | `edit` の場合に指定する |

### 7.2 出力
| 項目 | 型 | 必須 | 説明 |
|------|----|------|------|
| equipmentItem | `EquipmentItem` | 任意 | `edit` 時に返却する備品表示情報 |
| equipmentTypeOptions | `List<Option>` | ○ | 備品種別マスタから取得した候補 |
| equipmentStatusOptions | `List<Option>` | ○ | 備品状態候補 |

#### EquipmentItem
| 項目 | 型 | 必須 | 説明 |
|------|----|------|------|
| equipmentId | long | ○ | 備品ID |
| equipmentCode | string | ○ | 備品管理番号 |
| equipmentName | string | ○ | 備品名 |
| equipmentType | string | ○ | 備品種別マスタを参照するコード |
| storageLocation | string | ○ | 保管場所 |
| systemRegisteredDate | date | ○ | システム登録日 |
| currentStatus | string | ○ | 現在状態コード |
| remarks | string | 任意 | 備考 |
| version | long | ○ | 楽観ロック確認用 |

#### Option
| 項目 | 型 | 必須 | 説明 |
|------|----|------|------|
| value | string | ○ | 選択値 |
| label | string | ○ | 表示名 |

## 8. 例外方針
- 業務例外：対象備品不存在や参照不可は送出する。
- システム例外：永続化層障害や想定外障害は送出する。
- 例外の捕捉・変換・ユーザー通知は呼び出し元の Application Service の責務とする。

参照：`02_architecture/error-handling.md`

## 9. 禁止事項
- 状態更新や登録処理を行わない。
- トランザクションを開始・終了しない。
- 画面遷移判断やメッセージ整形を行わない。

## 10. 補足
- 関連画面：`管理者備品編集画面(V700)`
- 関連ユースケース：`UC-008`、`UC-009`
- 関連機能要件：`FR-009`、`FR-010`
- 実装上のインターフェース名：`HfpElSqs701AdminEquipmentEditQueryService`

---
