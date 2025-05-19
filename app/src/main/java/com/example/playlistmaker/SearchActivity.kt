package com.example.playlistmaker

import HistoryAdapter
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivity : AppCompatActivity() {

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://itunes.apple.com")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val itunesService = retrofit.create(ItunesAPI::class.java)

    private var searchText: String = ""

    private lateinit var adapter: TrackAdapter
    private lateinit var historyAdapter: HistoryAdapter
    private lateinit var queryInput: EditText
    private lateinit var clearButton: ImageView
    private lateinit var updateButton: Button
    private lateinit var placeholderMessage: TextView
    private lateinit var placeholderImage: ImageView
    private lateinit var recyclerView: RecyclerView
    private lateinit var historyRecyclerView: RecyclerView
    private lateinit var searchHistory: SearchHistory
    private lateinit var clearHistoryButton: Button
    private lateinit var historyGroup: View

    private val tracks = ArrayList<Track>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        queryInput = findViewById(R.id.query_input)
        clearButton = findViewById(R.id.icon_clear)
        updateButton = findViewById(R.id.update_button)
        placeholderMessage = findViewById(R.id.placeholder_message)
        placeholderImage = findViewById(R.id.placeholder_image)
        recyclerView = findViewById(R.id.recyclerView)
        historyRecyclerView = findViewById(R.id.historyRecyclerView)
        historyGroup = findViewById(R.id.searchHistoryGroup)
        clearHistoryButton = findViewById(R.id.clear_history)

        searchHistory = SearchHistory(getSharedPreferences("search_prefs", MODE_PRIVATE))

        adapter = TrackAdapter()
        historyAdapter = HistoryAdapter()

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        historyRecyclerView.layoutManager = LinearLayoutManager(this)
        historyRecyclerView.adapter = historyAdapter

        val backSearch = findViewById<MaterialToolbar>(R.id.toolbar)
        backSearch.setNavigationOnClickListener { finish() }

        clearButton.setOnClickListener {
            queryInput.setText("")
            hideKeyboard(queryInput)
            tracks.clear()
            adapter.setTracks(emptyList())
            hideMessage()
        }

        clearHistoryButton.setOnClickListener {
            searchHistory.clearHistory()
            showResults()
        }

        queryInput.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && queryInput.text.isNullOrEmpty()) {
                showHistory()
            }
        }

        queryInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchText = s.toString()
                clearButton.visibility = if (s.isNullOrEmpty()) View.GONE else View.VISIBLE

                if (s.isNullOrEmpty() && queryInput.hasFocus()) {
                    showHistory()
                } else {
                    showResults()
                    }
            }

            override fun afterTextChanged(s: Editable?) {
                if (s.isNullOrEmpty()) {
                    adapter.setTracks(emptyList())
                    showHistory()
                    hideMessage()
                }
            }
        })

        queryInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val query = queryInput.text.toString().trim()
                if (query.isNotEmpty()) {
                    searchMusic(query)
                    hideKeyboard(queryInput)
                }
                true
            } else {
                false
            }
        }

        updateButton.setOnClickListener {
            if (searchText.isNotEmpty()) {
                searchMusic(searchText)
            }
        }


        adapter.setOnItemClickListener { track ->
            searchHistory.saveTrack(track)
        }

    }

    private fun searchMusic(query: String) {
        itunesService.searchMusic(query)
            .enqueue(object : Callback<SearchResponse> {
                override fun onResponse(call: Call<SearchResponse>, response: Response<SearchResponse>) {
                    if (response.isSuccessful) {
                        val results = response.body()?.results.orEmpty()
                        tracks.clear()
                        tracks.addAll(results)
                        adapter.setTracks(tracks)

                        if (tracks.isEmpty()) {
                            showMessage(R.drawable.search_null, getString(R.string.nothing_found), false)
                        } else {
                            hideMessage()
                            showResults()
                        }
                    } else {
                        showMessage(R.drawable.error_internet, getString(R.string.no_connection), true)
                    }
                }

                override fun onFailure(call: Call<SearchResponse>, t: Throwable) {
                    showMessage(R.drawable.error_internet, getString(R.string.no_connection), true)
                }
            })
    }

    private fun showHistory() {
        val history = searchHistory.getHistory()
        if (history.isNotEmpty()) {
            historyAdapter.setTracks(history)
            historyGroup.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        } else {
            historyGroup.visibility = View.GONE
            recyclerView.visibility = View.GONE
        }
    }

    private fun showResults() {
        historyGroup.visibility = View.GONE
        recyclerView.visibility = View.VISIBLE
    }

    private fun showMessage(imageResId: Int, message: String, showUpdateButton: Boolean) {
        placeholderImage.setImageResource(imageResId)
        placeholderMessage.text = message
        updateButton.visibility = if (showUpdateButton) View.VISIBLE else View.GONE
        findViewById<View>(R.id.placeholder_container).visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
        historyRecyclerView.visibility = View.GONE
    }

    private fun hideMessage() {
        findViewById<View>(R.id.placeholder_container).visibility = View.GONE
    }

    private fun hideKeyboard(view: View) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_TEXT_KEY, searchText)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        val restoredText = savedInstanceState.getString(SEARCH_TEXT_KEY, "")
        queryInput.setText(restoredText)
    }

    companion object {
        private const val SEARCH_TEXT_KEY = "SEARCH_TEXT"
    }
}
