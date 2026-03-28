# アプリケーションサービス設計書（Application Service）

## 1. サービスID・名称
- ID：`HFP-EL-SAS101_user-mypage-init_service`
- 名称：`利用者マイページ初期表示サービス`

## 2. 役割と責務
- 利用者マイページ初期表示時のユースケース境界を担う。
- 利用者IDを受け取り、マイページ表示用データの取得を Query Service に委譲する。
- UI から Query Service を直接呼び出させず、画面初期表示の入口を一本化する。

## 3. 目的・スコープ
- 目的：利用者本人に紐づく貸出中申請一覧、申請中一覧、却下申請有無を取得し、マイページ表示に必要なデータを返す。
- スコープ：
  - 対象画面：`利用者マイページ(V100)`
  - 対象データ：利用者本人の貸出申請サマリ
  - 業務範囲：初期表示用参照

## 4. 前提条件・事後条件

### 4.1 前提条件
- 認証済みの利用者であること。
- `userId` の型・必須検証は Controller 層で完了している前提とする。

### 4.2 事後条件
- 正常終了時：
  - 利用者マイページ表示に必要な `FindUserMypageQueryServiceImpl.Response` を返却する。
- 異常終了時：
  - トランザクションを必要とする更新は存在しない。
  - UI へエラーが通知される。

## 5. 処理フロー概要
1. 利用者IDを受け取る
2. 利用者IDをもとに、マイページ表示に必要な取得条件を作成する
3. マイページ表示用データの取得処理を呼び出す
4. 取得結果をそのまま UI へ返却する

## 6. 内部で使用するサービス
- Query Service：
  - `利用者マイページ情報取得サービス(SQS101)`
- Command Service：
  - なし
- Pure Service：
  - なし

## 7. 入出力DTO

### 7.1 入力DTO
| 項目 | 型 | 必須 | 備考 |
|-----|----|-----|-----|
| userId | string | ○ | 利用者ID |

### 7.2 出力DTO
| 項目 | 型 | 必須 | 説明 |
|-----|----|-----|-----|
| lentRequests | `List<FindUserMypageQueryServiceImpl.RequestItem>` | ○ | 貸出中申請一覧 |
| pendingRequests | `List<FindUserMypageQueryServiceImpl.RequestItem>` | ○ | 承認待ち、返却確認待ち、却下申請一覧 |
| hasRejectedRequest | boolean | ○ | 却下済み未確認申請の有無 |

## 8. 例外マッピング方針
- 業務例外：利用者本人のデータ取得に失敗した場合は UI へ通知する。
- システム例外：汎用エラーとして通知する。
- 参照：`02_architecture/error-handling.md`

## 9. トランザクション・整合性
- 参照専用サービスであり、状態変更は行わない。
- トランザクション境界は Application Service であるが、更新系処理は持たない。

## 10. 補足
- 関連画面：`利用者マイページ(V100)`
- 関連ユースケース：`UC-002`、`UC-004`、`UC-006`
- 関連機能要件：`FR-004`、`FR-006`、`FR-007`
- 実装上のインターフェース名：`HfpElSas101UserMypageInitApplicationService`
- 実装上の主な入出力：
  - 入力オブジェクト：`FindUserMypageQueryServiceImpl.Request`
  - 出力オブジェクト：`FindUserMypageQueryServiceImpl.Response`

---
