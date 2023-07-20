/// A sensor sample from a attitude motion sensor as quaternion.
///
/// Measures the attitude of the phone with respect to the Earth's magnatic field,
class AttitudeQuaternionEvent {
  /// Constructs a new instance with the given [w], [x], [y]and [z] values.
  ///
  /// See [AttitudeQuaternionEvent] for more information.
  AttitudeQuaternionEvent(this.w, this.x, this.y, this.z);

  final double w, x, y, z;

  @override
  String toString() => '[AttitudeQuaternionEvent (w: $w, x: $x, y: $y, z: $z)]';
}
