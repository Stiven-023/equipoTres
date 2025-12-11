package com.univalle.equipotres.di

import com.google.firebase.firestore.FirebaseFirestore
import com.univalle.equipotres.utils.SessionManager
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface WidgetEntryPoint {
    fun sessionManager(): SessionManager
    fun firestore(): FirebaseFirestore
}