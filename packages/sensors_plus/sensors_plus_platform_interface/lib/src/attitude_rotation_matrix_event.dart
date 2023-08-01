/// A sensor sample from a attitude motion sensor as rotation matrix.
///
/// Measures the attitude of the phone with respect to the Earth's magnatic field,
class AttitudeRotationMatrixEvent {
  /// See [AttitudeRotationMatrixEvent] for more information.
  AttitudeRotationMatrixEvent(
      this.m11, this.m12, this.m13,
      this.m21, this.m22, this.m23,
      this.m31, this.m32, this.m33
      );

  final double m11, m12, m13, m21, m22, m23, m31, m32, m33;

  @override
  String toString() => '[AttitudeRotationMatrixEvent (m11: $m11, m12: $m12, m13: $m13, m21: $m21, m22: $m22, m23: $m23, m31: $m31, m32: $m32, m33: $m33)]';
}
