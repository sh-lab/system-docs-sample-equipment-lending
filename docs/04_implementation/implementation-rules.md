# 実装規約

## 1. 目的

本書は、本リポジトリにおける実装上の共通ルールおよび判断基準を定義する。

本書では、実装時に守るべき責務分担、依存関係、状態変更、例外処理、画面実装上の共通方針を整理する。
人間の実装者および GitHub Copilot は、本書の内容を遵守すること。

---

## 2. 適用範囲

- 本方針は、本システムに対する新規実装、改修、不具合修正に適用する。
- Java 実装、Thymeleaf テンプレート、i18n 資材、永続化実装を対象とする。
- 実装時は `docs/02_architecture/` および `docs/03_designs/` の内容を前提とし、本書はそれらを実装へ落とし込む際の規約として扱う。

---

## 3. 実装の基本原則

- 実装は、要件・アーキテクチャ・設計ドキュメントに従って行う。
- 本プロジェクトは Transaction Script スタイルを採用する。
- 可読性、追跡性、学習用途での分かりやすさを優先する。
- 責務の異なる処理を同一箇所に混在させない。
- 一時的な回避策よりも、継続的に保守可能な構造を優先する。

---

## 4. 責務分担の方針

### 4.1 基本構成

```text
net.shlab.hogefugapiyo.equipmentlending
├─ presentation
│  └─ Controller
│
├─ application
│  ├─ Application Service
│  ├─ impl
│  │  └─ Application Service 実装
│  ├─ command
│  │  ├─ Command Service
│  │  └─ impl
│  │     └─ Command Service 実装
│  ├─ query
│  │  ├─ Query Service
│  │  └─ impl
│  │     └─ Query Service 実装
│  └─ pure
│     ├─ Pure Service
│     └─ impl
│        └─ Pure Service 実装
│
├─ model
│  ├─ entity
│  │  └─ Entity
│  ├─ value
│  │  └─ Value
│  ├─ record
│  │  └─ Record
│  └─ history
│     └─ 操作履歴のレコード
│
└─ infrastructure
   └─ repository
      ├─ entity
      │  ├─ Entity Repository
      │  ├─ jpa
      │  │  └─ Spring Data JPA Repository
      │  └─ impl
      │     └─ Entity Repository 実装
      ├─ query
      │  ├─ Query Repository
      │  └─ impl
      │     └─ Query Repository 実装
      └─ history
         ├─ History Repository
         └─ impl
            └─ History Repository 実装
```

### 4.2 Controller

- Controller は UI / HTTP 層の責務のみを持つ。
- 入力値の受け取り、形式検証、画面遷移、表示用データの受け渡しに限定する。
- 基本バリデーション（必須、型、文字数、形式）は `Spring Boot Validation` を用いて実装する。
- 入力受付がある `POST` は `@ModelAttribute` と `@Valid`、`BindingResult` を基本形とし、バリデーションエラー時は業務ロジックを呼び出さない。
- 単項目バリデーションメッセージはメッセージプロパティに定義し、画面上で再表示する。
- バリデーションエラー時は、画面上部に該当エラーを全件表示する。
- 単項目バリデーションメッセージは、対象入力項目の右または下に表示する。
- 業務判断、状態変更、永続化を行ってはならない。
- 画面 Controller は `net.shlab.hogefugapiyo.equipmentlending.presentation.controller` 配下へ配置する。
- 全ての画面 Controller は `net.shlab.hogefugapiyo.equipmentlending.presentation.controller.AbstractBaseController` を継承する。
- `@GetMapping` および `@PostMapping` の経路は `net.shlab.hogefugapiyo.equipmentlending.presentation.route.RoutePaths` の定数を参照する。
- 画面表示用 `@GetMapping` に対応するメソッド名は `show()` とする。
- 更新系 `POST` は成功時に Post/Redirect/Get を適用し、`redirect:` を返すことを原則とする。
- バリデーションエラー等で同一画面を再表示する必要がある場合に限り、`POST` から画面を再描画してよい。
- リダイレクト後に表示する完了メッセージ等は `RedirectAttributes` 等の一時的な受け渡し手段を用いる。
- 重複送信防止が必要な更新系 `POST` では、PRG に加えて `HandlerInterceptor` によるワンタイムトークン検証を適用し、送信中の一時非活性化を併用する。
- Controller はワンタイムトークンの表示用 hidden 値を払い出すが、検証・消費は個別 Controller で実装しない。
- ワンタイムトークン不正時は、個別画面の再表示へ戻さず、共通の専用エラー画面へ遷移させる。

