package com.example.gepetinho.presentation.details

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.gepetinho.domain.model.PokemonDetails
import com.example.gepetinho.ui.theme.GepetinhoTheme
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class DetailsScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun pokemonDetailsContentIsDisplayed() {
        setContent(
            uiState = DetailsUiState(pokemon = pokemonDetails())
        )

        composeRule.onNodeWithText("Pikachu").assertIsDisplayed()
        composeRule.onNodeWithText("No. 25").assertIsDisplayed()
        composeRule.onNodeWithText("Electric").assertIsDisplayed()
        composeRule.onNodeWithText("Height").assertIsDisplayed()
        composeRule.onNodeWithText("4").assertIsDisplayed()
        composeRule.onNodeWithText("Weight").assertIsDisplayed()
        composeRule.onNodeWithText("60").assertIsDisplayed()
        composeRule.onNodeWithText("Base XP").assertIsDisplayed()
        composeRule.onNodeWithText("112").assertIsDisplayed()
    }

    @Test
    fun backButtonCallsBackClick() {
        var backClicked = false

        setContent(
            uiState = DetailsUiState(pokemon = pokemonDetails()),
            onBackClick = { backClicked = true }
        )

        composeRule.onNodeWithText("Back").performClick()

        assertTrue(backClicked)
    }

    @Test
    fun favoriteButtonCallsFavoriteClick() {
        var favoriteClicked = false

        setContent(
            uiState = DetailsUiState(pokemon = pokemonDetails(isFavorite = false)),
            onFavoriteClick = { favoriteClicked = true }
        )

        composeRule.onNodeWithContentDescription("Add favorite").performClick()

        assertTrue(favoriteClicked)
    }

    @Test
    fun emptyStateShowsRetryAndCallsRetry() {
        var retryClicked = false

        setContent(
            uiState = DetailsUiState(pokemon = null, isLoading = false),
            onRetryClick = { retryClicked = true }
        )

        composeRule.onNodeWithText("No Pokemon details loaded yet.").assertIsDisplayed()
        composeRule.onNodeWithText("Retry").performClick()

        assertTrue(retryClicked)
    }

    @Test
    fun errorMessageShowsDismissAndCallsDismiss() {
        var dismissed = false

        setContent(
            uiState = DetailsUiState(
                pokemon = pokemonDetails(),
                errorMessage = "Could not refresh Pokemon details."
            ),
            onDismissError = { dismissed = true }
        )

        composeRule.onNodeWithText("Could not refresh Pokemon details.").assertIsDisplayed()
        composeRule.onNodeWithText("Dismiss").performClick()

        assertTrue(dismissed)
    }

    private fun setContent(
        uiState: DetailsUiState,
        onBackClick: () -> Unit = {},
        onFavoriteClick: () -> Unit = {},
        onRetryClick: () -> Unit = {},
        onDismissError: () -> Unit = {}
    ) {
        composeRule.setContent {
            GepetinhoTheme {
                DetailsScreen(
                    uiState = uiState,
                    onBackClick = onBackClick,
                    onFavoriteClick = onFavoriteClick,
                    onRetryClick = onRetryClick,
                    onDismissError = onDismissError
                )
            }
        }
    }

    private fun pokemonDetails(isFavorite: Boolean = true): PokemonDetails {
        return PokemonDetails(
            id = 25,
            name = "pikachu",
            imageUrl = null,
            height = 4,
            weight = 60,
            baseExperience = 112,
            types = listOf("electric"),
            isFavorite = isFavorite
        )
    }
}
