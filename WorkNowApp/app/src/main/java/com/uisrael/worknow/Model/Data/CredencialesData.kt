package com.uisrael.worknow.Model.Data

import com.google.firebase.database.Exclude
import com.google.firebase.database.PropertyName

class CredencialesData (
        @PropertyName("correo") var correo: String = "",
        @PropertyName("password") var password:String = "",
        @PropertyName("fromSocNet") var fromSocNet:Boolean = false,
        @PropertyName("estado") var estado:Boolean = true )