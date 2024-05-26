package com.education.ekagratagkquiz.core.di

import android.content.Context
import androidx.compose.ui.platform.LocalContext
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.education.ekagratagkquiz.contribute_quiz.domain.model.FirebaseUser
import com.education.ekagratagkquiz.core.data.UserStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.first
import javax.inject.Named
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object BaseModule {

    @Provides
    fun currentUser(): FirebaseUser = FirebaseUser()

    @Provides
    @Singleton
    fun dataStore(@ApplicationContext appContext: Context): UserStore =
        UserStore(appContext)

    @Provides
    @Singleton
    fun getFireStoreInstance(): FirebaseFirestore = FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun getCloudStorageInstance(): FirebaseStorage = FirebaseStorage.getInstance()
}