package treecompany.cityofideas.fragments.profile


import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.AppCompatButton
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers

import treecompany.cityofideas.R
import treecompany.cityofideas.activities.profile.AccountActivity
import treecompany.cityofideas.activities.project.project.ProjectActivity
import treecompany.cityofideas.responses.LoginResponse

class LoginFragment : Fragment(){

    private lateinit var compositeDisposable: CompositeDisposable
    private lateinit var edit_email : EditText
    private lateinit var edit_password : EditText
    private lateinit var text_already : TextView
    private lateinit var login_button : AppCompatButton

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root_view = inflater.inflate(R.layout.fragment_login, container, false)
        compositeDisposable = CompositeDisposable()
        text_already = root_view.findViewById(R.id.log_already)
        edit_email = root_view.findViewById(R.id.log_email)
        edit_password = root_view.findViewById(R.id.log_password)
        login_button = root_view.findViewById(R.id.login_button)
        addEventHandlers()
        return root_view
    }

    fun addEventHandlers(){
        text_already.setOnClickListener {
            replaceFragment(RegisterFragment())
        }

        login_button.setOnClickListener{
            performLogin()
        }
    }

    private fun performLogin(){
        val email = edit_email.text.toString().trim()
        val password = edit_password.text.toString().trim()

        if(email.isEmpty()){
            edit_email.setError("Email is vereist!")
            edit_email.requestFocus()
            return
        }

        if(password.isEmpty()){
            edit_password.setError("Wachtwoord vereist!")
            edit_password.requestFocus()
            return
        }

        val disposable : Disposable = AccountActivity.mService.login(email, password)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Consumer<LoginResponse>{
                override fun accept(t: LoginResponse?) {
                    if(t!!.error == true){
                        AccountActivity.preConfig.writeLoginStatus(true)
                        AccountActivity.preConfig.saveUserId(t.userId)
                        val intent : Intent = Intent(context, ProjectActivity::class.java)
                        startActivity(intent)
                        AccountActivity.preConfig.displayToast(t.message.toString())
                    }
                    else
                    {
                        AccountActivity.preConfig.displayToast(t.message.toString())
                    }
                }

            }, object : Consumer<Throwable>{
                override fun accept(t: Throwable?) {
                    println(t!!.message.toString())
                    AccountActivity.preConfig.displayToast(t.message.toString())
                }

            })

        compositeDisposable.add(disposable)
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }

    private fun replaceFragment(fragment : Fragment){
        val fragmentTransaction = fragmentManager?.beginTransaction()
        fragmentTransaction?.replace(R.id.profile_fragment_container, fragment)
        fragmentTransaction?.commit()
    }

}
