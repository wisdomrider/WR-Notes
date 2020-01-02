package org.wisdomrider.notes

import android.app.Dialog
import android.text.InputType
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*


/*
   Created By WisdomRider(Avishek Adhikari)
    Email : avishekzone@gmail.com
    Make Sure to Star Me On Github :
       https://github.com/wisdomrider/SqliteClosedHelper
     Credit Me SomeWhere In Your Project :)
     Thanks !!
*/
class NotesAdapter(internal var home: Home, var notes: ArrayList<LoginPage.NoteData>) :
    RecyclerView.Adapter<NotesAdapter.WisdomHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): WisdomHolder {
        val v = View.inflate(viewGroup.context, R.layout.note, null)
        return WisdomHolder(v)
    }


    override fun onBindViewHolder(wisdomHolder: WisdomHolder, i: Int) {
        val note = notes[i]
        wisdomHolder.title.text = note.title
        wisdomHolder.content.text = note.desc.replace("**", "'")
        if (note.title.toLowerCase().contains("password") || note.title.toLowerCase().contains("secret")
        ) wisdomHolder.content.setInputType(InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD)
        else wisdomHolder.content.setInputType(InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_MULTI_LINE)
        wisdomHolder.title.tag = note._id
        val inputFormat = SimpleDateFormat("yyyy-mm-dd hh:mm:ss", Locale.US)
        inputFormat.setTimeZone(TimeZone.getTimeZone("Etc/UTC"))
        val outputFormat = SimpleDateFormat("dd MMMM yyyy")
        try {
            val date = inputFormat.parse(note.createdAt.replace("T", " ").replace("Z", "").split(".")[0])
            val outputText = "- " + outputFormat.format(date)
            wisdomHolder.date.text = outputText
        } catch (e: Exception) {
            val d = Date()
            d.time = note.time!!
            wisdomHolder.date.text = "- ${SimpleDateFormat("dd MMMM yyyy").format(d)}"
        }


        val click = View.OnClickListener {
            val dialog = Dialog(home)
            dialog.setContentView(R.layout.note)
            var title = dialog.findViewById<EditText>(R.id.title)
            var desc = dialog.findViewById<EditText>(R.id.desc)
            title.isFocusable = true
            title.isFocusableInTouchMode = true
            desc.isFocusable = true
            desc.isFocusableInTouchMode = true
            title.setText(note.title)
            title.tag = note._id
            desc.setText(note.desc.replace("**", "'"))
            dialog.findViewById<TextView>(R.id.date).setText(wisdomHolder.date.text.toString())
            dialog.show()
            dialog.setOnDismissListener {
                if (desc.text.trim().isEmpty() || title.text.trim().isEmpty()) {

                } else if (!title.text.toString().equals(note.title) || !desc.text.toString().equals(note.desc)) {
                    home.onCancel(title.text.toString(), desc.text.toString(), title.tag.toString(), wisdomHolder)
                }
            }
        }

        wisdomHolder.title.setOnClickListener(click)
        wisdomHolder.content.setOnClickListener(click)
    }

    override fun getItemCount(): Int {
        return notes.size
    }

    inner class WisdomHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var title: TextView = itemView.findViewById(R.id.title)
        internal var content: TextView = itemView.findViewById(R.id.desc)
        internal var date: TextView = itemView.findViewById(R.id.date)


    }


}
