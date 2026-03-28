# コマンドサービス設計書（Command Service）

## 1. サービスID・名称
- ID：`HFP-EL-SCS001_register-lending-request_service`
- 名称：`貸出申請登録コマンドサービス`

---

## 2. 役割と責務
- 本サービスは、貸出申請の新規登録を担当する。
- 入力された対象備品ID一覧の件数確認、重複検証、対象備品エンティティの取得、貸出可否判定、貸出申請本体および明細の永続化、対象備品状態の更新、対応する履歴テーブルへの操作履歴登録を行う。
- 履歴登録は、変更が確定した対象エンティティインスタンスごとに 1 行を追加する方式とし、複数件を更新した場合は同一 `operationId` で複数行を登録する。
- 実装クラスは `CommandBaseService<REQ, RES>` を継承する前提とする。
- 外部公開する `execute` メソッドは戻り値を返さず、内部では履歴登録に必要な最小限の情報を `RES` として扱う。
- トランザクション境界は管理せず、呼び出し元のアプリケーションサービスの境界を引き継ぐ。
- 画面都合の DTO 生成やメッセージ整形は行わない。

---

## 3. 目的・スコープ
- 目的：利用者が選択した備品ID群を 1 件の貸出申請として登録する。
- 対象エンティティ：`HFP-EL-E003_lending-request`、`HFP-EL-E001_equipment`、`T_LENDING_REQUEST_DETAIL` に対応する明細エンティティ
- 操作種別：登録・更新

---

## 4. 前提条件・事後条件

### 4.1 前提条件
- 入力DTOの型・必須・形式検証は上位層で完了している前提とする。
- 対象備品エンティティ一覧は 1 件以上であり、同一申請内で重複していないこと。

### 4.2 事後条件
- 正常終了時：
  - `T_LENDING_REQUEST` に貸出申請本体が 1 件登録される。
  - `T_LENDING_REQUEST_DETAIL` に対象備品分の明細が登録される。
  - 申請状態は `PENDING_APPROVAL` となる。
  - `M_EQUIPMENT` の対象備品状態が `PENDING_LENDING` に更新される。
  - 貸出申請、貸出申請明細、備品の各変更内容について、対応する履歴テーブルへ `operationId` 単位の操作履歴が登録される。
- 異常終了時：
  - 例外が送出され、上位でロールバック・変換される。
  - 履歴テーブルへの操作履歴は登録されない。

---

## 5. 処理内容概要
1. 入力された対象備品ID一覧が 1 件以上であることを確認する。
2. 同一申請内で同一備品IDが重複していないことを確認する。
3. Entity Repository から対象備品エンティティ一覧を取得する。
4. `貸出申請可否判定サービス(SPS001)` を用いて、対象備品に貸出不可状態の備品が含まれないことを判定する。
5. `LendingRequestRepository` からデータベースシーケンスにより新規貸出申請IDを取得する。
6. 貸出申請エンティティを生成し、取得した貸出申請ID、申請者、状態、申請コメント、申請日時、監査項目を設定する。
7. 対象備品エンティティごとに状態を `PENDING_LENDING` へ変更し、監査項目を設定する。
8. 貸出申請本体を Entity Repository 経由で永続化し、明細を保存する。
9. 対象備品状態の変更を Entity Repository 経由で永続化する。
10. 履歴登録に必要な最小限の情報として、貸出申請IDと対象備品ID一覧を `RES` に保持する。
11. 10. の `RES` を用いて、`H_LENDING_REQUEST_HISTORY` に 1 行、`H_LENDING_REQUEST_DETAIL_HISTORY` と `H_EQUIPMENT_HISTORY` に対象備品件数分の行を登録する。
12. 履歴登録で用いる `operationId` は framework 共通部のコンテキストから取得し、`操作時刻` は `recordHistory` 開始時に 1 回だけ取得して当該 `operationId` に紐づく全履歴行へ同一値を設定する。
13. 外部公開する `execute` メソッドは結果を返却せずに処理を終了する。

---

