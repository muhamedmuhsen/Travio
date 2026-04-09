# Plan: Search Screen — `feature/search` Module

Build a fully featured Search screen (as shown in the mockup) following the same architectural
patterns used across Travio: `UiState` sealed interface, `UiText`, `Channel`-based events,
`hiltViewModel`, and `TravioTheme` tokens. The screen covers two visual states — **idle/recent** (
search bar + recent destinations list + suggestions grid) and **results** (search bar + result list
with category chips).

---

## Steps

### 1. Create `feature/search` module

Create `feature/search/build.gradle.kts` mirroring [
`feature/home/build.gradle.kts`](feature/home/build.gradle.kts) (same plugins: `android.library`,
`kotlin.android`, `kotlin.compose`, `hilt`, `ksp`; same dependencies: `:domain`, `:data`,
`:core:designsystem`, `:feature:utils`, Compose BOM, Hilt, Coil). Wire the module into [
`settings.gradle.kts`](settings.gradle.kts) with `include(":feature:search")`.

### 2. Define state, events & actions

Create three files in `com.dev.search.presentation`:

**`SearchUiState.kt`**

```kotlin
data class SearchUiState(
    val query: String = "",
    val recentDestinations: List<Destination> = emptyList(),
    val suggestionsState: UiState<List<Destination>> = UiState.Idle,
    val searchResultsState: UiState<List<Destination>> = UiState.Idle
)
```

- `recentDestinations` is always a plain list (driven by a live Room Flow — no loading/error
  needed).
- `suggestionsState` and `searchResultsState` are separate to prevent impossible combined states (
  e.g., loading=true AND error=true simultaneously).
- When `query.isBlank()`, only `recentDestinations` + `suggestionsState` are shown; when
  `query.isNotBlank()`, only `searchResultsState` is shown.

**`SearchEvent.kt`**

```kotlin
sealed interface SearchEvent {
    data class NavigateToDestination(val id: String) : SearchEvent
    data object NavigateBack : SearchEvent
    data class ShowErrorSnackbar(val message: UiText) : SearchEvent
}
```

**`SearchAction.kt`**

```kotlin
sealed interface SearchAction {
    data class OnQueryChanged(val query: String) : SearchAction
    data class OnDestinationClicked(val id: String, val destination: Destination) : SearchAction
    data object OnBackClicked : SearchAction
    data object OnClearQuery : SearchAction
    data object OnSeeAllRecentClicked : SearchAction
}
```

### 3. Implement `SearchViewModel`

Inject `SearchForDestinationsUseCase`, `GetRecentlyViewedUseCase`, `GetAllDestinationsUseCase`, and
`AddToRecentlyViewedUseCase`.

**Key implementation details:**

- Observe `getRecentlyViewedUseCase()` as a `Flow` in `init {}`, updating `recentDestinations` (
  mirrors `HomeViewModel.observeRecentlyViewed`).
- Load suggestions via `getAllDestinationsUseCase` (page 1, size 6) in `init {}` using the shared
  `launchLoad` helper pattern.
- For search: expose a `MutableStateFlow<String>` for the query; use `debounce(300)` +
  `flatMapLatest` to cancel in-flight requests on each new keystroke — avoids race conditions and
  stale data winning.
- Guard against empty query client-side (skip network call, reset `searchResultsState` to
  `UiState.Idle`) in addition to the use-case validation.
- On destination click: call `addToRecentlyViewedUseCase(destination)` before sending
  `NavigateToDestination` event (mirrors `HomeViewModel.navigateToDestination`).
- Use `Channel.UNLIMITED` for the event channel.
- Use `viewModelScope` + `Dispatchers.Main` (default) for all coroutines; the use cases handle
  dispatcher switching internally.

```kotlin
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchForDestinationsUseCase: SearchForDestinationsUseCase,
    private val getRecentlyViewedUseCase: GetRecentlyViewedUseCase,
    private val getAllDestinationsUseCase: GetAllDestinationsUseCase,
    private val addToRecentlyViewedUseCase: AddToRecentlyViewedUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private val _event = Channel<SearchEvent>(Channel.UNLIMITED)
    val event = _event.receiveAsFlow()

    init {
        observeRecentDestinations()
        loadSuggestions()
        observeQueryForSearch()
    }

    fun onAction(action: SearchAction) {
        ...
    }

    private fun observeQueryForSearch() {
        viewModelScope.launch {
            _uiState
                .map { it.query }
                .distinctUntilChanged()
                .debounce(300)
                .collectLatest { query ->
                    if (query.isBlank()) {
                        _uiState.update { it.copy(searchResultsState = UiState.Idle) }
                    } else {
                        performSearch(query)
                    }
                }
        }
    }
    // ...
}
```

