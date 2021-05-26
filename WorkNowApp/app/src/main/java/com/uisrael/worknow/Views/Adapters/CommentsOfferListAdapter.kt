package com.uisrael.worknow.Views.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.uisrael.worknow.Model.Data.ComentariosData
import com.uisrael.worknow.R
import kotlinx.android.synthetic.main.comment_item_emisor.view.*
import kotlinx.android.synthetic.main.comment_item_receptor.view.*
class CommentsOfferListAdapter(
    val c: Context,
    var comentarios: ArrayList<ComentariosData>,
    val currentUid:String,
    ) : BaseAdapter() {

    override fun getCount(): Int {
        return comentarios.size
    }

    override fun getItem(position: Int): Any {
        return comentarios[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {

        val convertView: View? = when (comentarios[position].idEmisor){
            currentUid ->{
                LayoutInflater.from(c).inflate(R.layout.comment_item_emisor, null)
            }
            else ->{
                LayoutInflater.from(c).inflate(R.layout.comment_item_receptor, null)
            }
        }

        when (comentarios[position].idEmisor){
            currentUid ->{
                convertView?.mensajeTxtEmisor?.text = comentarios[position].mensaje
            }else ->{
                convertView?.mensajeTxtReceptor?.text = comentarios[position].mensaje
            }
        }

        return convertView
    }
}