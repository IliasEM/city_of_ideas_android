package treecompany.cityofideas.fragments.profile


import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers

import treecompany.cityofideas.R
import treecompany.cityofideas.activities.profile.AccountActivity
import treecompany.cityofideas.activities.profile.ProfileActivity
import treecompany.cityofideas.responses.RegisterResponse

class RegisterFragment : Fragment() {

    private lateinit var compositeDisposable: CompositeDisposable
    private lateinit var edit_username: EditText
    private lateinit var edit_email: EditText
    private lateinit var edit_street: EditText
    private lateinit var edit_city: EditText
    private lateinit var edit_post: EditText
    private lateinit var edit_password: EditText
    private lateinit var edit_password_confirm: EditText
    private lateinit var text_already: TextView
    private lateinit var register_button: Button


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root_view = inflater.inflate(R.layout.fragment_register, container, false)
        edit_username = root_view.findViewById(R.id.reg_username)
        edit_email = root_view.findViewById(R.id.reg_email)
        edit_street = root_view.findViewById(R.id.reg_street)
        edit_city = root_view.findViewById(R.id.reg_city)
        edit_post = root_view.findViewById(R.id.reg_post)
        edit_password = root_view.findViewById(R.id.reg_password)
        edit_password_confirm = root_view.findViewById(R.id.reg_password_confirm)

        text_already = root_view.findViewById(R.id.reg_already)

        register_button = root_view.findViewById(R.id.reg_regbutton)
        addEventHandlers()
        return root_view
    }

    fun addEventHandlers() {
        text_already.setOnClickListener {
            val fragment = LoginFragment()
            fragmentManager?.beginTransaction()
                ?.replace(R.id.profile_fragment_container, fragment)
                ?.addToBackStack(null)
                ?.commit()
        }

        register_button.setOnClickListener {
            performRegistration()
        }
    }

    private fun performRegistration() {
        val username = edit_username.text.toString().trim()
        val email = edit_email.text.toString().trim()
        val street = edit_street.text.toString().trim()
        val city = edit_city.text.toString().trim()
        val post = edit_post.text.toString().trim()
        val password = edit_password.text.toString().trim()
        val passwordConfirm = edit_password_confirm.text.toString().trim()

        if (email.isEmpty()) {
            edit_email.setError("Email is veresit!")
            edit_email.requestFocus()
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            edit_email.setError("Gebruik een geldige email!")
            edit_email.requestFocus()
            return
        }

        if (password.isEmpty()) {
            edit_password.setError("Wachtwoord is vereist!")
            edit_password.requestFocus()
            return
        }

        if (password.length < 6) {
            edit_password.setError("Wachtwoord moet minstens 6 tekens lang zijn")
            edit_password.requestFocus()
            return
        }

        if (!password.equals(passwordConfirm)) {
            edit_password.setError("Wachtwoorden moeten het zelfde zijn")
            edit_password_confirm.setError("Wachtwoorden moeten het zelfde zijn")
            return
        }

        val disposable: Disposable = AccountActivity.mService.register(
            username,
            email,
            phonenumber = "",
            street = street,
            city = city,
            post = post,
            password = password
        )
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Consumer<RegisterResponse> {
                override fun accept(t: RegisterResponse?) {
                    if (t!!.error) {
                        AccountActivity.preConfig.writeLoginStatus(true)
                        AccountActivity.preConfig.saveUserId(t.userId)
                        println(AccountActivity.preConfig.getUserId())
                        val intent: Intent = Intent(context, ProfileActivity::class.java)
                        startActivity(intent)
                        AccountActivity.preConfig.displayToast(t.message.toString())
                    } else {
                        AccountActivity.preConfig.displayToast(t.message.toString())
                    }
                }
            }, object : Consumer<Throwable> {
                override fun accept(t: Throwable?) {
                    AccountActivity.preConfig.displayToast(t!!.message.toString())
                }
            })
        compositeDisposable.add(disposable)
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }
}
