package com.uisrael.worknow.Model.Data

import com.google.firebase.database.PropertyName

class TokenData (
    @PropertyName("token") var token: String = "",
    @PropertyName("estado") var estado:Boolean = true)