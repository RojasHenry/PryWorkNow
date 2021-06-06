package com.uisrael.worknow.Model

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.sendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.lang.Exception

class FirebaseAuthRepository {

    private var firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private var userLiveData: MutableLiveData<FirebaseUser> = MutableLiveData()
    private var loggedOutLiveData:MutableLiveData<Boolean> = MutableLiveData()
    private var uidUserCurrent:MutableLiveData<String> = MutableLiveData()

    init {
        if (firebaseAuth.currentUser != null){
            userLiveData.postValue(firebaseAuth.currentUser)
            loggedOutLiveData.postValue(true)
            uidUserCurrent.postValue(firebaseAuth.currentUser.uid)
        }
    }

    fun validateUserLogged(): MutableLiveData<Boolean> {
        return loggedOutLiveData
    }

    fun getUserLogged(): MutableLiveData<FirebaseUser> {
        return userLiveData
    }

    fun getUserUid(): String? {
        return uidUserCurrent.value
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
        uidUserCurrent.postValue("")
        firebaseAuth.signOut()
    }

    fun loginViewWithGoogle(idToken: String): Flow<FirebaseUser?> {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        return callbackFlow {
            firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener {
                    if (it.isSuccessful){
                        this@callbackFlow.sendBlocking(firebaseAuth.currentUser)
                    }else{
                        this@callbackFlow.sendBlocking(null)
                    }
                }

            awaitClose {
                cancel()
            }
        }
    }

}