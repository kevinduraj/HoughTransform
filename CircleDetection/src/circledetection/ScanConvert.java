package circledetection;


public class ScanConvert 
{

    public static void BresenhamLine(int Ax, int Ay, int Bx, int By, int[][] _image, int _color)
    {
        // -- Initialize the components of the algorithm that are not affected by the
        //    slope or direction of the line
        int dX = Math.abs(Bx-Ax);    // store the change in X and Y of the line endpoints
        int dY = Math.abs(By-Ay);

        int CurrentX = Ax;              // store the starting point (just point A)
        int CurrentY = Ay;

        // DETERMINE "DIRECTIONS" TO INCREMENT X AND Y (REGARDLESS OF DECISION)
        int Xincr, Yincr;
        if (Ax > Bx) { Xincr=-1; } else { Xincr=1; }    // which direction in X?
        if (Ay > By) { Yincr=-1; } else { Yincr=1; }    // which direction in Y?

        // DETERMINE INDEPENDENT VARIABLE (ONE THAT ALWAYS INCREMENTS BY 1 (OR -1) )
        // AND INITIATE APPROPRIATE LINE DRAWING ROUTINE (BASED ON FIRST OCTANT
        // ALWAYS). THE X AND Y'S MAY BE FLIPPED IF Y IS THE INDEPENDENT VARIABLE.

        if (dX >= dY) {   // if X is the independent variable
		    int dPr = dY << 1;    // amount to increment decision if right is chosen (always)
		    int dPru = dPr - (dX << 1);  // amount to increment decision if up is chosen
		    int P = dPr - dX;  // decision variable start value
		    
		    for (; dX>=0; dX--) {  // process each point in the line one at a time (just use dX)
				putpixel(CurrentX, CurrentY, _image, _color);    // plot the pixel
				if (P > 0) {                              // is the pixel going right AND up?
				    CurrentX+=Xincr;                                        // increment independent variable
				    CurrentY+=Yincr;                                        // increment dependent variable
				    P+=dPru;                                                        // increment decision (for up)
				}
				else {                                                                    // is the pixel just going right?
				    CurrentX+=Xincr;                                        // increment independent variable
				    P+=dPr;                                                         // increment decision (for right)
				}
		    }
        }
        else {                    // if Y is the independent variable
		    int dPr = dX<<1; // amount to increment decision if right is chosen (always)
		    int dPru = dPr - (dY << 1);    // amount to increment decision if up is chosen
		    int P = dPr - dY;       // decision variable start value
		    for (; dY>=0; dY--) { // process each point in the line one at a time (just use dY)
				putpixel(CurrentX, CurrentY, _image, _color);    // plot the pixel
				if (P > 0)  { // is the pixel going up AND right?
				    CurrentX+=Xincr;                                        // increment dependent variable
				    CurrentY+=Yincr;                                        // increment independent variable
				    P+=dPru;                                                        // increment decision (for up)
				}
				else {                                                                    // is the pixel just going up?
				    CurrentY+=Yincr;                                        // increment independent variable
				    P+=dPr;                                                         // increment decision (for right)
				}
		    }
        }
    }

    private static void putpixel(int _x, int _y, int[][] _image, int _color)
    {
		_image[_y][_x] = _color;
		/* -- this is for animating the line drawing process
		try {
			String s = String.format("frame%03d.BMP", imageNo++);
			BMP.WriteInt(s, bimage, bimage, bimage);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		-- */
   }
    
    
    // -- Bresenham circle, this version is for drawing in _image with color _color
	public static int CalculateCirclePoints(int radius, int _x, int _y, int[][] _image, int _color)
	{
		int numPoints = 0;

		int x, y;
		int d;

		x = 0;
		y = radius;
		d = 1 - radius;
	
		CirclePoints(x, y, _image, _x, _y, _color);
		numPoints += 8;
		while (y > x) {
			if (d < 0) {
				d = d + (2 * x) + 3;
				++x;
			}
			else {
				d = d + (2 * (x - y)) + 5;
				++x;
				--y;
			}
			CirclePoints(x, y, _image, _x, _y, _color);
			numPoints += 8;
		}

		return numPoints;
	}

	private static void CirclePoints (int _x, int _y, int[][] _image, int _ox, int _oy, int _color) 
	{
		_image[ _x + _oy][ _y + _ox] = _color;
		_image[ _y + _oy][ _x + _ox] = _color;
		_image[ _y + _oy][-_x + _ox] = _color;
		_image[ _x + _oy][-_y + _ox] = _color;
		_image[-_x + _oy][-_y + _ox] = _color;
		_image[-_y + _oy][-_x + _ox] = _color;
		_image[-_y + _oy][ _x + _ox] = _color;
		_image[-_x + _oy][ _y + _ox] = _color;
	}
	
}

