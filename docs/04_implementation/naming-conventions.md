# 命名規約

## 1. 目的

本書は、本リポジトリにおける名称付けの原則および判断基準を定義する。

本書では、コード上の識別子、設計要素名、テンプレート名、メッセージキーを一貫して定義するための考え方を整理する。
人間の実装者および GitHub Copilot は、本書の内容を遵守すること。

---

## 2. 適用範囲

- 本方針は、クラス名、インターフェース名、パッケージ名、変数名、メソッド名、ファイル名、文書内の名称に適用する。
- 新規作成時だけでなく、既存名称の見直しや統一にも適用する。
- 実装時は `docs/03_designs/id-and-naming-conventions.md` に定義された ID 体系と整合すること。

---

## 3. 命名の基本原則

- 名称は、役割と責務が第三者に伝わるものにする。
- 同じ意味の概念には、同じ語を継続して使用する。
- 略語や省略形は、プロジェクト内で意味が共有されている場合に限って使用する。
- 命名に迷った場合は、短さよりも説明性を優先する。
- 曖昧な語や意味の広すぎる語を避ける。

---

## 4. 命名の観点

- 役割が分かること。
- 粒度が適切であること。
- 業務用語、設計用語、実装上の用語が不必要にずれないこと。
- 将来の変更に対して破綻しにくいこと。

---

## 5. 対象ごとの命名ルール

### 5.1 パッケージ

- すべて小文字とする。
- 単語は `_` や `-` で区切らず、意味単位で階層を分ける。

### 5.2 Controller

- クラス名は `{画面ID}Controller` とする。
- Controller 名に `Service` を含めてはならない。
- 業務概念よりも画面・操作単位を優先して命名する。

例：
- `HfpElV100UserMypageController`

### 5.3 Application Service

- インターフェース名は `{アプリケーションサービスID}ApplicationService` とする。
- 実装クラス名は `{InterfaceName}Impl` とする。

例：
- `HfpElSas001CreateLendingRequestApplicationService`
- `HfpElSas001CreateLendingRequestApplicationServiceImpl`

### 5.4 Command Service

- インターフェース名は `{Action}{Aggregate}CommandService` とする。
- 実装クラス名は `{InterfaceName}Impl` とする。

例：
- `CreateLendingRequestCommandService`

### 5.5 Query Service / Query Repository

- Query Service は `{Query}{Target}QueryService` とする。
- Query Repository は `{Target}QueryRepository` とする。
- 実装クラス名は `{InterfaceName}Impl` とする。

例：
- `FindLendingRequestDetailQueryService`
- `SearchLendingRequestsQueryService`
- `LendingRequestQueryRepository`

### 5.5.1 History Repository

- History Repository は `{Aggregate|Purpose}HistoryRepository` とする。
- 実装クラス名は `{InterfaceName}Impl` とする。

例：
- `LendingRequestHistoryRepository`
- `OperationHistoryRepository`

### 5.6 Pure Service

- インターフェース名は `{Policy|Rule|Calculator}Service` とする。
- `Manager`、`Helper`、`Util` などの曖昧な語は禁止する。
- Pure Service は業務概念を必ず名前に含める。

例：
- `BorrowablePolicyService`
- `LendingDurationCalculatorService`

### 5.7 Entity / Value

- Entity クラス名は `{Aggregate}` とする。
- enum などの Value は `{Aggregate}{Meaning}` または意味が直ちに伝わる業務名で命名する。
- 履歴モデルは `{Aggregate}History` または `{Aggregate}{Detail}History` とする。

例：
- `LendingRequest`
- `LendingRequestItem`
- `LendingRequestStatus`
- `ApprovalResult`
- `UserRole`
- `LendingRequestHistory`
- `LendingRequestDetailHistory`

### 5.8 Repository

- Entity Repository は `{Aggregate}Repository` とする。
- 実装クラス名は `{InterfaceName}Impl` とする。
- パッケージは `entity` / `query` / `history` で責務別に分ける。

例：
- `LendingRequestRepository`

### 5.9 Form / DTO

- Form は Controller（画面 ID）と 1対1 で対応させ、`{画面ID}Form` とする。
- DTO は `{Purpose}Dto` とする。

例：
- `HfpElV100UserMypageForm`
- `LendingRequestDetailDto`

### 5.10 HTML テンプレート / Fragment

- HTML テンプレートファイル名は `{画面ID}.html` とする。
- 画面 ID のファイル名は、すべて小文字、単語区切りはハイフン（`-`）のみ、拡張子は `.html` 固定とする。
- アンダースコア（`_`）は禁止する。
- Fragment は画面を表さないため画面 ID を使用せず、`fragment-{purpose}.html` とする。

例：
- `hfp-elv100-user-mypage.html`
- `hfp-elv300-equipment-search.html`
- `fragment-header.html`

### 5.11 メッセージ ID / i18n ラベルキー

- 業務メッセージ ID は `MSG_{種別}_{連番}` とする。
- 種別は `I`、`W`、`E` を使用する。
- 連番は 3 桁で採番する。
- 単項目バリデーションメッセージは本 ID 体系の適用対象外とする。
- 管理対象の業務メッセージを画面表示する際は、`[メッセージID]メッセージ本文` 形式とする。
- 画面固有ラベルキーは `screen.{画面ID由来の識別子}.*` とする。
- 共通ラベルキーは `label.*` とする。
- ラベルおよび管理対象メッセージの本文は `src/main/resources/i18n/` 配下のプロパティから解決し、ソースコードへ文言を直書きしない。

例：
- `MSG_I_001`
- `MSG_W_001`
- `[MSG_I_001]登録が完了しました。`
- `screen.elv100.title`
- `screen.elv300.search-result`
- `label.status`

### 5.12 規約適用外（基盤画面）

- ログイン、認証、エラー画面等の基盤機能は業務画面の画面 ID 命名規約の適用対象外とする。
- 基盤画面については、一般的・慣習的な命名を使用してよい。

例：
- `login.html`
- `LoginController`

---

## 6. 禁止事項

- `Util`、`Helper`、`Manager` などの曖昧な命名
- `Req`、`Svc`、`Mgr` などの省略形
- レイヤ名を含まない Service 名
- Controller と対応しない Form 名
- 規約に合致しない HTML テンプレート名
- 同一概念に対する表記ゆれの放置

---

## 7. 略語・短縮形の方針

- 略語は、対象プロジェクト内で意味が共有されている場合に限定して使用する。
- 新しい略語を導入する場合は、正式名称との対応が分かるようにする。
- 業務用語として定着していない略語は原則として避ける。

---

## 8. 命名変更の考え方

- 名称変更は、可読性向上だけでなく影響範囲も踏まえて判断する。
- 意味の誤解を招く名称は、互換性よりも明確性を優先して見直しを検討する。
- 名称変更時は、関連箇所の表記ゆれが残らないようにする。

---

## 9. レビュー観点

- 名称から役割や意味を推測できるか。
- 同じ概念に対して同じ語が使われているか。
- 略語や短縮形が過剰でないか。
- 名称が現在の責務や実態と一致しているか。

---

## 10. 変更管理

- 命名規約の変更時は、既存文書や実装への適用範囲を確認する。
- 重要な用語変更は、背景と理由が追跡できるように記録する。
- 本書の変更時は、`docs/04_implementation/implementation-rules.md` および関連設計書との整合を確認する。
