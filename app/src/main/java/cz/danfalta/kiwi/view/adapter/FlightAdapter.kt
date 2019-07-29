package cz.danfalta.kiwi.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import cz.danfalta.kiwi.R
import cz.danfalta.kiwi.service.model.Flight
import cz.danfalta.kiwi.util.DateConverter
import java.util.*

open class FlightAdapter : RecyclerView.Adapter<FlightAdapter.FlightViewHolder>() {

    var data: List<Flight> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FlightViewHolder {
        val flightView = LayoutInflater.from(parent.context).inflate(R.layout.item_flight, parent, false)
        return FlightViewHolder(flightView)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: FlightViewHolder, position: Int) {
        holder.bind(data[position])
    }

    class FlightViewHolder(flightView: View) : RecyclerView.ViewHolder(flightView) {

        //TODO API of images not working as described - using random images
        private val images = mutableListOf(
            "https://cdn.pixabay.com/photo/2017/03/29/15/18/tianjin-2185510_1280.jpg",
            "https://cdn.pixabay.com/photo/2017/01/18/16/46/hong-kong-1990268_1280.jpg",
            "https://cdn.pixabay.com/photo/2015/03/26/09/48/chicago-690364_1280.jpg",
            "https://cdn.pixabay.com/photo/2017/12/10/17/40/prague-3010407_1280.jpg"
        )
        private val textViewFrom: TextView = flightView.findViewById(R.id.textViewFrom)
        private val textViewTo: TextView = flightView.findViewById(R.id.textViewTo)
        private val textViewDepartureDate: TextView = flightView.findViewById(R.id.textViewDepartureDate)
        private val image: ImageView = flightView.findViewById(R.id.image)

        fun bind(model: Flight) {
            textViewFrom.text = model.countryFrom.name
            textViewTo.text = model.countryTo.name
            textViewDepartureDate.text = DateConverter.toNiceDateString(Date(model.dTime * 1000))
            Glide
                .with(image)
                .load(images.shuffled().first())
                .centerCrop()
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(image)
        }
    }
}