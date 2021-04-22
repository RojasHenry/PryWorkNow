import { Injectable } from '@angular/core';
import { AngularFireAuth } from '@angular/fire/auth';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  usuarioData: any;

  constructor(
    public afAuth: AngularFireAuth
  ) {
  }

  iniciarSesionFire(email: string, password: string ){
    return this.afAuth.signInWithEmailAndPassword(email, password)
  }

  cerrarSesion(){
    return this.afAuth.signOut()
  }

  isUserLog() {
    return this.afAuth.authState
  }

}
