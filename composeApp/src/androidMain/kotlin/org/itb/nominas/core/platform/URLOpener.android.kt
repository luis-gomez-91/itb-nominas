package org.itb.nominas.core.platform

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast

class URLOpenerAndroid(private val context: Context) : URLOpener {
    override fun openURL(url: String) {
        try {
//            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Log.e("urlOpener", "Error: ${e.message}")
            Toast.makeText(context, "Error al intentar abrir la URL: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}
