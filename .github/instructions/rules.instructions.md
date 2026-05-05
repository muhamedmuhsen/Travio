# Engineering Rules (Jetpack Compose / Android)

---

## 1. General Engineering Principles

- **Clean Code First:** Prioritize readability, maintainability, and clarity over cleverness. Code
  should be self-explanatory.
- **Small Files & Functions:** Adhere to the Single Responsibility Principle. Keep functions short
  and split files to avoid "God Files."
- **Minimalist Comments:** Avoid redundant or AI-generated boilerplate comments. Use comments only
  to explain the *Why*, not the *What*.
- **Root Cause Analysis:** Identify and fix the root cause of bugs rather than applying
  superficial "band-aid" fixes.
- **Minimal Side Effects:** Avoid changing code outside the scope of the current task unless
  explicitly requested.
- **Project Consistency:** Respect the existing architecture (MVVM/MVI) and patterns to ensure a
  unified codebase.
- **Senior Partner Mode:** Act as a Senior Engineer, not just a task executor. Challenge ideas,
  suggest better alternatives, and discuss trade-offs proactively.
- **No Assumptions:** If a requirement is ambiguous or context is missing, ask for clarification
  before implementing.

---

## 2. Jetpack Compose & Kotlin Best Practices (2026)

- **Strict Clean Architecture:** Enforce separation between Data, Domain, and Presentation layers.
  *Business logic* belongs in Use Cases or ViewModels — never in Composable functions. Local
  ephemeral UI state (e.g., dropdown open/closed, focus state) is acceptable in Composables when it
  has no business relevance.

- **Unidirectional Data Flow (UDF):** Always follow UDF patterns. Use `StateFlow` or Compose `State`
  for UI state. For one-time side effects (navigation, toasts, dialogs), prefer `Channel` exposed as
  `receiveAsFlow()` over `SharedFlow`. `SharedFlow` can silently drop events during configuration
  changes when no subscriber is active — this is a known pitfall. If `SharedFlow` is already in use
  in the project, document the trade-off explicitly in the ViewModel with a comment.

- **Modifier Convention:** Every public `@Composable` that renders UI must accept a
  `modifier: Modifier = Modifier` parameter as the first optional parameter (after required
  content/data params). This is a non-negotiable API guideline for reusability and layout
  delegation.

- **Sealed UI State:** Represent screen state as a `sealed class` with `Loading`, `Success`, and
  `Error` variants. Never swallow exceptions in `catch` blocks — always surface errors to the UI
  state layer.

- **Atomic Composables:** Break down the UI into small, stateless (preferred), and reusable
  `@Composable` functions.

- **Preview Discipline:** Every atomic Composable must have at least one `@Preview`. Previews must
  be kept up to date and should cover at minimum the default and error/empty states.

- **Recomposition Optimization:** Be mindful of recomposition. Use `remember`, `derivedStateOf`, and
  `@Stable`/`@Immutable` annotations where necessary to prevent unnecessary UI updates.

- **Theming Contract:** Never hardcode colors, font sizes, or spacing values. Always use
  `MaterialTheme` tokens. Hardcoded values in production code are a review blocker.

- **Modern Kotlin Standards:** Utilize Coroutines, sealed interfaces, and value classes. If
  targeting Kotlin 2.2+, prefer **Context Parameters** (stabilized in K2) over the now-deprecated
  Context Receivers syntax. Do not introduce Context Parameters in modules still on Kotlin < 2.2 —
  confirm the Kotlin version before use.

- **Dependency Discipline:** Prefer the stdlib and Jetpack ecosystem. Any new external dependency
  must include a comment in the PR justifying why it was chosen over the existing stack, and must be
  confirmed version-stable. See Section 4 for infrastructure prerequisites before introducing Hilt
  or Navigation.

- **Robust Error Handling:** Avoid silent failures. Use `Result` wrappers or sealed UI state to
  handle and display errors gracefully.

---

## 3. Project Structure & Naming

- **Naming Conventions:** Use `PascalCase` for Composable functions and `camelCase` for variables
  and standard functions. File names must match the primary class or Composable they contain.

- **Dependency Injection — Hilt:** Hilt is the DI solution. It is fully configured:
  `com.google.dagger:hilt-android`, `hilt-android-gradle-plugin`, and KSP are present in
  `libs.versions.toml` and applied in module-level `build.gradle.kts` files. All ViewModels use
  `@HiltViewModel` and the Application class is annotated with `@HiltAndroidApp`. Do not mix DI
  frameworks.

