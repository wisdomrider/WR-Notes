package org.wisdomrider.notes

import android.content.Intent
import android.os.Bundle

class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        helper.createTable(LoginPage.NoteData())
        try {
            preferences.putString("add_text", intent.getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT).toString()).apply()
        } catch (e: Exception) {
            preferences.putString("add_text", "").apply()
        }
        api.Notes().get("", object : Do {
            override fun <T> Do(body: T?) {
                helper.removeAll(LoginPage.NoteData())
                helper.insertAll(
                    gson.fromJson(
                        gson.toJson(body).replace("\\u0027", "**"),
                        LoginPage.NotesResponse::class.java
                    ).data
                )
                startActivity(Intent(this@MainActivity, Home::class.java))
            }

        }, on406 = object : Do {
            override fun <T> Do(body: T?) {
                startActivity(Intent(this@MainActivity, LoginPage::class.java))
            }
        }, onNetworkError = object : Do {
            override fun <T> Do(body: T?) {
                if (preferences.getString("token", "")!!.isNotEmpty()) {
                    startActivity(Intent(this@MainActivity, Home::class.java))
                } else showAlert("Unable to Connect to Server !")
            }
        })

    }

}
