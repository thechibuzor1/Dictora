package com.downbadbuzor.dictora

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.recyclerview.widget.LinearLayoutManager
import com.downbadbuzor.dictora.databinding.ActivitySearchResultsBinding
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchResults : AppCompatActivity() {

    private lateinit var binding: ActivitySearchResultsBinding
    private lateinit var adapter: MeaningAdapter
    private lateinit var exoPlayer: ExoPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySearchResultsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        exoPlayer = ExoPlayer.Builder(this).build() // Create ExoPlayer instance
        exoPlayer.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                super.onIsPlayingChanged(isPlaying)
                if (isPlaying) {
                    binding.lottieAnimation.visibility = View.VISIBLE
                    binding.speaker.visibility = View.GONE
                } else {
                    binding.lottieAnimation.visibility = View.GONE
                    binding.speaker.visibility = View.VISIBLE
                }
            }
        })


        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Search"

        val word = intent.getStringExtra("word")
        getMeaning(word!!)


        adapter = MeaningAdapter(this)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        binding.content.setOnScrollChangeListener { _, _, scrollY, _, _ ->
            val scrollThreshold = 250
            if (scrollY > scrollThreshold && supportActionBar?.title == "Search") {
                supportActionBar?.title = word
            } else if (scrollY < scrollThreshold && supportActionBar?.title != "Search") {
                supportActionBar?.title = "Search"
            }

        }
        binding.speaker.setOnClickListener {
            exoPlayer.seekToDefaultPosition(0) // Reset playback position
            exoPlayer.play() // Start playback
        }


    }

    @Deprecated("Deprecated in Java")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed() // Navigate back
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun getMeaning(word: String) {
        setInProgress(true)
        // Use lifecycle-aware coroutine scope
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val res = RetrofitInstance.dictionaryApi.getMeaning(word)
                if (res.body() === null) throw Exception()
                // Update UI on the main thread
                withContext(Dispatchers.Main) {
                    setInProgress(false)
                    res.body()?.first()?.let { setUi(it) }
                }
            } catch (e: Exception) {
                // Handle specific exceptions
                withContext(Dispatchers.Main) {
                    setInProgress(false)
                    binding.content.visibility = View.GONE
                    binding.noResults.visibility = View.VISIBLE
                }
            }
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        exoPlayer.release()
    }

    private fun setInProgress(inProgress: Boolean){
        if(inProgress){
            binding.progressBar.visibility = View.VISIBLE
            binding.content.visibility = View.GONE
        }else{
            binding.progressBar.visibility = View.GONE
            binding.content.visibility = View.VISIBLE

        }

    }

    private fun setUi(response: SearchModel){
        binding.word.text = response.word

        binding.phonetic.text = response.phonetics
            .filterNotNull()
            .filterNot { it.text.isNullOrEmpty() } // Filter out empty or null text
            .joinToString(", ") { it.text }
        adapter.setMeanings(response.meanings)


        //audio playback
        if (response.phonetics.isNotEmpty()){

            val mediaItems = response.phonetics // Assuming 'response' is accessible here
                .filter { it.audio.isNotEmpty() } // Filter for valid audio URLs
                .map { MediaItem.fromUri(it.audio) } // Create MediaItems

            if (mediaItems.isNotEmpty()) {
                binding.speaker.visibility = View.VISIBLE
                exoPlayer.setMediaItems(mediaItems) // Set the playlist to ExoPlayer
                exoPlayer.prepare() // Prepare ExoPlayer
            }
        }


    }

}