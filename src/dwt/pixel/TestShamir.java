package dwt.pixel;

import java.io.File;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.math.BigInteger;

import javax.imageio.ImageIO;

import dwt.pixel.Shamir.SecretShare;

public class TestShamir {


	public static void MakeShares(String filePath, String outFile, int k, int n,int Secret) throws IOException {
		/**
		 * Read a sample image from the filesystem
		 */
	//	String filePath = "C:\\Users\\Fahad\\Documents\\SSS\\";
		BufferedImage image = readImage(filePath);
		BigInteger secret = BigInteger.valueOf(Secret);
	//	BigInteger prime = BigInteger.valueOf(17);
		/**
		 * Call the method that prints out ARGB color Information.
		 * ARGB = Alpha, Red, Green and Blue
		 */
		// printAllARGBDetails(image);

		//	System.out.println("================   Shamir   ======================");
		//printAllShamirDetails(image);
		BufferedImage newImage = (BufferedImage) CallShamir(image,k,n, secret);

		System.out.println(" Making Shares...");
		for (int i = 1; i <= n; i++) {
			ImageIO.write(newImage, "jpg", new File(outFile+"\\\\"+"share"+i+".jpg"));
		}
		System.out.println("Shares written...");


		/*// If at least k shares selected the reConstruct original secret
		int k1 = 3;
		final Shamir shamir = new Shamir(k1, n);
		final SecretShare[] shares = shamir.split(secret);
		final Shamir shamir2 = new Shamir(k1, n);
		final BigInteger result = shamir2.combine(shares, prime);

		if(k1>=3){
			System.out.println("Reconstructed Secret:"+result);

			System.out.println(" Reconstructing Original Image...");
				ImageIO.write(image, "jpeg 2000", new File(filePath+"ReconImage.j2k"));
			System.out.println("Shares written...");
		}
		else
			System.out.println("Atleast 3 images should be selected");*/

	}
	
	public static void reconstructImage(String filePath, String outFile, int k, int n,int Secret) throws IOException {

	// If at least k shares selected the reConstruct original secret
		
		BufferedImage image = readImage(filePath);
		BigInteger secret = BigInteger.valueOf(Secret);
		BigInteger prime = BigInteger.valueOf(17);
		
		final Shamir shamir = new Shamir(k, n);
		final SecretShare[] shares = shamir.split(secret);
		final Shamir shamir2 = new Shamir(k, n);
		final BigInteger result = shamir2.combine(shares, prime);

		if(k>=3){
			System.out.println("Reconstructed Secret:"+result);
			System.out.println(" Reconstructing Original Image...");
				ImageIO.write(image, "jpeg 2000", new File(outFile+"\\\\"+"ReconImage.j2k"));
			System.out.println("Shares written...");
		}
		else
			System.out.println("Atleast 3 images should be selected");

	}
	
	public static String printAllARGBDetails(BufferedImage image) {

		int width = image.getWidth();
		int height = image.getHeight();

		String storeOriginal = "";

		System.out.println("Image Dimension: Height-" + height + ", Width-"+ width);
		System.out.println("Total Pixels: " + (height * width));
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {

				int pixel = image.getRGB(i, j);
				//	System.out.println("Originl Pixel Location(" + i + "," + j + ")- ["
				//			+ getARGBPixelData(pixel) + "]");
				storeOriginal += getARGBPixelData(pixel);

			}
		}
		return storeOriginal;
	}


	public static String[] parts;
	public static String[] strArray;

	public static Image CallShamir(BufferedImage image, int k,int n, BigInteger secret) {

		int width = image.getWidth();
		int height = image.getHeight();
		int totalPixels = width*height;

		int[] intShares= new int[n];
		int[] intPixels = new int[totalPixels]; 


		String shares = Shamir.getShares(k,n,secret);
		//	System.out.println("Final Shamir Shares "+shares);

		for (int j = 0; j < n; j++) {
			parts = shares.split(" ");
			System.out.println("String Share"+j+" :"+parts[j]);
			intShares[j] = Integer.parseInt(parts[j]);
		}

		// Call printAllARGBDetails
		String pix = printAllARGBDetails(image);

		System.out.println("String converted to String array");
		System.out.println();

		//print elements of String array
		for(int i=0; i < totalPixels ; i++){
			strArray = pix.split(" ");
			// System.out.println("String Array "+Integer.parseInt(strArray[i]));
			intPixels[i] = Integer.parseInt(strArray[i]);
			//	System.out.println("Integer Pixels Array "+intPixels[i]);
		}

		System.out.println();
		// Adding Shares to each pixel, to get new Image
		int [] addition  = new int[totalPixels];

		for (int m = 0; m < totalPixels/100; m++) {
			addition [m] = intShares[0]+intPixels[m];
			//	System.out.println("Addition Pixels Array "+addition[m]);
		}

		BufferedImage image1 = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		try{
			WritableRaster raster = (WritableRaster) image1.getData();
			try{
				raster.setPixels(0,0,width,height,addition);
			}
			catch(ArrayIndexOutOfBoundsException e)
			{
				// e.printStackTrace();
			}
			return image1;
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return image1;

	}

	/**
	 * Image Pixels are Arrays of Integers [32 bits/4Bytes]
	 * Consider a 32 pixel as 11111111-00110011-00111110-00011110
	 *
	 * First Byte From Left [11111111]= Alpha
	 * Second Byte From Left[00110011]= Red
	 * Third Byte From Left [00111110]= Green
	 * Fourth Byte From Left[00011110]= Blue
	 *
	 * The following method will do a proper bit shift and
	 * logical AND operation to extract the correct values
	 * of different color/alpha components.
	 *
	 */
	public static String getARGBPixelData(int pixel) {
		String pixelARGBData = "";
		/**
		 * Shift all pixels 24 bits to the right.
		 * Do a logical and with 0x000000FF
		 * i.e. 0000 0000 0000 0000 0000 0000 1111 1111
		 * You will get the alpha value for the pixel
		 */
		int alpha = (pixel >> 24) & 0x000000FF;

		/**
		 * Shift all pixels 16 bits to the right.
		 * Do a logical and with 0x000000FF
		 * i.e. 0000 0000 0000 0000 0000 0000 1111 1111
		 * You will get the red value for the pixel
		 */

		int red = (pixel >> 16) & 0x000000FF;

		/**
		 * Shift all pixels 8 bits to the right.
		 * Do a logical and with 0x000000FF
		 * i.e. 0000 0000 0000 0000 0000 0000 1111 1111
		 * You will get the green value for the pixel
		 */
		int green = (pixel >>8 ) & 0x000000FF;

		/**
		 * Dont do any shift.
		 * Do a logical and with 0x000000FF
		 * i.e. 0000 0000 0000 0000 0000 0000 1111 1111
		 * You will get the blue value for the pixel
		 */
		int blue = (pixel) & 0x000000FF;

		/*pixelARGBData = "Alpha: " + alpha + ", " + "Red: " + red + ", "
				+ "Green: " + green + ", " + "Blue: " + blue;*/

		pixelARGBData = alpha+" "+red+" " + green +" " +blue +" ";


		return pixelARGBData;
	}

	/**
	 * This method reads an image from the file
	 * @param fileLocation -- > eg. "C:/testImage.jpg"
	 * @return BufferedImage of the file read
	 */
	public static BufferedImage readImage(String fileLocation) {
		BufferedImage img = null;
		try {
			img = ImageIO.read(new File(fileLocation));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return img;
	}
}