package com.rival.noteapps.adapter



import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.content.Intent.getIntent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.rival.noteapps.MainActivity
import com.rival.noteapps.R
import com.rival.noteapps.database.NoteDB
import com.rival.noteapps.room.Note
import kotlinx.android.synthetic.main.add_layout.view.*
import kotlinx.android.synthetic.main.content_main.view.*
import kotlinx.android.synthetic.main.content_main.view.txt_title
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class NoteAdapter(private val noteList: ArrayList<Note>) :
    RecyclerView.Adapter<NoteAdapter.ViewHolder>() {

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.content_main, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.view.txt_title.text = noteList[position].title

        holder.itemView.setOnClickListener {
            val alertDialog = AlertDialog.Builder(holder.view.context).create()
            alertDialog.setTitle(noteList[position].title)
            alertDialog.setMessage(noteList[position].description)

            alertDialog.setButton(
                AlertDialog.BUTTON_POSITIVE, "Close"
            ) { dialog, which -> dialog.dismiss() }
            alertDialog.show()

            val btnPositive = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
            val layoutParams = btnPositive.layoutParams as LinearLayout.LayoutParams
            layoutParams.weight = 100f
            btnPositive.layoutParams = layoutParams
        }

        holder.view.btn_edit.setOnClickListener {
            val alert = AlertDialog.Builder(holder.view.context)
            val views = LayoutInflater.from(holder.view.context).inflate(R.layout.add_layout, null)
            alert.setView(views)
            alert.setCancelable(true)
            alert.setTitle("Update Catatan")
            val title = views.findViewById<View>(R.id.txt_title) as TextView
            val desc = views.findViewById<View>(R.id.txt_description) as TextView
            title.text = noteList[position].title
            desc.text = noteList[position].description
            alert.setPositiveButton(
                "Update",
                DialogInterface.OnClickListener { dialogInterface, i ->

                    val title = views.findViewById<EditText>(R.id.txt_title)
                    val desc = views.findViewById<EditText>(R.id.txt_description)

                    CoroutineScope(Dispatchers.IO).launch {
                        val db by lazy { NoteDB(holder.view.context as MainActivity) }
                        db.noteDao().updateNote(
                            // menggunakan entitiy yang sudah di buat
                            Note(noteList[position].id, title.text.toString(), desc.text.toString())
                        )
                        (holder.view.context as Activity).finish()
                        (holder.view.context as Activity).startActivity(
                            Intent(
                                holder.view.context,
                                MainActivity::class.java
                            )
                        )
                    }
                })
            alert.setNeutralButton(
                "close",
                DialogInterface.OnClickListener { dialogInterface, i -> dialogInterface.dismiss() })
            alert.show()
        }

        holder.view.btn_delete.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                val db by lazy { NoteDB(holder.view.context as MainActivity) }
                db.noteDao().deleteNote(noteList[position])
                (holder.view.context as Activity).finish()
                (holder.view.context as Activity).startActivity(
                    Intent(
                        holder.view.context,
                        MainActivity::class.java
                    )
                )
            }
        }
    }


    override fun getItemCount() = noteList.size

    fun setData(list: List<Note>) {
        noteList.clear()
        noteList.addAll(list)
        notifyDataSetChanged()
    }
}