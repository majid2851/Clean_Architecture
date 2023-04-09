package com.majid2851.clean_architecture.framework.datasource.prefrences

class PrefrencesKeys
{
    companion object{

        // Shared Preference Files:
        const val NOTE_PREFERENCES: String = "com.majid2851.clean_architecture.notes"

        // Shared Preference Keys
        val NOTE_FILTER: String = "${NOTE_PREFERENCES}.NOTE_FILTER"
        val NOTE_ORDER: String = "${NOTE_PREFERENCES}.NOTE_ORDER"

    }



}