### 4.3 Application Service

- Application Service は 1ユースケース = 1メソッドを原則とする。
- Application Service は、フレームワークの役割 IF を継承した契約 IF と、その実装クラスを必ず対で定義する。
- 契約 IF は `application` 直下に配置し、実装クラスは `application/impl` に配置して契約 IF を実装する。
- 業務ロジックを直接抱え込まず、処理の流れを制御する役割を持つ。
- Command Service、Query Service、Pure Service を組み合わせて処理を構成する。
- トランザクション境界は Application Service に置く。
- トランザクションは実装クラスに `@Transactional(rollbackFor = Exception.class)` をクラスレベルで付与する。

### 4.4 Command Service

- 状態変更を担当する。
- Command Service は、フレームワークの役割 IF を継承した契約 IF と、その実装クラスを必ず対で定義する。
- 契約 IF は `application/command` 直下に配置し、実装クラスは `application/command/impl` に配置して契約 IF を実装する。
- 各コマンドサービスの実装クラスは `CommandBaseService<REQ, RES>` を継承する。
- 外部公開するリクエストは、原則として当該契約 IF 内の `public static record Request` として定義する。
- 履歴登録に用いる `RES` は、原則として当該契約 IF 内の `public static record HistoryResponse` として定義する。
- 永続化や外部書き込みを伴う処理を担当してよい。
- 外部公開する `execute` メソッドは戻り値を返さない。
- `RES` は登録・更新・削除があったエンティティの ID など、履歴登録に必要な最小限の情報のみを保持する。
- `RES` に DTO や View 用オブジェクトを含めてはならない。
- エンティティの生成、更新、状態遷移判定後の状態変更は Command Service で行う。
- Command Service は、対象エンティティを Entity Repository から取得し、エンティティクラスまたは enum を用いて状態を変更した上で、Entity Repository へ永続化を依頼する。
- Command Service から SQL 更新条件を表す文字列コードや列値の組合せを直接組み立てて状態変更を表現してはならない。
- 状態変更を伴う業務操作では、対応する履歴テーブルへの操作履歴登録までをコマンドサービスの責務に含める。
- 履歴登録はコマンドサービスが正常終了した場合にのみ行い、`operationId` 単位で記録する。
- 複数件を更新した場合は、同一 `operationId` で対象エンティティインスタンスごとに複数行を登録する。
- 同一業務操作内で同一対象を複数回更新した場合でも、履歴登録は変更確定後の対象主キー単位で 1 行とする。
- `RES` は履歴対象主キーを表現できる構造とし、複合主キーを持つ明細は親 ID と従属 ID を復元できる情報を保持する。
- 履歴登録用レコードに画面表示用情報、不要な派生値、メッセージ文言を含めてはならない。

### 4.5 Query Service / Query Repository

- 読み取り専用とする。
- Query Service は、フレームワークの役割 IF を継承した契約 IF と、その実装クラスを必ず対で定義する。
- Query Service の契約 IF は `application/query` 直下に配置し、実装クラスは `application/query/impl` に配置して契約 IF を実装する。
- 外部公開するリクエストは、原則として当該契約 IF 内の `public static record Request` として定義する。
- 外部公開するレスポンスは、原則として当該契約 IF 内の `public static record Response` として定義する。
- Entity を返してはならない。
- DTO、Projection、View 用オブジェクトを返す。
- 状態変更を行ってはならない。
- 履歴登録を行ってはならない。
- 参照系の入出力レコードは、別ファイルにトップレベル DTO として切り出して横断再利用してはならない。

### 4.5.1 サービス入出力レコードの実装規約

