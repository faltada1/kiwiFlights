package cz.danfalta.kiwi.view.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import cz.danfalta.kiwi.R
import cz.danfalta.kiwi.service.model.Flight
import cz.danfalta.kiwi.service.model.InterestingFlights
import cz.danfalta.kiwi.service.util.DataWrapper
import cz.danfalta.kiwi.viewmodel.InterestingFlightsViewModel

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import okhttp3.Cache

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }
}
