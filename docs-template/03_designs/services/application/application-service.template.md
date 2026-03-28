# アプリケーションサービス設計書（Application Service）

## 1. サービスID・名称
- ID：［AS-XXX］
- 名称：［○○ApplicationService］

## 2. 役割と責務
- ユースケース単位の流れ制御を担う。
- トランザクション境界の管理と例外の分類・変換を行う。
- UI は本サービスのみを呼び出す（UI → Application Service 以外は不可）。
- 参照：`02_architecture/layering-overview.md`

## 3. 目的・スコープ
- 目的：当該ユースケースでユーザーが達成することを記載する。
- スコープ：
  - 対象エンティティ：例）Asset
  - 業務範囲：登録／更新／参照 等
  - 画面／外部API連携があれば簡潔に記載する。

## 4. 前提条件・事後条件

### 4.1 前提条件
- 認証／認可：例）ROLE_ADMIN 必須
- 入力検証：型・必須・形式は Controller 層で完了している前提とする。

### 4.2 事後条件
- 正常終了時：
  - 状態遷移／生成ID／更新結果などを簡潔に記載する。
- 異常終了時：
  - トランザクションはロールバックされる。
  - UI へエラーが通知される。

## 5. 処理フロー概要
以下は処理の流れのみを記載し、実装詳細は含めない。

1. 入力DTOを受け取る
2. 必要に応じて Query Service により事前確認を行う
3. Pure Service により計算・判定を行う（副作用なし）
4. Command Service により状態変更・永続化を行う
5. 発生した例外を分類し Application Service で変換する
6. 出力DTOを組み立て UI へ返却する

## 6. 内部で使用するサービス
- Query Service：
  - 例）存在確認、重複チェック
- Command Service：
  - 例）登録、更新、削除
- Pure Service：
  - 例）計算、業務判定

ルール：
- Query Service から Command Service を直接呼び出してはならない。
- 副作用は Command Service に集約する。

## 7. 入出力DTO

### 7.1 入力DTO
| 項目 | 型 | 必須 | 備考 |
|-----|----|-----|-----|
| name | string | ○ | 最大100文字 |

### 7.2 出力DTO
| 項目 | 型 | 必須 | 説明 |
|-----|----|-----|-----|
| resultCode | string | ○ | OK / CONFLICT / VALIDATION_ERROR |
| id | string | 任意 | 対象エンティティ識別子 |
| meta | object | 任意 | 件数・補助情報など |

## 8. 例外マッピング方針
- 業務例外：ユーザー向けに内容を通知する。
- システム例外：汎用エラーとして通知する。
- 参照：`02_architecture/error-handling.md`

## 9. トランザクション・整合性
- トランザクション境界は Application Service とする。
- 原則として 1 ユースケース = 1 トランザクションとする。
- 分離レベルは Read Committed とする（変更時は ADR に記録）。

