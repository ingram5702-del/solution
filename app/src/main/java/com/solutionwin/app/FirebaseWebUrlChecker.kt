package com.solutionwin.app

import com.google.firebase.database.FirebaseDatabase

object FirebaseWebUrlChecker {
    private const val DATABASE_URL =
        "https://solution-a1c55-default-rtdb.europe-west1.firebasedatabase.app"
    private const val URL_NODE = "url"

    fun checkUrl(onUrlFound: (String) -> Unit) {
        try {
            FirebaseDatabase.getInstance(DATABASE_URL)
                .reference
                .child(URL_NODE)
                .get()
                .addOnSuccessListener { snapshot ->
                    val url = snapshot.getValue(String::class.java)?.trim().orEmpty()
                    if (WebUrlStore.isWebUrl(url)) onUrlFound(url)
                }
                .addOnFailureListener {
                    // Native UI remains active if the configuration cannot be loaded.
                }
        } catch (_: Throwable) {
            // Firebase is optional at runtime; native UI remains available.
        }
    }
}
