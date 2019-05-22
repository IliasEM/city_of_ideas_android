package treecompany.cityofideas.fragments.profile

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers

import treecompany.cityofideas.R
import treecompany.cityofideas.activities.profile.ProfileActivity
import treecompany.cityofideas.responses.ProfileResponse

class ProfileAccountFragment : Fragment() {

    private lateinit var compositeDisposable: CompositeDisposable
    private var userId : String = ""
    private var addressId : String = ""

    private lateinit var text_phonenumber : TextView
    private lateinit var text_straat : TextView
    private lateinit var text_city : TextView
    private lateinit var text_post : TextView

    private lateinit var save_button : Button

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root_view = inflater.inflate(R.layout.fragment_profile_account_settings, container, false)
        compositeDisposable = CompositeDisposable()
        text_phonenumber = root_view.findViewById(R.id.acc_phonenumber)
        text_straat = root_view.findViewById(R.id.acc_street)
        text_city = root_view.findViewById(R.id.acc_city)
        text_post = root_view.findViewById(R.id.acc_post)
        save_button = root_view.findViewById(R.id.acc_save)

        loadData()
        addEventHandlers()
        userId = ProfileActivity.preConfig.getUserId()
        return root_view
    }

    private fun loadData() {

        val disposable : Disposable = ProfileActivity.mService.getUser(userId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Consumer<ProfileResponse> {
                override fun accept(t: ProfileResponse?) {
                    if(t!!.error){
                        text_phonenumber.setText(t.user.phoneNumber, TextView.BufferType.EDITABLE)
                        text_straat.setText(t.user.address.street, TextView.BufferType.EDITABLE)
                        text_city.setText(t.user.address.city, TextView.BufferType.EDITABLE)
                        text_post.setText(t.user.address.zipcode, TextView.BufferType.EDITABLE)

                        userId = t.user.id
                        addressId = t.user.address.id
                    }
                    else
                    {
                        Log.d("AccountSettings", "geen user")
                    }
                }

            }, object : Consumer<Throwable>{
                override fun accept(t: Throwable?) {
                    ProfileActivity.preConfig.displayToast(t!!.message.toString())
                }
            })
            compositeDisposable.add(disposable)
    }

    fun addEventHandlers(){
        save_button.setOnClickListener {
            updateUser()
        }
    }

    private fun updateUser() {
        val phonenumber = text_phonenumber.text.toString()
        val street = text_straat.text.toString()
        val city = text_city.text.toString()
        val post = text_post.text.toString()


       val disposable : Disposable = ProfileActivity.mService.updateUser(userId,addressId, phonenumber, street, city, post)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Consumer<ProfileResponse>{
                override fun accept(t: ProfileResponse?) {
                    if(t!!.error){
                        val intent : Intent = Intent(context, ProfileActivity::class.java)
                        startActivity(intent)
                    }
                    else
                    {
                        Log.d("AccountSettings", "geen user")
                    }
                }
            }, object : Consumer<Throwable>{
                override fun accept(t: Throwable?) {
                    ProfileActivity.preConfig.displayToast(t!!.message.toString())
                }
            })
            compositeDisposable.add(disposable)
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }
}
