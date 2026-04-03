Next block name
BLOCK 002 - Pokemon List First Page

Objective
Wire the seeded Pokemon remote data source into the home presentation layer so the app loads the first page of Pokemon on launch and renders explicit loading, success, and error UI states.

Relevant files
- AGENTS.md
- docs/blueprint.md
- handoff/validation-report.md
- app/src/main/java/com/example/pokemonapp/pokemon/presentation/home/PokemonHomeViewModel.kt
- app/src/main/java/com/example/pokemonapp/pokemon/presentation/home/PokemonHomeState.kt
- app/src/main/java/com/example/pokemonapp/pokemon/presentation/home/PokemonHomeScreen.kt
- app/src/main/java/com/example/pokemonapp/pokemon/domain/PokemonRemoteDataSource.kt
- app/src/main/java/com/example/pokemonapp/pokemon/data/remote/KtorPokemonRemoteDataSource.kt
- app/src/test/java/com/example/pokemonapp/pokemon/data/remote/KtorPokemonRemoteDataSourceTest.kt

Constraints
- Keep the project single-module and single-activity
- Reuse the existing `HttpClientFactory`, `Result`, and `DataError` foundation instead of replacing them
- Keep the list scope to the first page only; pagination controls and infinite loading can wait
- Make request state explicit in presentation logic
- Keep validation targeted to the changed production and test sources

What not to change
- Do not add navigation or a detail screen yet
- Do not add search in the same block
- Do not introduce a repository layer unless the implementation genuinely starts combining multiple data sources
- Do not add local persistence, caching, or DI framework wiring

Done criteria
- `PokemonHomeViewModel` requests the first page from the remote data source during initial load
- `PokemonHomeScreen` renders distinct loading, success, and error states
- The success state shows the first page of Pokemon names from the real API contract
- A focused test covers the new presentation or data logic introduced for the block
- `handoff/validation-report.md` records the actual validation run(s) used for the block

## Execution Recommendation
- Recommended reasoning effort: medium
- Recommended execution mode: direct
- Rationale: The next block is now specific and bounded because the networking and presentation foundations already exist.
