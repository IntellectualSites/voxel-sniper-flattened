package com.thevoxelbox.voxelsniper.brush.type.performer;

import com.sk89q.worldedit.math.BlockVector3;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessageSender;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;

/**
 * FOR ANY BRUSH THAT USES A SPLINE, EXTEND THAT BRUSH FROM THIS BRUSH!!! That way, the spline calculations are already there. Also, the UI for
 * the splines will be included.
 */
public class SplineBrush extends AbstractPerformerBrush {

    private final List<BlockVector3> endPts = new ArrayList<>();
    private final List<BlockVector3> ctrlPts = new ArrayList<>();
    private final List<Point> spline = new ArrayList<>();
    private boolean set;
    private boolean ctrl;

    @Override
    public void handleCommand(String[] parameters, Snipe snipe) {
        SnipeMessenger messenger = snipe.createMessenger();
        for (String parameter : parameters) {
            if (parameter.equalsIgnoreCase("info")) {
                snipe.createMessageSender()
                        .message(ChatColor.GOLD + "Spline brush parameters")
                        .message(ChatColor.AQUA + "ss: Enable endpoint selection mode for desired curve")
                        .message(ChatColor.AQUA + "sc: Enable control point selection mode for desired curve")
                        .message(ChatColor.AQUA + "clear: Clear out the curve selection")
                        .message(ChatColor.AQUA + "ren: Render curve from control points")
                        .send();
                return;
            } else if (parameter.equalsIgnoreCase("sc")) {
                if (this.ctrl) {
                    this.ctrl = false;
                    messenger.sendMessage(ChatColor.AQUA + "Control point selection mode disabled.");
                } else {
                    this.set = false;
                    this.ctrl = true;
                    messenger.sendMessage(ChatColor.GRAY + "Control point selection mode ENABLED.");
                }
            } else if (parameter.equalsIgnoreCase("ss")) {
                if (this.set) {
                    this.set = false;
                    messenger.sendMessage(ChatColor.AQUA + "Endpoint selection mode disabled.");
                } else {
                    this.set = true;
                    this.ctrl = false;
                    messenger.sendMessage(ChatColor.GRAY + "Endpoint selection mode ENABLED.");
                }
            } else if (parameter.equalsIgnoreCase("clear")) {
                clear(snipe);
            } else if (parameter.equalsIgnoreCase("ren")) {
                if (spline(
                        new Point(this.endPts.get(0)),
                        new Point(this.endPts.get(1)),
                        new Point(this.ctrlPts.get(0)),
                        new Point(this.ctrlPts.get(1)),
                        snipe
                )) {
                    render();
                }
            } else {
                messenger.sendMessage(ChatColor.RED + "Invalid brush parameters! use the info parameter to display parameter info.");
            }
        }
    }

    @Override
    public void handleArrowAction(Snipe snipe) {
        BlockVector3 targetBlock = getTargetBlock();
        if (this.set) {
            removeFromSet(snipe, true, targetBlock);
        } else if (this.ctrl) {
            removeFromSet(snipe, false, targetBlock);
        }
    }

    @Override
    public void handleGunpowderAction(Snipe snipe) {
        BlockVector3 targetBlock = getTargetBlock();
        if (this.set) {
            addToSet(snipe, true, targetBlock);
        }
        if (this.ctrl) {
            addToSet(snipe, false, targetBlock);
        }
    }

    private void addToSet(Snipe snipe, boolean ep, BlockVector3 targetBlock) {
        SnipeMessenger messenger = snipe.createMessenger();
        if (ep) {
            if (this.endPts.contains(targetBlock) || this.endPts.size() == 2) {
                return;
            }
            this.endPts.add(targetBlock);
            messenger.sendMessage(ChatColor.GRAY + "Added block " + ChatColor.RED + "(" + targetBlock.getX() + ", " + targetBlock.getY() + ", " + targetBlock
                    .getZ() + ") " + ChatColor.GRAY + "to endpoint selection");
            return;
        }
        if (this.ctrlPts.contains(targetBlock) || this.ctrlPts.size() == 2) {
            return;
        }
        this.ctrlPts.add(targetBlock);
        messenger.sendMessage(ChatColor.GRAY + "Added block " + ChatColor.RED + "(" + targetBlock.getX() + ", " + targetBlock.getY() + ", " + targetBlock
                .getZ() + ") " + ChatColor.GRAY + "to control point selection");
    }

