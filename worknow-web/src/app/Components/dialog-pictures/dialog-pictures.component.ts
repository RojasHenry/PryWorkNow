import { Component, Inject, OnInit } from '@angular/core';
import { MatDialogRef,MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatCarousel, MatCarouselComponent } from '@ngmodule/material-carousel';
import { map } from 'rxjs/operators';
import { DataBaseConnService } from 'src/app/Services/data-base-conn.service';

export interface DialogData {
  uidPublicacion: string;
}

@Component({
  selector: 'app-dialog-pictures',
  templateUrl: './dialog-pictures.component.html',
  styleUrls: ['./dialog-pictures.component.css']
})
export class DialogPicturesComponent implements OnInit {

  uidPublicacionDialog = ""

  listFotosPublicacion:Array<any> = []
  listFotos:Array<any> = []

  constructor(public dialogRef: MatDialogRef<Component>, @Inject(MAT_DIALOG_DATA) public data: DialogData , private dbService: DataBaseConnService) {
   }

  ngOnInit(): void {
    this.uidPublicacionDialog = this.data.uidPublicacion
    this.getAllFotos(this.uidPublicacionDialog)
  }

  onNoClick(): void {
    this.dialogRef.close();
  }

  getAllFotos(uid: string){
    this.dbService.getFotosPublicacionByUid(uid)
    .snapshotChanges()
    .pipe(
      map(changes =>
        changes.map(c =>
          ({ key: c.key, ...c.payload.val() })
        )
      )
    )
    .subscribe(data =>{
      this.listFotosPublicacion = data
      this.listFotos = this.listFotosPublicacion[0].fotos
    })
  }
}
