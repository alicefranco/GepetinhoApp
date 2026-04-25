# Android App Implementation Plan

## 1. Overview

This plan keeps the app architecture clean, but intentionally lightweight for a small-to-medium sized Android app.

The app has three screens:

- Login
- Item list
- Item details

Technical stack:

- `MVVM + Clean Architecture`
- `Kotlin Coroutines + Flow`
- `Hilt`
- `Retrofit + OkHttp`
- `Room`
- `Firebase Authentication`
- `Jetpack Compose`

Guiding principle:

- Keep clear separation between `presentation`, `domain`, and `data`
- Avoid extra abstractions that do not provide immediate value
- Prefer direct, readable code over highly granular architecture

## 2. Project Structure

Use the existing base package `com.example.gepetinho` and keep the structure simple.

```text
com.example.gepetinho
├── GepetinhoApp.kt
├── MainActivity.kt
├── di
│   ├── AuthModule.kt
│   ├── NetworkModule.kt
│   ├── DatabaseModule.kt
│   └── RepositoryModule.kt
├── data
│   ├── remote
│   │   ├── ItemApiService.kt
│   │   └── dto
│   │       ├── ItemDto.kt
│   │       └── ItemDetailsDto.kt
│   ├── local
│   │   ├── GepetinhoDatabase.kt
│   │   ├── ItemDao.kt
│   │   └── entity
│   │       ├── ItemEntity.kt
│   │       └── ItemDetailsEntity.kt
│   ├── repository
│   │   ├── AuthRepositoryImpl.kt
│   │   └── ItemRepositoryImpl.kt
│   └── extensions
│       └── ItemMappings.kt
├── domain
│   ├── model
│   │   ├── User.kt
│   │   ├── Item.kt
│   │   └── ItemDetails.kt
│   ├── repository
│   │   ├── AuthRepository.kt
│   │   └── ItemRepository.kt
│   └── usecase
│       ├── LoginUseCase.kt
│       ├── GetItemsUseCase.kt
│       ├── GetItemDetailsUseCase.kt
│       └── ToggleFavoriteUseCase.kt
├── presentation
│   ├── login
│   │   ├── LoginScreen.kt
│   │   ├── LoginViewModel.kt
│   │   ├── LoginUiState.kt
│   │   └── LoginEvent.kt
│   ├── list
│   │   ├── ListScreen.kt
│   │   ├── ListViewModel.kt
│   │   ├── ListUiState.kt
│   │   └── ListEvent.kt
│   └── details
│       ├── ItemDetailsScreen.kt
│       ├── ItemDetailsViewModel.kt
│       ├── ItemDetailsUiState.kt
│       └── ItemDetailsEvent.kt
└── navigation
    ├── AppRoutes.kt
    └── AppNavGraph.kt
```

### Layer Responsibilities

#### Presentation

- Compose screens
- ViewModels
- UI state and one-time UI events
- Navigation calls from the UI layer only

#### Domain

- Core models
- Repository interfaces
- A small set of use cases for meaningful business actions

#### Data

- Retrofit API calls
- Firebase auth calls
- Room database access
- Repository implementations
- Simple extension-based mapping between DTOs, entities, and domain models

#### DI

- Hilt modules for Firebase, Retrofit, Room, DAOs, and repositories

## 3. Data Flow

## 3.1 Login Flow

1. User enters email and password in `LoginScreen`
2. `LoginViewModel` validates basic input
3. `LoginViewModel` calls `LoginUseCase`
4. `LoginUseCase` delegates to `AuthRepository`
5. `AuthRepositoryImpl` uses Firebase Auth
6. Result is returned to the `ViewModel`
7. `LoginViewModel` updates `LoginUiState`
8. UI reacts to state changes and handles one-time navigation event on success

This keeps validation and login orchestration in one place without adding extra auth observers or session-specific layers.

## 3.2 List Flow

1. `ListViewModel` starts observing cached items from Room through `GetItemsUseCase`
2. Repository exposes `Flow<List<Item>>` backed by Room
3. On screen load, `ListViewModel` triggers a refresh from the remote API
4. Repository fetches from Retrofit and stores the result in Room
5. Room emits updated data
6. `ListViewModel` combines:
   - item flow from Room
   - search query
   - favorites-only filter
