package com.thevoxelbox.voxelsniper.brush.type.performer;

import com.sk89q.worldedit.math.BlockVector3;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import com.thevoxelbox.voxelsniper.util.math.MathHelper;
import org.bukkit.ChatColor;

import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

public class TriangleBrush extends AbstractPerformerBrush {

    private final double[] coordinatesOne = new double[3]; // Three corners
    private final double[] coordinatesTwo = new double[3];
    private final double[] coordinatesThree = new double[3];
    private final double[] currentCoordinates = new double[3]; // For loop tracking
    private final double[] vectorOne = new double[3]; // Point 1 to 2
    private final double[] vectorTwo = new double[3]; // Point 1 to 3
    private final double[] vectorThree = new double[3]; // Point 2 to 3, for area calculations
    private final double[] normalVector = new double[3];
    private int cornerNumber = 1;

    @Override
    public void handleCommand(String[] parameters, Snipe snipe) {
        if (parameters[0].equalsIgnoreCase("info")) {
            SnipeMessenger messenger = snipe.createMessenger();
            messenger.sendMessage(ChatColor.GOLD + "Triangle Brush instructions: Select three corners with the arrow brush, then generate the triangle with the powder brush.");
        }
    }

    @Override
    public void handleArrowAction(Snipe snipe) {
        triangleA(snipe);
    }

    @Override
    public void handleGunpowderAction(Snipe snipe) { // Add a point
        triangleP(snipe);
    }

    private void triangleA(Snipe snipe) {
        SnipeMessenger messenger = snipe.createMessenger();
        BlockVector3 targetBlock = getTargetBlock();
        int targetBlockX = targetBlock.getX();
        int targetBlockY = targetBlock.getY();
        int targetBlockZ = targetBlock.getZ();
        double x = targetBlockX + 0.5 * targetBlockX / Math.abs(targetBlockX);
        double y = targetBlockY + 0.5;
        double z = targetBlockZ + 0.5 * targetBlockZ / Math.abs(targetBlockZ);
        switch (this.cornerNumber) {
            case 1:
                this.coordinatesOne[0] = x; // I hate you sometimes, Notch. Really? Every quadrant is
                // different?
                this.coordinatesOne[1] = y;
                this.coordinatesOne[2] = z;
                this.cornerNumber = 2;
                messenger.sendMessage(ChatColor.GRAY + "First Corner set.");
                break;
            case 2:
                this.coordinatesTwo[0] = x; // I hate you sometimes, Notch. Really? Every quadrant is
                // different?
                this.coordinatesTwo[1] = y;
                this.coordinatesTwo[2] = z;
                this.cornerNumber = 3;
                messenger.sendMessage(ChatColor.GRAY + "Second Corner set.");
                break;
            case 3:
                this.coordinatesThree[0] = x; // I hate you sometimes, Notch. Really? Every quadrant is
                // different?
                this.coordinatesThree[1] = y;
                this.coordinatesThree[2] = z;
                this.cornerNumber = 1;
                messenger.sendMessage(ChatColor.GRAY + "Third Corner set.");
                break;
            default:
                break;
        }
    }

    private void triangleP(Snipe snipe) {
        SnipeMessenger messenger = snipe.createMessenger();
        // Calculate slope vectors
        for (int index = 0; index < 3; index++) {
            this.vectorOne[index] = this.coordinatesTwo[index] - this.coordinatesOne[index];
            this.vectorTwo[index] = this.coordinatesThree[index] - this.coordinatesOne[index];
            this.vectorThree[index] = this.coordinatesThree[index] - this.coordinatesTwo[index];
        }
        // Calculate the cross product of vectorone and vectortwo
        this.normalVector[0] = this.vectorOne[1] * this.vectorTwo[2] - this.vectorOne[2] * this.vectorTwo[1];
        this.normalVector[1] = this.vectorOne[2] * this.vectorTwo[0] - this.vectorOne[0] * this.vectorTwo[2];
        this.normalVector[2] = this.vectorOne[0] * this.vectorTwo[1] - this.vectorOne[1] * this.vectorTwo[0];
        // Calculate magnitude of slope vectors
        double lengthOne = Math.sqrt(IntStream.of(0, 1, 2)
                .mapToDouble(number -> MathHelper.square(this.vectorOne[number]))
                .sum());
        double lengthTwo = Math.sqrt(IntStream.of(0, 1, 2)
                .mapToDouble(number -> MathHelper.square(this.vectorTwo[number]))
                .sum());
        double lengthThree = Math.sqrt(IntStream.of(0, 1, 2)
                .mapToDouble(number -> MathHelper.square(this.vectorThree[number]))
                .sum());
        // Bigger vector determines brush size
        int brushSize = (int) Math.ceil(Math.max(lengthOne, lengthTwo));
        // Calculate constant term
        double planeConstant = this.normalVector[0] * this.coordinatesOne[0] + this.normalVector[1] * this.coordinatesOne[1] + this.normalVector[2] * this.coordinatesOne[2];
        // Calculate the area of the full triangle
        double heronBig = 0.25 * Math.sqrt(MathHelper.square(DoubleStream.of(lengthOne, lengthTwo, lengthThree)
                .map(MathHelper::square)
                .sum()) - 2 * DoubleStream.of(lengthOne, lengthTwo, lengthThree)
                .map(number -> Math.pow(number, 4))
                .sum());
        if (lengthOne == 0 || lengthTwo == 0 || IntStream.of(0, 1, 2)
                .allMatch(number -> this.coordinatesOne[number] == 0) || IntStream.of(0, 1, 2)
                .allMatch(number -> this.coordinatesTwo[number] == 0) || IntStream.of(0, 1, 2)
                .allMatch(number -> this.coordinatesThree[number] == 0)) {
            messenger.sendMessage(ChatColor.RED + "ERROR: Invalid corners, please try again.");
        } else {
            // Make the Changes
            double[] cVectorOne = new double[3];
            double[] cVectorTwo = new double[3];
            double[] cVectorThree = new double[3];
            perform(brushSize, planeConstant, heronBig, cVectorOne, cVectorTwo, cVectorThree, 1, 2, 0);
            perform(brushSize, planeConstant, heronBig, cVectorOne, cVectorTwo, cVectorThree, 0, 2, 1);
            perform(brushSize, planeConstant, heronBig, cVectorOne, cVectorTwo, cVectorThree, 0, 1, 2);
        }
        // reset brush
        this.coordinatesOne[0] = 0;
        this.coordinatesOne[1] = 0;
        this.coordinatesOne[2] = 0;
        this.coordinatesTwo[0] = 0;
        this.coordinatesTwo[1] = 0;
        this.coordinatesTwo[2] = 0;
        this.coordinatesThree[0] = 0;
        this.coordinatesThree[1] = 0;
        this.coordinatesThree[2] = 0;
        this.cornerNumber = 1;
    }

