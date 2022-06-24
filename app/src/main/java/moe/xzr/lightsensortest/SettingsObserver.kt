package moe.xzr.lightsensortest

import android.app.Activity
import android.content.res.Resources
import android.database.ContentObserver
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.widget.TextView
import java.lang.Exception

class SettingsObserver : Activity(), SensorEventListener {
    companion object {
        const val SCREEN_AUTO_BRIGHTNESS_ADJ = "screen_auto_brightness_adj"
    }

    private lateinit var sensorManager: SensorManager
    private lateinit var lightSensor: Sensor

    private lateinit var settingsText: TextView
    private lateinit var sensorText: TextView
    private lateinit var overlayText: TextView
    private val settingsContentObserver = SettingsContentObserver()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings_observer)

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)

        settingsText = findViewById(R.id.settings_text)
        sensorText = findViewById(R.id.sensor_text)
        overlayText = findViewById(R.id.overlay_text)
        updateSettingsText()
        updateOverlay()
    }

    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL)
        contentResolver.registerContentObserver(
            Settings.System.getUriFor(Settings.System.SCREEN_BRIGHTNESS),
            false,
            settingsContentObserver
        )
        contentResolver.registerContentObserver(
            Settings.System.getUriFor(SCREEN_AUTO_BRIGHTNESS_ADJ),
            false,
            settingsContentObserver
        )
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
        contentResolver.unregisterContentObserver(settingsContentObserver)
    }

    private fun updateOverlay() {
        val res = Resources.getSystem()
        val str = StringBuilder()
        val names = listOf(
            "config_autoBrightnessLevels",
            "config_autoBrightnessDisplayValuesNits",
            "config_screenBrightnessNits",
            "config_screenBrightnessBacklight"
        )
        for (name in names) {
            try {
                val typedArray = res.obtainTypedArray(
                    res.getIdentifier(
                        name,
                        "array",
                        "android"
                    )
                )
                str.appendLine("$name =")
                for (i in 0 until typedArray.length()) {
                    str.append("[$i] ").appendLine(typedArray.getFloat(i, -1f))
                }
                typedArray.recycle()
            } catch (e: Exception) {
                str.appendLine("Unable to get $name")
            }
        }
        overlayText.text = str.toString()
    }

    private fun updateSettingsText() {
        settingsText.text = "${Settings.System.SCREEN_BRIGHTNESS} = ${
            Settings.System.getInt(
                contentResolver,
                Settings.System.SCREEN_BRIGHTNESS,
                -1
            )
        }\n$SCREEN_AUTO_BRIGHTNESS_ADJ = ${
            Settings.System.getFloat(
                contentResolver,
                SCREEN_AUTO_BRIGHTNESS_ADJ,
                Float.NaN
            )
        }"
    }

    private inner class SettingsContentObserver :
        ContentObserver(Handler(Looper.getMainLooper())) {
        override fun onChange(selfChange: Boolean) {
            updateSettingsText()
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        sensorText.text = "${event?.values?.get(0)} lux"
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }
}