- コマンドサービスおよびクエリサービスの入出力レコードは、サービスごとの契約を表す型として扱う。
- サービス利用側は `インターフェース名.Request`、`インターフェース名.HistoryResponse`、`インターフェース名.Response` の形式で参照する。
- サービス利用側は、同一意味の DTO を別名で再定義してはならない。
- 補助的な明細、候補、行データ等が必要な場合は、`Item`、`Detail`、`Option` など役割を表す nested record を用いる。
- サービス間で共有すべき不変の業務概念でない限り、項目構成が似ていても DTO を共通化してはならない。
- package-private な nested record をサービス契約として利用してはならない。

### 4.6 Pure Service

- 副作用のない業務ロジックを担当する。
- Pure Service は、フレームワークの役割 IF を継承した契約 IF と、その実装クラスを必ず対で定義する。
- 契約 IF は `application/pure` 直下に配置し、実装クラスは `application/pure/impl` に配置して契約 IF を実装する。
- 条件判定、ポリシー判定、計算ロジックに限定する。
- 永続化、Repository 呼び出し、I/O を行ってはならない。

### 4.7 Entity

- Entity はデータ構造と状態保持に専念する。
- 業務ロジックを持たせない。
- Getter / Setter は許可する。
- `equals` / `hashCode` を独自実装しない。
- エンティティは監査項目（作成者・作成日時・更新者・更新日時）を保持する。
- 原則として `net.shlab.hogefugapiyo.framework.persistence.entity.AuditVersionEntity` を継承する。
- `VERSION` を親エンティティで一括管理する明細エンティティに限り、`net.shlab.hogefugapiyo.framework.persistence.entity.AuditEntity` を継承してよい。
- 明細エンティティは原則として `VERSION` を持たない。
- 業務エンティティは `record` で定義してはならず、継承可能な通常クラスとして定義する。
- 業務エンティティは `src/main/java/net/shlab/hogefugapiyo/equipmentlending/model/entity` 配下へ配置する。
- append-only の履歴テーブルに対応する履歴モデルは、業務エンティティとは分離し、`src/main/java/net/shlab/hogefugapiyo/equipmentlending/model/history` 配下へ配置する。

### 4.8 Value

- Value は状態、区分、結果などの値的概念を表す層である。
- 主に enum を配置し、Entity、Service、DTO から参照される。
- Value は業務状態を表す識別子・分類子であり、複雑な業務ロジックを持たせない。
- エンティティの状態コードは文字列リテラルで散在させず、`src/main/java/net/shlab/hogefugapiyo/equipmentlending/model/value` 配下の enum として定義する。

### 4.9 Repository

- Entity Repository の契約 IF は `infrastructure/repository/entity` 直下に定義し、実装クラスは `infrastructure/repository/entity/impl` に配置する。
- Entity Repository を Spring Data JPA で実装する場合、技術要素としての Repository IF は `infrastructure/repository/entity/jpa` に配置する。
- `infrastructure/repository/entity/jpa` には Spring Data JPA 依存の技術的な Repository IF のみを配置し、業務側の契約 IF を置いてはならない。
- `infrastructure/repository/entity/impl` の実装クラスは、業務側の Entity Repository 契約 IF を実装し、必要に応じて `infrastructure/repository/entity/jpa` 配下の Spring Data JPA Repository IF へ委譲する。
- 将来、永続化方式を追加する場合は、`jpa` と同粒度で技術別サブパッケージを追加してよい。
- Query Repository の契約 IF は `infrastructure/repository/query` 直下に定義し、実装クラスは `infrastructure/repository/query/impl` に配置する。
- History Repository の契約 IF は `infrastructure/repository/history` 直下に定義し、実装クラスは `infrastructure/repository/history/impl` に配置する。
- 書き込み用と読み取り用は分離する。
- 業務エンティティを永続化する Repository は、`net.shlab.hogefugapiyo.framework.core.repository.EntityRepository` を継承する。
- Command Service からエンティティを永続化する際は、対象の Entity Repository を経由する。
- 履歴登録専用 Repository は append-only の追記責務に限定し、業務エンティティ更新を行ってはならない。

