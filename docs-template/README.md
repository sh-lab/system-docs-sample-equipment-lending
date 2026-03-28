# docs-template

本ディレクトリは、業務システム開発において使用する  
**要件定義・アーキテクチャ・設計ドキュメント**および  
**実装を支援する補足情報のテンプレート**を格納します。

本ディレクトリ配下の内容はすべて **テンプレート（雛形）** であり、  
特定プロジェクトの完成成果物は含みません。

本ディレクトリ（docs-template）に含まれるテンプレート文書は、
system-docs-template リポジトリ全体に適用される
Creative Commons Attribution 4.0 International（CC BY 4.0）
ライセンスの下で提供されます。

## ディレクトリ構成

```text
docs-template/
├─ 01_requirements/
│  ├─ business-and-scope.md
│  ├─ functional-requirements.md
│  ├─ non-functional-requirements.md
│  ├─ glossary.md
│  └─ use-cases/
│     ├─ use-case-list.md
│     └─ use-case.md
│
├─ 02_architecture/
│  ├─ layering-overview.md
│  ├─ transaction-boundaries.md
│  ├─ error-handling.md
│  └─ data-architecture-policy.md
│
├─ 03_designs/
│  ├─ ui/
│  │  ├─ screen-list.template.md
│  │  └─ screen-design.template.md
│  ├─ services/
│  │  ├─ application-service.template.md
│  │  ├─ command-service.template.md
│  │  ├─ query-service.template.md
│  │  └─ pure-service.template.md
│  ├─ entity/
│  │  └─ entity-design.template.md
│  └─ data/
│     └─ table-design.template.md
│
├─ 04_implementation/
│  ├─ README.md
│  ├─ implementation-rules.template.md
│  ├─ naming-conventions.template.md
│  └─ testing-policy.template.md
│
└─ 99_adr/
   ├─ README.md
   └─ adr-template.md
```

---

## 各ディレクトリの役割

### 01_requirements
- ビジネス要件、機能要件、非機能要件を定義する
- ユースケースを中心に「何を作るか」を明確にする

### 02_architecture
- レイヤ構成、トランザクション境界、例外方針など
- 設計上の判断基準・制約を定義する
- 実装方法の詳細は記載しない

### 03_designs
- 実装前段階の 設計表現の型（テンプレート） を提供する
- 画面一覧、画面設計、サービス、エンティティ、テーブル設計をカバー
- 実装コードや具体的な SQL 等は含めない

### 04_implementation
- 設計ドキュメントを前提に、実装へ落とし込む際の補助情報を置く
- 実装そのものの設計を定義する場ではなく、実装支援を目的とする
- 具体的な規約やサンプル、Copilot への指示は派生プロジェクト側で拡張する
- 実装ルール、命名規約、テスト方針などのテンプレートを格納する

### 99_adr
- 設計上の重要な判断理由（Architecture Decision Record）を記録する
- 方針の背景や代替案を残すための補助ディレクトリ
