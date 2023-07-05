package dev.fluttercommunity.plus.sensors

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.common.EventChannel.EventSink
import kotlin.math.PI

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
            SensorManager.SENSOR_DELAY_FASTEST,
        )
        sensorManager.registerListener(
            sensorEventListener,
            magneticFieldSensor,
            SensorManager.SENSOR_DELAY_FASTEST,
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
                var alpha: Float = 0.97f

                if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
                    accelerometerReading[0] = alpha * accelerometerReading[0] + (1 - alpha) * event.values[0]
                    accelerometerReading[1] = alpha * accelerometerReading[1] + (1 - alpha) * event.values[1]
                    accelerometerReading[2] = alpha * accelerometerReading[2] + (1 - alpha) * event.values[2]
                } else if (event.sensor.type == Sensor.TYPE_MAGNETIC_FIELD) {
                    magnetometerReading[0] = alpha * magnetometerReading[0] + (1 - alpha) * event.values[0]
                    magnetometerReading[1] = alpha * magnetometerReading[1] + (1 - alpha) * event.values[1]
                    magnetometerReading[2] = alpha * magnetometerReading[2] + (1 - alpha) * event.values[2]
                }

                SensorManager.getRotationMatrix(
                    rotationMatrix,
                    null,
                    accelerometerReading,
                    magnetometerReading
                )

                SensorManager.getOrientation(rotationMatrix, orientationAngles)

                val sensorValues = DoubleArray(3)

                sensorValues[0] = orientationAngles[2].toDouble()
                sensorValues[1] = - orientationAngles[1].toDouble()
                sensorValues[2] = - orientationAngles[0].toDouble() - PI/2

                events.success(sensorValues)
            }
        }
    }
}
