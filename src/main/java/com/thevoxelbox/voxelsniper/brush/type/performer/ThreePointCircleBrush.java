package com.thevoxelbox.voxelsniper.brush.type.performer;

import com.sk89q.worldedit.math.BlockVector3;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessageSender;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import com.thevoxelbox.voxelsniper.util.Vectors;
import org.bukkit.ChatColor;
import org.bukkit.util.NumberConversions;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.stream.Collectors;

public class ThreePointCircleBrush extends AbstractPerformerBrush {

    @Nullable
    private Vector coordinatesOne;
    @Nullable
    private Vector coordinatesTwo;
    @Nullable
    private Vector coordinatesThree;
    private Tolerance tolerance = Tolerance.DEFAULT;

    @Override
    public void handleCommand(String[] parameters, Snipe snipe) {
        SnipeMessenger messenger = snipe.createMessenger();
        if (parameters[0].equalsIgnoreCase("info")) {
            messenger.sendMessage(ChatColor.YELLOW + "3-Point Circle Brush instructions: Select three corners with the arrow brush, then generate the Circle with the powder brush.");
            String toleranceOptions = Arrays.stream(Tolerance.values())
                    .map(tolerance -> tolerance.name()
                            .toLowerCase())
                    .collect(Collectors.joining("|"));
            messenger.sendMessage(ChatColor.GOLD + "/b tpc " + toleranceOptions + " -- Toggle the calculations to emphasize accuracy or smoothness");
            return;
        }
        for (String s : parameters) {
            try {
                String parameter = s.toUpperCase();
                this.tolerance = Tolerance.valueOf(parameter);
                messenger.sendMessage(ChatColor.AQUA + "Brush set to " + this.tolerance.name()
                        .toLowerCase() + " tolerance.");
                return;
            } catch (IllegalArgumentException exception) {
                messenger.sendMessage(ChatColor.LIGHT_PURPLE + "No such tolerance.");
            }
        }
    }

    @Override
    public void handleArrowAction(Snipe snipe) {
        SnipeMessenger messenger = snipe.createMessenger();
        BlockVector3 targetBlock = getTargetBlock();
        if (this.coordinatesOne == null) {
            this.coordinatesOne = Vectors.toBukkit(targetBlock);
            messenger.sendMessage(ChatColor.GRAY + "First Corner set.");
        } else if (this.coordinatesTwo == null) {
            this.coordinatesTwo = Vectors.toBukkit(targetBlock);
            messenger.sendMessage(ChatColor.GRAY + "Second Corner set.");
        } else if (this.coordinatesThree == null) {
            this.coordinatesThree = Vectors.toBukkit(targetBlock);
            messenger.sendMessage(ChatColor.GRAY + "Third Corner set.");
        } else {
            this.coordinatesOne = Vectors.toBukkit(targetBlock);
            this.coordinatesTwo = null;
            this.coordinatesThree = null;
            messenger.sendMessage(ChatColor.GRAY + "First Corner set.");
        }
    }

