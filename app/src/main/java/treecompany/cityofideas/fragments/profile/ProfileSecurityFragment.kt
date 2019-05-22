package treecompany.cityofideas.fragments.profile


import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
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
import treecompany.cityofideas.activities.profile.ProfileActivity
import treecompany.cityofideas.responses.ProfileResponse

class ProfileSecurityFragment : Fragment() {

    private lateinit var compositeDisposable: CompositeDisposable

    private lateinit var edit_current: EditText
    private lateinit var edit_new: EditText
    private lateinit var edit_confirm: EditText
    private lateinit var change_password_button: Button

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root_view = inflater.inflate(R.layout.fragment_profile_security_settings, container, false)
        compositeDisposable = CompositeDisposable()
        edit_current = root_view.findViewById(R.id.sec_old_password)
        edit_new = root_view.findViewById(R.id.sec_new_password)
        edit_confirm = root_view.findViewById(R.id.sec_new_password_confirm)
        change_password_button = root_view.findViewById(R.id.change_password_button)
        addEventHandlers()
        return root_view
    }

    fun addEventHandlers() {
        change_password_button.setOnClickListener {
            changePassword()
        }
    }

    fun changePassword() {
        val current = edit_current.text.toString().trim()
        val new = edit_new.text.toString().trim()
        val confirm = edit_confirm.text.toString().trim()

        if (current.isEmpty()) {
            edit_current.setError("Huidig wachtwoord is vereist! ")
            edit_current.requestFocus()
            return
        }
        if (new.isEmpty()) {
            edit_new.setError("Nieuw wachtwoord is vereist! ")
            edit_new.requestFocus()
            return
        }
        if (new.length < 6) {
            edit_new.setError("Wachtwoord moet minstens 6 tekens lang zijn")
            edit_new.requestFocus()
            return
        }
        if (confirm.isEmpty()) {
            edit_confirm.setError("Dit wachtwoord is vereist!")
            edit_confirm.requestFocus()
            return
        }
        if (!confirm.equals(new)) {
            edit_confirm.setError("De wachtwoorden moeten gelijk zijn")
            edit_confirm.requestFocus()
            return
        }
        val disposable: Disposable =
            ProfileActivity.mService.changePassword(ProfileActivity.preConfig.getUserId(), current, new)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Consumer<ProfileResponse> {
                    override fun accept(t: ProfileResponse?) {
                        if (t!!.error) {
                            ProfileActivity.preConfig.displayToast("Wachtwoord veranderd")
                        } else {
                            Log.d("AccountSettings", "geen user")
                        }
                    }
                }, object : Consumer<Throwable> {
                    override fun accept(t: Throwable?) {
                        ProfileActivity.preConfig.displayToast(t!!.message.toString())
                    }
                })

        compositeDisposable.add(disposable)

        edit_confirm.setText("", TextView.BufferType.EDITABLE)
        edit_current.setText("", TextView.BufferType.EDITABLE)
        edit_new.setText("", TextView.BufferType.EDITABLE)
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }


}
