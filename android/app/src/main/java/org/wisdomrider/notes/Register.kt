package org.wisdomrider.notes

import android.content.Intent
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_register.*
import kotlin.math.log

class Register : BaseActivity() {

    class Register(var username: String, var password: String, var name: String)
    class RResponse(var success: Boolean)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        validator.add(name)
        validator.add(username)
        validator.add(password)

        register.setOnClickListener {
            if (validator.validate()) {
                api.Register(
                    org.wisdomrider.notes.Register.Register(
                        username.text.toString(),
                        password.text.toString(),
                        name.text.toString()
                    )
                )
                    .get("Registering", object : Do {
                        override fun <T> Do(body: T?) {
                            if (!(body as RResponse).success) {
                                showAlert("Email already exists !")
                            } else {
                                wisdom.toast("Registered now please login !")
                                startActivity(Intent(this@Register,LoginPage::class.java))
                            }
                        }
                    })
            }
        }
        login.setOnClickListener {
            startActivity(Intent(this@Register,LoginPage::class.java))
        }
    }
}
