package circledetection;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Main {

    private static final int threshold = 30;
    private static String input = "src/images/CoinsThresh.png";

    public static void main(String[] args) throws IOException {

        int grn[][] = ImageRead(input);

        //-- formulate the hough space
        HoughCircle houghcircle = new HoughCircle();
        houghcircle.Apply(grn);
        int maxacc = 0;
        int maxpos = 0;
        int maxrad = 0;
        
        for (int i = 0; i < houghcircle.accumulator.length; ++i) {
            for (int j = 0; j < houghcircle.accumulator[i].length; ++j) {
                if (houghcircle.accumulator[i][j] > maxacc) {
                    maxacc = houghcircle.accumulator[i][j];
                    maxpos = i;
                    maxrad = j;
                }
            }
        }
        
        // loc = (( _x + _oy) * samples + ( _y + _ox));
        System.out.println("------------- Hough Space Result -------------");
        int y = maxpos / houghcircle.samples;
        int x = maxpos % houghcircle.samples;
        System.out.println("Maximum: " + maxacc + " " + maxpos + " " + (maxrad + houghcircle.radiusStart) + " (" + x + ", " + y + ")");

        // -- make an image showing where the circles were found
        int counts[] = new int[80];
        grn = new int[grn.length][grn[0].length];
        
        for (int i = 0; i < houghcircle.accumulator.length; ++i) {
            for (int j = 0; j < houghcircle.accumulator[i].length; ++j) {
                if (houghcircle.accumulator[i][j] > threshold) {
                    if (threshold % 10 == 0) {
                        int y1 = i / houghcircle.samples;
                        int x1 = i % houghcircle.samples;
                        ScanConvert.CalculateCirclePoints(j + houghcircle.radiusStart, x1, y1, grn, 255);
                        System.out.println(houghcircle.accumulator[i][j] + " " + i + " " + (j + houghcircle.radiusStart) + " (" + x1 + ", " + y1 + ")");
                    }
                    ++counts[threshold];
                }
            }
        }

        ImageWrite("src/images/HoughCircle" + threshold + ".png", grn);

    }

    /*--------------------------------------------------------------------------------------------*/
    private static int[][] ImageRead(String filename) throws IOException {

        File infile = new File(filename);
        BufferedImage bi = ImageIO.read(infile);

        int red[][] = new int[bi.getHeight()][bi.getWidth()];
        int grn[][] = new int[bi.getHeight()][bi.getWidth()];
        int blu[][] = new int[bi.getHeight()][bi.getWidth()];

        for (int i = 0; i < red.length; ++i) {
            for (int j = 0; j < red[i].length; ++j) {
                red[i][j] = bi.getRGB(j, i) >> 16 & 0xFF;
                grn[i][j] = bi.getRGB(j, i) >> 8 & 0xFF;
                blu[i][j] = bi.getRGB(j, i) & 0xFF;
            }
        }
        return grn;
    }
    /*--------------------------------------------------------------------------------------------*/

    private static void ImageWrite(String filename, int img[][]) throws IOException {

        BufferedImage bi = new BufferedImage(img[0].length, img.length, BufferedImage.TYPE_INT_RGB);

        for (int i = 0; i < bi.getHeight(); ++i) {
            for (int j = 0; j < bi.getWidth(); ++j) {
                int val = img[i][j];
                int pixel = (val << 16) | (val << 8) | (val);
                bi.setRGB(j, i, pixel);
            }
        }

        File outputfile = new File(filename);
        ImageIO.write(bi, "png", outputfile);
    }
    /*--------------------------------------------------------------------------------------------*/
}