    @Override
    public void handleGunpowderAction(Snipe snipe) {
        if (this.coordinatesOne == null || this.coordinatesTwo == null || this.coordinatesThree == null) {
            return;
        }
        // Calculate triangle defining vectors
        Vector vectorOne = this.coordinatesTwo.clone();
        vectorOne.subtract(this.coordinatesOne);
        Vector vectorTwo = this.coordinatesThree.clone();
        vectorTwo.subtract(this.coordinatesOne);
        Vector vectorThree = this.coordinatesThree.clone();
        vectorThree.subtract(vectorTwo);
        SnipeMessenger messenger = snipe.createMessenger();
        // Redundant data check
        if (vectorOne.length() == 0 || vectorTwo.length() == 0 || vectorThree.length() == 0 || vectorOne.angle(vectorTwo) == 0 || vectorOne
                .angle(vectorThree) == 0 || vectorThree.angle(vectorTwo) == 0) {
            messenger.sendMessage(ChatColor.RED + "ERROR: Invalid points, try again.");
            this.coordinatesOne = null;
            this.coordinatesTwo = null;
            this.coordinatesThree = null;
            return;
        }
        // Calculate normal vector of the plane.
        Vector normalVector = vectorOne.clone();
        normalVector.crossProduct(vectorTwo);
        // Calculate constant term of the plane.
        double planeConstant = normalVector.getX() * this.coordinatesOne.getX() + normalVector.getY() * this.coordinatesOne.getY() + normalVector
                .getZ() * this.coordinatesOne.getZ();
        Vector midpointOne = this.coordinatesOne.getMidpoint(this.coordinatesTwo);
        Vector midpointTwo = this.coordinatesOne.getMidpoint(this.coordinatesThree);
        // Find perpendicular vectors to two sides in the plane
        Vector perpendicularOne = normalVector.clone();
        perpendicularOne.crossProduct(vectorOne);
        Vector perpendicularTwo = normalVector.clone();
        perpendicularTwo.crossProduct(vectorTwo);
        // determine value of parametric variable at intersection of two perpendicular bisectors
        Vector tNumerator = midpointTwo.clone();
        tNumerator.subtract(midpointOne);
        tNumerator.crossProduct(perpendicularTwo);
        Vector tDenominator = perpendicularOne.clone();
        tDenominator.crossProduct(perpendicularTwo);
        double t = tNumerator.length() / tDenominator.length();
        // Calculate Circumcenter and Brushcenter.
        Vector circumcenter = new Vector();
        circumcenter.copy(perpendicularOne);
        circumcenter.multiply(t);
        circumcenter.add(midpointOne);
        Vector brushCenter = new Vector(
                Math.round(circumcenter.getX()),
                Math.round(circumcenter.getY()),
                Math.round(circumcenter.getZ())
        );
        // Calculate radius of circumcircle and determine brushsize
        double radius = circumcenter.distance(new Vector(
                this.coordinatesOne.getX(),
                this.coordinatesOne.getY(),
                this.coordinatesOne.getZ()
        ));
        int brushSize = NumberConversions.ceil(radius) + 1;
        for (int x = -brushSize; x <= brushSize; x++) {
            for (int y = -brushSize; y <= brushSize; y++) {
                for (int z = -brushSize; z <= brushSize; z++) {
                    // Calculate distance from center
                    double tempDistance = Math.pow(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2), 0.5);
                    // gets corner-on blocks
                    double cornerConstant = normalVector.getX() * (circumcenter.getX() + x) + normalVector.getY() * (circumcenter.getY() + y) + normalVector
                            .getZ() * (circumcenter.getZ() + z);
                    // gets center-on blocks
                    double centerConstant = normalVector.getX() * (circumcenter.getX() + x + 0.5) + normalVector.getY() * (circumcenter
                            .getY() + y + 0.5) + normalVector.getZ() * (circumcenter.getZ() + z + 0.5);
                    // Check if point is within sphere and on plane (some tolerance given)
                    if (tempDistance <= radius && (Math.abs(cornerConstant - planeConstant) < this.tolerance.getValue() || Math.abs(
                            centerConstant - planeConstant) < this.tolerance.getValue())) {
                        this.performer.perform(
                                getEditSession(),
                                brushCenter.getBlockX() + x,
                                clampY(brushCenter.getBlockY() + y),
                                brushCenter.getBlockZ() + z,
                                this.clampY(brushCenter.getBlockX() + x, brushCenter.getBlockY() + y, brushCenter.getBlockZ() + z)
                        );
                    }
                }
            }
        }
        messenger.sendMessage(ChatColor.GREEN + "Done.");
        // Reset Brush
        this.coordinatesOne = null;
        this.coordinatesTwo = null;
        this.coordinatesThree = null;
    }

    @Override
    public void sendInfo(Snipe snipe) {
        SnipeMessageSender messageSender = snipe.createMessageSender()
                .brushNameMessage();
        switch (this.tolerance) {
            case ACCURATE:
                messageSender.message(ChatColor.GOLD + "Mode: Accurate");
                break;
            case DEFAULT:
                messageSender.message(ChatColor.GOLD + "Mode: Default");
                break;
            case SMOOTH:
                messageSender.message(ChatColor.GOLD + "Mode: Smooth");
                break;
            default:
                messageSender.message(ChatColor.GOLD + "Mode: Unknown");
                break;
        }
        messageSender.send();
    }

    private enum Tolerance {

        DEFAULT(1000),
        ACCURATE(10),
        SMOOTH(2000);

        private final int value;

        Tolerance(int value) {
            this.value = value;
        }

        public int getValue() {
            return this.value;
        }
    }

}
