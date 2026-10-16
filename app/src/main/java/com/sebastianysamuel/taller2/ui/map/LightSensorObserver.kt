package com.sebastianysamuel.taller2.ui.map

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager

class LightSensorObserver(context: Context) {
    private val sensorManager = context.applicationContext
        .getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
    private var listener: SensorEventListener? = null

    fun start(onLuxChanged: (Float) -> Unit): Boolean {
        if (listener != null) return lightSensor != null
        val sensor = lightSensor ?: return false

        val sensorListener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                event.values.firstOrNull()?.let(onLuxChanged)
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit
        }

        listener = sensorListener
        return sensorManager.registerListener(
            sensorListener,
            sensor,
            SensorManager.SENSOR_DELAY_NORMAL,
        )
    }

    fun stop() {
        listener?.let(sensorManager::unregisterListener)
        listener = null
    }
}
