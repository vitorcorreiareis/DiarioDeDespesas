package com.example.diariodedespesas.data.repository

import android.content.SharedPreferences
import com.example.diariodedespesas.data.local.dao.UserDao
import com.example.diariodedespesas.data.local.entity.UserEntity
import java.security.MessageDigest
import java.util.UUID

class AuthRepository(
    private val userDao: UserDao,
    private val prefs: SharedPreferences
) {

    data class User(val uid: String, val email: String)

    private var _currentUser: User? = null
    val currentUser: User?
        get() = _currentUser

    init {
        val uid = prefs.getString("uid", null)
        val email = prefs.getString("email", null)
        if (uid != null && email != null) {
            _currentUser = User(uid, email)
        }
    }

    suspend fun login(email: String, password: String): Result<User> {
        val entity = userDao.findByEmail(email.trim().lowercase())
            ?: return Result.failure(Exception("E-mail ou senha incorretos."))
        if (entity.passwordHash != sha256(password)) {
            return Result.failure(Exception("E-mail ou senha incorretos."))
        }
        val user = User(entity.uid, entity.email)
        saveSession(user)
        return Result.success(user)
    }

    suspend fun register(email: String, password: String): Result<User> {
        val normalized = email.trim().lowercase()
        if (userDao.findByEmail(normalized) != null) {
            return Result.failure(Exception("E-mail já cadastrado."))
        }
        val user = User(uid = UUID.randomUUID().toString(), email = normalized)
        userDao.insert(UserEntity(uid = user.uid, email = user.email, passwordHash = sha256(password)))
        saveSession(user)
        return Result.success(user)
    }

    fun logout() {
        _currentUser = null
        prefs.edit().remove("uid").remove("email").apply()
    }

    private fun saveSession(user: User) {
        _currentUser = user
        prefs.edit().putString("uid", user.uid).putString("email", user.email).apply()
    }

    private fun sha256(input: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(input.toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