/* -- circle code (and more)
//ScanConvertDoc.cpp : implementation of the CScanConvertDoc class
//

CScanConvertDoc::CScanConvertDoc()
{
	// TODO: add one-time construction code here


	// -- file I/O preparations
	reset = false;
	height = 64;//2 * MAX(P0y, P1y) + 10;
	width = 64;//2 * MAX(P0x, P1x) + 10;
	points = new unsigned char * [height];
	points[0] = new unsigned char [height * width];
	for (int i = 1; i < height; ++i) {
		points[i] = points[i-1] + width;
	}
	for (i = 0; i < height; ++i) {
		for (int j = 0; j < width; ++j) {
			points[i][j] = 0;
		}
	}

}

//unsigned char **points;
//int height, width;
//bool reset = true;
#define MAX(x,y) (fabs(x) > fabs(y) ? fabs(x) : fabs(y))

int CScanConvertDoc::CalculateEllipsePoints()
{
	// -- file I/O preparations
	if (reset) {
		for (int i = 0; i < height; ++i) {
			for (int j = 0; j < width; ++j) {
				points[i][j] = 0;
			}
		}
	}

	numPoints = 0;

	int a = 25; // -- 2a is major axis length
	int b = 10;  // -- 2b is minor axis length

	int x, y;
	double d1, d2;

	x = 0;
	y = b;
	d1 = (b * b) - (a * a * b) + ((a * a) / 4.0);
	EllipsePoints(x, y);
	numPoints += 4;

	while ((a * a * (y - 0.5)) > (b * b * (x + 1))) {
		if (d1 < 0) {
			d1 = d1 + (b * b * (2 * x + 3));
			++x;
		}
		else {
			d1 = d1 + (b * b * (2 * x + 3)) + (a * a * (-2 * y + 2));
			++x;
			--y;
		}
		EllipsePoints(x, y);
		numPoints += 4;
	}

	d2 = (b * b * (x + 0.5) * (x + 0.5)) + (a * a * (y - 1) * (y - 1)) - (a * a * b * b);
	while (y > 0) {
		if (d2 < 0) {
			d2 = d2 + (b * b * (2 * x + 2)) + (a * a * (-2 * y + 3));
			++x;
			--y;
		}
		else {
			d2 = d2 + (a * a * (-2 * y + 3));
			--y;
		}
		EllipsePoints(x, y);
		numPoints += 4;
	}

	return numPoints;
}


void CScanConvertDoc::EllipsePoints (int _x, int _y)
{
	SetPixel( _x,  _y, 0);
	SetPixel(-_x,  _y, 0);
	SetPixel( _x, -_y, 0);
	SetPixel(-_x, -_y, 0);
}


int CScanConvertDoc::CalculateLinePoints()
{

	numPoints = 0;

	int dx, dy, incrE, incrNE, d, x, y;
	int xinc, yinc;

	// -- delta x and delta y (slope components)
	dx = P1x - P0x;
	dy = P1y - P0y;

	// -- normalize based on quadrants 
	if (dx < 0) {
		dx = -dx;
		xinc = -1;
	}
	else {
		xinc = 1;
	}

	if (dy < 0) {
		dy = -dy;
		yinc = -1;
	}
	else {
		yinc = 1;
	}

	// -- file I/O preparations
	if (reset) {
		for (int i = 0; i < height; ++i) {
			for (int j = 0; j < width; ++j) {
				points[i][j] = 0;
			}
		}
	}

	// -- set initial point
	x = P0x;
	y = P0y;
	SetPixel(x, y, numPoints++);

	// -- slope less than 1 will traverse x coordinate
	if (dy <= dx) {

		d = (2 * dy) - dx;
		incrE = 2 * dy;
		incrNE = 2 * (dy - dx);
		
		// -- always traverse from point 0 to point 1
		//    two sets of code to handle while condition
		if (P0x < P1x) {
			while (x < P1x) {
				if (d <= 0) {
					d = d + incrE;
					x += xinc;
				}
				else {
					d = d + incrNE;
					x += xinc;
					y += yinc;
				}
				SetPixel(x, y, numPoints++);
			}
		}
		else {
			while (x > P1x) {
				if (d <= 0) {
					d = d + incrE;
					x += xinc;
				}
				else {
					d = d + incrNE;
					x += xinc;
					y += yinc;
				}
				SetPixel(x, y, numPoints++);
			}
		}
	}
	// -- slope greater than 1 will traverse y coordinate
	else {

		d = (2 * dx) - dy;
		incrE = 2 * dx;
		incrNE = 2 * (dx - dy);

		// -- always traverse from point 0 to point 1
		//    two sets of code to handle while condition
		if (P0y < P1y) {
			while (y < P1y) {
				if (d <= 0) {
					d = d + incrE;
					y += yinc;
				}
				else {
					d = d + incrNE;
					x += xinc;
					y += yinc;
				}
				SetPixel(x, y, numPoints++);
			}
		}
		else {
			while (y > P1y) {
				if (d <= 0) {
					d = d + incrE;
					y += yinc;
				}
				else {
					d = d + incrNE;
					x += xinc;
					y += yinc;
				}
				SetPixel(x, y, numPoints++);
			}
		}
	}
	return numPoints;
}

void CScanConvertDoc::SetPixel(int _x, int _y, int _loc)
{
	endPoints[_loc][0] = _x;
	endPoints[_loc][1] = _y;

	{
		char filename[128];
		static int count = 0;
		sprintf(filename, "c:\\ani\\line%03d.bmp", count++);
		points[_y + (height >> 1)][_x + (width >> 1)] = 255;
		WriteBMP(filename, points, points, points, height, width);

	}

}


-- */