### 4.10 サービス IF / Repository IF の継承規約

- すべてのサービス IF と Repository IF は、フレームワーク側の役割 IF を継承して役割を明示する。
- サービスは、ベースとなる役割 IF を直接実装クラスへ書くのではなく、業務上の契約 IF を定義した上で、その契約 IF にベース IF を継承させる。
- 実装クラスは契約 IF を実装する責務のみを持ち、役割 IF を直接実装してはならない。
- Application Service は `net.shlab.hogefugapiyo.framework.core.service.ApplicationService` を継承する。
- Command Service の契約 IF は `net.shlab.hogefugapiyo.framework.core.service.CommandService<REQ>` を継承し、実装クラスは `net.shlab.hogefugapiyo.framework.service.CommandBaseService<REQ, RES>` を継承する。
- Query Service は `QueryService<REQ, RES>` を継承する。
- Pure Service は `net.shlab.hogefugapiyo.framework.core.service.PureService` を継承する。
- Entity Repository は `net.shlab.hogefugapiyo.framework.core.repository.EntityRepository` を継承する。
- Spring Data JPA 用の技術的な Repository IF は `JpaRepository` 等の技術基盤 IF を継承してよいが、業務層はそれらへ直接依存してはならない。
- Query Repository は `net.shlab.hogefugapiyo.framework.core.repository.QueryRepository` を継承する。
- History Repository は、履歴追記専用の業務契約 IF として定義し、必要なメソッドのみを公開する。
- History Repository は、履歴追記専用の業務契約 IF として定義し、必要なメソッドのみを公開する。

---

## 5. 依存関係のルール

- 依存関係は、定義されたレイヤ構造および責務分担に従う。
- 下位の詳細に強く依存する実装を避け、境界を越えるやり取りは明示的にする。
- 相互依存や循環参照を生まない構造を維持する。
- 共通化は、意味的に同じ責務が複数箇所に存在する場合にのみ行う。
- 命名は `docs/04_implementation/naming-conventions.md` に従う。

---

## 6. 状態変更と整合性

- 状態変更を伴う処理では、変更前提、変更内容、変更後の整合性を明確にする。
- 複数の要素にまたがる変更は、一連の業務処理として整合性を保てるように扱う。
- 再実行や重複実行が起こり得る処理については、その影響を考慮する。
- 監査や追跡が必要な変更は、別途定義された記録方針に従う。
- 状態変更を伴う業務操作では、変更対象に対応する履歴テーブルへ操作履歴を登録する。
- 操作履歴はコマンドサービスの正常終了時にのみ登録し、例外送出時や処理失敗時には登録しない。
- 操作履歴は `operationId` 単位で一意に扱い、同一業務操作を追跡できるようにする。
- 履歴テーブルには、少なくとも `operationId`、対応するエンティティの主キー、コマンドサービス ID、操作時刻を保持する。
- 履歴テーブルの主キーは `operationId` 単独ではなく、`operationId` と対象エンティティ主キーの組合せとする。
- 複数エンティティを更新するコマンドサービスでは、同一 `operationId`、同一 `commandServiceId`、同一 `operatedAt` を用いて対象件数分の履歴行を登録する。
- Query Service / Query Repository を用いる参照系処理では、履歴登録を行わない。

---

## 7. 例外・エラー処理の方針

- 業務上想定される失敗と、システム上の異常を区別して扱う。
- 業務エラーは RuntimeException 派生で表現する。
- 例外は握りつぶさず、適切な責務を持つ箇所で判断、通知、記録する。
- Controller での広範な `try-catch` は原則禁止とする。
- 呼び出し元に伝えるべき失敗は、利用者または上位処理が判断可能な形で表現する。

---

## 8. 共通実装原則

