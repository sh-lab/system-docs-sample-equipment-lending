# コマンドサービス設計書（Command Service）

## 1. サービスID・名称
- ID：`HFP-EL-SCS012_return-confirm_service`
- 名称：`返却確認コマンドサービス`

---

## 2. 役割と責務
- 本サービスは、返却確認待ちの貸出申請に対する返却確認処理を担当する。
- 対象申請の取得、状態遷移可否確認、楽観ロック確認、返却確認情報の更新、対象備品状態の更新、対応する履歴テーブルへの操作履歴登録を行う。
- 履歴登録は、変更が確定した対象エンティティインスタンスごとに 1 行を追加する方式とし、複数件を更新した場合は同一 `operationId` で複数行を登録する。
- 実装クラスは `CommandBaseService<REQ, RES>` を継承する前提とする。
- 外部公開する `execute` メソッドは戻り値を返さず、内部では履歴登録に必要な最小限の情報を `RES` として扱う。
- トランザクション境界は管理せず、呼び出し元のアプリケーションサービスの境界を引き継ぐ。
- 画面都合の DTO 生成やメッセージ整形は行わない。

---

## 3. 目的・スコープ
- 目的：返却確認待ちの貸出申請を完了状態へ更新し、対象備品を貸出可能状態へ戻す。
- 対象エンティティ：`HFP-EL-E003_lending-request`、`HFP-EL-E001_equipment`
- 操作種別：更新

---

## 4. 前提条件・事後条件

### 4.1 前提条件
- 入力DTOの型・必須・形式検証は上位層で完了している前提とする。
- 対象申請IDと楽観ロック用バージョンが指定されていること。

### 4.2 事後条件
- 正常終了時：
  - 対象申請の状態が `COMPLETED` に更新される。
  - 返却確認者ユーザーID、返却確認コメント、完了日時が記録される。
  - 対象備品の状態が `AVAILABLE` に更新される。
  - 貸出申請および備品の変更内容について、対応する履歴テーブルへ `operationId` 単位の操作履歴が登録される。
- 異常終了時：
  - 例外が送出され、上位でロールバック・変換される。
  - 履歴テーブルへの操作履歴は登録されない。

---

## 5. 処理内容概要
1. Entity Repository から貸出申請IDに対応する申請エンティティを取得する。
2. 対象申請が存在することを確認する。
3. 対象申請の現在状態が `PENDING_RETURN_CONFIRMATION` であること、および `VERSION` が入力値と一致することを確認する。
4. Entity Repository から対象申請に紐づく備品一覧を取得する。
5. 申請エンティティの状態を `COMPLETED` へ変更し、返却確認者ユーザーID、返却確認コメント、完了日時、監査項目を設定する。
6. 対象備品エンティティごとに状態を `AVAILABLE` へ変更し、監査項目を設定する。
7. 更新後の申請エンティティおよび備品エンティティを、それぞれの Entity Repository 経由で永続化する。
8. 履歴登録に必要な最小限の情報として、更新対象の貸出申請IDと状態更新した備品ID一覧を `RES` に保持する。
9. 8. の `RES` を用いて、`H_LENDING_REQUEST_HISTORY` に 1 行、`H_EQUIPMENT_HISTORY` に対象備品件数分の行を登録する。
10. 履歴登録で用いる `operationId` は framework 共通部のコンテキストから取得し、`操作時刻` は `recordHistory` 開始時に 1 回だけ取得して当該 `operationId` に紐づく全履歴行へ同一値を設定する。
11. 外部公開する `execute` メソッドは結果を返却せずに処理を終了する。

---

## 6. 使用するコンポーネント
- **Entity Repository**：
  - `LendingRequestRepository`：対象申請の取得と更新
  - `EquipmentRepository`：対象備品状態の更新
- **Pure Service**：
  - なし

---

## 7. 入出力仕様

### 7.1 入力
- Application Service から以下の情報を受け取る。
  - 管理者ユーザーID
  - 貸出申請ID
  - 返却確認コメント
  - バージョン

### 7.2 外部公開メソッドの戻り値
- 外部公開する `execute` メソッドは戻り値を返さない。

### 7.3 内部利用する `RES`
- `RES` は履歴登録専用の内部データであり、登録・更新対象を特定する最小限の情報のみを保持する。

| 項目 | 型 | 必須 | 説明 |
|------|----|------|------|
| lendingRequestId | long | ○ | 更新対象の貸出申請ID |
| equipmentIds | list<long> | ○ | 状態更新した備品ID一覧 |

### 7.4 履歴登録仕様

| 履歴テーブル | 登録件数 | 対象主キー | 記録項目 |
|--------------|----------|------------|----------|
| `H_LENDING_REQUEST_HISTORY` | 1 件 | `LENDING_REQUEST_ID = lendingRequestId` | `OPERATION_ID`、`LENDING_REQUEST_ID`、`COMMAND_SERVICE_ID`、`OPERATED_AT` |
| `H_EQUIPMENT_HISTORY` | 対象備品件数分 | `EQUIPMENT_ID ∈ equipmentIds` | `OPERATION_ID`、`EQUIPMENT_ID`、`COMMAND_SERVICE_ID`、`OPERATED_AT` |

- 複数の履歴テーブルへ登録する場合でも、同一業務操作であれば `operationId`、`commandServiceId`、`operatedAt` は共通値を用いる。
- `equipmentIds` は重複を許可せず、履歴登録前に対象備品単位で一意化されている前提とする。

---

## 8. 例外方針
- 業務例外：対象申請が存在しない、返却確認待ちでない、またはバージョン不一致の場合は更新不可として送出する。
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
- 関連画面：`管理者承認・却下・返却確認画面(V500)`
- 関連ユースケース：`UC-005`
- 関連機能要件：`FR-005`
- 関連データ：`T_LENDING_REQUEST`、`T_LENDING_REQUEST_DETAIL`、`M_EQUIPMENT`
- 履歴登録で用いる `operationId` は Application Service 呼び出し時に framework 共通部で設定された値を利用する。
- `コマンドサービスID` には `HFP-EL-SCS012_return-confirm_service` を設定する。

---
