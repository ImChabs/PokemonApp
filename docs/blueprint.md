# Project Blueprint

## Purpose

This project is a native Android practice app focused on learning and validating HTTP networking with Ktor against a real public API.

The product goal is educational and technical: build a small but realistic Pokémon app that exercises request execution, response parsing, DTO modeling, optional mapping, error handling, timeout configuration, loading/error/success UI states, and manual retry flows.

Keep this document project-specific. `AGENTS.md` holds durable repository rules; this blueprint holds the current product direction and scope for this repository.

## Product Direction

Optimize for a small, clean, and technically honest Android app that is useful for practicing networking fundamentals without unnecessary product complexity.

For this project:
- keep the scope intentionally small
- prioritize networking quality over feature breadth
- prefer simple UX that clearly exposes loading, error, empty, and success states
- use real API consumption instead of fake-only flows, while keeping tests deterministic
- add abstraction only when it provides clear value

## Core Scope

The current scope is limited to a Pokémon app that demonstrates:

- Ktor HTTP client setup in Android
- GET requests to a public Pokémon API
- DTO-based response parsing
- mappers only where they reduce coupling or improve clarity
- repository-level error handling
- timeout handling
- loading, success, and error UI states
- manual retry from the UI
- search by Pokémon name
- targeted unit tests for important logic
- targeted Compose UI tests for important visible states

## Initial Scope Or MVP

The first meaningful version of this repository should include:

- a screen that shows a paginated Pokémon list
- a search flow by Pokémon name
- a Pokémon detail screen
- real loading and error states in the UI
- a manual retry action for failed requests
- timeout configuration in the Ktor client
- clear domain/UI models where direct DTO usage would be too coupled
- unit tests for important repository/state logic
- Compose UI tests for important screen states and interactions

## Priorities

1. Correct and understandable networking flow
2. Clear state handling in the UI
3. Strong error handling and timeout behavior
4. Small, maintainable architecture
5. Relevant tests only
6. Minimal overengineering

## Out Of Scope For Now

- authentication
- POST/PUT/DELETE flows
- local persistence or offline-first behavior
- background sync
- push notifications
- multi-module architecture
- aggressive caching strategies
- advanced design/polish work
- complex search/filter combinations beyond name search
- broad test coverage for trivial code

## Technical Direction

- Keep the project single-module for now.
- Use native Android with Kotlin and Jetpack Compose.
- Use Ktor as the networking client.
- Use Kotlin serialization or another Ktor-compatible serialization approach as needed.
- Model network responses with DTOs.
- Introduce mappers only when they meaningfully improve separation between API models and app-facing models.
- Prefer a simple layered structure such as UI / data / domain-like boundaries without forcing unnecessary abstractions.
- Represent request states explicitly in presentation logic.
- Keep retry user-driven in the UI.
- Prefer deterministic unit tests for data/state logic and targeted Compose UI tests for visible behavior.
- Use fake data sources or Ktor MockEngine in tests when real network dependency would reduce reliability.

## Roadmap

- Phase 1: App foundation
  Set up app shell, Ktor client, base networking configuration, serialization, timeout support, and foundational models/state handling.

- Phase 2: Pokémon list and search
  Implement paginated list loading and search by Pokémon name, including loading, empty, and error states.

- Phase 3: Pokémon detail
  Implement detail screen with relevant Pokémon information from the API, keeping the data flow simple and explicit.

- Phase 4: Hardening and relevant tests
  Improve error handling, manual retry flows, and add targeted unit tests plus targeted Compose UI tests for important scenarios.

## Block Strategy

Implementation should happen in small blocks. Each block should produce a meaningful, verifiable improvement in the app.

Preferred block shape:
- one clear networking or UI milestone per block
- one small validation target per block
- tests only when the block introduces meaningful logic or a meaningful user-visible state

This blueprint provides persistent direction across multiple block-based chats:
- `handoff/next-block.md` defines the immediate next block
- this blueprint defines the broader destination and boundaries

If a future block conflicts with this blueprint, prefer the smaller and simpler implementation that still satisfies the learning goals.

## Success Criteria

This repository is successful if it becomes:

- a clean Ktor networking practice project
- easy to understand and extend
- able to demonstrate real request/response handling end to end
- explicit about loading, error, and retry behavior
- covered by a small but relevant set of unit tests and Compose UI tests
- free of unnecessary architectural complexity

## Maintenance Notes

- Update this document when the project direction changes in a durable way.
- Keep this file focused on product/technical direction, not task history.
- Keep temporary execution details and progress tracking in handoff artifacts, not here.
- Do not expand scope casually; protect the networking-focused learning goal.