### 4. Build composable components

Create in `com.dev.search.components`:

#### `SearchTopBar`

- Row with: back `IconButton` (chevron left icon, `contentDescription = "Back"`, min 48 dp touch
  target) + `TextField` (search icon leading, placeholder "Exploration", no underline border,
  `MaterialTheme.colorScheme.surface` container, rounded shape).
- Auto-focus the text field on composition via
  `LaunchedEffect(Unit) { focusRequester.requestFocus() }`.
- Query stored in ViewModel `StateFlow` (single source of truth — do NOT also use
  `rememberSaveable`).
- Show a clear (`×`) trailing icon when `query.isNotBlank()`.

#### `RecentSearchItem`

- Full-width `Row` (min height 48 dp), location-pin icon (`MaterialTheme.colorScheme.primary`),
  destination `name` in `titleSmall`, `cityName` in `bodySmall` / `onSurfaceVariant`.
- `clickable` modifier **before** `padding` so the ripple covers the full touch target.
- `contentDescription` on the pin icon: `"Location pin"`.

#### `SearchResultItem`

- `Row` with fixed-size thumbnail (80×80 dp, `clip(MaterialTheme.shapes.medium)`, `AsyncImage` with
  shimmer placeholder + error drawable), then a `Column`: destination `name` (`titleMedium`),
  `cityName` (`bodySmall`, `onSurfaceVariant`), first `Interest.interestName` as a`SuggestionChip` (
  teal primary container background, `labelSmall` text). If `interests` is empty, omit the chip.
- `contentDescription` on the thumbnail = destination name.

#### `LoadingSearchResultItem`

- Shimmer skeleton matching `SearchResultItem` layout exactly (80×80 dp box shimmer + two text line
  boxes + a chip-sized box), using the shared `Modifier.shimmerEffect()` extension from
  `core:designsystem`.

#### Reuse for Suggestions

