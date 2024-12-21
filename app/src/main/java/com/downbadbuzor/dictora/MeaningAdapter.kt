package com.downbadbuzor.dictora

import android.content.Context
import android.content.Intent
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.downbadbuzor.dictora.databinding.MeaningRecyclerRowBinding

class MeaningAdapter(val context: Context) :RecyclerView.Adapter<MeaningAdapter.MeaningViewHolder>() {

    private var meanings: List<Meaning> = emptyList()

    fun setMeanings(meanings: List<Meaning>) {
        this.meanings = meanings
    }


    inner class MeaningViewHolder(private val binding: MeaningRecyclerRowBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(meaning: Meaning) {

            binding.partOfSpeech.text = meaning.partOfSpeech
            binding.definitions.text = meaning.definitions.joinToString("\n\n") {
                val index = meaning.definitions.indexOf(it) + 1
                 index.toString()+". "+ it.definition
            }




            if (meaning.synonyms.isNotEmpty()) {
                binding.synonymsTitle.visibility = View.VISIBLE
                binding.synonyms.visibility = View.VISIBLE

                val synonymsText = meaning.synonyms.joinToString(", ")
                val spannableString = SpannableString(synonymsText)

                meaning.synonyms.forEachIndexed { _, synonym ->
                    val startIndex = synonymsText.indexOf(synonym, ignoreCase = true)
                    val endIndex = startIndex + synonym.length

                    val clickableSpan = object : ClickableSpan() {
                        override fun onClick(widget: View) {
                            val intent = Intent(context, SearchResults::class.java) // Use context here
                            intent.putExtra("word", synonym)
                            context.startActivity(intent) // Start activity using context
                        }
                    }

                    spannableString.setSpan(clickableSpan, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
                binding.synonyms.text = spannableString
                binding.synonyms.movementMethod = LinkMovementMethod.getInstance()
            }
            else{
                binding.synonymsTitle.visibility = View.GONE
                binding.synonyms.visibility = View.GONE
            }



            if (meaning.antonyms.isNotEmpty()) {
                binding.antonymsTitle.visibility = View.VISIBLE
                binding.antonyms.visibility = View.VISIBLE

                val antonymsText = meaning.antonyms.joinToString(", ")
                val spannableString = SpannableString(antonymsText)

                meaning.antonyms.forEachIndexed { _, antonym ->
                    val startIndex = antonymsText.indexOf(antonym, ignoreCase = true)
                    val endIndex = startIndex + antonym.length

                    val clickableSpan = object : ClickableSpan() {
                        override fun onClick(widget: View) {
                            val intent = Intent(context, SearchResults::class.java) // Use context here
                            intent.putExtra("word", antonym)
                            context.startActivity(intent) // Start activity using context
                        }
                    }

                    spannableString.setSpan(clickableSpan, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
                binding.antonyms.text = spannableString
                binding.antonyms.movementMethod = LinkMovementMethod.getInstance()
            }
            else{
                binding.antonymsTitle.visibility = View.GONE
                binding.antonyms.visibility = View.GONE
            }
        }
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MeaningViewHolder {
        val binding = MeaningRecyclerRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MeaningViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return meanings.size
    }

    override fun onBindViewHolder(holder: MeaningViewHolder, position: Int) {
        holder.bind(meanings[position])
    }
}