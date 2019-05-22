package treecompany.cityofideas.fragments.project


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers

import treecompany.cityofideas.R
import treecompany.cityofideas.activities.project.project.ProjectActivity
import treecompany.cityofideas.adapters.project.ProjectPopularAdapter
import treecompany.cityofideas.models.ProjectClasses.Project
import treecompany.cityofideas.responses.ProjectsResponse
import android.app.ProgressDialog
import android.widget.TextView
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable


class ProjectPopularFragment : Fragment() {

    private lateinit var compositeDisposable: CompositeDisposable
    private lateinit var recycler_pop: RecyclerView
    private lateinit var recycler_recent: RecyclerView
    private lateinit var projects: List<Project>
    private lateinit var empty_recycler_text_pop: TextView
    private lateinit var empty_recycler_text_rec: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root_view = inflater.inflate(R.layout.fragment_project_popular, container, false)
        compositeDisposable = CompositeDisposable()
        recycler_pop = root_view.findViewById(R.id.recycler_project_pop)
        recycler_recent = root_view.findViewById(R.id.recycler_project_recent)
        empty_recycler_text_pop = root_view.findViewById(R.id.empty_recycler_text_pop)
        empty_recycler_text_rec = root_view.findViewById(R.id.empty_recycler_text_rec)

        projects = listOf()
        recycler_pop.layoutManager =
            LinearLayoutManager(context, LinearLayout.HORIZONTAL, false) as RecyclerView.LayoutManager?
        recycler_recent.layoutManager =
            LinearLayoutManager(context, LinearLayout.HORIZONTAL, false) as RecyclerView.LayoutManager?

        fetchInformation()
        addEventHandlers()
        return root_view
    }

    fun addEventHandlers() {
    }

    fun fetchInformation() {

        val mProgressDialog = ProgressDialog(context)
        mProgressDialog.isIndeterminate = true
        mProgressDialog.setMessage("Laden...")
        mProgressDialog.show()

        val disposable: Disposable =
            ProjectActivity.mService.getPopularProjects(ProjectActivity.preConfig.getPlatform())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Consumer<ProjectsResponse> {
                    override fun accept(t: ProjectsResponse?) {
                        if (mProgressDialog.isShowing())
                            mProgressDialog.dismiss();
                        if (t!!.error) {
                            if (t.projects.size == 0) {
                                empty_recycler_text_pop.visibility = View.VISIBLE
                                recycler_pop.visibility = View.GONE

                            } else {
                                recycler_pop.visibility = View.VISIBLE
                                empty_recycler_text_pop.visibility = View.GONE
                                recycler_pop.adapter =
                                    ProjectPopularAdapter(context, t.projects)
                            }

                        } else {
                            ProjectActivity.preConfig.displayToast(t.toString())
                        }
                    }
                }, object : Consumer<Throwable> {
                    override fun accept(t: Throwable?) {
                        if (mProgressDialog.isShowing())
                            mProgressDialog.dismiss();
                        ProjectActivity.preConfig.displayToast(t!!.message.toString())
                    }

                })
        compositeDisposable.add(disposable)

        val disposable1: Disposable =
            ProjectActivity.mService.getRecentProjects(ProjectActivity.preConfig.getPlatform())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Consumer<ProjectsResponse> {
                    override fun accept(t: ProjectsResponse?) {
                        if (mProgressDialog.isShowing())
                            mProgressDialog.dismiss();
                        if (t!!.error) {
                            if (t.projects.size == 0) {
                                empty_recycler_text_rec.visibility = View.VISIBLE
                                recycler_recent.visibility = View.GONE

                            } else {
                                recycler_recent.visibility = View.VISIBLE
                                empty_recycler_text_rec.visibility = View.GONE
                                recycler_recent.adapter =
                                    ProjectPopularAdapter(context, t.projects)
                            }
                        } else {
                            ProjectActivity.preConfig.displayToast(t.toString())
                        }
                    }
                }, object : Consumer<Throwable> {
                    override fun accept(t: Throwable?) {
                        if (mProgressDialog.isShowing())
                            mProgressDialog.dismiss();
                        ProjectActivity.preConfig.displayToast(t!!.message.toString())
                    }
                })
        compositeDisposable.add(disposable1)
    }

    override fun onResume() {
        super.onResume()
        fetchInformation()
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }

}