7. UI collects `StateFlow<ListUiState>` and renders the result

Filtering stays in the `ViewModel` because it is simple UI-driven logic and does not justify a separate use case.

## 3.3 Details Flow

1. User clicks an item in the list
2. Navigation passes `itemId`
3. `ItemDetailsViewModel` calls `GetItemDetailsUseCase`
4. Repository returns cached details from Room and refreshes from API if needed
5. UI observes `ItemDetailsUiState`
6. Tapping favorite calls `ToggleFavoriteUseCase`
7. Room updates, and list/details screens both receive the new favorite state

## 3.4 Flow Usage

### Repository flows

- Use `Flow` for Room-backed data streams
- Keep repository APIs simple:
  - `observeItems(): Flow<List<Item>>`
  - `observeItemDetails(itemId: String): Flow<ItemDetails?>`
  - `refreshItems()`
  - `refreshItemDetails(itemId: String)`
  - `toggleFavorite(itemId: String)`

### ViewModel state

- Use `StateFlow` for screen state
- Use `SharedFlow` for one-time events:
  - navigation
  - snackbar
  - error message event when needed

### Cold vs hot flows

- Repository/Room streams remain cold until collected
- `StateFlow` in the `ViewModel` is hot and represents the current screen state

## 4. Feature Breakdown

Implement in this order.

### Phase 1. Foundation

- Set up Hilt
- Add dependencies for Firebase Auth, Retrofit, OkHttp, Room, Coroutines, Navigation Compose
- Create package structure
- Add base models, repository interfaces, and Hilt modules

### Phase 2. Login

- Implement `AuthRepository`
- Implement `AuthRepositoryImpl` using Firebase Auth
- Create `LoginUseCase`
- Build `LoginViewModel`
- Build `LoginScreen`
- Handle:
  - loading state
  - invalid credentials
  - Firebase auth errors
  - navigation to list on success

### Phase 3. Items Data Layer

- Create Retrofit service for list and details endpoints
- Add DTOs
- Create Room entities, DAO, and database
- Implement simple mapping extension functions
- Implement `ItemRepositoryImpl`

### Phase 4. List Screen

- Create `GetItemsUseCase`
- Build `ListViewModel`
- Observe items from Room
- Trigger remote refresh on first load
- Add search query filtering in `ListViewModel`
- Add favorites-only filter in `ListViewModel`
- Add favorite toggle from the list
- Add item click navigation

### Phase 5. Details Screen

- Create `GetItemDetailsUseCase`
- Create `ToggleFavoriteUseCase`
- Build `ItemDetailsViewModel`
- Build `ItemDetailsScreen`
- Load item details by `itemId`
- Allow favorite toggle from the details screen

### Phase 6. Polish and Testing

- Improve empty, loading, and error states
- Add unit tests for:
  - use cases
  - repositories
  - ViewModels
- Add DAO tests if time allows

## 5. Key Components

### ViewModels

- `LoginViewModel`
  - Handles form state, login action, validation, and login result
- `ListViewModel`
  - Observes items, applies search and favorites filter, refreshes data, and toggles favorite state
- `ItemDetailsViewModel`
  - Loads one item, handles refresh state, and toggles favorite state

Only these three ViewModels are needed for the current scope.

### Use Cases

- `LoginUseCase`
  - Executes login through the auth repository
- `GetItemsUseCase`
  - Exposes the list flow from the repository
- `GetItemDetailsUseCase`
  - Exposes item details flow from the repository
- `ToggleFavoriteUseCase`
  - Toggles favorite state

These are enough for the current app. Refresh logic can stay in the repository and be triggered directly from the `ViewModel`.

### Repositories

- `AuthRepository`
  - Login with Firebase
- `ItemRepository`
  - Observe cached list data
  - Observe item details
  - Refresh list/details from API
  - Toggle favorite state in Room

## 6. State Management

Keep state handling predictable and small.

### Login

