package com.thevoxelbox.voxelsniper.util.math;

public final class MathHelper {

    private MathHelper() {
        throw new UnsupportedOperationException("Cannot create an instance of this class");
    }

    public static double circleArea(int radius) {
        return Math.PI * square(radius);
    }

    public static double sphereVolume(int radius) {
        return 4.0 / 3.0 * Math.PI * cube(radius);
    }

    public static int square(int number) {
        return number * number;
    }

    public static double square(double number) {
        return number * number;
    }

    public static int cube(int number) {
        return number * number * number;
    }

    public static double cube(double number) {
        return number * number * number;
    }

}
