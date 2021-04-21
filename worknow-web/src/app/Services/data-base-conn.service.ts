import { Injectable } from '@angular/core';
import { AngularFireDatabase, AngularFireList} from '@angular/fire/database';
import Categoria from '../Models/categoria';

@Injectable({
  providedIn: 'root'
})
export class DataBaseConnService {

  private dbPath = '/Categorias'

  categoriasRef: AngularFireList<Categoria>

  constructor(private db: AngularFireDatabase) {
    this.categoriasRef = db.list(this.dbPath)
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

}
