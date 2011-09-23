package video;

import java.io.IOException;

/**
 * @author Kamila
 * @date 17-09-2011
 * @version 1.0
 * 
 * Test class, a substitute of consumer in producer-consumer model. To be deleted or hidden in later implementation.
 * 
 */

public class ProducerConsumerTest {

	public static void main(String[] args) throws IOException, InterruptedException{
		ImageBuffer ib = new ImageBuffer();
		VideoStreamReader vsr = new VideoStreamReader(args[0], ib);
		for(int i = 0 ; i< 10; i++)
		{
			ib.get();
			Thread.sleep(1000L);
		}
		
	}
}
