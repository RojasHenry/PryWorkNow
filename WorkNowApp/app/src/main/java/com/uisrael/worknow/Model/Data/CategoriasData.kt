package com.uisrael.worknow.Model.Data

import com.google.firebase.database.Exclude
import com.google.firebase.database.PropertyName

class CategoriasData (
        @Exclude var uid: String = "",
        @PropertyName("nombre") var nombre: String = "",
        @PropertyName("fechaIniFin") var fechaIniFin:Boolean = false,
        @PropertyName("masPersonas") var masPersonas:Boolean = false,
        @PropertyName("detalleFoto") var detalleFoto:Boolean = true,
        @PropertyName("estado") var estado:Boolean = true )