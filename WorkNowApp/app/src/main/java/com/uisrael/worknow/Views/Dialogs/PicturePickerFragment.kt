package com.uisrael.worknow.Views.Dialogs

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.FileProvider
import androidx.fragment.app.DialogFragment
import com.uisrael.worknow.R
import com.uisrael.worknow.Views.TabsFragments.OffersRegisterFragment
import kotlinx.android.synthetic.main.picture_dialog_fragment.view.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class PicturePickerFragment (private val c: Context,  var activity: OffersRegisterFragment, var currentPhotoPath: String ) :  DialogFragment() {

    private val REQUESTCAMERA = 200
    private val REQUESTGALLERY = 201

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return  inflater.inflate(R.layout.picture_dialog_fragment, container)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.btnCameraRlt.setOnClickListener {
            takePictureIntent()
            dismiss()
        }

        view.btnGalleryRlt.setOnClickListener {
            openGalleryForImage()
            dismiss()
        }

        view.btnCancel.setOnClickListener {
            dismiss()
        }
    }

    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = c.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
                "JPEG_${timeStamp}_",
                ".jpg",
                storageDir
        ).apply {
            currentPhotoPath = absolutePath
        }
    }

    private fun takePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(c.packageManager)?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                            c,
                            "com.uisrael.worknow.fileprovider",
                            it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    activity.startActivityForResult(takePictureIntent, REQUESTCAMERA)
                }
            }
        }
    }

    private fun openGalleryForImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        activity.startActivityForResult(intent, REQUESTGALLERY)
    }

}