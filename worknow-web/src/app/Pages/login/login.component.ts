import { Component } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from 'src/app/Services/auth.service';

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

  constructor(private fb: FormBuilder, private router:Router, private authservice: AuthService) {
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
      this.authservice.iniciarSesionFire(this.email,this.password)
      .then((result)=>{
        this.router.navigate(['/menuprincipal'])
      })
      .catch((error)=>{
        alert("Error: "+ error)
      })
    }else{
      alert("Complete los campos del formulario.")
    }
  }

}
