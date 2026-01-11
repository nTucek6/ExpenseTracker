package com.example.expensetracker.ui.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.expensetracker.R

class DashboardFragment : Fragment(R.layout.fragment_dashboard)
{
    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val topText = view.findViewById<TextView>(R.id.topText)
        topText.text = topText.text.toString() + ", Nikola"
    }

}