- Reuse `DestinationCard` + `LoadingDestinationCard` from `feature:home` components in a
  horizontal-scrolling `LazyRow` (same pattern as Home's `HorizontalSection`).
- **Note:** Either extract these cards into `core:designsystem` or add `:feature:home` as a
  dependency of `:feature:search`. Prefer extraction to avoid circular dependency risk; raise this
  as a decision point.

### 5. Build `SearchScreen` composable

Split into a stateful entry-point and a stateless content composable:

```kotlin
// Stateful entry-point — only talks to ViewModel
@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    navigateBack: () -> Unit = {},
    navigateToDestination: (String) -> Unit = {},
    viewModel: SearchViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when (event) {
                is SearchEvent.NavigateToDestination -> navigateToDestination(event.id)
                SearchEvent.NavigateBack -> navigateBack()
                is SearchEvent.ShowErrorSnackbar ->
                    snackbarHostState.showSnackbar(event.message.asString(context))
            }
        }
    }

    SearchContent(
        state = state,
        snackbarHostState = snackbarHostState,
        onAction = viewModel::onAction,
        modifier = modifier
    )
}
```

**`SearchContent` layout:**

- `Scaffold` with `snackbarHost`.
- Top area: `SearchTopBar` (always visible, `WindowInsets` top padding for edge-to-edge).
- Body: `LazyColumn` with:
    - **When `query.isBlank()`:**
        - `item` → "Recent" section header row with label + "See all" text button (if list
          non-empty)
        - `items(recentDestinations, key = { it.destinationID })` → `RecentSearchItem`
        - `item` → "Suggestions" section header
        - Suggestions state handling:
            - `UiState.Idle` → nothing
            - `UiState.Loading` → 3× `LoadingDestinationCard` in a `LazyRow`
            - `UiState.Error` → inline error with retry button
            - `UiState.Success` → `LazyRow` of `DestinationCard`s
    - **When `query.isNotBlank()`:**
        - Search results state handling:
            - `UiState.Idle` → nothing
            - `UiState.Loading` → 5× `LoadingSearchResultItem`
            - `UiState.Error` → full-width error message + retry button
            - `UiState.Success` with empty list → empty state illustration + "No results for…"
              message
            - `UiState.Success` with data → `items(results, key = { it.destinationID })` →
              `SearchResultItem`
- Use `key` on all `items` calls to ensure correct recomposition and avoid off-by-one animation
  bugs.
- Use `derivedStateOf` if computing derived display values inside the composable that depend on
  state (e.g., `isQueryActive = remember { derivedStateOf { state.query.isNotBlank() } }`).

### 6. Wire navigation

In [`Navigation.kt`](app/src/main/java/com/example/travio/Navigation.kt), replace the placeholder:

```kotlin
// Before
composable(Screen.SearchScreen.route) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Search – coming soon")
    }
}

// After
composable(Screen.SearchScreen.route) {
    SearchScreen(
        navigateBack = { navController.popBackStack() },
        navigateToDestination = { id ->
            navController.navigate(Screen.DestinationDetailScreen.route + "/$id")
        }
    )
}
```

Add the `SearchScreen` import from `com.dev.search.presentation`.

---

## Considerations & Open Questions

### 1. Debounce vs. explicit submit

The mockup shows results appearing after the user types "Egypt" without pressing search. The plan
uses **debounced keystroke search (300 ms)**. However, the existing `HomeSearchBar` uses
`ImeAction.Search` + `onSearch` callback. Confirm which UX is preferred before implementation. If
both are needed, the ViewModel's `onAction(OnQueryChanged)` naturally handles both — the debounce
fires on typing and a direct `performSearch(query)` call (bypassing debounce) fires on IME submit.

### 2. "Suggestions" data source

The idle screen shows curated suggestion cards (Santorini, Cappadocia, Maldives). Three options:

- **Option A:** Reuse `GetAllDestinationsUseCase` (page 1, size 6) — simplest, already available.
- **Option B:** Use `getTopRatedDestinationsUseCase` — more semantically correct (already exists in
  `DestinationsRepository`).
- **Option C:** Hardcode — not recommended; breaks dark mode and i18n.
  **Recommendation: Option B.**

### 3. Reusing `DestinationCard` from `feature:home`

`DestinationCard` and `LoadingDestinationCard` live in `feature:home`. Options:

- **Option A:** Extract them to `core:designsystem` (preferred long-term).
- **Option B:** Add `:feature:home` as a dependency of `:feature:search` (quick, but creates
  inter-feature coupling).
- **Option C:** Duplicate the cards in `feature:search` (avoid — violates DRY).
  **Recommendation: Option A — extract shared destination/place card components
  to `core:designsystem` as part of this task.**

### 4. State source of truth for query

The query is stored in `SearchViewModel._uiState.query` (a `StateFlow`). Do NOT also store it in
`rememberSaveable` in the composable — that creates two sources of truth. If process-death
resilience is needed, add `SavedStateHandle` to the ViewModel.

### 5. Accessibility

- All `AsyncImage` calls must have `contentDescription` set to the destination name.
- The back button `IconButton` must have
  `contentDescription = stringResource(R.string.navigate_back)`.
- The clear query button must have `contentDescription = stringResource(R.string.clear_search)`.
- `SuggestionChip` label has implicit semantics from its text; no extra annotation needed.
- Ensure all touch targets are ≥ 48 dp (use
  `Modifier.defaultMinSize(minWidth = 48.dp, minHeight = 48.dp)` on icon buttons if needed).

### 6. Dark mode

- Use `MaterialTheme.colorScheme.*` tokens exclusively — no hardcoded colors.
- The teal chip background in the mockup maps to `MaterialTheme.colorScheme.primaryContainer` with
  `MaterialTheme.colorScheme.onPrimaryContainer` label color.
- The light beige/cream list background maps to `MaterialTheme.colorScheme.surface`.

### 7. Race condition guard

`flatMapLatest` + `collectLatest` in the query observer already cancels the previous in-flight
search coroutine when a new query arrives. No additional mutex or job-cancellation logic is needed
for the search path. The suggestions load (one-shot in `init`) uses `launchLoad` which does not need
cancellation.

### 8. "See all" recent destinations

The "See all" button in the Recent section header should navigate to a full list of recent
destinations. This screen does not exist yet. For now, wire it to a no-op or log a TODO; do not
block the Search screen implementation on it.

