package com.uisrael.worknow.Model

import android.util.Log
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.uisrael.worknow.Model.Data.*
import com.uisrael.worknow.Views.Utilities.Utilitity
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
        const val REF_COMENTARIOS = "Comentarios"
        const val REF_TOKENS = "Tokens"
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

     @ExperimentalCoroutinesApi
     val getCategorias = callbackFlow<MutableList<CategoriasData>> {
         val databaseReference = database.getReference(REF_CATEGORIAS)
         val eventListener = databaseReference.addValueEventListener(object : ValueEventListener {
             override fun onDataChange(snapshot: DataSnapshot) {
                 snapshot.let {
                     val listaCategoriasData: MutableList<CategoriasData> = ArrayList()
                     for (child in snapshot.children) {
                         val categoriasData: CategoriasData? = child.getValue(CategoriasData::class.java)
                         if (categoriasData != null) {
                             categoriasData.uid = child.key!!
                             listaCategoriasData.add(categoriasData)
                         }
                     }
                     this@callbackFlow.sendBlocking(listaCategoriasData)
                 }
             }
             override fun onCancelled(error: DatabaseError) {
                 this@callbackFlow.close(error.toException())
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

                        val currentUser: UsuariosData? = snapshot.getValue(UsuariosData::class.java)

                        if(finish)
                            databaseReference.removeEventListener(this)

                        this@callbackFlow.sendBlocking(currentUser)

                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    this@callbackFlow.close(error.toException())
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
                null
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
                            val listaPublicationsData: MutableList<PublicationsData> = ArrayList()
                            for (child in snapshot.children) {
                                val publicacionData: PublicationsData? = child.getValue(PublicationsData::class.java)
                                if (publicacionData != null && publicacionData.estado == Utilitity.ESTADO_PUBLICADO){
                                    publicacionData.uid = child.key!!
                                    listaPublicationsData.add(publicacionData)
                                }
                            }
                            val filterPublicacionesData: MutableList<PublicationsData> = listaPublicationsData.filter {
                                    publicationsData -> categorias.any { s ->  publicationsData.idCategoria == s } } as MutableList<PublicationsData>
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
                        val fotosPublicacionData = snapshot.getValue(FotosPublicacionData::class.java)
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
                        val categoriaData = snapshot.getValue(CategoriasData::class.java)
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
                            val publicacionData = snapshot.getValue(PublicationsData::class.java)
                            this@callbackFlow.sendBlocking(publicacionData)
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

    fun getOffersPorStatusAndCli(uidUser:String, estado:String): Flow<MutableList<PublicationsData>> {
        return callbackFlow {
            val databaseReference = database.getReference(REF_PUBLICACION).orderByChild("idUsuarioCli").equalTo(uidUser)
            val eventListener = databaseReference.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val listaPublicationsData: MutableList<PublicationsData> = ArrayList()
                    for (child in snapshot.children) {
                        val publicacionData: PublicationsData? = child.getValue(PublicationsData::class.java)
                        if (publicacionData != null) {
                            when (publicacionData.estado) {
                                estado -> {
                                    publicacionData.uid = child.key!!
                                    listaPublicationsData.add(publicacionData)
                                }
                            }
                        }
                    }
                    this@callbackFlow.sendBlocking(listaPublicationsData)
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

    fun getOffersAcceptAndPublic(uidUser:String): Flow<MutableList<PublicationsData>> {
        return callbackFlow {
            val databaseReference = database.getReference(REF_PUBLICACION).orderByChild("idUsuarioCli").equalTo(uidUser)
            val eventListener = databaseReference.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val listaPublicationsData: MutableList<PublicationsData> = ArrayList()
                    for (child in snapshot.children) {
                        val publicacionData: PublicationsData? = child.getValue(PublicationsData::class.java)
                        if (publicacionData != null) {
                            when (publicacionData.estado){
                                Utilitity.ESTADO_PUBLICADO,
                                Utilitity.ESTADO_ACEPTADO,
                                Utilitity.ESTADO_PRO_TERMINADO ->{
                                    publicacionData.uid = child.key!!
                                    listaPublicationsData.add(publicacionData)
                                }
                            }
                        }
                    }
                    this@callbackFlow.sendBlocking(listaPublicationsData)
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

    fun getOffersNoCalifCli(uidUser:String, estado:String): Flow<MutableList<PublicationsData>> {
        return callbackFlow {
            val databaseReference = database.getReference(REF_PUBLICACION).orderByChild("idUsuarioCli").equalTo(uidUser)
            val eventListener = databaseReference.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val listaPublicationsData: MutableList<PublicationsData> = ArrayList()
                    for (child in snapshot.children) {
                        val publicacionData: PublicationsData? = child.getValue(PublicationsData::class.java)
                        if (publicacionData != null) {
                            when {
                                publicacionData.estado == estado && publicacionData.calificacion == 0.0 ->{
                                    publicacionData.uid = child.key!!
                                    listaPublicationsData.add(publicacionData)
                                }
                            }
                        }
                    }
                    this@callbackFlow.sendBlocking(listaPublicationsData)
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

    fun getOfferViewAccepted(uidProf: String): Flow<MutableList<PublicationsData>> {
        return callbackFlow {
            val databaseReference = database.getReference(REF_PUBLICACION).orderByChild("idAceptadoProf").equalTo(uidProf)
            val eventListener = databaseReference.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val listaPublicationsData: MutableList<PublicationsData> = ArrayList()
                    for (child in snapshot.children) {
                        val publicacionData: PublicationsData? = child.getValue(PublicationsData::class.java)
                        if (publicacionData != null) {
                            when (publicacionData.estado){
                                Utilitity.ESTADO_ACEPTADO, Utilitity.ESTADO_PRO_TERMINADO ->{
                                    publicacionData.uid = child.key!!
                                    listaPublicationsData.add(publicacionData)
                                }
                            }
                        }
                    }
                    this@callbackFlow.sendBlocking(listaPublicationsData)
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

    fun getOffersCliAcceptedOnProgress(uidCli: String): Flow<MutableList<PublicationsData>> {
        return callbackFlow {
            val databaseReference = database.getReference(REF_PUBLICACION).orderByChild("idUsuarioCli").equalTo(uidCli)
            val eventListener = databaseReference.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val listaPublicationsData: MutableList<PublicationsData> = ArrayList()
                    for (child in snapshot.children) {
                        val publicacionData: PublicationsData? = child.getValue(PublicationsData::class.java)
                        if (publicacionData != null) {
                            when {
                                publicacionData.estado != Utilitity.ESTADO_SOL_TERMINADO && publicacionData.estado != Utilitity.ESTADO_PUBLICADO && publicacionData.estado != Utilitity.ESTADO_CANCELADO ->{
                                    publicacionData.uid = child.key!!
                                    listaPublicationsData.add(publicacionData)
                                }
                            }
                        }
                    }
                    this@callbackFlow.sendBlocking(listaPublicationsData)
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

    fun getOffersProfAceptedOnProgress(uidProf: String): Flow<MutableList<PublicationsData>> {
        return callbackFlow {
            val databaseReference = database.getReference(REF_PUBLICACION).orderByChild("idAceptadoProf").equalTo(uidProf)
            val eventListener = databaseReference.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val listaPublicationsData: MutableList<PublicationsData> = ArrayList()
                    for (child in snapshot.children) {
                        val publicacionData: PublicationsData? = child.getValue(PublicationsData::class.java)
                        if (publicacionData != null) {
                            when {
                                publicacionData.estado != Utilitity.ESTADO_SOL_TERMINADO &&
                                        publicacionData.estado != Utilitity.ESTADO_PUBLICADO &&
                                        publicacionData.estado != Utilitity.ESTADO_CANCELADO ->{
                                    publicacionData.uid = child.key!!
                                    listaPublicationsData.add(publicacionData)
                                }
                            }
                        }
                    }
                    this@callbackFlow.sendBlocking(listaPublicationsData)
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

    fun setOfferAcceptProf(uidProf: String, uidPub: String, status: String): Any? {
        return try {
            val databaseReference = database.getReference(REF_PUBLICACION)
            databaseReference.child(uidPub).child("estado").setValue(status)
            databaseReference.child(uidPub).child("idAceptadoProf").setValue(uidProf)
        }catch (e: Exception){
            Log.i("Error", e.message)
            null
        }
    }

    fun setOfferProfUpdateEstado(uidPub: String, status: String): Any? {
        return try {
            val databaseReference = database.getReference(REF_PUBLICACION)
            databaseReference.child(uidPub).child("estado").setValue(status)
        }catch (e: Exception){
            Log.i("Error", e.message)
            null
        }
    }

    fun setQualificationOffer(uidPub: String, qualif: Double): Any? {
        return try {
            val databaseReference = database.getReference(REF_PUBLICACION)
            databaseReference.child(uidPub).child("calificacion").setValue(qualif)
        }catch (e: Exception){
            Log.i("Error", e.message)
            null
        }
    }

    fun setQualificationProf(uidProf: String, calificaciones: List<CalificacionData>): Any? {
        return try {
            val databaseReference = database.getReference(REF_USUARIOS)
            databaseReference.child(uidProf).child("datosProf").child("calificaciones").setValue(calificaciones)
        }catch (e: Exception){
            Log.i("Error", e.message)
            null
        }
    }

    fun getCredencialesUser(uidUser: String): Flow<CredencialesData?> {
        return callbackFlow {
            val databaseReference = database.getReference(REF_CREDENCIALES).child(uidUser)
            val eventListener = databaseReference.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.let {
                        val credencialesData = snapshot.getValue(CredencialesData::class.java)
                        databaseReference.removeEventListener(this)
                        this@callbackFlow.sendBlocking(credencialesData)
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

    fun getCredentialEmailUser(email:String):Flow<CredencialesData?> {
        return callbackFlow {
            val databaseReference = database.getReference(REF_CREDENCIALES).orderByChild("correo").equalTo(email)
            val eventListener = databaseReference.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.let {
                        var credencialesData: CredencialesData? = null
                        if (snapshot.hasChildren()){
                            for (child in snapshot.children) {
                                credencialesData = child.getValue(CredencialesData::class.java)
                            }
                        }
                        databaseReference.removeEventListener(this)
                        this@callbackFlow.sendBlocking(credencialesData)
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    this@callbackFlow.close(error.toException())
                }
            })

            awaitClose{
                databaseReference.removeEventListener(eventListener)
            }
        }
    }

    fun setUpdateUserProfile(uidUsuario: String, currentUser: UsuariosData): Any? {
        return try {
            val databaseReference = database.getReference(REF_USUARIOS)
            databaseReference.child(uidUsuario).setValue(currentUser)
        }catch (e: Exception){
            Log.i("Error", e.message)
            null
        }
    }

    fun sendCommentOffer(uidPub: String, comment: ComentariosData): Any? {
        return try {
            val userDatabase = database.getReference(REF_COMENTARIOS)
            val uid = userDatabase.push().key
            return if (uid != null) {
                userDatabase.child(uidPub).child(uid).setValue(comment)
                uid
            }else{
                null
            }
        }catch (e: Exception){
            Log.i("Error", e.message)
            null
        }
    }

    fun getCommentsOffer(uidPub: String): Flow<MutableList<ComentariosData>> {
        return callbackFlow {
            val databaseReference = database.getReference(REF_COMENTARIOS).child(uidPub)
            val eventListener = databaseReference.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val listaComentariosData: MutableList<ComentariosData> = ArrayList()
                    for (child in snapshot.children) {
                        val comentariosData: ComentariosData? = child.getValue(ComentariosData::class.java)
                        if (comentariosData != null) {
                            comentariosData.uid = child.key!!
                            listaComentariosData.add(comentariosData)
                        }
                    }
                    this@callbackFlow.sendBlocking(listaComentariosData)
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

    fun isNewComments(uidPub: String, uidUser: String): Flow<MutableList<ComentariosData>> {
        return callbackFlow {
            val databaseReference = database.getReference(REF_COMENTARIOS).child(uidPub).orderByChild("idReceptor").equalTo(uidUser)
            val eventListener = databaseReference.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val listaComentariosData: MutableList<ComentariosData> = ArrayList()
                    for (child in snapshot.children) {
                        val comentariosData: ComentariosData? = child.getValue(ComentariosData::class.java)
                        if (comentariosData != null) {
                            when (comentariosData.estado){
                                Utilitity.COMMENT_ENVIADO ->{
                                    comentariosData.uid = child.key!!
                                    listaComentariosData.add(comentariosData)
                                }
                            }
                        }
                    }
                    this@callbackFlow.sendBlocking(listaComentariosData)
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

    fun setUpdateComment(uidPub: String,uidComment: String): Any? {
        return try {
            val databaseReference = database.getReference(REF_COMENTARIOS)
            databaseReference.child(uidPub).child(uidComment).child("estado").setValue(Utilitity.COMMENT_LEIDO)
        }catch (e: Exception){
            Log.i("Error", e.message)
            null
        }
    }

    fun registerViewUserToken(uid: String, tokenData: TokenData): Any?  {
        return try {
            val databaseReference = database.getReference(REF_TOKENS)
            databaseReference.child(uid).setValue(tokenData)
        }catch (e: Exception){
            Log.i("Error", e.message)
            null
        }
    }

}