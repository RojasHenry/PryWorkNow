package com.uisrael.worknow.Model

import android.util.Log
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.uisrael.worknow.Model.Data.*
import com.uisrael.worknow.Views.Utilities.Utilitity
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.sendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow


class FirebaseModelsRepository {

    private var database = Firebase.database

    companion object{

        const val REF_USUARIOS = "Usuarios"
        const val REF_CREDENCIALES = "Credenciales"
        const val REF_CATEGORIAS = "Categorias"
        const val REF_PUBLICACION = "Publicacion"
        const val REF_FOTOS = "FotosPublicacion"

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

    fun getCurrentUser (uid: String, finish:Boolean): Flow<UsuariosData?> {
        return callbackFlow {
            val databaseReference = database.getReference(REF_USUARIOS).child(uid)
            val eventListener = databaseReference.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.let {

                        var currentUser: UsuariosData? = snapshot.getValue(UsuariosData::class.java)

                        if(finish)
                            databaseReference.removeEventListener(this)

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


    fun registerOffer(value: PublicationsData): Any? {
        return try {
            val userDatabase = database.getReference(REF_PUBLICACION)
            val uid = userDatabase.push().key
            return if (uid != null) {
                userDatabase.child(uid).setValue(value)
                uid
            }else{
                ""
            }
        }catch (e: Exception){
            Log.i("Error", e.message)
            null
        }
    }

    fun registerPicturesOffer(value: FotosPublicacionData, uidOffer:String): Any? {
        return try {
            val userDatabase = database.getReference(REF_FOTOS)
            return  userDatabase.child(uidOffer).setValue(value)
        }catch (e: Exception){
            Log.i("Error", e.message)
            null
        }
    }

    fun getOffersPorCategoria(categorias:List<String>): Flow<MutableList<PublicationsData>> {
        return callbackFlow {
            val databaseReference = database.getReference(REF_PUBLICACION)
            val eventListener = databaseReference.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                        snapshot.let {
                            var listaPublicationsData: MutableList<PublicationsData> = ArrayList()
                            for (child in snapshot.children) {
                                var publicacionData: PublicationsData? = child.getValue(PublicationsData::class.java)
                                if (publicacionData != null) {
                                    publicacionData.uid = child.key!!
                                    listaPublicationsData.add(publicacionData)
                                }
                            }
                            var filterPublicacionesData: MutableList<PublicationsData> = listaPublicationsData.filter { publicationsData -> categorias.any { s ->  publicationsData.idCategoria == s } } as MutableList<PublicationsData>
                            this@callbackFlow.sendBlocking(filterPublicacionesData)
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

    fun getOffersRespFoto(uidPub: String): Flow<FotosPublicacionData?> {
        return callbackFlow {
            val databaseReference = database.getReference(REF_FOTOS).child(uidPub)
            val eventListener = databaseReference.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.let {
                        var fotosPublicacionData = snapshot.getValue(FotosPublicacionData::class.java)
                        this@callbackFlow.sendBlocking(fotosPublicacionData)
                        databaseReference.removeEventListener(this)
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

    fun getCategoriaOfferName(uidCat: String): Flow<CategoriasData?> {
        return callbackFlow {
            val databaseReference = database.getReference(REF_CATEGORIAS).child(uidCat)
            val eventListener = databaseReference.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.let {
                        var categoriaData = snapshot.getValue(CategoriasData::class.java)
                        this@callbackFlow.sendBlocking(categoriaData)
                        databaseReference.removeEventListener(this)
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
    fun getEstadoCurrentOffer(uidPub: String): Flow<PublicationsData?> {
        return callbackFlow {
            val databaseReference = database.getReference(REF_PUBLICACION).child(uidPub)
            val eventListener = databaseReference.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        snapshot.let {
                            var publicacionData = snapshot.getValue(PublicationsData::class.java)
                            sendBlocking(publicacionData)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        close(error.toException())
                    }
                })

            awaitClose {
                databaseReference.removeEventListener(eventListener)
            }
        }
    }

}