`LoginUiState` should contain:

- `email`
- `password`
- `isLoading`
- `errorMessage`

Events:

- `NavigateToList`

### List

`ListUiState` should contain:

- `items`
- `searchQuery`
- `showFavoritesOnly`
- `isLoading`
- `errorMessage`

Events:

- `OpenDetails(itemId)`
- optional snackbar event for refresh failure

### Details

`ItemDetailsUiState` should contain:

- `item`
- `isLoading`
- `errorMessage`

Events:

- optional snackbar event for detail refresh failure

### Practical pattern

- Expose one `StateFlow` per screen
- Use one `SharedFlow` for one-time events when needed
- Keep search/filter state inside `ListViewModel`
- Do not create extra state holders unless the screen becomes too large

## 7. Navigation Approach

Use plain `Navigation Compose`.

### Recommended setup

- `AppRoutes.kt` contains route constants or simple route builders
- `AppNavGraph.kt` contains the `NavHost`
- `MainActivity` hosts the root composable

### Navigation rules

- Start destination is `login` if user is not authenticated
- Start destination is `list` if user is already authenticated
- After successful login:
  - navigate to list
  - clear login from back stack
- From list:
  - navigate to details with `itemId`

### Important simplification

- Do not add a `Navigator` abstraction
- Do not move navigation into the domain layer
- Let the UI collect `ViewModel` events and call `NavController.navigate(...)`

This is the simplest clean approach for the current app size.

## 8. Offline Strategy

Use Room as the local source of truth for item screens.

### List caching

- On entering the list screen, call repository refresh
- Save remote items into Room
- Render the list from Room flow only

Benefits:

- UI continues to work with cached data
- Data survives process death
- UI reacts automatically when local data changes

### Favorites

- Store favorite state locally in Room
- Simplest practical option:
  - keep `isFavorite` in the item entity if the API response fully replaces items in a controlled way
- Safer option:
  - use a separate favorites table if refreshes overwrite item rows

For this app, choose one based on the API behavior, but avoid a more complex sync model unless required.

### Details caching

- Cache item details in Room if the details payload is larger than the list payload
- If no separate details cache is needed, reuse the main item table and fetch missing detail fields on demand
- If network fails but cached data exists, show cached data
- If network fails and there is no cache, show error with retry

## 9. Practical Implementation Notes

### Use cases

- Do not create use cases for every repository method
- Keep only use cases that represent clear app actions
- Keep simple UI-specific filtering in the `ViewModel`

### Mappers

- Do not create one mapper class per model
- Prefer extension functions in `data/extensions/`
- Examples:
  - `fun ItemDto.toEntity(): ItemEntity`
  - `fun ItemEntity.toDomain(): Item`
  - `fun ItemDetailsDto.toEntity(): ItemDetailsEntity`

This keeps mapping easy to read and easy to maintain.

### Error handling

- Map raw Firebase and network exceptions into user-friendly strings or a small sealed error type
- Do not expose raw throwable messages directly in the UI

### Threading

- Network and database work run on `Dispatchers.IO`
- `ViewModel`s collect and update UI state in `viewModelScope`

## 10. Suggested Implementation Order

1. Set up dependencies and Hilt modules
2. Implement Firebase login
3. Build login screen and login flow
4. Add Retrofit API and DTOs
5. Add Room entities, DAO, and database
6. Implement item repository with Room-backed flows
7. Build list screen with search and favorites filter
8. Add item details screen
9. Add favorite toggling from both screens
10. Add tests and refine loading/error states

## 11. Future Improvements

- Add pagination with Paging 3 if the list becomes large
- Add pull-to-refresh
- Add better retry UX
- Add analytics if product requirements need it
- Split into multiple Gradle modules only if the codebase becomes large enough to justify it

## 12. Definition of Done

The first version is complete when:

- User can log in with email/password using Firebase Auth
- Login errors are shown clearly
- List data is fetched from API and cached in Room
- User can search the list
- User can filter favorites
- User can toggle favorite from list and details
- Details screen shows the selected item
- Cached data is still shown when offline
- Architecture remains simple and maintainable