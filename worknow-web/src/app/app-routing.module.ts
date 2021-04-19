import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { CategoriasComponent } from './Components/categorias/categorias.component';
import { LoginComponent } from './Pages/login/login.component';
import { MenuprincipalComponent } from './Pages/menuprincipal/menuprincipal.component';
import { SolicitudesListComponent } from './Components/solicitudes-list/solicitudes-list.component';
import { UsuariosListComponent } from './Components/usuarios-list/usuarios-list.component';

const routes: Routes = [
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: 'login' , component: LoginComponent},
  { path: 'menuprincipal', component: MenuprincipalComponent, children: [
    { path: 'solicitudeslist', component: SolicitudesListComponent},
    { path: 'usuarioslist', component: UsuariosListComponent},
    { path: 'categorias', component: CategoriasComponent},
    { path: '', redirectTo:'usuarioslist', pathMatch:"full" }
  ]},
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }

export const routingComponents = 
[ LoginComponent, 
  MenuprincipalComponent, 
  SolicitudesListComponent,
  UsuariosListComponent,
  CategoriasComponent]
