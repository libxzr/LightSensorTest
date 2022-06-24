package moe.xzr.lightsensortest

import android.app.Activity
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.*
import android.widget.SeekBar
import android.widget.TextView

class UDSensorTestActivity : Activity(), SeekBar.OnSeekBarChangeListener, SensorEventListener,
    ViewTreeObserver.OnDrawListener, GestureDetector.OnGestureListener {
    companion object {
        private val LIGHT_COLORS = listOf("#ffffff", "#ff0000", "#00ff00", "#0000ff")
    }

    private lateinit var sensorManager: SensorManager
    private lateinit var lightSensor: Sensor

    private lateinit var lightSource: View
    private lateinit var sizeChanger: SeekBar
    private lateinit var brightnessChanger: SeekBar
    private lateinit var posText: TextView
    private lateinit var lightText: TextView
    private lateinit var brightnessText: TextView

    private lateinit var gestureDetector: GestureDetector

    private var lightSourceColorIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_udsensortest)
        window.insetsController?.hide(WindowInsets.Type.navigationBars())
        updateBrightness(255f)

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)

        lightSource = findViewById(R.id.light_source)
        sizeChanger = findViewById(R.id.size_changer)
        brightnessChanger = findViewById(R.id.brightness_changer)
        posText = findViewById(R.id.pos_text)
        lightText = findViewById(R.id.light_text)
        brightnessText = findViewById(R.id.brightness_text)

        gestureDetector = GestureDetector(this, this)

        sizeChanger.setOnSeekBarChangeListener(this)
        brightnessChanger.setOnSeekBarChangeListener(this)
        lightSource.viewTreeObserver.addOnDrawListener(this)
        updateBrightnessText()
    }

    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (gestureDetector.onTouchEvent(event))
            return true
        var x = event?.x ?: 0f
        var y = event?.y ?: 0f
        x -= lightSource.width / 2 * lightSource.scaleX
        y -= lightSource.height / 2 * lightSource.scaleY
        lightSource.translationX = x
        lightSource.translationY = y
        return true
    }

    private fun updateBrightness(brightness: Float) {
        window.attributes.apply {
            screenBrightness = brightness / 255f
        }
        onWindowAttributesChanged(window.attributes)
    }

    private fun updateBrightnessText() {
        brightnessText.text = brightnessChanger.progress.toString()
    }

    private fun updatePosText() {
        posText.text =
            "X=${lightSource.translationX + lightSource.width / 2 * lightSource.scaleX} " +
                    "Y=${lightSource.translationY + lightSource.height / 2 * lightSource.scaleY} " +
                    "R=${lightSource.width / 2 * lightSource.scaleX}"
    }

    private fun progressToScale(progress: Int): Float {
        return progress.toFloat() / 100
    }

    private fun updateLightColor() {
        lightSourceColorIndex = ++lightSourceColorIndex % LIGHT_COLORS.size
        lightSource.setBackgroundColor(Color.parseColor(LIGHT_COLORS[lightSourceColorIndex]))
    }

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        when (seekBar) {
            sizeChanger -> {
                val scale = progressToScale(progress)
                lightSource.scaleX = scale
                lightSource.scaleY = scale
            }
            brightnessChanger -> {
                updateBrightness(progress.toFloat())
                updateBrightnessText()
            }
        }
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {

    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {

    }

    override fun onSensorChanged(event: SensorEvent?) {
        lightText.text = "${event?.values?.get(0)} lux"
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }

    override fun onDraw() {
        updatePosText()
    }

    override fun onDown(e: MotionEvent?): Boolean {
        return false
    }

    override fun onShowPress(e: MotionEvent?) {

    }

    override fun onSingleTapUp(e: MotionEvent?): Boolean {
        return false
    }

    override fun onScroll(
        e1: MotionEvent?,
        e2: MotionEvent?,
        distanceX: Float,
        distanceY: Float
    ): Boolean {
        return false
    }

    override fun onLongPress(e: MotionEvent?) {
        updateLightColor()
    }

    override fun onFling(
        e1: MotionEvent?,
        e2: MotionEvent?,
        velocityX: Float,
        velocityY: Float
    ): Boolean {
        return false
    }
}