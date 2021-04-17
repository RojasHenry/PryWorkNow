package com.uisrael.worknow.Model.Data

import com.google.firebase.database.Exclude
import com.google.firebase.database.PropertyName
import java.io.Serializable

data class UsuariosData(
    @Exclude val uid: String,
    @PropertyName("nombre") val nombre: String,
    @PropertyName("apellido") val apellido:String,
    @PropertyName("ciudad") val ciudad:String,
    @PropertyName("telefono") val telefono:String,
    @PropertyName("foto") val foto:String,
    @PropertyName("rol") val rol:String){
    constructor(): this ("","","","","","","")
}