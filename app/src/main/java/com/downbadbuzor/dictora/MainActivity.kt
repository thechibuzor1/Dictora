package com.downbadbuzor.dictora

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.SearchView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.downbadbuzor.dictora.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        enableEdgeToEdge()

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.searchButton.setOnClickListener {
            val word = binding.searchInput.query.toString()
            if(word.isNotEmpty()){
                val intent = Intent(this, SearchResults::class.java)
                intent.putExtra("word", word)
                startActivity(intent)
            }

        }

        binding.searchInput.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                val word = binding.searchInput.query.toString()
                if (word.isNotEmpty()) {
                    val intent = Intent(this@MainActivity, SearchResults::class.java)
                    intent.putExtra("word", word)
                    startActivity(intent)
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Update button visibility
                binding.searchButton.visibility = if (newText.isNullOrEmpty()) View.GONE else View.VISIBLE
                return false
            }
        })

    }

}