- 同じ意図の処理が複数箇所に分散しないようにする。
- 可読性よりも短さを優先しない。
- コメントは、コードだけでは意図が伝わりにくい判断や前提に限定する。
- 暗黙の前提に依存せず、必要な条件は構造または文書で明示する。
- 時刻依存の処理は `LocalDate.now()`、`LocalDateTime.now()`、`Instant.now()` などの直書きに寄せず、テスト時に時刻を固定できる構造を正とする。
- 採用理由と設計判断の背景は `docs/99_adr/ADR-004_current-time-provider-adoption.md` を参照する。
- 業務コードから現在時刻を取得する場合は、`net.shlab.hogefugapiyo.framework.core.time.CurrentTimeProvider` を利用する。
- `CurrentTimeProvider` の内部実装では、`net.shlab.hogefugapiyo.framework.core.configuration.TimeConfiguration` で定義する `Clock` Bean を利用する。
- `Clock` を直接扱うのは framework 共通部に限定し、Application Service、Command Service、Controller 等の業務コードでは `CurrentTimeProvider` を注入して利用する。
- Pure Service で時刻が必要な場合は、`CurrentTimeProvider` を直接注入せず、呼び出し元から値または引数として受け取る。
- 同一ユースケース内で時刻取得が複数箇所に分散しないよう、取得責務を明確にする。
- テスト容易性を損なうため、業務コードでの `System.currentTimeMillis()` の直書きは原則禁止とする。
- 業務操作単位の相関 ID は `operationId` とし、framework 共通部のコンテキスト機構で管理する。
- `operationId` は Application Service 呼び出し時に横断処理で生成・設定し、業務コードから UUID を都度生成してはならない。
- `operationId` の取得が必要な場合は framework 共通部の Holder / Context を参照し、`ThreadLocal` を業務コードで直接定義・操作してはならない。
- `operationId` はログ、内部トレース、および履歴テーブルの追跡キーとして利用し、画面表示値、メッセージ本文、永続化対象の業務データへ流用してはならない。
- 設計・運用上の ID は文書やログ上の識別に用い、意味のない短縮名や曖昧な名称を実装へ持ち込まない。

---

## 9. JavaDoc 記述規約

### 9.1 基本方針

- 本リポジトリでは、設計書（`docs/**`）を正とする。
- Java クラスの JavaDoc は、設計書の代替として詳細な業務仕様を記載してはならない。
- JavaDoc の主目的は、該当クラスが参照すべき設計書への導線を明示することとする。
- JavaDoc は、クラスの責務を補足する最小限の記述に留める。

### 9.2 対象クラス

- 以下のクラスには、原則として対応する設計書への参照を JavaDoc に記載する。
- Controller
- 画面用アプリケーションサービス
- コマンドサービス
- クエリサービス
- ピュアサービス
- エンティティ
- エンティティリポジトリ
- クエリリポジトリ

### 9.3 JavaDoc に記載する内容

- JavaDoc には、最低限以下を記載する。
- クラスの責務を 1〜2 行程度で簡潔に記載する。
- 本クラスが依存または委譲する主要な役割を、必要な場合に限って記載する。
- 対応する設計書へのパスを `docs/**` 配下で明示する。
- インターフェースを実装するクラスおよびメソッドの JavaDoc には、
  原則として `{@inheritDoc}` を用いてインターフェース側の記述を継承すること。
- 実装クラス固有の責務や振る舞いがある場合のみ、
  `{@inheritDoc}` に続けて補足を記載してよい。
