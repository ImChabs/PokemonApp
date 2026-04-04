Next block name
BLOCK 004 - Pokemon Detail Screen

Objective
Add a bounded Pokemon detail flow so the user can open a Pokemon from the home screen and see a dedicated detail view backed by a direct remote lookup with explicit loading, success, and error UI states.

Relevant files
- AGENTS.md
- docs/blueprint.md
- handoff/validation-report.md
- app/src/main/java/com/example/pokemonapp/MainActivity.kt
- app/src/main/java/com/example/pokemonapp/pokemon/presentation/home/PokemonHomeScreen.kt
- app/src/main/java/com/example/pokemonapp/pokemon/domain/PokemonRemoteDataSource.kt
- app/src/main/java/com/example/pokemonapp/pokemon/data/remote/KtorPokemonRemoteDataSource.kt
- app/src/main/java/com/example/pokemonapp/pokemon/data/remote/PokemonMappers.kt
- app/src/main/java/com/example/pokemonapp/pokemon/data/remote/dto/
- app/src/main/java/com/example/pokemonapp/pokemon/presentation/detail/
- app/src/test/java/com/example/pokemonapp/pokemon/data/remote/KtorPokemonRemoteDataSourceTest.kt
- app/src/test/java/com/example/pokemonapp/pokemon/presentation/detail/

Constraints
- Keep the project single-module and single-activity
- Reuse the existing `HttpClientFactory`, `Result`, `DataError`, and `PokemonRemoteDataSource` foundation instead of introducing a repository layer
- Keep the current first-page list and submitted-name search behavior intact on the home screen
- Keep detail lookup bounded to a single selected Pokemon at a time
- Make detail loading, success, and error state explicit in presentation logic
- Keep validation targeted to the changed production and test sources

What not to change
- Do not broaden into pagination, infinite loading, or caching work
- Do not add local persistence, offline behavior, or a DI framework
- Do not redesign the home screen beyond the minimum interaction needed to open the detail flow
- Do not add broad or ceremonial tests outside the detail flow

Done criteria
- The user can open a Pokemon detail screen from a home-screen Pokemon item
- The home search result can also open the same detail flow without duplicating detail logic
- `PokemonRemoteDataSource` supports a dedicated detail lookup with DTO mapping appropriate for the detail UI
- The detail presentation keeps explicit loading, success, and error state
- Focused tests cover the new detail data or presentation behavior
- `handoff/validation-report.md` records the actual validation run(s) used for the block

## Execution Recommendation
- Recommended reasoning effort: high
- Recommended execution mode: plan_first
- Rationale: The next block is still bounded, but it now needs coordinated data, presentation, and navigation decisions to add detail behavior without destabilizing the new home flow.
