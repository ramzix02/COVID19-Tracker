package com.ITIMADIsmailia.COVID19

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import java.lang.Exception

class SplashScreen : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splashscreen)

        val background = object: Thread() {
            override fun run() {
                super.run()
                try {
                    Thread.sleep(3000)
                    val intent = Intent(baseContext,MainActivity::class.java)
                    startActivity(intent)
                }catch (e: Exception){
                    print(e.printStackTrace())
                }
            }
        }.start()
    }
}
