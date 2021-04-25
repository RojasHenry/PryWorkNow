package com.uisrael.worknow.Model

import android.util.Log
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.uisrael.worknow.Model.Data.CategoriasData
import com.uisrael.worknow.Model.Data.CredencialesData
import com.uisrael.worknow.Model.Data.UsuariosData
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.sendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow


class FirebaseModelsRepository {

    private var database = Firebase.database

    companion  object{

        const val REF_USUARIOS = "Usuarios"
        const val REF_CREDENCIALES = "Credenciales"
        const val REF_CATEGORIAS = "Categorias"

    }

     fun registerUser(usuariosData: UsuariosData, uid: String): Any? {
        return try {
            val userDatabase = database.getReference(REF_USUARIOS)
            return userDatabase.child(uid).setValue(usuariosData)
        }catch (e: Exception){
            Log.i("Error", e.message)
            null
        }
    }

    fun registerCredenciales(credencialesData: CredencialesData, uid: String): Any?{
        return try {
            val userDatabase = database.getReference(REF_CREDENCIALES)
            return userDatabase.child(uid).setValue(credencialesData)
        }catch (e: Exception){
            Log.i("Error", e.message)
            null
        }
    }

     val getCategorias = callbackFlow<MutableList<CategoriasData>> {
         val databaseReference = database.getReference(REF_CATEGORIAS)
         val eventListener = databaseReference.addValueEventListener(object : ValueEventListener {
             override fun onDataChange(snapshot: DataSnapshot) {
                 snapshot.let {
                     var listaCategoriasData: MutableList<CategoriasData> = ArrayList()
                     for (child in snapshot.children) {
                         var categoriasData: CategoriasData? = child.getValue(CategoriasData::class.java)
                         if (categoriasData != null) {
                             categoriasData.uid = child.key!!
                             listaCategoriasData.add(categoriasData)
                         }
                     }
                     this@callbackFlow.sendBlocking(listaCategoriasData)
                 }
             }
             override fun onCancelled(error: DatabaseError) {
                 this@callbackFlow.close(error?.toException())
             }
         })
         awaitClose{
             databaseReference.removeEventListener(eventListener)
         }
     }

    fun getCurrentUser (uid: String): Flow<UsuariosData> {
        return callbackFlow {
            val databaseReference = database.getReference(REF_USUARIOS).child(uid)
            val eventListener = databaseReference.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.let {

                        var currentUser: UsuariosData? = snapshot.getValue(UsuariosData::class.java)
                        
                        this@callbackFlow.sendBlocking(currentUser)
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    this@callbackFlow.close(error?.toException())
                }
            })
            awaitClose{
                databaseReference.removeEventListener(eventListener)
            }
        }
    }


}