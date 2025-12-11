package com.univalle.equipotres

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.univalle.equipotres.model.AuthResult
import com.univalle.equipotres.model.User
import com.univalle.equipotres.repository.AuthRepository
import com.univalle.equipotres.viewmodel.LoginViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var authRepository: AuthRepository

    private lateinit var viewModel: LoginViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        MockitoAnnotations.openMocks(this)
        viewModel = LoginViewModel(authRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // -------------------------
    // LOGIN
    // -------------------------
    @Test
    fun `login success updates authResult`() = runTest {
        val expectedUser = User("123", "test@test.com")
        val expected = AuthResult.Success(expectedUser)

        `when`(authRepository.login("test@test.com", "123456"))
            .thenReturn(expected)

        viewModel.login("test@test.com", "123456")

        // Procesar Loading + resultado final
        advanceUntilIdle()

        assertEquals(expected, viewModel.authResult.value)
    }



    @Test
    fun `login failure updates authResult`() = runTest {
        val expected = AuthResult.Error("Invalid credentials")

        `when`(authRepository.login("bad@test.com", "wrong"))
            .thenReturn(expected)

        viewModel.login("bad@test.com", "wrong")
        advanceUntilIdle()

        assertEquals(expected, viewModel.authResult.value)
    }



    // -------------------------
    // REGISTER
    // -------------------------
    @Test
    fun `register success updates authResult`() = runTest {
        val expectedUser = User("999", "a@a.com")
        val expected = AuthResult.Success(expectedUser)

        `when`(authRepository.register("a@a.com", "123456"))
            .thenReturn(expected)

        viewModel.register("a@a.com", "123456")
        advanceUntilIdle()

        assertEquals(expected, viewModel.authResult.value)
    }



    // -------------------------
    // EMAIL VALIDATION
    // -------------------------
    @Test
    fun `validateEmail sets emailValid true`() {
        viewModel.validateEmail("test@test.com")
        assertTrue(viewModel.emailValid.value!!)
    }

    @Test
    fun `validateEmail sets emailValid false on empty`() {
        viewModel.validateEmail("")
        assertFalse(viewModel.emailValid.value!!)
    }

    // -------------------------
    // PASSWORD VALIDATION
    // -------------------------
    @Test
    fun `validatePassword empty sets invalid and null error`() {
        viewModel.validatePassword("")
        assertFalse(viewModel.passwordValid.value!!)
        assertNull(viewModel.passwordError.value)
    }

    @Test
    fun `validatePassword short sets error`() {
        viewModel.validatePassword("123")
        assertFalse(viewModel.passwordValid.value!!)
        assertEquals("Mínimo 6 dígitos", viewModel.passwordError.value)
    }

    @Test
    fun `validatePassword valid sets no error`() {
        viewModel.validatePassword("123456")
        assertTrue(viewModel.passwordValid.value!!)
        assertNull(viewModel.passwordError.value)
    }

    // -------------------------
    // FIELDS VALID
    // -------------------------
    @Test
    fun `fieldsValid true only when email and password valid`() {
        viewModel.validateEmail("a@a.com")
        viewModel.validatePassword("123456")

        assertTrue(viewModel.fieldsValid.value!!)
    }

    // -------------------------
    // USER LOGGED IN
    // -------------------------
    @Test
    fun `isUserLoggedIn delegates to repository`() {
        `when`(authRepository.isUserLoggedIn()).thenReturn(true)

        val result = viewModel.isUserLoggedIn()

        assertTrue(result)
    }
}
