package dev.fluttercommunity.plus.sensors

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.common.EventChannel.EventSink

internal class AttitudeStreamHandlerImpl(
    private val sensorManager: SensorManager
) : EventChannel.StreamHandler {
    private var sensorEventListener: SensorEventListener? = null

    private var gravitySensor: Sensor? = null
    private var magneticFieldSensor: Sensor? = null


    private val accelerometerReading = FloatArray(3)
    private val magnetometerReading = FloatArray(3)

    private val rotationMatrix = FloatArray(9)
    private val orientationAngles = FloatArray(3)

    override fun onListen(arguments: Any?, events: EventSink) {
        sensorEventListener = createSensorEventListener(events)

        gravitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        magneticFieldSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)

        sensorManager.registerListener(
            sensorEventListener,
            gravitySensor,
            SensorManager.SENSOR_DELAY_NORMAL,
        )
        sensorManager.registerListener(
            sensorEventListener,
            magneticFieldSensor,
            SensorManager.SENSOR_DELAY_NORMAL,
        )
    }

    override fun onCancel(arguments: Any?) {
        sensorManager.unregisterListener(sensorEventListener, gravitySensor)
        sensorManager.unregisterListener(sensorEventListener, magneticFieldSensor)
    }

    private fun createSensorEventListener(events: EventSink): SensorEventListener {
        return object : SensorEventListener {
            override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}

            override fun onSensorChanged(event: SensorEvent) {

                if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
                    System.arraycopy(event.values, 0, accelerometerReading, 0, accelerometerReading.size)
                } else if (event.sensor.type == Sensor.TYPE_MAGNETIC_FIELD) {
                    System.arraycopy(event.values, 0, magnetometerReading, 0, magnetometerReading.size)
                }

                SensorManager.getRotationMatrix(
                    rotationMatrix,
                    null,
                    accelerometerReading,
                    magnetometerReading
                )

                SensorManager.getOrientation(rotationMatrix, orientationAngles)

                val sensorValues = DoubleArray(3)
                orientationAngles.forEachIndexed { index, value ->
                    sensorValues[index] = value.toDouble()
                }
                events.success(sensorValues)
            }
        }
    }
}