    private void perform(
            int brushSize,
            double planeConstant,
            double heronBig,
            double[] cVectorOne,
            double[] cVectorTwo,
            double[] cVectorThree,
            int i,
            int i2,
            int i3
    ) {
        for (int y = -brushSize; y <= brushSize; y++) { // X DEPENDENT
            for (int z = -brushSize; z <= brushSize; z++) {
                this.currentCoordinates[i] = this.coordinatesOne[i] + y;
                this.currentCoordinates[i2] = this.coordinatesOne[i2] + z;
                this.currentCoordinates[i3] = (planeConstant - this.normalVector[i] * this.currentCoordinates[i] - this.normalVector[i2] * this.currentCoordinates[i2]) / this.normalVector[i3];
                // Area of triangle currentcoords, coordsone, coordstwo
                double heronOne = calculateHeron(cVectorOne, cVectorTwo, cVectorThree, this.coordinatesOne, this.coordinatesTwo);
                // Area of triangle currentcoords, coordsthree, coordstwo
                double heronTwo = calculateHeron(
                        cVectorOne,
                        cVectorTwo,
                        cVectorThree,
                        this.coordinatesThree,
                        this.coordinatesTwo
                );
                // Area of triangle currentcoords, coordsthree, coordsone
                double heronThree = calculateHeron(
                        cVectorOne,
                        cVectorTwo,
                        cVectorThree,
                        this.coordinatesThree,
                        this.coordinatesOne
                );
                double barycentric = (heronOne + heronTwo + heronThree) / heronBig;
                if (barycentric <= 1.1) {
                    this.performer.perform(
                            getEditSession(),
                            (int) this.currentCoordinates[0],
                            clampY((int) this.currentCoordinates[1]),
                            (int) this.currentCoordinates[2],
                            clampY(
                                    (int) this.currentCoordinates[0],
                                    (int) this.currentCoordinates[1],
                                    (int) this.currentCoordinates[2]
                            )
                    );
                }
            }
        }
    }

    private double calculateHeron(
            double[] cVectorOne,
            double[] cVectorTwo,
            double[] cVectorThree,
            double[] coordinatesOne,
            double[] coordinatesTwo
    ) {
        for (int i = 0; i < 3; i++) {
            cVectorOne[i] = coordinatesTwo[i] - coordinatesOne[i];
            cVectorTwo[i] = this.currentCoordinates[i] - coordinatesOne[i];
            cVectorThree[i] = this.currentCoordinates[i] - coordinatesTwo[i];
        }
        double cLengthOne = Math.sqrt(IntStream.of(0, 1, 2)
                .mapToDouble(number -> MathHelper.square(cVectorOne[number]))
                .sum());
        double cLengthTwo = Math.sqrt(IntStream.of(0, 1, 2)
                .mapToDouble(number -> MathHelper.square(cVectorTwo[number]))
                .sum());
        double cLengthThree = Math.sqrt(IntStream.of(0, 1, 2)
                .mapToDouble(number -> MathHelper.square(cVectorThree[number]))
                .sum());
        return 0.25 * Math.sqrt(MathHelper.square(DoubleStream.of(cLengthOne, cLengthTwo, cLengthThree)
                .map(MathHelper::square)
                .sum()) - 2 * DoubleStream.of(cLengthOne, cLengthTwo, cLengthThree)
                .map(number -> Math.pow(number, 4))
                .sum());
    }

    @Override
    public void sendInfo(Snipe snipe) { // Make the triangle
        SnipeMessenger messenger = snipe.createMessenger();
        messenger.sendBrushNameMessage();
    }

}
