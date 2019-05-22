package treecompany.cityofideas.fragments.profile


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers

import treecompany.cityofideas.R
import treecompany.cityofideas.activities.profile.ProfileActivity
import treecompany.cityofideas.responses.ProfileResponse

class ProfileOverviewFragment : Fragment() {

    private lateinit var compositeDisposable: CompositeDisposable

    private var userId: String = ""

    private lateinit var text_email: TextView
    private lateinit var text_name: TextView
    private lateinit var text_phonenumber: TextView
    private lateinit var text_address: TextView
    private lateinit var prof_image: ImageView

    private lateinit var prof_votes: TextView
    private lateinit var prof_likes: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root_view = inflater.inflate(R.layout.fragment_profile_overview, container, false)
        compositeDisposable = CompositeDisposable()
        text_email = root_view.findViewById(R.id.prof_email)
        text_name = root_view.findViewById(R.id.prof_name)
        text_phonenumber = root_view.findViewById(R.id.prof_phonenumber)
        text_address = root_view.findViewById(R.id.prof_address)
        prof_image = root_view.findViewById(R.id.prof_image)

        prof_votes = root_view.findViewById(R.id.prof_votes)
        prof_likes = root_view.findViewById(R.id.prof_likes)

        addEventHandlers()
        userId = ProfileActivity.preConfig.getUserId()
        loadData();
        return root_view
    }

    private fun loadData() {

        val disposable : Disposable = ProfileActivity.mService.getUser(userId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Consumer<ProfileResponse> {
                    override fun accept(t: ProfileResponse?) {
                        if (t!!.error) {
                            text_email.text = t.user.email
                            text_name.text = t.user.userName
                            prof_votes.text = "" + t.votes
                            prof_likes.text = "" + t.likes

                            if (t.user.phoneNumber == null) {
                                text_phonenumber.text = "Geen"
                            } else {
                                text_phonenumber.text = t.user.phoneNumber
                            }
                            if (t.user.address == null) {
                                text_address.text = "Geen"
                            } else {
                                if (t.user.address.street.equals("") && t.user.address.zipcode.equals("") && t.user.address.city.equals(""))
                                    text_address.text = "Geen"
                                else
                                    text_address.text = t.user.address.street + ", " + t.user.address.zipcode + " " + t.user.address.city
                            }

                            prof_image.setImageResource(resources.getIdentifier(splitImagePath(t.user.image), "drawable", context!!.packageName))
                            println()

                        } else {
                            ProfileActivity.preConfig.displayToast(t.toString())
                        }
                    }
                }, object : Consumer<Throwable> {
                    override fun accept(t: Throwable?) {
                        ProfileActivity.preConfig.displayToast(t!!.message.toString())
                    }
                })
                 compositeDisposable.add(disposable)
    }

    fun splitImagePath(imagePath: String): String {
        val image = imagePath.split("""\\""".toRegex())
        val finalImage = image[2].split(".")
        return finalImage[0].toLowerCase()
    }

    fun addEventHandlers() {
    }


    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }
}

