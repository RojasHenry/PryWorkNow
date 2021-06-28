import { Component } from '@angular/core';
import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { Observable } from 'rxjs';
import { map, shareReplay } from 'rxjs/operators';
import { AuthService } from 'src/app/Services/auth.service';
import { Router } from '@angular/router';
import { MatDialog } from '@angular/material/dialog';
import { DialogComponent } from 'src/app/Components/dialog/dialog.component';

@Component({
  selector: 'app-menuprincipal',
  templateUrl: './menuprincipal.component.html',
  styleUrls: ['./menuprincipal.component.scss']
})
export class MenuprincipalComponent {

  emailuser:string = ""

  titleToolbar:string = "Worknow Web"
  isHandset$: Observable<boolean> = this.breakpointObserver.observe(Breakpoints.Handset)
    .pipe(
      map(result => result.matches),
      shareReplay()
    );

  constructor(private breakpointObserver: BreakpointObserver, private router:Router, private authservice: AuthService, public dialog: MatDialog) {
    /*this.authservice.isUserLog().subscribe(user => {
      localStorage.setItem('user', JSON.stringify(user));
      const usuario = JSON.parse(localStorage.getItem('user')|| '{}');
      if(usuario !== null && usuario !== undefined && usuario !== {}){
        this.emailuser = usuario.email
      }else{
        this.router.navigate(['/login'])
      }
    })*/
  }

  cerrarSesion(){
    const dialogRef = this.dialog.open(DialogComponent,{
      disableClose: true,
      data: {title: "Aviso", message: `Esta seguro que desea salir del sistema?`,twoButtons:true}
    });

    dialogRef.afterClosed().subscribe(result => {
      if(result){
        this.authservice.cerrarSesion()
        .then((result)=>{
          localStorage.removeItem('user');
          this.router.navigate(['/login'])
        })
      }
    })
    
  }

  changeTitle(title: string){
    this.titleToolbar = title
  }
}
