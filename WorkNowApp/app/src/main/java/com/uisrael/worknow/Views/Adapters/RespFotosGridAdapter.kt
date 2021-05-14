package com.uisrael.worknow.Views.Adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.squareup.picasso.Picasso
import com.uisrael.worknow.R
import com.uisrael.worknow.Views.Dialogs.PicturePickerFragment
import com.uisrael.worknow.Views.TabsFragments.OffersRegisterFragment
import kotlinx.android.synthetic.main.item_image_adapter.view.*
import java.io.File
import kotlin.collections.ArrayList

class RespFotosGridAdapter (private val c: Context, var images: ArrayList<String>, private val activity: OffersRegisterFragment) :
    BaseAdapter() {
    private var currentPhotoPath: String = ""
    private var itemIndex = -1
    private lateinit var picturePickerFragment: PicturePickerFragment

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val convertView: View? = LayoutInflater.from(c).inflate(R.layout.item_image_adapter, null)

        val path = images[position]

        if(path.isNotBlank()){
            convertView!!.viewNoimage.visibility = View.GONE
            convertView.itemImage.visibility = View.VISIBLE
            convertView.deleteItemImage.visibility = View.VISIBLE

            convertView.itemImage.setOnClickListener{
                itemIndex = position
                picturePickerFragment = PicturePickerFragment(c,activity,currentPhotoPath)
                picturePickerFragment.show(activity.childFragmentManager,"frg_dialog_picker")
            }

            convertView.deleteItemImage.setOnClickListener{
                activity.updateImages(position)
            }

            Picasso.get()
                .load(File(path))
                .resize(250, 250)
                .centerCrop()
                .into(convertView.itemImage)
        }

        convertView!!.viewNoimage.setOnClickListener {
            itemIndex = -1
            picturePickerFragment = PicturePickerFragment(c,activity,currentPhotoPath)
            picturePickerFragment.show(activity.childFragmentManager,"frg_dialog_picker")
        }

        return convertView
    }

    fun getCurrentPhotoPath():String {
        return picturePickerFragment.currentPhotoPath
    }

    fun getItemIndex():Int {
        return itemIndex
    }

    override fun getCount(): Int {
        return images.size
    }

    override fun getItem(position: Int): String {
        return images[position]
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

}

