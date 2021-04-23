package com.uisrael.worknow.Model

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.uisrael.worknow.ViewModel.ValidatorRespuestas.Respuesta
import com.wajahatkarim3.easyvalidation.core.Validator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.tasks.await
import java.lang.Exception

class FirebaseAuthRepository {

    private var firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private var userLiveData: MutableLiveData<FirebaseUser> = MutableLiveData()
    private var loggedOutLiveData:MutableLiveData<Boolean> = MutableLiveData()

    init {
        if (firebaseAuth.currentUser != null){
            userLiveData.postValue(firebaseAuth.currentUser)
            loggedOutLiveData.postValue(true)
        }
    }

    fun validateUserLogged(): MutableLiveData<Boolean> {
        return loggedOutLiveData
    }

    fun getUserLogged(): MutableLiveData<FirebaseUser> {
        return userLiveData
    }

    suspend fun registerUser (email: String, password: String): AuthResult?{
        return try {
            firebaseAuth.createUserWithEmailAndPassword(email, password).await()
        }catch (e: Exception){
            Log.e("Error", e.message)
            null
        }
    }

    suspend fun loginUser(email: String, password: String): AuthResult?{
        return try {
            firebaseAuth.signInWithEmailAndPassword(email, password).await()
        }catch (e: Exception){
            null
        }
    }

    fun loginOutUser(){
        loggedOutLiveData.postValue(false)
        firebaseAuth.signOut()
    }

}