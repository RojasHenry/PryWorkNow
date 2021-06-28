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
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

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
                convertView?.fechaHoraTxtEmisor?.text =  comentarios[position].estado + " · " + getTime(comentarios[position].timespan.toLong())
            }else ->{
                convertView?.mensajeTxtReceptor?.text = comentarios[position].mensaje
                convertView?.fechaHoraTxtReceptor?.text = getTime(comentarios[position].timespan.toLong())+ " · " + comentarios[position].estado
            }
        }

        return convertView
    }

    private fun getTime (timespan:Long): String{
        val sdf = SimpleDateFormat("dd/MM/yy hh:mm a")
        val netDate = Date(timespan)
        val date = sdf.format(netDate)
        return date.toString()
    }
}