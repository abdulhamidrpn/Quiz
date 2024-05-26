package com.education.ekagratagkquiz.profile.domain.repository

import android.net.Uri
import com.education.ekagratagkquiz.core.util.Resource

interface UserProfileRepository {
    suspend fun updateUserName(name: String): Resource<Unit>
}