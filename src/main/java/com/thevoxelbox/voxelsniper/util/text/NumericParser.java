package com.thevoxelbox.voxelsniper.util.text;

import org.jetbrains.annotations.Nullable;

public final class NumericParser {

    private NumericParser() {
        throw new UnsupportedOperationException("Cannot create an instance of this class");
    }

    @Nullable
    public static Integer parseInteger(String integerString) {
        try {
            return Integer.parseInt(integerString);
        } catch (NumberFormatException exception) {
            return null;
        }
    }

    @Nullable
    public static Double parseDouble(String doubleString) {
        try {
            return Double.parseDouble(doubleString);
        } catch (NumberFormatException exception) {
            return null;
        }
    }

}
