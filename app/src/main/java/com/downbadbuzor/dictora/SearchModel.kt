package com.downbadbuzor.dictora

data class SearchModel(
    val word: String,
    val phonetic: String,
    val phonetics: List<Phonetic>,
    val meanings: List<Meaning>,
    val sourceUrls: List<String>,
)

data class Phonetic(
    val text: String,
    val audio: String,
    val sourceUrl: String?,
)

data class Meaning(
    val partOfSpeech: String,
    val definitions: List<Definition>,
    val synonyms: List<String>,
    val antonyms: List<String>,
)

data class Definition(
    val definition: String,
    val synonyms: List<String>,
    val antonyms: List<String>,
    val example: String?,
)
