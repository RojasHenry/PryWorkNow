import { Injectable } from '@angular/core';
import { AngularFireDatabase, AngularFireList} from '@angular/fire/database';
import Categoria from '../Models/categoria';
import { Credenciales } from '../Models/credenciales';
import { FotosPublicacion } from '../Models/fotospublicacion';
import { Publicaciones } from '../Models/publicaciones';
import { Usuario } from '../Models/usuario';

@Injectable({
  providedIn: 'root'
})

export class DataBaseConnService {

  categoriasRef: AngularFireList<Categoria>
  usuariosRef: AngularFireList<Usuario>
  credencialesRef: AngularFireList<Credenciales>

  constructor(private db: AngularFireDatabase) {
    this.categoriasRef = db.list(PathsFireDatabase.Categorias)
    this.usuariosRef = db.list(PathsFireDatabase.Usuarios)
    this.credencialesRef = db.list(PathsFireDatabase.Credenciales)
  }

  getAllCategorias(): AngularFireList<Categoria>{
    return this.categoriasRef
  }

  createCategoria(categoria:Categoria){
    return this.categoriasRef.push(categoria)
  }

  updateCategoria(key:string, categoria:Categoria): Promise<void>{
    return this.categoriasRef.update(key,categoria)
  }

  getAllUsuarios() {
    return this.usuariosRef
  }

  getAllCredenciales() {
    return this.credencialesRef
  }

  updateCredencial(key:string, credencial:Credenciales){
    return this.credencialesRef.update(key,credencial)
  }

  getAllPublicacionesByUid(key:string): AngularFireList<Publicaciones>{
    return this.db.list(PathsFireDatabase.Publicaciones,ref => ref.orderByChild('idCategoria').equalTo(key))
  }

  getUsuariosByUid(key:string): AngularFireList<Usuario>{
    return this.db.list(PathsFireDatabase.Usuarios, ref => ref.orderByKey().equalTo(key))
  }

  getFotosPublicacionByUid(key:string): AngularFireList<FotosPublicacion>{
    return this.db.list(PathsFireDatabase.FotosPublicacion, ref => ref.orderByKey().equalTo(key))
  }

  updateStatusPublicacion(key:string, credencial:Publicaciones){
    return this.db.list(PathsFireDatabase.Publicaciones).update(key,credencial)
  }

}

enum PathsFireDatabase {
    Categorias = '/Categorias',
    Credenciales = '/Credenciales',
    Usuarios = '/Usuarios',
    Publicaciones = '/Publicacion',
    FotosPublicacion = "/FotosPublicacion"
}

enum StatusOfferDatabase {
  Publicado = 'Publicado',
  OnProgress = 'EnProgreso',
  Cancel = 'Cancelado',
  Finish = 'Terminado',
}
