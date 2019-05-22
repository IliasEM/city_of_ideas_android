package treecompany.cityofideas.fragments.project

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter

class SectionsProjectPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

    private val mFragmentList : ArrayList<Fragment> = ArrayList()

    override fun getItem(p0: Int): Fragment {
        return mFragmentList[p0]
    }

    override fun getCount(): Int {
        return mFragmentList.size
    }

    fun addFragment(fragment : Fragment){
        mFragmentList.add(fragment)
    }

}