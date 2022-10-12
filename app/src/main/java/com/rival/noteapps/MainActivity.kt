package com.rival.noteapps

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.rival.noteapps.adapter.NoteAdapter
import com.rival.noteapps.database.NoteDB
import com.rival.noteapps.databinding.ActivityMainBinding
import com.rival.noteapps.room.Note
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    val db by lazy { NoteDB(this) }

    var progressDialog: Dialog? = null
    lateinit var noteAdapter: NoteAdapter
    private lateinit var binding: ActivityMainBinding

    public fun notez() = reload()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()

        binding.fab.setOnClickListener {
            insertDialog()
        }

        progressDialog = Dialog(this)
        progressDialog?.setContentView(R.layout.loading_dialog)
        progressDialog?.let {
            it.setCancelable(false)
            it.window?.setBackgroundDrawableResource(android.R.color.transparent)
        }
        // progress dialog with time out





    }

    override fun onStart() {
        super.onStart()
        CoroutineScope(Dispatchers.IO).launch {
            val notes = db.noteDao().getAllNotes()
            Log.d("MainActivity", "DBResponse: $notes")
            withContext(Dispatchers.Main) {
                noteAdapter.setData(notes)
            }
        }
    }

    private fun insertDialog() {

        progressDialog?.show()


        val alert = AlertDialog.Builder(this)
        val view = layoutInflater.inflate(R.layout.add_layout, null)
        alert.setView(view)
        alert.setCancelable(false)
        alert.setTitle("Buat Catatan Baru")
        alert.setPositiveButton(
            "Save",
            DialogInterface.OnClickListener { dialogInterface, i ->
                val title = view.findViewById<EditText>(R.id.txt_title)
                val desc = view.findViewById<EditText>(R.id.txt_description)

                CoroutineScope(Dispatchers.IO).launch {
                    db.noteDao().createNote(
                        // menggunakan entitiy yang sudah di buat
                        Note(0, title.text.toString(), desc.text.toString())
                    )
                    withContext(Dispatchers.Main) {
                        Handler().postDelayed({
                            progressDialog?.dismiss()
                        }, 2000)
                    }

//          this@MainActivity.let { reload() }
                }






            })
        alert.setNeutralButton(
            "close",
            DialogInterface.OnClickListener { dialogInterface, i -> dialogInterface.dismiss()
                progressDialog?.dismiss()}

        )
        alert.show()

    }

    private fun reload() {
        CoroutineScope(Dispatchers.IO).launch {
            val notes = db.noteDao().getAllNotes()
            Log.d("MainActivity", "DBResponse: $notes")
            withContext(Dispatchers.Main) {
                noteAdapter.setData(notes)
            }
        }
    }

    private fun setupRecyclerView() {
        noteAdapter = NoteAdapter(arrayListOf())
        rv_note.apply {
            layoutManager = LinearLayoutManager(applicationContext)
            adapter = noteAdapter
        }
    }


}