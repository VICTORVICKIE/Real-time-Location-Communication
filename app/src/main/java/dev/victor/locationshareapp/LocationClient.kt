package dev.victor.locationshareapp

import android.app.Activity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.InetSocketAddress
import java.nio.ByteBuffer
import java.nio.channels.AsynchronousSocketChannel
import java.nio.channels.CompletionHandler

class LocationClient private constructor(){
    companion object {
        // Create a singleton instance of the LocationClient class
        val instance: LocationClient by lazy { LocationClient() }
    }
    private var client: AsynchronousSocketChannel? = null

    suspend fun connect(host: String, port: Int) {
        client = withContext(Dispatchers.IO) {
            AsynchronousSocketChannel.open()
        }
        withContext(Dispatchers.IO) {
            client!!.connect(InetSocketAddress(host, port)).get()
        }
    }

    suspend fun read(activity: Activity, callback: (String) -> Unit) {
        withContext(Dispatchers.IO) {
            val buffer = ByteBuffer.allocate(1024)
            client!!.read(buffer, null, object : CompletionHandler<Int, Void?> {
                override fun completed(result: Int, attachment: Void?) {
                    if (result == -1) {
                        activity.runOnUiThread{callback("-1")}
                    }
                    buffer.flip()
                    val locData = String(buffer.array(), 0, buffer.limit())

                    buffer.clear()
                    client!!.read(buffer, null, this)
                    activity.runOnUiThread {
                        callback(locData)
                    }
                }

                override fun failed(exc: Throwable, attachment: Void?) {
                    activity.runOnUiThread{ callback("-1") }
                }
            })
        }
    }

    fun send(message: String) {
        val buffer = ByteBuffer.wrap(message.toByteArray(Charsets.UTF_8))
        client!!.write(buffer, null, object : CompletionHandler<Int, Void?> {
            override fun completed(result: Int, attachment: Void?) {
                // Data was successfully written to the server
               //  callback(1)
            }

            override fun failed(exc: Throwable, attachment: Void?) {
                // An error occurred while writing the data to the server
                // callback(-1)
            }
        })
    }
}