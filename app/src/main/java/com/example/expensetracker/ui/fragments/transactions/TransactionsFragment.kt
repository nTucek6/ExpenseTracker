package com.example.expensetracker.ui.fragments.transactions

import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import com.example.expensetracker.R
import com.example.expensetracker.ui.adapters.ViewPagerAdapter
import com.example.expensetracker.ui.fragments.ExpensesFragment
import com.example.expensetracker.ui.fragments.SummaryFragment
import com.google.android.material.tabs.TabLayout


class TransactionsFragment : Fragment(R.layout.fragment_transactions) {

    private lateinit var viewpagerAdapter: ViewPagerAdapter

    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager2: ViewPager2


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return super.onCreateView(inflater, container, savedInstanceState)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tabLayout = view.findViewById(R.id.tabLayout)
        viewPager2 = view.findViewById(R.id.viewPager2)
        setUpViewPager()

        viewPager2.adapter = viewpagerAdapter

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(p0: TabLayout.Tab?) {
                if (p0 != null) {
                    viewPager2.currentItem = p0.position
                }
            }
            override fun onTabUnselected(p0: TabLayout.Tab?) {
            }
            override fun onTabReselected(p0: TabLayout.Tab?) {
            }
        })

        viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                tabLayout.selectTab(tabLayout.getTabAt(position))
            }
        })

    }

    private fun setUpViewPager() {

        val fragments = listOf(ExpensesFragment(), SummaryFragment())
        viewpagerAdapter = ViewPagerAdapter(
            fragments,
            childFragmentManager,
            lifecycle
        )
    }

}