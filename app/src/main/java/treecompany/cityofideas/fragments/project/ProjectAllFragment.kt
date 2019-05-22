package treecompany.cityofideas.fragments.project


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SearchView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import treecompany.cityofideas.R
import treecompany.cityofideas.activities.project.project.ProjectActivity
import treecompany.cityofideas.adapters.project.ProjectAllAdapter
import treecompany.cityofideas.models.ProjectClasses.Project
import treecompany.cityofideas.responses.ProjectsResponse


class ProjectAllFragment : Fragment() {
    private lateinit var compositeDisposable: CompositeDisposable
    private lateinit var recycler: RecyclerView
    private lateinit var projects: List<Project>
    private lateinit var searchBar: SearchView
    private lateinit var empty_recycler_text: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root_view = inflater.inflate(R.layout.fragment_project_all, container, false)
        compositeDisposable = CompositeDisposable()
        recycler = root_view.findViewById(R.id.all_project_recycler)
        searchBar = root_view.findViewById(R.id.searchView)
        empty_recycler_text = root_view.findViewById(R.id.empty_recycler_text)
        projects = listOf()
        recycler.layoutManager = LinearLayoutManager(context, LinearLayout.VERTICAL, false)
        fetchInformation("")
        addEventHandlers()
        return root_view
    }

    fun addEventHandlers() {
        searchBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                fetchInformation(p0!!)
                return true
            }
        })
    }

    fun fetchInformation(searchString: String) {

       val disposable : Disposable = ProjectActivity.mService.getAllProjects(ProjectActivity.preConfig.getPlatform(), searchString)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Consumer<ProjectsResponse> {
                    override fun accept(t: ProjectsResponse?) {
                        if (t!!.error) {
                            if (t.projects.size == 0) {
                                empty_recycler_text.visibility  = View.VISIBLE
                                recycler.visibility = View.GONE

                            } else {
                                recycler.visibility = View.VISIBLE
                                empty_recycler_text.visibility  = View.GONE
                                recycler.adapter =
                                        ProjectAllAdapter(context, t.projects)
                            }
                        } else {
                            ProjectActivity.preConfig.displayToast(t.toString())
                        }
                    }
                }, object : Consumer<Throwable> {
                    override fun accept(t: Throwable?) {
                        ProjectActivity.preConfig.displayToast(t!!.message.toString())
                    }

                })
        compositeDisposable.add(disposable)
    }

    override fun onResume() {
        super.onResume()
        fetchInformation("")
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }
}
