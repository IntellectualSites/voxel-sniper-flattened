package com.thevoxelbox.voxelsniper.sniper;

public class UnknownSniperPlayerException extends RuntimeException {

    private static final long serialVersionUID = -3759901632482923871L;

    public UnknownSniperPlayerException() {
        super("Sniper player not found");
    }

}
