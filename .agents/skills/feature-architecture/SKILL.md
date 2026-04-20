---
name: feature-architecture
description: Product feature architecture guide. Use when creating, modifying, reviewing, or wiring feature modules under features/*, especially tasks involving API/Impl split, Decompose components, Metro DI graphs, KMP source sets, data/domain/presentation layers, RemoteDataSource, LocalDataSource, DataStore, repositories, use cases, or feature navigation contracts.
---

# TastyHome Feature Architecture

## Purpose

Use this skill as the implementation playbook for product features. Keep project-wide invariants in the root `AGENTS.md`; keep detailed feature rules and examples in the reference files below.

## Workflow

1. Classify the change: new feature, API change, impl change, shared wiring, data layer, domain layer, presentation layer, DI, storage, or navigation.
2. Re-read the relevant root project rules from `AGENTS.md` before editing module dependencies or public contracts.
3. Load only the reference files needed for the task.
4. Prefer existing project patterns over introducing new abstractions.
5. Before finishing, run through `references/implementation-checklist.md`.

## References

- Feature overview and module boundaries: `references/feature-overview.md`
- API module rules: `references/feature-api.md`
- Impl module structure: `references/feature-impl-structure.md`
- Data layer rules: `references/data-layer.md`
- Repository rules: `references/repositories.md`
- DTO model rules: `references/dto-models.md`
- Mapper rules: `references/mappers.md`
- Domain layer rules: `references/domain-layer.md`
- Domain model rules: `references/domain-models.md`
- Use case rules: `references/usecases.md`
- Presentation layer rules: `references/presentation-layer.md`
- Decompose navigation rules: `references/navigation-decompose.md`
- Metro DI rules: `references/metro-di.md`
- RemoteDataSource rules: `references/remote-datasource.md`
- LocalDataSource rules: `references/local-datasource.md`
- DataStore rules: `references/datastore.md`
- Final implementation checklist: `references/implementation-checklist.md`

## Editing This Skill

Keep this `SKILL.md` short. Put detailed conventions, examples, anti-examples, snippets, and checklists into `references/`.