## 6. 使用するコンポーネント
- **Entity Repository**：
  - `LendingRequestRepository`：貸出申請IDの採番、貸出申請本体および明細の永続化
  - `EquipmentRepository`：対象備品状態の更新
- **Pure Service**：
  - `貸出申請可否判定サービス(SPS001)`：対象備品の貸出可否判定

---

## 7. 入出力仕様

### 7.1 入力
- Application Service から以下の情報を受け取る。
  - 利用者ID
  - 対象備品ID一覧
  - 申請コメント

### 7.2 外部公開メソッドの戻り値
- 外部公開する `execute` メソッドは戻り値を返さない。

### 7.3 内部利用する `RES`
- `RES` は履歴登録専用の内部データであり、登録・更新対象を特定する最小限の情報のみを保持する。

| 項目 | 型 | 必須 | 説明 |
|------|----|------|------|
| lendingRequestId | long | ○ | 登録した貸出申請ID |
| equipmentIds | list<long> | ○ | 登録した貸出申請明細および状態更新した備品を識別する備品ID一覧 |

### 7.4 履歴登録仕様

| 履歴テーブル | 登録件数 | 対象主キー | 記録項目 |
|--------------|----------|------------|----------|
| `H_LENDING_REQUEST_HISTORY` | 1 件 | `LENDING_REQUEST_ID = lendingRequestId` | `OPERATION_ID`、`LENDING_REQUEST_ID`、`COMMAND_SERVICE_ID`、`OPERATED_AT` |
| `H_LENDING_REQUEST_DETAIL_HISTORY` | 対象備品件数分 | `LENDING_REQUEST_ID = lendingRequestId` かつ `EQUIPMENT_ID ∈ equipmentIds` | `OPERATION_ID`、`LENDING_REQUEST_ID`、`EQUIPMENT_ID`、`COMMAND_SERVICE_ID`、`OPERATED_AT` |
| `H_EQUIPMENT_HISTORY` | 対象備品件数分 | `EQUIPMENT_ID ∈ equipmentIds` | `OPERATION_ID`、`EQUIPMENT_ID`、`COMMAND_SERVICE_ID`、`OPERATED_AT` |

- 複数の履歴テーブルへ登録する場合でも、同一業務操作であれば `operationId`、`commandServiceId`、`operatedAt` は共通値を用いる。
- `equipmentIds` は重複を許可せず、履歴登録前に対象備品単位で一意化されている前提とする。

---

## 8. 例外方針
- 業務例外：対象備品が 0 件、重複している、または `AVAILABLE` 以外の状態である場合は登録不可として送出する。
- システム例外：永続化失敗や想定外障害はシステム例外として送出する。
- 例外の捕捉・変換・ユーザー通知は呼び出し元のアプリケーションサービスの責務とする。
- 履歴登録は正常終了時のみ行い、例外送出時は行わない。

参照：`02_architecture/error-handling.md`

---

## 9. 禁止事項
- トランザクションを開始・終了しない。
- Query Service や別の Command Service を直接呼び出さない。
- 画面都合のデータ整形を行わない。
- 例外を内部で握りつぶさない。
- 参照系処理として履歴登録を行わない。
- `RES` に DTO や表示用情報を含めない。

---

## 10. 補足
- 関連画面：`利用者貸出申請・返却画面(V400)`
- 関連ユースケース：`UC-002`
- 関連機能要件：`FR-002`、`FR-007`
- 関連データ：`T_LENDING_REQUEST`、`T_LENDING_REQUEST_DETAIL`、`M_EQUIPMENT`
- 本サービスにより `PENDING_LENDING` となった備品は、承認待ちの申請に紐づく占有済み備品として扱う。
- 対象備品の取得責務は `貸出申請サービス(SAS402)` から利用される `備品ID指定取得サービス(SQS001)` が担う。
- 履歴登録で用いる `operationId` は Application Service 呼び出し時に framework 共通部で設定された値を利用する。
- `コマンドサービスID` には `HFP-EL-SCS001_register-lending-request_service` を設定する。

---
