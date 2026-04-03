# 認証・認可方針

## 1. 文書の目的
本書は、本システムにおける認証・認可のアーキテクチャ方針を定義し、
要件定義書および設計書の責務分担を明確にすることを目的とする。

---

## 2. 適用範囲
- 認証・認可基盤の採用方針
- 認可判定を配置する層
- 利用者区分の扱い
- 要件、アーキテクチャ、設計の責務分担

---

## 3. 認証・認可設計方針
- 本システムでは、認証・認可基盤として Spring Security を採用する。
- 認可判定は Controller 層で完結させ、業務サービス層では行わない。
- 利用者区分は `USER` / `ADMIN` のロールで表現する。
- 業務画面に対する認証・認可は、Controller や業務サービスへ個別実装を分散させず、横断的なセキュリティ設計として統制する。

---

## 4. 文書責務
- 要件定義書では、画面の利用可否や権限区分など、業務上満たすべき条件を定義する。
- 本書では、どの基盤を採用し、どの層に責務を置くかというアーキテクチャ上の方針を定義する。
- 具体的な認証コンテキスト、画面別認可方法、権限不足時制御などの詳細設計は `docs/03_designs/security-design.md` に定義する。
- 個別画面設計書では、当該画面に固有の利用条件や業務制約を記述し、共通の認証・認可設計は重複定義しない。

---

## 5. 横断制御方針
- 原則として、未認証アクセスを許可する対象は `login` 画面およびログイン処理のみとする。
- ただし本サンプルでは、教育用の導線簡素化および実行補助のため、`/`、静的リソース、`/h2-console/**` を未認証アクセス可能な例外として許可する。
- 上記例外は学習用途に限定した取り扱いであり、業務画面の利用条件を緩和するものではない。
- `login` を除く業務画面は、認証済み利用者のみを対象とする。
- 利用者向け画面は `USER`、管理者向け画面は `ADMIN` を前提とする。
- 認証済み利用者情報は Spring Security の認証コンテキストを正とし、独自の `HttpSession` 属性を認証・認可判定の正としない。
- 認証済み利用者の識別情報は Controller が取得し、必要な業務サービスへ引き渡す。
- 未認証や権限不足時の制御は、セキュリティ設定または専用ハンドラへ集約する。

---

## 6. 関連資料
- `docs/01_requirements/functional-requirements.md`
- `docs/03_designs/security-design.md`
- `docs/03_designs/ui/screen-list.md`
- `docs/03_designs/ui/HFP-EL-V100_user-mypage.md`
- `docs/03_designs/ui/HFP-EL-V200_admin-mypage.md`
- `docs/03_designs/ui/HFP-EL-V300_equipment-search.md`
- `docs/03_designs/ui/HFP-EL-V400_user-lending-request.md`
- `docs/03_designs/ui/HFP-EL-V500_admin-lending-review.md`
- `docs/99_adr/ADR-003_spring-security-csrf-adoption.md`
