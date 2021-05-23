package com.uisrael.worknow.Model.Data

import com.google.firebase.database.Exclude
import com.google.firebase.database.PropertyName

class ProfesionalData (
    @PropertyName("categorias") var categorias: List<String> = listOf(),
    @PropertyName("califUsuarios") var calificaciones: List<CalificacionData> = listOf(),
    @PropertyName("descripcion") var descripcion: String = "")