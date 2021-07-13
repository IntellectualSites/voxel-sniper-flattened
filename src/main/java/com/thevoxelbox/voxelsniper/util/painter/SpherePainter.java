package com.thevoxelbox.voxelsniper.util.painter;

import com.sk89q.worldedit.math.BlockVector3;
import com.thevoxelbox.voxelsniper.util.Vectors;
import com.thevoxelbox.voxelsniper.util.math.MathHelper;
import org.bukkit.Location;
import org.bukkit.block.Block;

public class SpherePainter implements Painter {

    private static final double TRUE_CIRCLE_ADDITIONAL_RADIUS = 0.5;

    private BlockVector3 center;
    private int radius;
    private boolean trueCircle;
    private BlockSetter blockSetter;

    public SpherePainter center(Block block) {
        BlockVector3 center = Vectors.of(block);
        return center(center);
    }

    public SpherePainter center(Location location) {
        BlockVector3 center = Vectors.of(location);
        return center(center);
    }

    public SpherePainter center(BlockVector3 center) {
        this.center = center;
        return this;
    }

    public SpherePainter radius(int radius) {
        this.radius = radius;
        return this;
    }

    public SpherePainter trueCircle() {
        return trueCircle(true);
    }

    public SpherePainter trueCircle(boolean trueCircle) {
        this.trueCircle = trueCircle;
        return this;
    }

    public SpherePainter blockSetter(BlockSetter blockSetter) {
        this.blockSetter = blockSetter;
        return this;
    }

    @Override
    public void paint() {
        if (this.center == null) {
            throw new RuntimeException("Center must be specified");
        }
        if (this.blockSetter == null) {
            throw new RuntimeException("Block setter must be specified");
        }
        paintSphere();
    }

    private void paintSphere() {
        Painters.block(this)
                .at(0, 0, 0)
                .paint();
        double radiusSquared = MathHelper.square(this.trueCircle ? this.radius + TRUE_CIRCLE_ADDITIONAL_RADIUS : this.radius);
        for (int first = 1; first <= this.radius; first++) {
            Painters.block(this)
                    .at(first, 0, 0)
                    .at(-first, 0, 0)
                    .at(0, first, 0)
                    .at(0, -first, 0)
                    .at(0, 0, first)
                    .at(0, 0, -first)
                    .paint();
            double firstSquared = MathHelper.square(first);
            for (int second = 1; second <= this.radius; second++) {
                double secondSquared = MathHelper.square(second);
                if (firstSquared + secondSquared <= radiusSquared) {
                    Painters.block(this)
                            .at(first, second, 0)
                            .at(first, -second, 0)
                            .at(-first, second, 0)
                            .at(-first, -second, 0)
                            .at(first, 0, second)
                            .at(first, 0, -second)
                            .at(-first, 0, second)
                            .at(-first, 0, -second)
                            .at(0, first, second)
                            .at(0, first, -second)
                            .at(0, -first, second)
                            .at(0, -first, -second)
                            .paint();
                }
                for (int third = 1; third <= this.radius; third++) {
                    int thirdSquared = MathHelper.square(third);
                    if (firstSquared + secondSquared + thirdSquared <= radiusSquared) {
                        Painters.block(this)
                                .at(first, second, third)
                                .at(first, second, -third)
                                .at(first, -second, third)
                                .at(first, -second, -third)
                                .at(-first, second, third)
                                .at(-first, second, -third)
                                .at(-first, -second, third)
                                .at(-first, -second, -third)
                                .paint();
                    }
                }
            }
        }
    }

    @Override
    public BlockVector3 getCenter() {
        return this.center;
    }

    public int getRadius() {
        return this.radius;
    }

    public boolean isTrueCircle() {
        return this.trueCircle;
    }

    @Override
    public BlockSetter getBlockSetter() {
        return this.blockSetter;
    }

}
