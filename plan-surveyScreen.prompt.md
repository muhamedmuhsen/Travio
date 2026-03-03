# Plan: Survey Screen – Travel Preference UI (4 Steps)

The screen is a 4-step preference survey rendered inside a single `SurveyScreen` using
`HorizontalPager`. Each step has a question, a 2-column grid of image cards, and a "Next" / "Enjoy"
button. The feature module (`feature/survey`) is already scaffolded with empty `presentation/` and
`components/` folders. The design system (`TravioTheme`, `AppButton`, `Spacing`, `Color`) is already
available from `core:designsystem`.

---

## Survey Steps & Content

| Step | Question                           | Categories (label → drawable name)                                |
|------|------------------------------------|-------------------------------------------------------------------|
| 1    | What type of travel do you prefer? | Beaches, City Life, Hotels, Safari Desert, Nature, Shopping Malls |
| 2    | How do you prefer to travel?       | Solo Travel, With Partner, Family Trip, With Friends              |
| 3    | What do you prefer to do on trips? | Relaxed, Diving, Adventurous, Photography                         |
| 4    | What's your budget preference?     | Budget Traveler, Mid-Range, Premium, Ultra Luxury                 |

- Steps 1 has a **3-row × 2-col** grid (6 cards).
- Steps 2, 3, and 4 have a **2-row × 2-col** grid (4 cards).
- The last step (4) shows **"Enjoy"** instead of "Next".

---

## Implementation Steps

1. **Add `core:designsystem` dependency** to [
   `feature/survey/build.gradle.kts`](feature/survey/build.gradle.kts) (mirrors `feature/home`). No
   Coil needed — all images are local drawables.

2. **Add drawable resources** — place one image per category inside
   `feature/survey/src/main/res/drawable/`:
    - `survey_beaches`, `survey_city_life`, `survey_hotels`, `survey_safari_desert`,
      `survey_nature`, `survey_shopping_malls`
    - `survey_solo_travel`, `survey_with_partner`, `survey_family_trip`, `survey_with_friends`
    - `survey_relaxed`, `survey_diving`, `survey_adventurous`, `survey_photography`
    - `survey_budget_traveler`, `survey_mid_range`, `survey_premium`, `survey_ultra_luxury`

3. **Create `SurveyStep` data class** in `presentation/` — holds `questionRes: Int` (string
   resource) and `categories: List<TravelCategory>`. Define a `surveySteps: List<SurveyStep>`
   constant with all 4 steps wired to the enums above.

4. **Expand `TravelCategory` enum** in `components/TravelCategory.kt` — each entry carries
   `labelRes: Int` and `drawableRes: Int`:
   ```
   BEACHES, CITY_LIFE, HOTELS, SAFARI_DESERT, NATURE, SHOPPING_MALLS,
   SOLO_TRAVEL, WITH_PARTNER, FAMILY_TRIP, WITH_FRIENDS,
   RELAXED, DIVING, ADVENTUROUS, PHOTOGRAPHY,
   BUDGET_TRAVELER, MID_RANGE, PREMIUM, ULTRA_LUXURY
   ```

5. **Create `SurveyUiState` data class** in `presentation/` holding:
    - `currentStep: Int` (0-based pager index)
    - `totalSteps: Int = 4`
    - `selectedPerStep: Map<Int, Set<TravelCategory>>` (selections per page)
    - `submitState: UiState<Unit> = UiState.Idle`

6. **Build `TravelCategoryCard` composable** in `components/` — a `Box` with:
    - Full-bleed `Image(painterResource(...))` with `ContentScale.Crop`
    - Dark scrim `Brush.verticalGradient` overlay at the bottom
    - `Text` label (white, bold) anchored to `Alignment.BottomStart` with padding
    - `RoundedCornerShape(12.dp)` clip
    - Teal `border(2.dp, primary)` + `scale(0.95f)` animation when selected

7. **Build `SurveyStepProgressBar` composable** in `components/` — a `Row` of `totalSteps`
   pill-shaped segments spaced evenly; active = `primary`, inactive = `surfaceVariant`.

8. **Build `SurveyScreen` composable** in `presentation/` using `Scaffold`:
    - **Top area (outside pager):** `SurveyStepProgressBar` + "Step X of 4" label
    - **Body:** `HorizontalPager` (user-swipe disabled; navigation only via button), each page
      contains the question `Text` + `LazyVerticalGrid(columns = Fixed(2))` of `TravelCategoryCard`s
    - **Bottom (outside pager):** full-width `AppButton` labelled **"Next"** on steps 1–3 and **"
      Enjoy"** on step 4, wired to `SurveyViewModel.onAction`

9. **Create `SurveyViewModel`** in `presentation/` with `onAction(SurveyAction)`:
    - `SurveyAction.ToggleCategory(step, category)` — toggles a category in `selectedPerStep`
    - `SurveyAction.NextStep` — advances pager; on final step triggers submit
    - On submit: calls `SubmitSurveyUseCase` (backend) and
      `PreferencesManager.saveSurveySelections` (local), exposes result via `submitState`

10. **Register the route** — add `SurveyScreen` to [
    `Screen.kt`](core/common/src/main/java/com/example/common/navigation/Screen.kt) and a
    `composable(Screen.SurveyScreen.route)` entry in [
    `Navigation.kt`](app/src/main/java/com/example/travio/Navigation.kt). On successful submit,
    navigate to `HomeScreen` and clear the back stack.

---

## Decisions

1. **Images** — Local drawable resources in `feature/survey/src/main/res/drawable/`.
   `painterResource` used; no Coil/network dependency.
2. **Multi-step flow** — Single `SurveyScreen` with `HorizontalPager` (swipe disabled). Progress bar
   and step label sit outside the pager. "Next" advances pages; step 4 shows "Enjoy".
3. **Selection persistence** — On "Enjoy" (final submit): selections saved locally via
   `PreferencesManager` **and** posted to backend via `SubmitSurveyUseCase`.
   `submitState: UiState<Unit>` drives loading spinner / error snackbar / success navigation.
