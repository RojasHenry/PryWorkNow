import { Component } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { DialogComponent } from 'src/app/Components/dialog/dialog.component';
import { Credenciales } from 'src/app/Models/credenciales';
import { AuthService } from 'src/app/Services/auth.service';
import { DataBaseConnService } from 'src/app/Services/data-base-conn.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent {

  email: string = ""
  password: string = ""
  
  loginForm = this.fb.group({
    email: [null, Validators.compose([Validators.required, Validators.pattern("^[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,4}$")])],
    password: [null, Validators.required],
  });

  hide = true;

  constructor(private fb: FormBuilder, private router:Router, private authservice: AuthService, public dialog: MatDialog, public dbService: DataBaseConnService) {
    this.authservice.isUserLog().subscribe(user => {
      localStorage.setItem('user', JSON.stringify(user));
      const usuario = JSON.parse(localStorage.getItem('user')|| '{}');
      if(usuario !== null && usuario !== undefined && usuario !== {}){
        this.router.navigate(['/menuprincipal'])
      }
    })
  }

  onSubmit(): void {
    if(this.loginForm.valid){
      this.dbService.getUsuarioEmail(this.email).valueChanges()
      .subscribe(resp => {
        var listCred: Array<Credenciales>  = resp as Array<Credenciales>
        console.log(listCred)
        if (listCred.length == 0){
          this.authservice.iniciarSesionFire(this.email,this.password)
          .then((result)=>{
            this.router.navigate(['/menuprincipal'])
          })
          .catch((error)=>{
            this.dialog.open(DialogComponent,{
              disableClose: true,
              data: {title: "Error", message: `Error: ${error}`,twoButtons:false}
            });
          })
        }else{
          this.dialog.open(DialogComponent,{
            disableClose: true,
            data: {title: "Error", message: `Usuario no autorizado`,twoButtons:false}
          });
        }
      })
    }else{
      this.dialog.open(DialogComponent,{
        disableClose: true,
        data: {title: "Error", message: `Complete los campos del formulario.`,twoButtons:false}
      });
    }
  }

}
