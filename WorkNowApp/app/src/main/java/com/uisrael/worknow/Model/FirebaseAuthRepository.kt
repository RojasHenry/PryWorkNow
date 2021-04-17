package com.uisrael.worknow.Model

import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
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

    suspend fun registerUser (email: String, password: String): AuthResult?{
        return try {
            firebaseAuth.createUserWithEmailAndPassword(email, password).await()
        }catch (e: Exception){
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