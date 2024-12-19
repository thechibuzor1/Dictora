package com.downbadbuzor.dictora

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.downbadbuzor.dictora.databinding.ActivitySearchResultsBinding
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class SearchResults : AppCompatActivity() {

    private lateinit var binding: ActivitySearchResultsBinding
    private lateinit var adapter: MeaningAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySearchResultsBinding.inflate(layoutInflater)
        setContentView(binding.root)

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


    }

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
        GlobalScope.launch {
            try{
                val res =  RetrofitInstance.dictionaryApi.getMeaning(word)
                if(res.body() === null) throw Exception()
                runOnUiThread {
                    setInProgress(false)
                    res.body()?.first()?.let { setUi(it) }
                }
            } catch (e: Exception){
                runOnUiThread {
                    setInProgress(false)
                    binding.content.visibility = View.GONE
                    binding.noResults.visibility = View.VISIBLE
                }
            }
        }
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

    }

}