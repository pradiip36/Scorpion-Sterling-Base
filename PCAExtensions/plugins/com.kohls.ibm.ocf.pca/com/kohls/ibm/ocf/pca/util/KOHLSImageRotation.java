package com.kohls.ibm.ocf.pca.util;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

public class KOHLSImageRotation {

	

	/**
	 * This method Rotate the image
	 * @param image
	 * @param scaledWidth
	 * @param scaledHeight
	 * @param currentAngle
	 * @param x
	 * @param y
	 * @return
	 */
	public static BufferedImage createResizedCopy(BufferedImage image,
			int scaledWidth, int scaledHeight, int currentAngle, int x, int y) {
		BufferedImage scaledBI = null;

		// If we are going from vertical to horizontal...
		if (currentAngle == 90 || currentAngle == -270 || currentAngle == -90
				|| currentAngle == 270) {
			scaledBI = new BufferedImage(scaledWidth, scaledHeight,
					BufferedImage.TYPE_INT_RGB); // First apply the scaling.
			Graphics2D g = scaledBI.createGraphics();
			g.drawImage(image, x, y, scaledWidth, scaledHeight, null);
			g.dispose();

			scaledBI = changeOrientation(scaledBI, currentAngle); // Then
																	// rotated
																	// it.
		} else {
			scaledBI = new BufferedImage(scaledWidth, scaledHeight,
					BufferedImage.TYPE_INT_RGB);
			Graphics2D g = scaledBI.createGraphics();
			g.rotate(Math.toRadians(currentAngle), scaledWidth / 2,
					scaledHeight / 2);
			g.drawImage(image, x, y, scaledWidth, scaledHeight, null);
			g.dispose();
		}

		return scaledBI;
	}

	/**
	 * This method used for rotation.
	 * @param image
	 * @param currentAngle
	 * @return
	 */
	private static BufferedImage changeOrientation(BufferedImage image,
			int currentAngle) {
		int j = image.getWidth();
		int i = image.getHeight();
		BufferedImage rotatedBI = new BufferedImage(i, j,
				BufferedImage.TYPE_INT_RGB);
		int p = 0;
		if (currentAngle == 90 || currentAngle == -270) // If we are rotating
														// right...
			for (int x1 = 0; x1 < j; x1++)
				for (int y1 = 0; y1 < i; y1++) {
					p = image.getRGB(x1, y1);
					rotatedBI.setRGB(i - 1 - y1, x1, p);
				}
		else
			// We are rotating left.
			for (int x1 = 0; x1 < j; x1++)
				for (int y1 = 0; y1 < i; y1++) {
					p = image.getRGB(x1, y1);
					rotatedBI.setRGB(y1, j - 1 - x1, p);
				}

		return rotatedBI;
	}


}