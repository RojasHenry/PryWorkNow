import { Component, OnInit } from '@angular/core';
import { map } from 'rxjs/operators';
import { Breakpoints, BreakpointObserver } from '@angular/cdk/layout';
import { DataBaseConnService } from 'src/app/Services/data-base-conn.service';
import { MatDialog } from '@angular/material/dialog';
import { DialogComponent } from '../dialog/dialog.component';
import { Credenciales } from 'src/app/Models/credenciales';

@Component({
  selector: 'app-usuarios-list',
  templateUrl: './usuarios-list.component.html',
  styleUrls: ['./usuarios-list.component.scss']
})
export class UsuariosListComponent implements OnInit{

  public circleColor: string;

  private colors = [
    '#3e3c3b', // black
    '#e8d900', // yellow
    '#ffab40', // orange
  ];

  listUsuarios: any = []

  listCategorias: Array<any> = []

  listCredenciales: Array<any> = []

  constructor(private dbService: DataBaseConnService, public dialog: MatDialog) {
    const randomIndex = Math.floor(Math.random() * Math.floor(this.colors.length));
    this.circleColor = this.colors[randomIndex];
  }

  ngOnInit(): void {
    this.getUsuarios()
    this.getCategorias()
    this.getCredenciales()
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

  getCredenciales(){
    this.dbService.getAllCredenciales().snapshotChanges()
    .pipe(
      map(changes =>
        changes.map(c =>
          ({ key: c.key, ...c.payload.val() })
        )
      )
    )
    .subscribe(data =>{
      this.listCredenciales = data
    })
  }

  getInitials(user:any){
    return user.nombre.substring(0,1).toUpperCase()+""+user.apellido.substring(0,1).toUpperCase()
  }

  getNombreCategoria(uidCategoria:any){
    var categoria: any = this.listCategorias.filter(cate => cate.key == uidCategoria)

    return categoria[0].nombre
  }

  getStatusUsuario(uidUsuario:any){
    var credencial: any = this.listCredenciales.filter(cred => cred.key == uidUsuario)

    return credencial[0].estado
  }

  getUsuarioCorreo(uidUsuario:any){
    var credencial: any = this.listCredenciales.filter(cred => cred.key == uidUsuario)
    return credencial[0].correo
  }

  getFromSocNetwork(uidUsuario:any){
    var credencial: any = this.listCredenciales.filter(cred => cred.key == uidUsuario)
    return credencial[0].fromSocNet
  }

  deshabilitarUsuario(element: any, newEstado:boolean){

    const credencial: Array<any> = this.listCredenciales.filter(cred => cred.key == element.key)

    const dialogRef = this.dialog.open(DialogComponent,{
      disableClose: true,
      data: {title: "Aviso", message: `Esta seguro que desea ${ newEstado ? 'habilitar' : 'deshabilitar'}  al usuario ${element.nombre} ${element.apellido}? `,twoButtons:true}
    });

    dialogRef.afterClosed().subscribe(result => {
      if(result){
        const key: string = credencial[0].key
        delete credencial[0]["key"]
        credencial[0].estado = newEstado

        this.dbService.updateCredencial(key,credencial[0])
        .then((success)=>{
          this.dialog.open(DialogComponent,{
            disableClose: true,
            data: {title: "Aviso", message: `El usuario ${element.nombre} ${element.apellido} fue ${ newEstado ? 'habilitado' : 'deshabilitado'}.`,twoButtons:false}
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

