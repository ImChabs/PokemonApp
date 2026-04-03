Next block name
BLOCK 003 - Pokemon Name Search

Objective
Add a bounded name-search flow to the home feature so the user can submit a Pokemon name, trigger a dedicated remote lookup, and see explicit search loading, success, empty, and error feedback without replacing the existing first-page list behavior.

Relevant files
- AGENTS.md
- docs/blueprint.md
- handoff/validation-report.md
- app/src/main/java/com/example/pokemonapp/pokemon/presentation/home/PokemonHomeViewModel.kt
- app/src/main/java/com/example/pokemonapp/pokemon/presentation/home/PokemonHomeState.kt
- app/src/main/java/com/example/pokemonapp/pokemon/presentation/home/PokemonHomeScreen.kt
- app/src/main/java/com/example/pokemonapp/pokemon/domain/PokemonRemoteDataSource.kt
- app/src/main/java/com/example/pokemonapp/pokemon/data/remote/KtorPokemonRemoteDataSource.kt
- app/src/main/java/com/example/pokemonapp/pokemon/data/remote/dto/
- app/src/test/java/com/example/pokemonapp/pokemon/presentation/home/PokemonHomeViewModelTest.kt
- app/src/test/java/com/example/pokemonapp/pokemon/data/remote/KtorPokemonRemoteDataSourceTest.kt

Constraints
- Keep the project single-module and single-activity
- Reuse the existing `HttpClientFactory`, `Result`, and `DataError` foundation instead of replacing them
- Keep the existing first-page load intact on launch
- Keep search bounded to one submitted Pokemon name at a time
- Make the added search state explicit in presentation logic
- Keep validation targeted to the changed production and test sources

What not to change
- Do not add navigation or a detail screen yet
- Do not broaden into pagination or infinite loading
- Do not introduce a repository layer unless the implementation genuinely starts combining multiple data sources
- Do not add local persistence, caching, or DI framework wiring

Done criteria
- The home UI exposes a simple name-search input and submit action
- `PokemonRemoteDataSource` supports a dedicated Pokemon name lookup needed by the home flow
- `PokemonHomeViewModel` keeps the initial first-page list behavior and also drives explicit search loading, success, empty, and error state
- `PokemonHomeScreen` renders the search result state clearly without breaking the existing first-page list section
- Focused tests cover the new search presentation or remote-data behavior
- `handoff/validation-report.md` records the actual validation run(s) used for the block

## Execution Recommendation
- Recommended reasoning effort: medium
- Recommended execution mode: direct
- Rationale: The next step is now specific and bounded because the initial list load is in place and search can build directly on the current home feature.