    private void removeFromSet(Snipe snipe, boolean ep, BlockVector3 targetBlock) {
        SnipeMessenger messenger = snipe.createMessenger();
        if (ep) {
            if (!this.endPts.contains(targetBlock)) {
                messenger.sendMessage(ChatColor.RED + "That block is not in the endpoint selection set.");
                return;
            }
            this.endPts.add(targetBlock);
            messenger.sendMessage(ChatColor.GRAY + "Removed block " + ChatColor.RED + "(" + targetBlock.getX() + ", " + targetBlock
                    .getY() + ", " + targetBlock.getZ() + ") " + ChatColor.GRAY + "from endpoint selection");
            return;
        }
        if (!this.ctrlPts.contains(targetBlock)) {
            messenger.sendMessage(ChatColor.RED + "That block is not in the control point selection set.");
            return;
        }
        this.ctrlPts.remove(targetBlock);
        messenger.sendMessage(ChatColor.GRAY + "Removed block " + ChatColor.RED + "(" + targetBlock.getX() + ", " + targetBlock.getY() + ", " + targetBlock
                .getZ() + ") " + ChatColor.GRAY + "from control point selection");
    }

    private boolean spline(Point start, Point end, Point c1, Point c2, Snipe snipe) {
        SnipeMessenger messenger = snipe.createMessenger();
        this.spline.clear();
        try {
            Point c = c1.subtract(start)
                    .multiply(3);
            Point b = c2.subtract(c1)
                    .multiply(3)
                    .subtract(c);
            Point a = end.subtract(start)
                    .subtract(c)
                    .subtract(b);
            for (double t = 0.0; t < 1.0; t += 0.01) {
                int px = (int) Math.round(a.getX() * (t * t * t) + b.getX() * (t * t) + c.getX() * t + this.endPts.get(0)
                        .getX());
                int py = (int) Math.round(a.getY() * (t * t * t) + b.getY() * (t * t) + c.getY() * t + this.endPts.get(0)
                        .getY());
                int pz = (int) Math.round(a.getZ() * (t * t * t) + b.getZ() * (t * t) + c.getZ() * t + this.endPts.get(0)
                        .getZ());
                if (!this.spline.contains(new Point(px, py, pz))) {
                    this.spline.add(new Point(px, py, pz));
                }
            }
            return true;
        } catch (RuntimeException exception) {
            messenger.sendMessage(ChatColor.RED + "Not enough points selected; " + this.endPts.size() + " endpoints, " + this.ctrlPts
                    .size() + " control points");
            return false;
        }
    }

    private void render() {
        if (this.spline.isEmpty()) {
            return;
        }
        for (Point point : this.spline) {
            this.performer.perform(
                    getEditSession(),
                    point.getX(),
                    clampY(point.getY()),
                    point.getZ(),
                    clampY(point.getX(), point.getY(), point.getZ())
            );
        }
    }

    private void clear(Snipe snipe) {
        this.spline.clear();
        this.ctrlPts.clear();
        this.endPts.clear();
        SnipeMessenger messenger = snipe.createMessenger();
        messenger.sendMessage(ChatColor.GRAY + "Bezier curve cleared.");
    }

    @Override
    public void sendInfo(Snipe snipe) {
        SnipeMessageSender messageSender = snipe.createMessageSender()
                .brushNameMessage();
        if (this.set) {
            messageSender.message(ChatColor.GRAY + "Endpoint selection mode ENABLED.");
        } else if (this.ctrl) {
            messageSender.message(ChatColor.GRAY + "Control point selection mode ENABLED.");
        } else {
            messageSender.message(ChatColor.AQUA + "No selection mode enabled.");
        }
        messageSender.send();
    }

    // Vector class for splines
    private static final class Point {

        private int x;
        private int y;
        private int z;

        private Point(BlockVector3 block) {
            this.x = block.getX();
            this.y = block.getY();
            this.z = block.getZ();
        }

        private Point(int x, int y, int z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public Point add(Point point) {
            return new Point(this.x + point.x, this.y + point.y, this.z + point.z);
        }

        public Point multiply(int scalar) {
            return new Point(this.x * scalar, this.y * scalar, this.z * scalar);
        }

        public Point subtract(Point point) {
            return new Point(this.x - point.x, this.y - point.y, this.z - point.z);
        }

        public int getX() {
            return this.x;
        }

        public void setX(int x) {
            this.x = x;
        }

        public int getY() {
            return this.y;
        }

        public void setY(int y) {
            this.y = y;
        }

        public int getZ() {
            return this.z;
        }

        public void setZ(int z) {
            this.z = z;
        }

    }

}
