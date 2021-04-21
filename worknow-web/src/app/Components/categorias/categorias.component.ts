import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import Categoria from 'src/app/Models/categoria';
import { DataBaseConnService } from 'src/app/Services/data-base-conn.service';
import { map } from 'rxjs/operators';
import { MatTableDataSource } from '@angular/material/table';

@Component({
  selector: 'app-categorias',
  templateUrl: './categorias.component.html',
  styleUrls: ['./categorias.component.scss']
})
export class CategoriasComponent implements OnInit {

  categoriaColum: string[] = ['nombre','fechaIniFin','masPersonas','detalleFoto','estado','mas'];
  newCategoria: Categoria = new Categoria()
  listCategorias: any = []
  dataSource = new MatTableDataSource()

  editKey:string = ""
  isEdit:boolean = false

  categoriaForm: FormGroup = this.fb.group({
    categoria: [null, Validators.required]
  });

  constructor(private fb: FormBuilder, private dbService: DataBaseConnService) {}

  ngOnInit(): void {
    this.getCategorias();
  }
  getCategorias() {
    this.dbService.getAllCategorias().snapshotChanges()
    .pipe(
      map(changes =>
        changes.map(c =>
          ({ key: c.key, ...c.payload.val() })
        )
      )
    )
    .subscribe(data =>{
      this.listCategorias = data
      console.log(data)
      this.dataSource.data = this.listCategorias;
    })
  }

  onSubmit(): void {
    if (this.categoriaForm.valid){
      if(!this.isEdit){
        this.dbService.createCategoria(this.newCategoria)
        .then((success)=>{
          alert("Creado correctamente. ")
          this.newCategoria = new Categoria();
        })
        .catch((error)=>{
          console.log(error);
        })
      }else{
        this.dbService.updateCategoria(this.editKey,this.newCategoria)
        .then((success)=>{
          this.newCategoria = new Categoria()
          this.isEdit = false
          this.categoriaForm.get('categoria')?.setErrors({ passwordMatch: true })
        })
        .catch((error)=>{
          console.log(error);
        })
      }
    }else{
      alert("complete los campos del formulario.")
    }
  }

  onChangeFecha(e: any) {
    this.newCategoria.fechaIniFin = e.checked
  }

  onChangePersonas(e: any) {
    this.newCategoria.masPersonas = e.checked
  }

  onChangeFoto(e: any) {
    this.newCategoria.detalleFoto = e.checked
  }


  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();
  }

  deshabilitar(element: any, newEstado:boolean){
    const key: string = element.key
    delete element["key"]
    element.estado = newEstado
    this.dbService.updateCategoria(key,element)
    .then((success)=>{
      alert("Actualizado correctamente. ")
    })
    .catch((error)=>{
      console.log(error);
    })
  }

  activarEditar(element: any){
    this.editKey = element.key
    delete element["key"]
    this.newCategoria = element
    this.isEdit = true
  }

  cancelarEditar(){
    this.newCategoria = new Categoria()
    this.isEdit = false
  }


}
