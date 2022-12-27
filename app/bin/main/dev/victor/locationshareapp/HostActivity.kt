package dev.victor.locationshareapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity


class HostActivity : AppCompatActivity() {


    private lateinit var hostInput: EditText
    private lateinit var portInput: EditText
    private lateinit var connectButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_host)

        hostInput = findViewById(R.id.host_input)
        portInput = findViewById(R.id.port_input)
        connectButton = findViewById(R.id.connect_button)

        // Connect to the server when the user clicks the "Connect" button
        connectButton.setOnClickListener {
            val host = hostInput.text.toString()
            val port = portInput.text.toString().toInt()
            val intent = Intent(this, ChatActivity::class.java)
            intent.putExtra("HOST", "150.230.140.227")
            intent.putExtra("PORT", 5555)
            startActivity(intent)
        }
        }
    }

