import { Component, OnInit } from '@angular/core';
import { map } from 'rxjs/operators';
import { Breakpoints, BreakpointObserver } from '@angular/cdk/layout';
import { DataBaseConnService } from 'src/app/Services/data-base-conn.service';
import { DialogPicturesComponent } from '../dialog-pictures/dialog-pictures.component';
import { MatDialog } from '@angular/material/dialog';
import { Publicaciones } from 'src/app/Models/publicaciones';
import { DialogComponent } from '../dialog/dialog.component';

@Component({
  selector: 'app-solicitudes-list',
  templateUrl: './solicitudes-list.component.html',
  styleUrls: ['./solicitudes-list.component.scss']
})
export class SolicitudesListComponent implements OnInit{

  listCategorias: Array<any> = []
  listPublicaciones: Array<any> = []
  listUsuarios: Array<any> = []

  selectedCategoria = '';
  firstime:boolean = false

  constructor(private dbService: DataBaseConnService, public dialog: MatDialog) {}

  ngOnInit(): void {
    this.getCategorias()
    this.getUsuarios()
  }

  getCategorias(){
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
    })
  }

  getUsuarios(){
    this.dbService.getAllUsuarios().snapshotChanges()
    .pipe(
      map(changes =>
        changes.map(c =>
          ({ key: c.key, ...c.payload.val() })
        )
      )
    )
    .subscribe(data =>{
      this.listUsuarios = data
    })
  }


  onChange(e:any){
    this.firstime = true
    this.dbService.getAllPublicacionesByUid(e.value).snapshotChanges()
    .pipe(
      map(changes =>
        changes.map(c =>
          ({ key: c.key, ...c.payload.val() })
        )
      )
    )
    .subscribe(data =>{
      this.listPublicaciones = data
    })
  }

  getSolicitanteNombre(idUsuarioCli:string){
    var usuario: any = this.listUsuarios.filter(cate => cate.key == idUsuarioCli)

    return usuario[0].nombre+ " " + usuario[0].apellido
   
  }

  getSolicitanteCiudad(idUsuarioCli:string){
    var usuario: any = this.listUsuarios.filter(cate => cate.key == idUsuarioCli)

    return usuario[0].ciudad
  }

  abrirDialogo(key:string ){
    const dialogRef = this.dialog.open(DialogPicturesComponent,{
      disableClose: false,
      data: {uidPublicacion: key}
    });

  }

  procesarSolicitud(publicacion:any){
    const dialogRef = this.dialog.open(DialogComponent,{
      disableClose: true,
      data: {title: "Aviso", message: `Esta seguro que desea cancelar esta solicitud? `,twoButtons:true}
    });


    dialogRef.afterClosed().subscribe(result => {
      if(result){
        const key: string = publicacion.key
        delete publicacion["key"]
        publicacion.estado = StatusOfferDatabase.Cancel
        this.dbService.updateStatusPublicacion(key,publicacion)
        .then((success)=>{
          this.dialog.open(DialogComponent,{
            disableClose: true,
            data: {title: "Aviso", message: `La solicitud fue cancelada exitosamente.`,twoButtons:false}
          });
        })
        .catch((error)=>{
          this.dialog.open(DialogComponent,{
            disableClose: true,
            data: {title: "Error", message: ` Error: ${error}`,twoButtons:false}
          });
        })
      }
      
    });
    
  }
}

enum StatusOfferDatabase {
  Publicado = 'Publicado',
  OnProgress = 'EnProgreso',
  Cancel = 'Cancelado',
  Finish = 'Terminado',
}
