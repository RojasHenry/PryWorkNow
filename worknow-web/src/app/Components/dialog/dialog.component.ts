import { Component, Inject, OnInit } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';

export interface DialogData {
  title: string;
  message: string;
  twoButtons: boolean;
}

@Component({
  selector: 'app-dialog',
  templateUrl: './dialog.component.html',
  styleUrls: ['./dialog.component.scss']
})
export class DialogComponent implements OnInit {

  title:string = ""
  message:string = ""
  twoButtons:boolean = false

  constructor(public dialogRef: MatDialogRef<Component>, @Inject(MAT_DIALOG_DATA) public data: DialogData) { 
    this.title = data.title
    this.message = data.message
    this.twoButtons = data.twoButtons
  }

  ngOnInit(): void {
  }

  onNoClick(): void {
    this.dialogRef.close();
  }

}
