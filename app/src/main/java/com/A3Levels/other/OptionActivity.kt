package com.A3Levels.other

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.media.AudioManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SeekBar
import com.A3Levels.HomeActivity
import com.A3Levels.R
import com.A3Levels.auth.LoginEmailActivity
import com.A3Levels.databinding.ActivityOptionBinding

class OptionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOptionBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private lateinit var originalPreferences: MutableMap<String, *>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOptionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize shared preferences
        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()
        originalPreferences = sharedPreferences.all //save the original preferences.

        // Set up sound settings
        val soundSwitch = binding.soundEnableCheckbox
        soundSwitch.isChecked = sharedPreferences.getBoolean("sound_enabled", true)
        soundSwitch.setOnCheckedChangeListener { _, isChecked ->
            editor.putBoolean("sound_enabled", isChecked)
            //editor.apply()
        }

        // Set up volume settings
        val volumeSeekBar = binding.soundVolumeSeekbar
        val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        volumeSeekBar.max = maxVolume
        val currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
        volumeSeekBar.progress = currentVolume
        volumeSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0)
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // Set up graphics settings
        val graphicsSpinner = binding.graphicsQualitySpinner
        val graphicsOptions = arrayOf("Low", "Medium", "High")
        val graphicsAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, graphicsOptions)
        graphicsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        graphicsSpinner.adapter = graphicsAdapter
        val graphicsLevel = sharedPreferences.getInt("graphics_level", 0)
        graphicsSpinner.setSelection(graphicsLevel)
        graphicsSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                editor.putInt("graphics_level", position)
                //editor.apply()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // Set up language settings
        val languageSpinner = binding.languageSpinner
        val languageOptions = arrayOf("English", "Italiano", "French")
        val languageAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, languageOptions)
        languageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        languageSpinner.adapter = languageAdapter
        val languageSelection = sharedPreferences.getInt("language_selection", 0)
        languageSpinner.setSelection(languageSelection)
        languageSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                editor.putInt("language_selection", position)
                //editor.apply()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // Set up notification settings
        val notificationSwitch = binding.notificationEnableCheckbox
        notificationSwitch.isChecked = sharedPreferences.getBoolean("notification_enabled", true)
        notificationSwitch.setOnCheckedChangeListener { _, isChecked ->
            editor.putBoolean("notification_enabled", isChecked)
            //editor.apply()
        }

        //Handling Save and Cancel option. want to put restore to default? . easy u know.
        binding.buttonExitNoSave.setOnClickListener{
            editor.clear()
            originalPreferences.forEach{
                when(it.value){
                    is Boolean -> editor.putBoolean(it.key,it.value as Boolean)
                    is Int -> editor.putInt(it.key, it.value as Int)
                    is Float -> editor.putFloat(it.key,it.value as Float)
                    is String -> editor.putString(it.key, it.value as String)
                }
            }
            //editor.apply()
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }
        binding.buttonSave.setOnClickListener{
            save()
        }
    }

    private fun save(){
        editor.apply()
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
    }
}