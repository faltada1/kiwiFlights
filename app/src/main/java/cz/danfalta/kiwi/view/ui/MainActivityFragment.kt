package cz.danfalta.kiwi.view.ui

import android.content.Context
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import cz.danfalta.kiwi.R
import cz.danfalta.kiwi.service.model.Flight
import cz.danfalta.kiwi.service.util.DataWrapper
import cz.danfalta.kiwi.view.adapter.FlightAdapter
import cz.danfalta.kiwi.viewmodel.InterestingFlightsViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.android.synthetic.main.fragment_main.view.*

/**
 * A placeholder fragment containing a simple view.
 */
class MainActivityFragment : Fragment() {

    private lateinit var viewModel: InterestingFlightsViewModel

    private val adapter: FlightAdapter = FlightAdapter()

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        observeViewModel()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_main, container, false);
        root.recyclerView.adapter = this.adapter
        return root
    }

    override fun onResume() {
        super.onResume()
        loadData()
    }

    fun loadData() {
        println("Load data")
        viewModel.loadData()
    }

    private fun observeViewModel() {
        //Initialize viewModel
        viewModel = ViewModelProviders.of(this).get(InterestingFlightsViewModel::class.java)

        //Observe on viewModel property changes
        viewModel.loadingObservable.observe(this,
            Observer<Boolean> {
                loading.visibility = if (it) View.VISIBLE else View.GONE
            })
        viewModel.flightListObservable
            .observe(
                this,
                Observer<DataWrapper<List<Flight>>> {
                    if (it.hasData()) {
                        //Data success
                        it.data?.let { list ->
                            adapter.data = list
                            adapter.notifyDataSetChanged()
                        }
                    } else {
                        //Error handling
                        Toast.makeText(context, R.string.fetch_error, Toast.LENGTH_LONG).show()
                    }
                })
    }
}
