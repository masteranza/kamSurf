package video;

import java.awt.image.BufferedImage;
import java.util.Vector;

/**
 * @author Kamila
 * @date 17-09-2011
 * @version 1.0
 * 
 * Images buffer in producer-consumer model. There should be the same instance for producer and consumer.
 * 
 */

public class ImageBuffer {
	
	
	public static final int MAX_SIZE = 10;
	private int currentSize = 0;
	private Vector<BufferedImage> listOfImages = null;
	
	public ImageBuffer()
	{
		listOfImages = new Vector<BufferedImage>();
	}

	/**
	 * 
	 * @param image - image to put into buffer
	 * 
	 * This method puts BufferedImage object into buffer. If buffer is full object which executed this method 
	 * has to wait for free space in buffer.
	 */
	public synchronized void put(BufferedImage image){
		while(currentSize >= MAX_SIZE)
		{
			try{
				wait();
			}
			catch(InterruptedException e){}
		}
		System.out.println("Doda³em!!!");
		listOfImages.add(image);
		currentSize++;
		notify();
	}
	
	/**
	 * @return image from the buffer
	 * 
	 * This method gets BufferedImage object from the buffer. If buffer is empty  object which executed this method
	 * has to wait until any object will appear.
	 */
	public synchronized BufferedImage get()
	{
		while(currentSize<=0)
		{
			try{
				wait();
			}
			catch(InterruptedException e){}
		}
		System.out.println("Buffor size " + currentSize);
		BufferedImage image = listOfImages.remove(0);
		currentSize--;
		notify();
		return image;
	}
}
