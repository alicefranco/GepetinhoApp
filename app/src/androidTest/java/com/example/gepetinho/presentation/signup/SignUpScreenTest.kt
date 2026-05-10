package com.example.gepetinho.presentation.signup

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.example.gepetinho.ui.theme.GepetinhoTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class SignUpScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun emptyForm_disablesCreateAccountButton() {
        setContent(uiState = SignUpUiState())

        composeRule.onNodeWithText("Email").assertIsDisplayed()
        composeRule.onNodeWithText("Password").assertIsDisplayed()
        composeRule.onNodeWithText("Confirm password").assertIsDisplayed()
        composeRule.onNodeWithText("Create account").assertIsNotEnabled()
    }

    @Test
    fun validForm_enablesCreateAccountButtonAndCallsSignUp() {
        var signUpClicked = false

        setContent(
            uiState = SignUpUiState(
                email = "ash@example.com",
                password = "pikachu123",
                confirmPassword = "pikachu123"
            ),
            onSignUpClick = { signUpClicked = true }
        )

        composeRule.onNodeWithText("Create account").assertIsEnabled()
        composeRule.onNodeWithText("Create account").performClick()

        assertTrue(signUpClicked)
    }

    @Test
    fun textInputCallsChangeCallbacks() {
        var email = ""
        var password = ""
        var confirmPassword = ""

        setContent(
            uiState = SignUpUiState(),
            onEmailChanged = { email = it },
            onPasswordChanged = { password = it },
            onConfirmPasswordChanged = { confirmPassword = it }
        )

        composeRule.onNodeWithText("Email").performTextInput("ash@example.com")
        composeRule.onNodeWithText("Password").performTextInput("pikachu123")
        composeRule.onNodeWithText("Confirm password").performTextInput("pikachu123")

        assertEquals("ash@example.com", email)
        assertEquals("pikachu123", password)
        assertEquals("pikachu123", confirmPassword)
    }

    @Test
    fun loginButtonCallsLoginClick() {
        var loginClicked = false

        setContent(
            uiState = SignUpUiState(),
            onLoginClick = { loginClicked = true }
        )

        composeRule.onNodeWithText("Already have an account? Login").performClick()

        assertTrue(loginClicked)
    }

    @Test
    fun errorMessageIsDisplayed() {
        setContent(
            uiState = SignUpUiState(errorMessage = "Password is too weak.")
        )

        composeRule.onNodeWithText("Password is too weak.").assertIsDisplayed()
    }

    private fun setContent(
        uiState: SignUpUiState,
        onEmailChanged: (String) -> Unit = {},
        onPasswordChanged: (String) -> Unit = {},
        onConfirmPasswordChanged: (String) -> Unit = {},
        onSignUpClick: () -> Unit = {},
        onLoginClick: () -> Unit = {}
    ) {
        composeRule.setContent {
            GepetinhoTheme {
                SignUpScreen(
                    uiState = uiState,
                    onEmailChanged = onEmailChanged,
                    onPasswordChanged = onPasswordChanged,
                    onConfirmPasswordChanged = onConfirmPasswordChanged,
                    onSignUpClick = onSignUpClick,
                    onLoginClick = onLoginClick
                )
            }
        }
    }
}
