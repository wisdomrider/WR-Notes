package org.wisdomrider.notes

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.EditText
import android.widget.SearchView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_home.*
import java.util.*


class Home : BaseActivity(), SearchView.OnQueryTextListener {

    class Response(var data: Dat)
    class Dat(var id: String)

    var showDialog = false
    var notes = ArrayList<LoginPage.NoteData>()
    lateinit var mainnotes: ArrayList<LoginPage.NoteData>
    lateinit var adapter: NotesAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        mainnotes = helper.getAll(LoginPage.NoteData())
        mainnotes.reverse()

        notes.clear()
        notes.addAll(mainnotes)
        adapter = NotesAdapter(this, notes)
        recycle.adapter = adapter
        recycle.layoutManager = LinearLayoutManager(this)

        fab.setOnClickListener {
            val dialog = Dialog(this)
            dialog.setContentView(R.layout.note)
            val date = Date().toLocaleString().split(" ")
            val date1 = date[0] + " " + date[1] + " " + date[2]
            dialog.findViewById<TextView>(R.id.date).text = "- ${date1}"
            dialog.show()
            val title = dialog.findViewById<EditText>(R.id.title)
            val desc = dialog.findViewById<EditText>(R.id.desc)
            title.isFocusable = true
            title.isFocusableInTouchMode = true
            desc.isFocusable = true
            desc.isFocusableInTouchMode = true
            dialog.setOnCancelListener {
                if (desc.text.trim().isEmpty() || title.text.trim().isEmpty()) {
                } else {
                    sync.visibility = VISIBLE
                    api.addNote(LoginPage.Add(title.text.toString(), desc.text.toString())).get("", object : Do {
                        override fun <T> Do(body: T?) {
                            sync.visibility = GONE
                            mainnotes.reverse()
                            mainnotes.add(
                                LoginPage.NoteData(
                                    title = title.text.toString(),
                                    desc = desc.text.toString(),
                                    time = System.currentTimeMillis()
                                    , _id = (body as Response).data.id
                                )
                            )
                            mainnotes.reverse()
                            notes.clear()
                            notes.addAll(mainnotes)
                            adapter.notifyDataSetChanged()
                        }

                    })
                }


            }

        }

        checkForPermission()
        title = "My Notes"
        if (preferences.getString("add_text", "")!!.isNotEmpty()) {
            val dialog = Dialog(this)
            dialog.setContentView(R.layout.note)
            dialog.findViewById<EditText>(R.id.desc).setText(preferences.getString("add_text", ""))
            val date = Date().toLocaleString().split(" ")
            val date1 = date[0] + " " + date[1] + " " + date[2]
            dialog.findViewById<TextView>(R.id.date).text = "- ${date1}"
            dialog.show()
            preferences.putString("add_text", "").apply()
        }

    }

    private fun checkForPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
            && !Settings.canDrawOverlays(this)
        ) {
            wisdom.toast("Enable this feature to save note anywhere !")
            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))
            startActivityForResult(intent, 1)
        } else {
            startService(Intent(this, ChatHead::class.java))
        }

    }

    override fun onBackPressed() {

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        val searchItem = menu.findItem(R.id.app_bar_search)
        val searchView = searchItem.getActionView() as SearchView
        searchView.setOnQueryTextListener(this)
        return true
    }

    override fun onQueryTextChange(query: String): Boolean {
        if (query.trim().isNotEmpty()) {
            val note =
                mainnotes.filter {
                    it.title.toLowerCase().contains(query.toLowerCase()) || it.desc.toLowerCase().contains(
                        query.toLowerCase()
                    )
                }
            notes.clear()
            notes.addAll(note)
            adapter.notifyDataSetChanged()
        } else {
            notes.clear()
            notes.addAll(mainnotes)
            adapter.notifyDataSetChanged()
        }
        // Here is where we are going to implement the filter logic
        return false
    }

    override fun onQueryTextSubmit(query: String): Boolean {
        return false
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1) {
            checkForPermission()
        }
    }

    fun onCancel(title: String, desc: String, tag: String, wisdomHolder: NotesAdapter.WisdomHolder) {
        sync.visibility = VISIBLE
        if (title.equals("DEL") || title.equals("DELETE")) {
            api.Delete(tag).get("", object : Do {
                override fun <T> Do(body: T?) {
                    sync.visibility = GONE
                    val note = mainnotes.filter { it._id.equals(tag) }[0]
                    mainnotes.remove(note)
                    notes.clear()
                    notes.addAll(mainnotes)
                    adapter.notifyDataSetChanged()
                }

            })
        } else {
            api.editNote(LoginPage.Add(title, desc), tag)
                .get("", object : Do {
                    override fun <T> Do(body: T?) {
                        sync.visibility = GONE
                        val note = mainnotes.filter { it._id.equals(tag) }[0]
                        note.title = title
                        note.desc = desc
                        notes.clear()
                        notes.addAll(mainnotes)
                        adapter.notifyDataSetChanged()
                    }
                })

        }


    }

}
