package com.thevoxelbox.voxelsniper.brush.type.performer;

import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import com.thevoxelbox.voxelsniper.sniper.Sniper;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;

/**
 * http://www.voxelwiki.com/minecraft/Voxelsniper#Triangle_Brush
 *
 * @author Giltwist
 */
public class TriangleBrush extends AbstractPerformerBrush {

	private double[] coordinatesOne = new double[3]; // Three corners
	private double[] coordinatesTwo = new double[3];
	private double[] coordinatesThree = new double[3];
	private int cornerNumber = 1;
	private double[] currentCoordinates = new double[3]; // For loop tracking
	private double[] vectorOne = new double[3]; // Point 1 to 2
	private double[] vectorTwo = new double[3]; // Point 1 to 3
	private double[] vectorThree = new double[3]; // Point 2 to 3, for area calculations
	private double[] normalVector = new double[3];

	@Override
	public void handleCommand(String[] parameters, Snipe snipe) {
		if (parameters[1].equalsIgnoreCase("info")) {
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
		Block targetBlock = getTargetBlock();
		int targetBlockX = targetBlock.getX();
		int targetBlockY = targetBlock.getY();
		int targetBlockZ = targetBlock.getZ();
		switch (this.cornerNumber) {
			case 1:
				this.coordinatesOne[0] = targetBlockX + 0.5 * targetBlockX / Math.abs(targetBlockX); // I hate you sometimes, Notch. Really? Every quadrant is
				// different?
				this.coordinatesOne[1] = targetBlockY + 0.5;
				this.coordinatesOne[2] = targetBlockZ + 0.5 * targetBlockZ / Math.abs(targetBlockZ);
				this.cornerNumber = 2;
				messenger.sendMessage(ChatColor.GRAY + "First Corner set.");
				break;
			case 2:
				this.coordinatesTwo[0] = targetBlockX + 0.5 * targetBlockX / Math.abs(targetBlockX); // I hate you sometimes, Notch. Really? Every quadrant is
				// different?
				this.coordinatesTwo[1] = targetBlockY + 0.5;
				this.coordinatesTwo[2] = targetBlockZ + 0.5 * targetBlockZ / Math.abs(targetBlockZ);
				this.cornerNumber = 3;
				messenger.sendMessage(ChatColor.GRAY + "Second Corner set.");
				break;
			case 3:
				this.coordinatesThree[0] = targetBlockX + 0.5 * targetBlockX / Math.abs(targetBlockX); // I hate you sometimes, Notch. Really? Every quadrant is
				// different?
				this.coordinatesThree[1] = targetBlockY + 0.5;
				this.coordinatesThree[2] = targetBlockZ + 0.5 * targetBlockZ / Math.abs(targetBlockZ);
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
		for (int i = 0; i < 3; i++) {
			this.vectorOne[i] = this.coordinatesTwo[i] - this.coordinatesOne[i];
			this.vectorTwo[i] = this.coordinatesThree[i] - this.coordinatesOne[i];
			this.vectorThree[i] = this.coordinatesThree[i] - this.coordinatesTwo[i];
		}
		// Calculate the cross product of vectorone and vectortwo
		this.normalVector[0] = this.vectorOne[1] * this.vectorTwo[2] - this.vectorOne[2] * this.vectorTwo[1];
		this.normalVector[1] = this.vectorOne[2] * this.vectorTwo[0] - this.vectorOne[0] * this.vectorTwo[2];
		this.normalVector[2] = this.vectorOne[0] * this.vectorTwo[1] - this.vectorOne[1] * this.vectorTwo[0];
		// Calculate magnitude of slope vectors
		double lengthOne = Math.pow(IntStream.of(0, 1, 2)
			.mapToDouble(v -> Math.pow(this.vectorOne[v], 2))
			.sum(), 0.5);
		double lengthTwo = Math.pow(IntStream.of(0, 1, 2)
			.mapToDouble(v -> Math.pow(this.vectorTwo[v], 2))
			.sum(), 0.5);
		double lengthThree = Math.pow(IntStream.of(0, 1, 2)
			.mapToDouble(v -> Math.pow(this.vectorThree[v], 2))
			.sum(), 0.5);
		// Bigger vector determines brush size
		int brushSize = (int) Math.ceil((lengthOne > lengthTwo) ? lengthOne : lengthTwo);
		// Calculate constant term
		double planeConstant = this.normalVector[0] * this.coordinatesOne[0] + this.normalVector[1] * this.coordinatesOne[1] + this.normalVector[2] * this.coordinatesOne[2];
		// Calculate the area of the full triangle
		double heronBig = 0.25 * Math.pow(Math.pow(DoubleStream.of(lengthOne, lengthTwo, lengthThree)
			.map(v -> Math.pow(v, 2))
			.sum(), 2) - 2 * (DoubleStream.of(lengthOne, lengthTwo, lengthThree)
			.map(v1 -> Math.pow(v1, 4))
			.sum()), 0.5);
		if (lengthOne == 0 || lengthTwo == 0 || (IntStream.of(0, 1, 2)
			.allMatch(v -> this.coordinatesOne[v] == 0)) || (IntStream.of(0, 1, 2)
			.allMatch(i1 -> this.coordinatesTwo[i1] == 0)) || (IntStream.of(0, 1, 2)
			.allMatch(i2 -> this.coordinatesThree[i2] == 0))) {
			messenger.sendMessage(ChatColor.RED + "ERROR: Invalid corners, please try again.");
		} else {
			// Make the Changes
			double[] cVectorOne = new double[3];
			double[] cVectorTwo = new double[3];
			double[] cVectorThree = new double[3];
			for (int y = -brushSize; y <= brushSize; y++) { // X DEPENDENT
				for (int z = -brushSize; z <= brushSize; z++) {
					this.currentCoordinates[1] = this.coordinatesOne[1] + y;
					this.currentCoordinates[2] = this.coordinatesOne[2] + z;
					this.currentCoordinates[0] = (planeConstant - this.normalVector[1] * this.currentCoordinates[1] - this.normalVector[2] * this.currentCoordinates[2]) / this.normalVector[0];
					// Area of triangle currentcoords, coordsone, coordstwo
					for (int i = 0; i < 3; i++) {
						cVectorOne[i] = this.coordinatesTwo[i] - this.coordinatesOne[i];
						cVectorTwo[i] = this.currentCoordinates[i] - this.coordinatesOne[i];
						cVectorThree[i] = this.currentCoordinates[i] - this.coordinatesTwo[i];
					}
					double cLengthOne = Math.pow(IntStream.of(0, 1, 2)
						.mapToDouble(v -> Math.pow(cVectorOne[v], 2))
						.sum(), 0.5);
					double cLengthTwo = Math.pow(IntStream.of(0, 1, 2)
						.mapToDouble(v -> Math.pow(cVectorTwo[v], 2))
						.sum(), 0.5);
					double cLengthThree = Math.pow(IntStream.of(0, 1, 2)
						.mapToDouble(v -> Math.pow(cVectorThree[v], 2))
						.sum(), 0.5);
					double heronOne = 0.25 * Math.pow(Math.pow(Math.pow(cLengthOne, 2) + Math.pow(cLengthTwo, 2) + Math.pow(cLengthThree, 2), 2) - 2 * (Math.pow(cLengthOne, 4) + Math.pow(cLengthTwo, 4) + Math.pow(cLengthThree, 4)), 0.5);
					// Area of triangle currentcoords, coordsthree, coordstwo
					for (int i = 0; i < 3; i++) {
						cVectorOne[i] = this.coordinatesTwo[i] - this.coordinatesThree[i];
						cVectorTwo[i] = this.currentCoordinates[i] - this.coordinatesThree[i];
						cVectorThree[i] = this.currentCoordinates[i] - this.coordinatesTwo[i];
					}
					cLengthOne = Math.pow(IntStream.of(0, 1, 2)
						.mapToDouble(v -> Math.pow(cVectorOne[v], 2))
						.sum(), 0.5);
					cLengthTwo = Math.pow(IntStream.of(0, 1, 2)
						.mapToDouble(v -> Math.pow(cVectorTwo[v], 2))
						.sum(), 0.5);
					cLengthThree = Math.pow(IntStream.of(0, 1, 2)
						.mapToDouble(v -> Math.pow(cVectorThree[v], 2))
						.sum(), 0.5);
					double heronTwo = 0.25 * Math.pow(Math.pow(Math.pow(cLengthOne, 2) + Math.pow(cLengthTwo, 2) + Math.pow(cLengthThree, 2), 2) - 2 * (Math.pow(cLengthOne, 4) + Math.pow(cLengthTwo, 4) + Math.pow(cLengthThree, 4)), 0.5);
					// Area of triangle currentcoords, coordsthree, coordsone
					for (int i = 0; i < 3; i++) {
						cVectorOne[i] = this.coordinatesOne[i] - this.coordinatesThree[i];
						cVectorTwo[i] = this.currentCoordinates[i] - this.coordinatesThree[i];
						cVectorThree[i] = this.currentCoordinates[i] - this.coordinatesOne[i];
					}
					cLengthOne = Math.pow(IntStream.of(0, 1, 2)
						.mapToDouble(i -> Math.pow(cVectorOne[i], 2))
						.sum(), 0.5);
					cLengthTwo = Math.pow(IntStream.of(0, 1, 2)
						.mapToDouble(i -> Math.pow(cVectorTwo[i], 2))
						.sum(), 0.5);
					cLengthThree = Math.pow(IntStream.of(0, 1, 2)
						.mapToDouble(i -> Math.pow(cVectorThree[i], 2))
						.sum(), 0.5);
					double heronThree = 0.25 * Math.pow(Math.pow(Math.pow(cLengthOne, 2) + Math.pow(cLengthTwo, 2) + Math.pow(cLengthThree, 2), 2) - 2 * (Math.pow(cLengthOne, 4) + Math.pow(cLengthTwo, 4) + Math.pow(cLengthThree, 4)), 0.5);
					double barycentric = (heronOne + heronTwo + heronThree) / heronBig;
					if (barycentric <= 1.1) {
						this.performer.perform(this.clampY((int) this.currentCoordinates[0], (int) this.currentCoordinates[1], (int) this.currentCoordinates[2]));
					}
				}
			}
			for (int x = -brushSize; x <= brushSize; x++) { // Y DEPENDENT
				for (int z = -brushSize; z <= brushSize; z++) {
					this.currentCoordinates[0] = this.coordinatesOne[0] + x;
					this.currentCoordinates[2] = this.coordinatesOne[2] + z;
					this.currentCoordinates[1] = (planeConstant - this.normalVector[0] * this.currentCoordinates[0] - this.normalVector[2] * this.currentCoordinates[2]) / this.normalVector[1];
					// Area of triangle currentcoords, coordsone, coordstwo
					for (int i = 0; i < 3; i++) {
						cVectorOne[i] = this.coordinatesTwo[i] - this.coordinatesOne[i];
						cVectorTwo[i] = this.currentCoordinates[i] - this.coordinatesOne[i];
						cVectorThree[i] = this.currentCoordinates[i] - this.coordinatesTwo[i];
					}
					double cLengthOne = Math.pow(IntStream.of(0, 1, 2)
						.mapToDouble(v -> Math.pow(cVectorOne[v], 2))
						.sum(), 0.5);
					double cLengthTwo = Math.pow(IntStream.of(0, 1, 2)
						.mapToDouble(v -> Math.pow(cVectorTwo[v], 2))
						.sum(), 0.5);
					double cLengthThree = Math.pow(IntStream.of(0, 1, 2)
						.mapToDouble(v -> Math.pow(cVectorThree[v], 2))
						.sum(), 0.5);
					double heronOne = 0.25 * Math.pow(Math.pow(Math.pow(cLengthOne, 2) + Math.pow(cLengthTwo, 2) + Math.pow(cLengthThree, 2), 2) - 2 * (Math.pow(cLengthOne, 4) + Math.pow(cLengthTwo, 4) + Math.pow(cLengthThree, 4)), 0.5);
					// Area of triangle currentcoords, coordsthree, coordstwo
					for (int i = 0; i < 3; i++) {
						cVectorOne[i] = this.coordinatesTwo[i] - this.coordinatesThree[i];
						cVectorTwo[i] = this.currentCoordinates[i] - this.coordinatesThree[i];
						cVectorThree[i] = this.currentCoordinates[i] - this.coordinatesTwo[i];
					}
					cLengthOne = Math.pow(IntStream.of(0, 1, 2)
						.mapToDouble(v -> Math.pow(cVectorOne[v], 2))
						.sum(), 0.5);
					cLengthTwo = Math.pow(IntStream.of(0, 1, 2)
						.mapToDouble(v -> Math.pow(cVectorTwo[v], 2))
						.sum(), 0.5);
					cLengthThree = Math.pow(IntStream.of(0, 1, 2)
						.mapToDouble(v -> Math.pow(cVectorThree[v], 2))
						.sum(), 0.5);
					double heronTwo = 0.25 * Math.pow(Math.pow(Math.pow(cLengthOne, 2) + Math.pow(cLengthTwo, 2) + Math.pow(cLengthThree, 2), 2) - 2 * (Math.pow(cLengthOne, 4) + Math.pow(cLengthTwo, 4) + Math.pow(cLengthThree, 4)), 0.5);
					// Area of triangle currentcoords, coordsthree, coordsone
					for (int i = 0; i < 3; i++) {
						cVectorOne[i] = this.coordinatesOne[i] - this.coordinatesThree[i];
						cVectorTwo[i] = this.currentCoordinates[i] - this.coordinatesThree[i];
						cVectorThree[i] = this.currentCoordinates[i] - this.coordinatesOne[i];
					}
					cLengthOne = Math.pow(IntStream.of(0, 1, 2)
						.mapToDouble(i -> Math.pow(cVectorOne[i], 2))
						.sum(), 0.5);
					cLengthTwo = Math.pow(IntStream.of(0, 1, 2)
						.mapToDouble(i -> Math.pow(cVectorTwo[i], 2))
						.sum(), 0.5);
					cLengthThree = Math.pow(IntStream.of(0, 1, 2)
						.mapToDouble(i -> Math.pow(cVectorThree[i], 2))
						.sum(), 0.5);
					double heronThree = 0.25 * Math.pow(Math.pow(Math.pow(cLengthOne, 2) + Math.pow(cLengthTwo, 2) + Math.pow(cLengthThree, 2), 2) - 2 * (Math.pow(cLengthOne, 4) + Math.pow(cLengthTwo, 4) + Math.pow(cLengthThree, 4)), 0.5);
					double barycentric = (heronOne + heronTwo + heronThree) / heronBig;
					if (barycentric <= 1.1) {
						this.performer.perform(this.clampY((int) this.currentCoordinates[0], (int) this.currentCoordinates[1], (int) this.currentCoordinates[2]));
					}
				}
			}
			for (int x = -brushSize; x <= brushSize; x++) { // Z DEPENDENT
				for (int y = -brushSize; y <= brushSize; y++) {
					this.currentCoordinates[0] = this.coordinatesOne[0] + x;
					this.currentCoordinates[1] = this.coordinatesOne[1] + y;
					this.currentCoordinates[2] = (planeConstant - this.normalVector[0] * this.currentCoordinates[0] - this.normalVector[1] * this.currentCoordinates[1]) / this.normalVector[2];
					// Area of triangle currentcoords, coordsone, coordstwo
					for (int i = 0; i < 3; i++) {
						cVectorOne[i] = this.coordinatesTwo[i] - this.coordinatesOne[i];
						cVectorTwo[i] = this.currentCoordinates[i] - this.coordinatesOne[i];
						cVectorThree[i] = this.currentCoordinates[i] - this.coordinatesTwo[i];
					}
					double cLengthOne = Math.pow(IntStream.of(0, 1, 2)
						.mapToDouble(v -> Math.pow(cVectorOne[v], 2))
						.sum(), 0.5);
					double cLengthTwo = Math.pow(IntStream.of(0, 1, 2)
						.mapToDouble(v -> Math.pow(cVectorTwo[v], 2))
						.sum(), 0.5);
					double cLengthThree = Math.pow(IntStream.of(0, 1, 2)
						.mapToDouble(v -> Math.pow(cVectorThree[v], 2))
						.sum(), 0.5);
					double heronOne = 0.25 * Math.pow(Math.pow(Math.pow(cLengthOne, 2) + Math.pow(cLengthTwo, 2) + Math.pow(cLengthThree, 2), 2) - 2 * (Math.pow(cLengthOne, 4) + Math.pow(cLengthTwo, 4) + Math.pow(cLengthThree, 4)), 0.5);
					// Area of triangle currentcoords, coordsthree, coordstwo
					for (int i = 0; i < 3; i++) {
						cVectorOne[i] = this.coordinatesTwo[i] - this.coordinatesThree[i];
						cVectorTwo[i] = this.currentCoordinates[i] - this.coordinatesThree[i];
						cVectorThree[i] = this.currentCoordinates[i] - this.coordinatesTwo[i];
					}
					cLengthOne = Math.pow(IntStream.of(0, 1, 2)
						.mapToDouble(v -> Math.pow(cVectorOne[v], 2))
						.sum(), 0.5);
					cLengthTwo = Math.pow(IntStream.of(0, 1, 2)
						.mapToDouble(v -> Math.pow(cVectorTwo[v], 2))
						.sum(), 0.5);
					cLengthThree = Math.pow(IntStream.of(0, 1, 2)
						.mapToDouble(v -> Math.pow(cVectorThree[v], 2))
						.sum(), 0.5);
					double heronTwo = 0.25 * Math.pow(Math.pow(Math.pow(cLengthOne, 2) + Math.pow(cLengthTwo, 2) + Math.pow(cLengthThree, 2), 2) - 2 * (Math.pow(cLengthOne, 4) + Math.pow(cLengthTwo, 4) + Math.pow(cLengthThree, 4)), 0.5);
					// Area of triangle currentcoords, coordsthree, coordsone
					for (int i = 0; i < 3; i++) {
						cVectorOne[i] = this.coordinatesOne[i] - this.coordinatesThree[i];
						cVectorTwo[i] = this.currentCoordinates[i] - this.coordinatesThree[i];
						cVectorThree[i] = this.currentCoordinates[i] - this.coordinatesOne[i];
					}
					cLengthOne = Math.pow(IntStream.of(0, 1, 2)
						.mapToDouble(i -> Math.pow(cVectorOne[i], 2))
						.sum(), 0.5);
					cLengthTwo = Math.pow(IntStream.of(0, 1, 2)
						.mapToDouble(i -> Math.pow(cVectorTwo[i], 2))
						.sum(), 0.5);
					cLengthThree = Math.pow(IntStream.of(0, 1, 2)
						.mapToDouble(i -> Math.pow(cVectorThree[i], 2))
						.sum(), 0.5);
					double heronThree = 0.25 * Math.pow(Math.pow(Math.pow(cLengthOne, 2) + Math.pow(cLengthTwo, 2) + Math.pow(cLengthThree, 2), 2) - 2 * (Math.pow(cLengthOne, 4) + Math.pow(cLengthTwo, 4) + Math.pow(cLengthThree, 4)), 0.5);
					double barycentric = (heronOne + heronTwo + heronThree) / heronBig;
					// VoxelSniper.log.info("Bary: "+barycentric+", hb: "+heronbig+", h1: "+heronone+", h2: "+herontwo+", h3: "+heronthree);
					if (barycentric <= 1.1) {
						this.performer.perform(this.clampY((int) this.currentCoordinates[0], (int) this.currentCoordinates[1], (int) this.currentCoordinates[2]));
					}
				}
			}
			Sniper sniper = snipe.getSniper();
			sniper.storeUndo(this.performer.getUndo());
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

	@Override
	public void sendInfo(Snipe snipe) { // Make the triangle
		SnipeMessenger messenger = snipe.createMessenger();
		messenger.sendBrushNameMessage();
	}
}