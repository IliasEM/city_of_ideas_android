package treecompany.cityofideas.activities.project.phase

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.StrictMode
import android.provider.MediaStore
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import treecompany.cityofideas.R
import treecompany.cityofideas.activities.platform.PlatformActivity
import treecompany.cityofideas.activities.project.project.ProjectActivity
import treecompany.cityofideas.activities.qrscanner.QrScannerActivity
import treecompany.cityofideas.api.IMyAPI
import treecompany.cityofideas.common.Common
import treecompany.cityofideas.responses.DefaultResponse
import treecompany.cityofideas.utils.PreConfig
import java.io.File

class IdeaAddActivity : AppCompatActivity() {
    private val RESULT_LOAD_IMAGE = 1
    private var image: String = ""
    private lateinit var compositeDisposable: CompositeDisposable

    private lateinit var idea_title: EditText
    private lateinit var idea_description: EditText
    private lateinit var idea_image: ImageView


    private lateinit var upload_idea_image: Button
    private lateinit var post_idea_button: Button

    private var ideationId: String = ""
    private var userId: String = ""

    companion object {
        lateinit var preConfig: PreConfig
        internal lateinit var mService: IMyAPI
        private const val ACTIVITY_NUM = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_idea_add)
        if (android.os.Build.VERSION.SDK_INT > 9) {
            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)
        }
        initialise()
        toolbarSetup()
        setUpBottomNavigationView()
        addEventHandlers()
    }

    fun initialise() {
        compositeDisposable = CompositeDisposable()
        preConfig = PreConfig(this)
        mService = Common.api

        idea_title = findViewById(R.id.idea_title)
        idea_description = findViewById(R.id.idea_description)
        idea_image = findViewById(R.id.idea_image_upload)

        upload_idea_image = findViewById(R.id.idea_upload_image)
        post_idea_button = findViewById(R.id.idea_post)

        ideationId = intent.getStringExtra("ideationId")
        userId = preConfig.getUserId()
    }

    fun addEventHandlers() {
        upload_idea_image.setOnClickListener {
            val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(galleryIntent, RESULT_LOAD_IMAGE)
        }

        post_idea_button.setOnClickListener {
            postIdea()
        }
    }

    private fun postIdea() {
        val title: String = idea_title.text.toString()
        val description: String = idea_description.text.toString()

        if (title.isEmpty()) {
            idea_title.setError("Titel is vereist!")
            idea_title.requestFocus()
            return
        }

        if (description.isEmpty()) {
            idea_description.setError("Beschrijving is vereist!")
            idea_description.requestFocus()
            return
        }

        if (idea_image.drawable == null) {
            preConfig.displayToast("Je moet een foto uploaden")
            return
        }

        val disposable: Disposable =
            mService.postIdea(ideationId, title, description, "images\\ideas\\" + image, userId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Consumer<DefaultResponse> {
                    override fun accept(t: DefaultResponse?) {
                        if (t!!.error) {
                            finish()
                            preConfig.displayToast("Uw idee wordt geverifieerd")
                        }
                    }
                }, object : Consumer<Throwable> {
                    override fun accept(t: Throwable?) {
                        preConfig.displayToast(t!!.message.toString())
                    }
                })
        compositeDisposable.add(disposable)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode === RESULT_LOAD_IMAGE && resultCode === Activity.RESULT_OK && data != null) {
            val selectedImage: Uri = data.data
            val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
            val cursor = contentResolver.query(
                selectedImage,
                filePathColumn, null, null, null
            )
            cursor!!.moveToFirst()
            val columnIndex = cursor.getColumnIndex(filePathColumn[0])
            val picturePath = cursor.getString(columnIndex)
            val f: File = File(picturePath)
            image = f.name
            idea_image.setImageURI(selectedImage)
        }
    }

    fun setUpBottomNavigationView(){
        val bottomNavigationViewEx: BottomNavigationViewEx = findViewById(R.id.bottomNavViewBar) as BottomNavigationViewEx
        PreConfig.setupBottomNavigationView(bottomNavigationViewEx)
        enableNavigation(this, bottomNavigationViewEx)
        val menu : Menu = bottomNavigationViewEx.menu
        val menuItem : MenuItem = menu.getItem(ACTIVITY_NUM)
        menuItem.setChecked(true)
    }

    private fun toolbarSetup() {
        val toolbar = findViewById(R.id.toolbar) as android.support.v7.widget.Toolbar
        toolbar.setTitle(ProjectActivity.preConfig.getPlatformName())
        setSupportActionBar(toolbar)
    }

    fun enableNavigation(context: Context, view : BottomNavigationViewEx){
        view.onNavigationItemSelectedListener = object: BottomNavigationView.OnNavigationItemSelectedListener {
            override fun onNavigationItemSelected(item: MenuItem): Boolean {
                when(item.itemId){
                    R.id.ic_house -> {
                        val intent = Intent(context, ProjectActivity::class.java)
                        context.startActivity(intent)
                        overridePendingTransition(0,0)
                    }
                    R.id.ic_platform ->{
                        val intent = Intent(context, PlatformActivity::class.java)
                        context.startActivity(intent)
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                    }
                    R.id.ic_qr_code->{
                        val intent = Intent(context, QrScannerActivity::class.java)
                        context.startActivity(intent)
                        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
                    }
                }
                return false
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }
}
