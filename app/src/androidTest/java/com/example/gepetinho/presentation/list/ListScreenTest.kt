package com.example.gepetinho.presentation.list

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.example.gepetinho.domain.model.Pokemon
import com.example.gepetinho.ui.theme.GepetinhoTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class ListScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun pokemonListContentIsDisplayed() {
        setContent(
            uiState = ListUiState(
                items = listOf(
                    Pokemon(1, "bulbasaur", null, isFavorite = false),
                    Pokemon(25, "pikachu", null, isFavorite = true)
                )
            )
        )

        composeRule.onNodeWithText("Pokemon").assertIsDisplayed()
        composeRule.onNodeWithText("2 shown").assertIsDisplayed()
        composeRule.onNodeWithText("Bulbasaur").assertIsDisplayed()
        composeRule.onNodeWithText("No. 1").assertIsDisplayed()
        composeRule.onNodeWithText("Pikachu").assertIsDisplayed()
        composeRule.onNodeWithText("No. 25").assertIsDisplayed()
    }

    @Test
    fun searchInputCallsSearchCallback() {
        var query = ""

        setContent(
            uiState = ListUiState(),
            onSearchQueryChanged = { query = it }
        )

        composeRule.onNodeWithText("Search").performTextInput("pika")

        assertEquals("pika", query)
    }

    @Test
    fun favoritesChipTogglesFavoriteFilter() {
        var showFavoritesOnly: Boolean? = null

        setContent(
            uiState = ListUiState(showFavoritesOnly = false),
            onFavoritesOnlyChanged = { showFavoritesOnly = it }
        )

        composeRule.onNodeWithText("Favorites").performClick()

        assertEquals(true, showFavoritesOnly)
    }

    @Test
    fun favoriteButtonCallsFavoriteCallback() {
        var favoritePokemonId: Int? = null

        setContent(
            uiState = ListUiState(
                items = listOf(Pokemon(25, "pikachu", null, isFavorite = false))
            ),
            onFavoriteClick = { favoritePokemonId = it }
        )

        composeRule.onNodeWithContentDescription("Add favorite").performClick()

        assertEquals(25, favoritePokemonId)
    }

    @Test
    fun logoutButtonCallsLogout() {
        var logoutClicked = false

        setContent(
            uiState = ListUiState(),
            onLogout = { logoutClicked = true }
        )

        composeRule.onNodeWithText("Logout").performClick()

        assertTrue(logoutClicked)
    }

    @Test
    fun emptyStateShowsRetryAndCallsRetry() {
        var retryClicked = false

        setContent(
            uiState = ListUiState(items = emptyList()),
            onRetryClick = { retryClicked = true }
        )

        composeRule.onNodeWithText("No Pokemon loaded yet.").assertIsDisplayed()
        composeRule.onNodeWithText("Retry").performClick()

        assertTrue(retryClicked)
    }

    @Test
    fun errorMessageShowsDismissAndCallsDismiss() {
        var dismissed = false

        setContent(
            uiState = ListUiState(errorMessage = "Could not refresh Pokemon."),
            onDismissError = { dismissed = true }
        )

        composeRule.onNodeWithText("Could not refresh Pokemon.").assertIsDisplayed()
        composeRule.onNodeWithText("Dismiss").performClick()

        assertTrue(dismissed)
    }

    private fun setContent(
        uiState: ListUiState,
        onSearchQueryChanged: (String) -> Unit = {},
        onFavoritesOnlyChanged: (Boolean) -> Unit = {},
        onPokemonClick: (Int) -> Unit = {},
        onFavoriteClick: (Int) -> Unit = {},
        onRetryClick: () -> Unit = {},
        onDismissError: () -> Unit = {},
        onLogout: () -> Unit = {}
    ) {
        composeRule.setContent {
            GepetinhoTheme {
                ListScreen(
                    uiState = uiState,
                    onSearchQueryChanged = onSearchQueryChanged,
                    onFavoritesOnlyChanged = onFavoritesOnlyChanged,
                    onPokemonClick = onPokemonClick,
                    onFavoriteClick = onFavoriteClick,
                    onRetryClick = onRetryClick,
                    onDismissError = onDismissError,
                    onLogout = onLogout
                )
            }
        }
    }
}
