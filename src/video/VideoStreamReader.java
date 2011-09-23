/**
 * 
 */
package video;

import java.awt.image.BufferedImage;
import java.io.IOException;

import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IContainer;
import com.xuggle.xuggler.IPacket;
import com.xuggle.xuggler.IPixelFormat;
import com.xuggle.xuggler.IStream;
import com.xuggle.xuggler.IStreamCoder;
import com.xuggle.xuggler.IVideoPicture;
import com.xuggle.xuggler.IVideoResampler;
import com.xuggle.xuggler.Utils;

/**
 * @author Kamila
 * @date 17-09-2011
 * @version 1.0
 * 
 * Class VideoStreamReader produces BufferedImage objects from video stream and puts them into
 * buffer. This class is a produces in the consumer-producer model. 
 * Thread ends when whole video is divided into images. 
 */
public class VideoStreamReader extends Thread{
	
	private IContainer container = null;
	private IStreamCoder videoCoder = null;
	private IPacket packet = null;
	private ImageBuffer buffer = null;
	private IVideoResampler resampler = null;
	
	private int videoStreamId = -1;

	/**
	 * @param filename - path to video file
	 * @param imageBuffer - buffer in which images will be stored
	 * 
	 * 
	 * @b the constructor executes run() method. Do not use method start() on created object!
	 */
	public VideoStreamReader(String filename, ImageBuffer imageBuffer) throws IOException
	{
		super("VideoStreamReader Thread");
		//buffer = new ImageBuffer();
		buffer = imageBuffer;
		//create on object of type IContainer
	    container = IContainer.make();
	    System.out.println(filename);
	    if (container.open(filename, IContainer.Type.READ, null) < 0)
	        throw new IOException("could not open file: " + filename);
	    
	    int numberOfStreams = container.getNumStreams();
	    
	    
	    for(int i = 0; i<numberOfStreams && videoStreamId == -1; i++)
	    {
	    	// Find the stream object
	        IStream stream = container.getStream(i);
	        // Get the pre-configured decoder that can decode this stream;
	        IStreamCoder coder = stream.getStreamCoder();

	        if (coder.getCodecType() == ICodec.Type.CODEC_TYPE_VIDEO)
	        {
	        	videoStreamId = i;
	        	videoCoder = coder;
	        }
	    }
	    
	    if (videoStreamId == -1)
	        throw new RuntimeException("could not find video stream in file: "+filename);
	    //open IStreamCoder object
	    if (videoCoder.open() < 0)
	        throw new RuntimeException("could not open video decoder for file: "+filename);
	    
	 // Let's make sure that we can actually convert video pixel formats.
	    if (!IVideoResampler.isSupported(
	        IVideoResampler.Feature.FEATURE_COLORSPACECONVERSION))
	      throw new RuntimeException("you must install the GPL version" +
	      		" of Xuggler (with IVideoResampler support) for " +
	      		"this demo to work");
	    
	    
	    if (videoCoder.getPixelType() != IPixelFormat.Type.BGR24)
	    {
	      // if this stream is not in BGR24, we're going to need to
	      // convert it.  The VideoResampler does that for us.
	      resampler = IVideoResampler.make(videoCoder.getWidth(), 
	          videoCoder.getHeight(), IPixelFormat.Type.BGR24,
	          videoCoder.getWidth(), videoCoder.getHeight(), videoCoder.getPixelType());
	      if (resampler == null)
	        throw new RuntimeException("could not create color space resampler");
	    }
	    
	    packet = IPacket.make();
	    start();
	}
	
	@Override
	public void run()
	{
		while(container.readNextPacket(packet) >= 0)
	    {
	      /*
	       * Now we have a packet, let's see if it belongs to our video stream
	       */
	      if (packet.getStreamIndex() == videoStreamId)
	      {
	        /*
	         * We allocate a new picture to get the data out of Xuggler
	         */
	        IVideoPicture picture = IVideoPicture.make(videoCoder.getPixelType(),
	            videoCoder.getWidth(), videoCoder.getHeight());
	        
	        int offset = 0;
	        while(offset < packet.getSize())
	        {
	          /*
	           * Now, we decode the video, checking for any errors.
	           * 
	           */
	          int bytesDecoded = videoCoder.decodeVideo(picture, packet, offset);
	          if (bytesDecoded < 0)
	            throw new RuntimeException("got error decoding video");
	          offset += bytesDecoded;
	          
	          /*
	           * Some decoders will consume data in a packet, but will not be able to construct
	           * a full video picture yet.  Therefore you should always check if you
	           * got a complete picture from the decoder
	           */
	          if (picture.isComplete())
	          {
	            IVideoPicture newPic = picture;
	            /*
	             * If the resampler is not null, that means we didn't get the
	             * video in BGR24 format and
	             * need to convert it into BGR24 format.
	             */
	            if (resampler != null)
	            {
	              // we must resample
	              newPic = IVideoPicture.make(resampler.getOutputPixelFormat(),
	                  picture.getWidth(), picture.getHeight());
	              if (resampler.resample(newPic, picture) < 0)
	                throw new RuntimeException("could not resample video");
	            }
	            if (newPic.getPixelType() != IPixelFormat.Type.BGR24)
	              throw new RuntimeException("could not decode video as BGR 24 bit data");
	            
	         // And finally, convert the BGR24 to an Java buffered image
	            BufferedImage javaImage = Utils.videoPictureToImage(newPic);
	            buffer.put(javaImage);
	          }
	        }
	      }
	    }
		freeMemory();
	}
	
	public void freeMemory()
	{
		if (videoCoder != null)
	    {
	      videoCoder.close();
	      videoCoder = null;
	    }
	    if (container !=null)
	    {
	      container.close();
	      container = null;
	    }
	}
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
