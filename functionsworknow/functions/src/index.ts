import * as functions from "firebase-functions";

import * as admin from "firebase-admin";
admin.initializeApp();

const ESTADO_ACEPTADO = "Aceptado";
const ESTADO_TERMINADO = "Terminado";
const TIPO_OFERTA_ACEPTADA = "OfferAcept";
const TIPO_OFERTA_COMENTARIO = "OfferComment";
// // Start writing Firebase Functions
// // https://firebase.google.com/docs/functions/typescript
//

export const offertAcepted = functions.database
    .ref("/Publicacion/{publiID}")
    .onUpdate( async (event) => {
      const dataOfferBefore = event.before.val();
      const dataOfferAfter = event.after.val();
      console.log("Before - Init" + JSON.stringify(dataOfferBefore));
      console.log("After - Init" + JSON.stringify(dataOfferAfter));
      if (dataOfferAfter.idAceptadoProf != "") {
        console.log("Before" + JSON.stringify(dataOfferBefore));
        console.log("After" + JSON.stringify(dataOfferAfter));
        if (dataOfferAfter.estado == ESTADO_ACEPTADO &&
            dataOfferBefore.estado != ESTADO_TERMINADO) {
          const db = admin.database();
          const tokenCli = await db.ref("Tokens")
              .child(dataOfferAfter.idUsuarioCli).get();
          console.log(JSON.stringify(tokenCli.val()));
          const profAccept= await db.ref("Usuarios")
              .child(dataOfferAfter.idAceptadoProf).get();
          console.log(JSON.stringify(profAccept.val()));
          const payload = {
            data: {
              offer: dataOfferAfter.descripcion,
              profAccept: `${profAccept.val().nombre} ${profAccept
                  .val().apellido}`,
              offerId: event.after.key,
              type: TIPO_OFERTA_ACEPTADA,
            },
          };
          console.log(JSON.stringify(payload));
          admin.messaging().sendToDevice(tokenCli.val().token, payload);
        }
      }
    });

export const newCommentOffer = functions.database
    .ref("/Comentarios/{commentId}/{offerCommentId}")
    .onCreate(async (snap, context)=>{
      const db = admin.database();
      const dataComment = snap.val();
      console.log("Comment - In" + JSON.stringify(dataComment));
      const tokenCli = await db.ref("Tokens")
          .child(dataComment.idReceptor).get();
      console.log(JSON.stringify(tokenCli.val()));
      const user= await db.ref("Usuarios")
          .child(dataComment.idEmisor).get();
      const payload = {
        data: {
          comment: dataComment.mensaje,
          userEmisor: `${user.val().nombre} ${user.val().apellido}`,
          offerId: snap.key,
          type: TIPO_OFERTA_COMENTARIO,
        },
      };
      console.log(JSON.stringify(payload));
      admin.messaging().sendToDevice(tokenCli.val().token, payload);
    });
