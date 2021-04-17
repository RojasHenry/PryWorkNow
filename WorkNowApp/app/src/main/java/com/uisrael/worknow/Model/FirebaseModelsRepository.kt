package com.uisrael.worknow.Model

import android.util.Log
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.uisrael.worknow.Model.Data.UsuariosData
import kotlinx.coroutines.tasks.await
import java.lang.Exception

class FirebaseModelsRepository {

    private var database = Firebase.database

    suspend fun registerUser(user:UsuariosData):Any? {
        return try {
            val userDatabase = database.reference
            val uid = userDatabase.push().key
            return uid?.let { userDatabase.child("Usuarios").child(it).setValue(user) }
        }catch (e: Exception){
            Log.i("Error",e.message)
            null
        }
    }
}