import Calificacion from "./calificacion";

export interface Profesional {
    categorias: Array<string>
    calificaciones: Array<Calificacion>
    descripcion: string
}
