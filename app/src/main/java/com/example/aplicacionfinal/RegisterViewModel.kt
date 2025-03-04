package com.example.aplicacionfinal

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlin.text.isEmpty
import kotlin.text.isNotEmpty
import kotlin.text.orEmpty

class RegisterViewModel : ViewModel() {

    private val _email = MutableLiveData<String>()

    private val _password = MutableLiveData<String>()

    private val _date = MutableLiveData<String>()

    private val _emailError = MutableLiveData<String?>()
    val emailError: LiveData<String?> get() = _emailError

    private val _passwordError = MutableLiveData<String?>()
    val passwordError: LiveData<String?> get() = _passwordError


    private val _dateError = MutableLiveData<String?>()
    val dateError: LiveData<String?> get() = _dateError

    private val _isFormValid = MutableLiveData<Boolean>()


    fun setEmail(email: String) {
        _email.value = email
        if (email.isNotEmpty()) _emailError.value = null
    }

    fun setPassword(password: String) {
        _password.value = password
        if (password.isNotEmpty()) _passwordError.value = null
    }

    fun setDate(date: String) {
        _date.value = date

        if (date.isNotEmpty() && date != "d-M-yyyy") {
            _dateError.value = null
        }
    }

    fun validateOnSubmit(): Boolean {
        val email = _email.value.orEmpty()
        val password = _password.value.orEmpty()
        val date = _date.value.orEmpty()


        _emailError.value = if (email.isEmpty()) "El correo no puede estar vacio" else null
        _passwordError.value = if (password.isEmpty()) "La contraseña no puede estar vacía" else null
        _dateError.value = if (date.isEmpty() || date == "d-M-yyyy") "Debes seleccionar una fecha" else null


        val isValid = email.isNotEmpty() && password.isNotEmpty() && date.isNotEmpty() && date != "d-M-yyyy"
        _isFormValid.value = isValid
        return isValid
    }
}
