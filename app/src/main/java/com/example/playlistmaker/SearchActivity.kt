package com.example.playlistmaker

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivity : AppCompatActivity() {
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://itunes.apple.com")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val itunesService = retrofit.create(ItunesAPI::class.java)

    private var searchText: String = ""

    private val tracks = ArrayList<Track>()
    private lateinit var adapter: TrackAdapter

    private lateinit var queryInput: EditText
    private lateinit var clearButton: ImageView
    private lateinit var updateButton: Button
    private lateinit var placeholderMessage: TextView
    private lateinit var placeholderImage: ImageView
    private lateinit var recyclerView: RecyclerView

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_TEXT_KEY, searchText)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        val restoredText = savedInstanceState.getString(SEARCH_TEXT_KEY, "")
        val inputEditText = findViewById<EditText>(R.id.query_input)
        inputEditText.setText(restoredText)

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        queryInput = findViewById(R.id.query_input)
        clearButton = findViewById(R.id.icon_clear)
        updateButton = findViewById(R.id.update_button)
        placeholderMessage = findViewById(R.id.placeholder_message)
        placeholderImage = findViewById(R.id.placeholder_image)
        recyclerView = findViewById(R.id.recyclerView)

        adapter = TrackAdapter()
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        val backSearch = findViewById<MaterialToolbar>(R.id.toolbar)
        backSearch.setNavigationOnClickListener { finish() }

        clearButton.setOnClickListener {
            queryInput.setText("")
            hideKeyboard(queryInput)
            tracks.clear()
            adapter.setTracks(emptyList())
            hideMessage()
        }

        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchText = s.toString()

                clearButton.visibility = if (s.isNullOrEmpty()) {
                    View.GONE
                } else {
                    View.VISIBLE
                }
            }

            override fun afterTextChanged(s: Editable?) {
                if (s.isNullOrEmpty()) {
                    hideMessage()
                    tracks.clear()
                    adapter.setTracks(emptyList())
                }
            }

        }

        queryInput.addTextChangedListener(simpleTextWatcher)

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
    }

    private fun searchMusic(query: String) {
        itunesService.searchMusic(query)
            .enqueue(object : Callback<SearchResponse> {
                override fun onResponse(
                    call: Call<SearchResponse>,
                    response: Response<SearchResponse>
                ) {
                    if (response.isSuccessful) {
                        val results = response.body()?.results.orEmpty()
                        tracks.clear()
                        tracks.addAll(results)
                        adapter.setTracks(tracks)
                        if (tracks.isEmpty()) {

                            showMessage(
                                R.drawable.search_null,
                                getString(R.string.nothing_found),
                                false
                            )
                        } else {
                            hideMessage()
                        }
                    } else {

                        showMessage(
                            R.drawable.error_internet,
                            getString(R.string.no_connection),
                            true
                        )
                    }
                }

                override fun onFailure(call: Call<SearchResponse>, t: Throwable) {

                    showMessage(R.drawable.error_internet, getString(R.string.no_connection), true)
                }
            })
    }

    private fun showMessage(imageResId: Int, message: String, showUpdateButton: Boolean) {
        placeholderImage.setImageResource(imageResId)
        placeholderMessage.text = message
        updateButton.visibility = if (showUpdateButton) View.VISIBLE else View.GONE
        findViewById<View>(R.id.placeholder_container).visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
    }

    private fun hideMessage() {
        findViewById<View>(R.id.placeholder_container).visibility = View.GONE
        recyclerView.visibility = View.VISIBLE
    }

    private fun hideKeyboard(view: View) {
        (getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager)
            .hideSoftInputFromWindow(view.windowToken, 0)
    }

    companion object {
        private const val SEARCH_TEXT_KEY = "SEARCH_TEXT"
    }
}
