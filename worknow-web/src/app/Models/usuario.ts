import { Profesional } from "./profesional";

export interface Usuario {
   foto: string
   nombre: string
   apellido: string
   ciudad: string
   rol: string
   telefono: string
   datosProf: Profesional
}
