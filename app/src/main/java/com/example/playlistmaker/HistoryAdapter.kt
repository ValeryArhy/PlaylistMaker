import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.Track
import com.example.playlistmaker.TrackAdapter

class HistoryAdapter : RecyclerView.Adapter<TrackAdapter.TrackViewHolder>() {

    private val history = mutableListOf<Track>()
    private var listener: ((Track) -> Unit)? = null

    fun setOnItemClickListener(listener: (Track) -> Unit) {
        this.listener = listener
    }

    fun setTracks(tracks: List<Track>) {
        history.clear()
        history.addAll(tracks)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackAdapter.TrackViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.track_item, parent, false)
        return TrackAdapter.TrackViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrackAdapter.TrackViewHolder, position: Int) {
        val track = history[position]
        holder.bind(track)
        holder.itemView.setOnClickListener {
            listener?.invoke(track)

        }
    }

    override fun getItemCount(): Int = history.size
}