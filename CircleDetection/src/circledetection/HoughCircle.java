package circledetection;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import javax.imageio.ImageIO;

//import Homework_02.Binarize;
//import Homework_02.Otsu;
//import Utilities.ImageReadWrite;

public class HoughCircle {

    private int radiusBins = 12;
    int radiusStart = 5;
    int[][] accumulator;
    int lines, samples;

    // -- primary driver routine for finding circles
    public void Apply(int[][] _image) {
        lines = _image.length;
        samples = _image[0].length;

        accumulator = new int[lines * samples][radiusBins + 1];

        for (int i = 0; i < lines; ++i) {
            for (int j = 0; j < samples; ++j) {
                if (_image[i][j] > 0) {
                    for (int r = radiusStart; r < radiusStart + radiusBins; ++r) {
                        CalculateCirclePoints(j, i, r);
                    }
                }
            }
        }
    }

    // -- Bresenham circle, this version is for finding circles via Hough transform
    private int CalculateCirclePoints(int _x, int _y, int radius) {
        int numPoints = 0;

        int x, y;
        int d;

        x = 0;
        y = radius;
        d = 1 - radius;

        CirclePoints(x, y, _x, _y, radius);
        numPoints += 8;
        while (y > x) {
            if (d < 0) {
                d = d + (2 * x) + 3;
                ++x;
            } else {
                d = d + (2 * (x - y)) + 5;
                ++x;
                --y;
            }
            CirclePoints(x, y, _x, _y, radius);
            numPoints += 8;
        }

        return numPoints;
    }

    public void CirclePoints(int _x, int _y, int _ox, int _oy, int _r) {
        int loc; // -- linearize the image coordinates for
        //    indexing into the hough accumulator

        loc = ((_x + _oy) * samples + (_y + _ox));
        ++accumulator[loc][_r - radiusStart];
        loc = ((_y + _oy) * samples + (_x + _ox));
        ++accumulator[loc][_r - radiusStart];
        loc = ((_y + _oy) * samples + (-_x + _ox));
        ++accumulator[loc][_r - radiusStart];
        loc = ((_x + _oy) * samples + (-_y + _ox));
        ++accumulator[loc][_r - radiusStart];
        loc = ((-_x + _oy) * samples + (-_y + _ox));
        ++accumulator[loc][_r - radiusStart];
        loc = ((-_y + _oy) * samples + (-_x + _ox));
        ++accumulator[loc][_r - radiusStart];
        loc = ((-_y + _oy) * samples + (_x + _ox));
        ++accumulator[loc][_r - radiusStart];
        loc = ((-_x + _oy) * samples + (_y + _ox));
        ++accumulator[loc][_r - radiusStart];
    }

}
