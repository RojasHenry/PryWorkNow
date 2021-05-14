package com.uisrael.worknow.Model.Data

import com.google.firebase.database.Exclude
import com.google.firebase.database.PropertyName

class PublicationsData(
        @Exclude var uid: String = "",
        @PropertyName("fechaIni") var fechaIni: String = "",
        @PropertyName("fechaFin") var fechaFin: String = "",
        @PropertyName("inmediato") var inmediato: Boolean = false,
        @PropertyName("descripcion") var descripcion: String = "",
        @PropertyName("soloUnaPersona") var soloUnaPersona: Boolean = false,
        @PropertyName("cantidad") var cantidad: Int = 0,
        @PropertyName("ubicacion") var ubicacion: String = "",
        @PropertyName("tieneFotos") var tieneFotos: Boolean = false,
        @PropertyName("telefono") var telefono: String = "",
        @PropertyName("estado") var estado: String = "Publicado",
        @PropertyName("idUsuarioCli") var idUsuarioCli: String = "",
        @PropertyName("idAceptadoProf") var idAceptadoProf: String = "",
        @PropertyName("calificacion") var calificacion: Double = 0.0,
        @PropertyName("idCategoria") var idCategoria: String = "")