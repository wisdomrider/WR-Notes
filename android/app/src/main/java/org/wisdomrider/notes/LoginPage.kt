package org.wisdomrider.notes

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import com.wisdomrider.sqliteclosedhelper.SqliteAnnotations
import kotlinx.android.synthetic.main.activity_login_page.*

class LoginPage : BaseActivity() {

    class Login(var username: String, var password: String)

    class LoginResponse(var data: Data)
    class Data(var key: String)
    class NotesResponse(var data: ArrayList<NoteData>)
    class NoteData
        (
        var time: Long? = null,
        var title: String = "",
        var desc: String = "",
        var createdAt: String = "",
        @SqliteAnnotations.Primary
        var _id: String = ""
    )

    class Add(var title: String, var desc: String);

    override fun onBackPressed() {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_page)
        validator.add(username)
        validator.add(password)
        checkForPermission()
        login.setOnClickListener {
            if (validator.validate()) {
                api.Login(Login(username.text.toString(), password.text.toString()))
                    .get("Logging In !", object : Do {
                        override fun <T> Do(body: T?) {
                            helper.removeAll(NoteData())
                            preferences.putString("token", (body as LoginResponse).data.key)
                                .apply()
                            api.Notes().get("Fetching Notes !", object : Do {
                                override fun <T> Do(body: T?) {
                                    helper.insertAll(
                                        gson.fromJson(
                                            gson.toJson(body).replace("\\u0027", "**"),
                                            LoginPage.NotesResponse::class.java
                                        ).data
                                    )
                                    startActivity(Intent(this@LoginPage, Home::class.java))
                                }

                            })
                        }

                    })

            }
        }
        register.setOnClickListener {
            startActivity(Intent(this@LoginPage, Register::class.java))
        }
    }

    private fun checkForPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
            && !Settings.canDrawOverlays(this)
        ) {
            wisdom.toast("Enable this feature to save note anywhere !")
            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))
            startActivityForResult(intent, 1)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1) {
            checkForPermission()
        }
    }
}
