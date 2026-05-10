package com.example.gepetinho.presentation.login

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

class LoginScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun emptyForm_disablesLoginButton() {
        setContent(uiState = LoginUiState())

        composeRule.onNodeWithText("Email").assertIsDisplayed()
        composeRule.onNodeWithText("Password").assertIsDisplayed()
        composeRule.onNodeWithText("Login").assertIsNotEnabled()
    }

    @Test
    fun validForm_enablesLoginButtonAndCallsLogin() {
        var loginClicked = false

        setContent(
            uiState = LoginUiState(
                email = "ash@example.com",
                password = "pikachu123"
            ),
            onLoginClick = { loginClicked = true }
        )

        composeRule.onNodeWithText("Login").assertIsEnabled()
        composeRule.onNodeWithText("Login").performClick()

        assertTrue(loginClicked)
    }

    @Test
    fun textInputCallsChangeCallbacks() {
        var email = ""
        var password = ""

        setContent(
            uiState = LoginUiState(),
            onEmailChanged = { email = it },
            onPasswordChanged = { password = it }
        )

        composeRule.onNodeWithText("Email").performTextInput("ash@example.com")
        composeRule.onNodeWithText("Password").performTextInput("pikachu123")

        assertEquals("ash@example.com", email)
        assertEquals("pikachu123", password)
    }

    @Test
    fun createAccountButtonCallsSignUpClick() {
        var signUpClicked = false

        setContent(
            uiState = LoginUiState(),
            onSignUpClick = { signUpClicked = true }
        )

        composeRule.onNodeWithText("Create account").performClick()

        assertTrue(signUpClicked)
    }

    @Test
    fun errorMessageIsDisplayed() {
        setContent(
            uiState = LoginUiState(errorMessage = "Invalid email or password.")
        )

        composeRule.onNodeWithText("Invalid email or password.").assertIsDisplayed()
    }

    private fun setContent(
        uiState: LoginUiState,
        onEmailChanged: (String) -> Unit = {},
        onPasswordChanged: (String) -> Unit = {},
        onLoginClick: () -> Unit = {},
        onSignUpClick: () -> Unit = {}
    ) {
        composeRule.setContent {
            GepetinhoTheme {
                LoginScreen(
                    uiState = uiState,
                    onEmailChanged = onEmailChanged,
                    onPasswordChanged = onPasswordChanged,
                    onLoginClick = onLoginClick,
                    onSignUpClick = onSignUpClick
                )
            }
        }
    }
}
