package com.example.id3

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox // Import CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONObject
import java.io.*
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity() {

    private lateinit var userIdEditText: EditText
    private lateinit var rememberCheckBox: CheckBox // Define rememberCheckBox

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_main)

        val btnConnect = findViewById<Button>(R.id.btn)
        userIdEditText = findViewById(R.id.user)
        val passwordEditText = findViewById<EditText>(R.id.pass)
        rememberCheckBox = findViewById(R.id.remember_checkbox) // Initialize rememberCheckBox

        btnConnect.setOnClickListener {
            val userId = userIdEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            val rememberChecked = rememberCheckBox.isChecked // Get the state of the checkbox

            if (userId.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter user ID and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val networkTask = NetworkTask()
            networkTask.execute(userId, password, rememberChecked.toString()) // Pass rememberChecked as a parameter
        }
    }

    inner class NetworkTask : AsyncTask<String, Void, String>() {

        override fun doInBackground(vararg params: String?): String {
            val userId = params[0]
            val password = params[1]
            val rememberChecked = params[2] // Retrieve rememberChecked from params

            val urlString = "http://3.109.34.34:8080/login"
            val jsonObject = JSONObject()
            jsonObject.put("user_id", userId)
            jsonObject.put("password", password)
            jsonObject.put("remember", rememberChecked) // Send rememberChecked to server

            return try {
                val url = URL(urlString)
                val urlConnection = url.openConnection() as HttpURLConnection
                urlConnection.requestMethod = "POST"
                urlConnection.setRequestProperty("Content-Type", "application/json;charset=UTF-8")
                urlConnection.doInput = true
                urlConnection.doOutput = true

                val outputStream = urlConnection.outputStream
                val writer = BufferedWriter(OutputStreamWriter(outputStream, "UTF-8"))
                writer.write(jsonObject.toString())
                writer.flush()
                writer.close()
                outputStream.close()

                val responseCode = urlConnection.responseCode

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val inputStream = urlConnection.inputStream
                    val reader = BufferedReader(InputStreamReader(inputStream))
                    val response = StringBuilder()
                    var line: String?
                    while (reader.readLine().also { line = it } != null) {
                        response.append(line)
                    }
                    reader.close()
                    inputStream.close()
                    response.toString()
                } else {
                    "Error: $responseCode"
                }
            } catch (e: Exception) {
                "Error: ${e.message}"
            }
        }

        override fun onPostExecute(result: String) {
            if (result == "Login successful") {
                val userId = userIdEditText.text.toString().trim()
                val intent = Intent(this@MainActivity, SecondActivity::class.java)
                intent.putExtra("userId", userId) // Pass userId to SecondActivity
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this@MainActivity, result, Toast.LENGTH_SHORT).show()
            }
        }
    }
}
