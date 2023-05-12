package com.A3Levels.game

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.A3Levels.R
import com.A3Levels.databinding.ActivityTestLevelBinding
import java.util.Timer

class TestLevelActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTestLevelBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTestLevelBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //setContentView(R.layout.activity_test_level)
        setup_layout()
        setup_start_layout()
        /*
        var your_time = Timer()

        while(true){
            print(your_time)
        }
        */

    }

    private fun setup_layout(){
        // Setup UI. mettere tutto a gone tranne How To Play. + Start del countdown
        binding.layoutTutorial.visibility = View.VISIBLE
        binding.layoutEnd.visibility = View.GONE
        binding.layoutGame.visibility = View.INVISIBLE
    }


    private fun setup_start_layout(){
        // Setup UI. Mettere tutto a gone tranne La navBar.
        binding.layoutTutorial.visibility = View.GONE
        binding.layoutEnd.visibility = View.GONE
        binding.layoutGame.visibility = View.VISIBLE
    }

    private fun setup_end_layout(){
        // Setup UI. Mettere tutto a gone tranne la end page. (Your Time + Waiting)
        binding.layoutTutorial.visibility = View.GONE
        binding.layoutEnd.visibility = View.VISIBLE
        binding.layoutGame.visibility = View.GONE
    }


}