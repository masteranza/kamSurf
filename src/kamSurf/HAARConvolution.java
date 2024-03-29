 

package kamSurf;

/**
 * A standard implementation of the HAAR_Convolution interface.
 * 
 *       -----------------------------------------------> x
 *       |       x1     x2    x3        x1          x3
 *       |     y1  -----------       y1  -----------
 *       |        |  B  |  W  |         |     W     |
 *       |        |     |     |         |     -1    |
 *       |        |  1  | -1  |      y2 |-----------|
 *       |        |     |     |         |     B     |
 *       |        |     |     |         |     1     |
 *       |     y3  -----------       y3  -----------
 *       |
 *       v
 *       y
 *           
 * Our Haar Wavelets approximation. The left filter computes the x-response, the right filter the y one.
 * Inside each region the wavelet value is specified.
 * Combined with the use of Integral Image, the Haar Convolution needs only 6 operations per filter.
 *
 */

public class HAARConvolution implements IHAARconvolution {

	private IIntegralImage integralImage;

	public HAARConvolution(IIntegralImage integralImage) {
		this.integralImage = integralImage;
	}

	public float makeConvolutionDx(int x, int y, float scale) {

		int x1, x2, x3, y1, y3;

		/*
		 * Calculation of points x1, x2, x3, y1, y2, y3. The center of the filter
		 * is at (x,y) and it is 2*_scale large.
		 */
		x1 = (int) (x - scale);
		x2 =   x;
		x3 = (int) (x + scale);
		y1 = (int) (y - scale);
		y3 = (int) (y + scale);

		return (integralImage.getIntegralSquare(x1, y1, x2, y3) -
                        integralImage.getIntegralSquare(x2, y1, x3, y3));

	}

	public float makeConvolutionDy(int x, int y, float scale) {

		int x1, x3, y1, y2, y3;

		/*
		 * Calculation of points x1, x2, x3, y1, y2, y3. The center of the filter
		 * is at (x,y) and it is 2*_scale large.
		 */
		x1 = (int) (x - scale);
		x3 = (int) (x + scale);
		y1 = (int) (y - scale);
		y2 =   y;
		y3 = (int) (y + scale);

		return (-integralImage.getIntegralSquare(x1, y1, x3, y2) + integralImage.getIntegralSquare(x1, y2, x3, y3));

	}

}
