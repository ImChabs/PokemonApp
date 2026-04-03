# Project Blueprint

## Purpose

This project is a neutral Android base used to validate and reuse agent-assisted development workflows. Its current purpose is operational rather than product-driven: provide just enough real Android surface area to exercise block handoffs, validation reporting, small compile and unit-test loops, and optional review-agent usage.

Keep this document project-specific. `AGENTS.md` holds durable repository rules; this blueprint holds the current product direction for this repository instance.

## Product Direction

Optimize for a clean, low-friction base that can support future Android projects without carrying product-specific assumptions.

For this operational trial:
- keep the app intentionally tiny
- prefer neutral naming and neutral copy
- use the smallest real code changes needed to prove the workflow works end-to-end

## Core Scope

The current scope is limited to a minimal Android app shell that is sufficient to test:
- block-based implementation flow
- handoff continuity between executions
- validation reporting
- targeted compile validation
- targeted unit-test validation
- optional review-agent usage after Level 1 completion

There is no active product feature scope yet beyond what is needed to keep the repository operational and verifiable.

## Initial Scope Or MVP

The first meaningful version of this repository instance should include:
- one neutral screen or app surface that compiles cleanly
- one tiny pure Kotlin behavior that can be covered by a unit test
- a working handoff flow from one block to the next
- a validation report that reflects what was actually run

## Priorities

1. Reusable workflow quality
2. Clear and accurate handoff artifacts
3. Minimal, low-impact code changes
4. Reliable targeted verification
5. Future product flexibility

## Out Of Scope For Now

- real product features
- navigation flows
- persistence
- networking
- authentication
- background work
- broad UI polish
- architecture depth beyond what the current block needs

## Technical Direction

- Keep the project single-module for now.
- Keep the app local-only and offline by default.
- Prefer pure Kotlin logic when a unit-test target is needed.
- Add Android-specific structure only when a future project direction actually requires it.

## Roadmap

- Phase 1: Operational base validation
  Prove that the repository workflows, handoffs, validation scripts, and review agent work in real usage.
- Phase 2: Minimal reusable app shell
  Keep a tiny neutral Android shell that future blocks can extend without undoing the operational setup.
- Phase 3: Future project specialization
  Allow a later project to replace this minimal direction with real product goals while preserving the reusable workflow layer.

## Block Strategy

Implementation should happen in small blocks. For the current phase, each block should primarily improve confidence in the reusable workflow layer, not expand the app into a real product.

This blueprint provides persistent direction across multiple block-based chats:
- `handoff/next-block.md` defines the immediate next block
- this blueprint defines the broader destination and boundaries

If a future block conflicts with this blueprint, prefer the smaller and more operationally focused interpretation unless the product direction is intentionally updated.

## Success Criteria

This repository instance is successful if it remains:
- maintainable
- neutral
- easy to verify
- usable as a starting point for future Android projects with agent-assisted workflows

## Maintenance Notes

- Update this document when the product direction changes in a durable way.
- Do not use this file as a task log or archive.
- Keep historical implementation details in handoff artifacts, not here.
