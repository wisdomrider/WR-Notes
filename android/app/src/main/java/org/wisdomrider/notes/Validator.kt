package org.wisdomrider.notes

import android.text.InputType
import android.widget.EditText
import java.util.*
/*
   Created By WisdomRider(Avishek Adhikari)
    Email : avishekzone@gmail.com
    Make Sure to Star Me On Github :
       https://github.com/wisdomrider/SqliteClosedHelper
     Credit Me SomeWhere In Your Project :)
     Thanks !!
*/
class Validator {
    internal var items = ArrayList<EditText>()

    fun add(e: EditText) {
        items.add(e)
    }

    fun remove(e: EditText) {
        items.remove(e)
    }

    fun validate(): Boolean {
        for (x in items) {
            var text = x.text.toString().trim()
            if (text.isEmpty()) {
                x.err(Constants.FILL)
                return false
            }
            when (x.inputType) {
                InputType.TYPE_CLASS_PHONE -> if (text.length != 10) {
                    x.err("Please enter valid phonenumber")
                    return false
                }
                InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS -> if (!text.contains("@") || !text.contains(".")) {
                    x.err("Please enter valid email")
                    return false
                }


            }
        }

        return true
    }

    fun EditText.err(fill: String) {
        this.requestFocus()
        this.error = fill

    }


}