- **Navigation — Type-Safe Compose Navigation:** `androidx.navigation:navigation-compose` is present
  in `libs.versions.toml` and used in `:app`. The current routing uses string-based
  `sealed class Screen(val route: String)` with manual argument concatenation. The target is to
  migrate to `@Serializable` route objects for full type safety. Do not introduce new screens with
  the legacy string pattern — new features should use type-safe routes.

- **Core Module:** The `core` module exists with sub-modules `:core:common`, `:core:designsystem`,
  `:core:network`, and `:core:database`. Shared utilities, the design system, and cross-feature
  logic live here. Cross-feature dependencies must go through `core`, never directly between
  features.

- **Feature-Based Modularization:** Organize code by feature. Each feature encapsulates its own UI,
  ViewModel, and Domain logic. Cross-feature dependencies must go through `core`, never directly
  between features.

---

## 4. Accessibility (a11y)

- **Content Descriptions:** Every non-decorative image, icon, and interactive element must have a
  meaningful `contentDescription`. Decorative elements must explicitly set
  `contentDescription = null`.
- **Touch Targets:** Minimum touch target size is 48×48dp for all interactive elements, per Material
  Design guidelines.
- **Semantic Roles:** Use `Modifier.semantics` to assign correct roles (e.g., `Role.Button`,
  `Role.Checkbox`) where the default semantics are insufficient.
- **No a11y Regressions:** a11y coverage is part of the Definition of Done. Launching a screen
  without verifying TalkBack compatibility is not acceptable.

---

## 5. Performance

- **Recomposition First:** Before shipping any screen with complex or nested state, verify it in
  Layout Inspector and check for unintended recompositions.
- **Compose Metrics in CI:** Enable Compose compiler metrics for modules with performance-sensitive
  screens using the `-Pcomposecompilerreports=true` Gradle flag. Skippability warnings for
  Composables receiving unstable parameters must be resolved before merge.
  Reference: [Compose Compiler Metrics setup](https://developer.android.com/develop/ui/compose/performance/stability/diagnose).
- **No Blocking the Main Thread:** All I/O, heavy computation, and data mapping must happen on
  `Dispatchers.IO` or `Dispatchers.Default`. Never on `Dispatchers.Main`.
- **Lazy Lists:** Always use `key` lambdas in `LazyColumn`/`LazyRow` to prevent unnecessary item
  recomposition on list updates.

---

## 6. Security & Logging

- **Data Privacy:** Never log sensitive information (API keys, tokens, PII) to Logcat under any
  circumstances.
- **Smart Logging:** Use `Timber` with a `DebugTree` registered only in debug builds. Release builds
  must not emit any logs. Verify strip behavior via ProGuard/R8 rules.
- **Secrets Management:** API keys and secrets must never be hardcoded in source files or committed
  to version control. Do not expose secrets via `BuildConfig` fields — values injected this way are
  extractable from the compiled APK. Use
  the [Secrets Gradle Plugin](https://github.com/google/secrets-gradle-plugin) to inject secrets
  from a local `secrets.properties` file (gitignored). For production secrets, prefer a
  backend-mediated approach where the client never holds raw keys.

---

## 7. Definition of Done (DoD)

> **Note:** The checklist below is for developers. The Git metadata block is a separate instruction
> for the AI agent and is intentionally kept distinct.

### Developer Checklist

A task is considered complete only when all of the following are satisfied:

1. **Functionality:** The feature works as specified with no known regressions.
2. **Tests:** New Use Case and ViewModel logic has unit tests. Critical UI flows have smoke tests.
3. **a11y:** Content descriptions, touch targets, and semantics have been verified.
4. **No Hardcoded Values:** Colors, fonts, and strings use the theme/resource system.
5. **No New Warnings:** Compose compiler metrics show no new skippability regressions.
6. **Logging:** No sensitive data in logs. Timber DebugTree gating confirmed.
7. **Secrets:** No keys or tokens exposed in source or via `BuildConfig`.

### AI Agent Output (on task completion)

Once a task is complete, always provide:

- **Branch Name:** e.g., `feature/login-validation`, `fix/cart-state-error`
- **Commit Message:** Following [Conventional Commits](https://www.conventionalcommits.org/), e.g.,
  `feat(auth): add biometric login fallback`
- **PR Metadata:**
    - *Title:* Concise summary of the change
    - *Description:* What changed, why, how to test it, and any side effects or known limitations

---