- 業務仕様や設計判断の詳細は JavaDoc に記載せず、
  対応する設計書（docs/**）を参照させること。

### 9.4 JavaDoc に記載してはならない内容

- 設計書に記載されている業務仕様や業務ルールの詳細
- 設計判断の理由や経緯（必要な場合は ADR に記載する）
- 実装手順や内部処理の詳細
- コードを読めば明らかな自明な処理説明

### 9.5 運用上の注意

- JavaDoc は設計書へのナビゲーションとして最小限に保つ。
- 実装クラスでは、インターフェースと同一内容を重複記載せず、継承可能な説明は `{@inheritDoc}` を優先する。
- 設計内容に変更が生じた場合は、JavaDoc を先に修正してはならない。
- 必ず設計書を修正し、その後 JavaDoc のリンク先が正しいことを確認する。
- JavaDoc と設計書の内容が乖離してはならない。

---

## 10. 画面実装と i18n の規約

### 10.1 テンプレート配置

- テンプレートは `src/main/resources/templates/` 配下に配置する。
- 再利用用テンプレート（Fragment）は `templates/fragments/` 配下に配置する。
- i18n 資材は `src/main/resources/i18n/` 配下に配置する。

### 10.2 Controller との対応

- 1 Controller = 1 HTML テンプレートを原則とする。
- Controller の画面 ID とテンプレート名は一致させる。
- Controller から返却する view 名は拡張子を除いた値とする。
- 業務画面の view 名は `net.shlab.hogefugapiyo.equipmentlending.presentation.views.Views` の定数を参照する。

### 10.3 i18n 資材

- 画面項目ラベルは `labels_ja.properties` に定義する。
- 業務メッセージは `message_ja.properties` に定義する。
- MessageSource の `basename` は `i18n/labels`、`i18n/message` を参照する。
- Spring の読込基点として `labels.properties`、`message.properties` を併置してよい。
- ラベルおよび管理対象メッセージは `src/main/resources/i18n/` 配下のプロパティに定義し、Java コードおよび HTML テンプレートへ文言を直書きしない。
- 画面固有ラベルキーは `screen.{画面ID由来の識別子}.*` 形式で定義する。
- 共通ラベルキーは `label.*` 形式で定義する。
- 画面 ID 由来の識別子は、`HFP-EL-V100` の場合 `elv100` のようにシステム接頭辞を除いた画面 ID を用いる。
- 管理対象の業務メッセージは `MSG_I_XXX`、`MSG_W_XXX`、`MSG_E_XXX` の ID で管理する。
- 管理対象の業務メッセージ文言は、ですます調で定義する。
- 単項目バリデーション以外の管理対象メッセージを画面表示する際は、`[メッセージID]メッセージ本文` の形式で表示する。
- 単項目バリデーションメッセージは ID を付与せず、対象入力項目の側に表示する。
- 単項目バリデーション以外の管理対象メッセージは、メッセージ定義から解決して利用する。
- ログイン失敗メッセージ等の基盤画面メッセージは管理対象外とし、ID を付与しない。
- 単項目バリデーションメッセージも、ソースコードへ直書きせずメッセージプロパティから解決する。

### 10.4 UI スタイル

- 業務画面の見た目は `docs/04_implementation/ui-style.md` を正として統一する。
- 共通スタイルは `src/main/resources/static/css/business-common.css` に集約する。
- 画面ごとの独自スタイル追加は、共通 CSS の責務を崩さない範囲で行う。

### 10.5 セキュリティ前提

- Web アプリケーションのセキュリティ前提は Spring Security で横断的に構成する。
- CSRF 対策は有効を正とし、更新系リクエストで無効化を前提にしない。
- `POST`、`PUT`、`PATCH`、`DELETE` を送信する画面テンプレートでは、Spring Security が提供する CSRF トークンを送信する。
- 重複送信防止用のワンタイムトークンは、CSRF トークンとは別目的の値として扱い、更新系フォームごとに送信する。
- ワンタイムトークンの生成・消費ロジックは共通部へ集約し、個別 Controller で独自実装しない。
- セキュリティ設定は framework / configuration 配下の共通設定へ集約し、業務 Controller や Service に分散させない。
- 認証・認可の詳細は学習用として簡易化してよいが、業務ロジックとは分離して扱う。

---

## 11. レビュー観点

- 実装が設計上の責務分担と整合しているか。
- 依存関係が不必要に複雑化していないか。
- 状態変更、エラー処理、境界条件への考慮が不足していないか。
- JavaDoc が設計書への導線として過不足なく記載されているか。
- 画面実装が i18n 規約および UI スタイル規約と整合しているか。
- 一時的な実装や局所最適な回避策に偏っていないか。

---

## 12. 変更管理

- 実装規約の変更は、既存コードへの影響範囲と移行方針を考慮して行う。
- 大きな方針変更が生じた場合は、理由と背景を ADR として記録する。
- 本書の変更時は、関連する `docs/03_designs/`、`docs/04_implementation/` 配下文書との整合を確認する。
