package com.education.ekagratagkquiz.profile.data.repository

import com.google.firebase.auth.FirebaseAuthException
import com.education.ekagratagkquiz.core.data.UserStore
import com.education.ekagratagkquiz.core.util.Resource
import com.education.ekagratagkquiz.profile.domain.repository.UserProfileRepository
import javax.inject.Inject

class UserProfileRepoImpl @Inject constructor(
    private val dataStore: UserStore,
) : UserProfileRepository {

    override suspend fun updateUserName(name: String): Resource<Unit> {
        return try {
            dataStore.saveUserName(name)
            Resource.Success(Unit)
        } catch (e: FirebaseAuthException) {
            e.printStackTrace()
            Resource.Error(e.message ?: "Firebase exception")
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Error(e.message ?: "Some error occurred")
        }
    }

}