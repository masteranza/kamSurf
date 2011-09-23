/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mite.test;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;


import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import kamSurf.IDescriptor;
import kamSurf.IDetector;
import kamSurf.ISurf;
import kamSurf.InterestPoint;
import kamSurf.Surf;

import video.ImageBuffer;
import video.VideoStreamReader;

/**
 * 
 * @author mite
 */
public class Test {

	
	static float threshold = 200;
	static float balanceValue = (float) 0.9;
	static int octaves = 5;
	static ArrayList interest_points;
	static ArrayList interest_points2;
	static String mainPath = "/Users/masteranza/Documents/Engineer/";
	public static void main(String argv[]) {


		
		BufferedImage img;
		BufferedImage img2;
		try {
			img = ImageIO.read(new File(mainPath + "kamSurf/logo.jpg"));
			ISurf mySURF = new Surf(img, balanceValue, threshold, octaves); 
					//Surf.createInstance(img, balanceValue, threshold, octaves, img);
			IDetector detector = mySURF.createDetector();
			interest_points = detector.generateInterestPoints();
			IDescriptor descriptor = mySURF
					.createDescriptor(interest_points);
			descriptor.generateAllDescriptors();
			
			//drawInterestPoints(interest_points);
			//drawDescriptors(interest_points);
		
			img2 = ImageIO.read(new File(mainPath + "kamSurf/test.jpg"));
			ISurf mySURF2 = new Surf(img2, balanceValue, threshold, octaves);
			IDetector detector2 = mySURF2.createDetector();
			interest_points2 = detector2.generateInterestPoints();
			IDescriptor descriptor2 = mySURF2.createDescriptor(interest_points2);
			descriptor2.generateAllDescriptors();
			
			//drawInterestPoints(interest_points2);
			//drawDescriptors(interest_points2);

		compareInterestPointsAndDrawThem();
		
			File out =new File(mainPath + "kamSurf/logo-o.jpg");
			ImageIO.write(img, "PNG", out);
			JFrame frame = new JFrame();
			frame.setDefaultCloseOperation(1);
			JLabel label = new JLabel(new ImageIcon(img));
			frame.getContentPane().add(label, BorderLayout.CENTER);
			frame.pack();
			frame.setVisible(true);
		
        File out2 =new File(mainPath + "kamSurf/test-o.jpg");
		ImageIO.write(img2, "PNG", out2);
		JFrame frame2 = new JFrame();
        frame2.setDefaultCloseOperation(1);
        JLabel label2 = new JLabel(new ImageIcon(img2));
        frame2.getContentPane().add(label2, BorderLayout.CENTER);
        frame2.pack();
        frame2.setVisible(true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	static void compareInterestPointsAndDrawThem()
	{
		
		for (int i=0; i<interest_points.size(); i++)
		{
			InterestPoint P = (InterestPoint) interest_points.get(i);
			InterestPoint Q = NearestNeighbour(P, interest_points2);
			if (Q==null) return;
			InterestPoint P2 = NearestNeighbour(Q, interest_points);
			if (P2==null) return;
			
			if (P==P2) 
			{
				float[] s =P.getDescriptorOfTheInterestPoint();
				float[] c =P2.getDescriptorOfTheInterestPoint();
				float sum =0;
				for (int k=0; k<c.length; k++)
				{
					sum += (c[k]-s[k])*(c[k]-s[k]);
				}
				//if (sum<100){
					P.drawPosition(5, new Color(0, 200, 0), String.valueOf(i));
					Q.drawPosition(5, new Color(0, 0, 200), String.valueOf(i) );
					
				//}
			}
			
		
		}
		
	}
	static InterestPoint NearestNeighbour(InterestPoint IP, ArrayList interest_points2)
	{
		float minValue=1000000000;
		int minIndex=-1;
		
		float[] s =IP.getDescriptorOfTheInterestPoint();
		for (int j=0; j<interest_points2.size(); j++)
		{
			InterestPoint IP2 = (InterestPoint) interest_points2.get(j);
			float[] c =IP2.getDescriptorOfTheInterestPoint();
			float sum =0;
			
			for (int k=0; k<c.length; k++)
			{
				sum += (c[k]-s[k])*(c[k]-s[k]);
			}
			
			if (sum<minValue){
				minValue = sum;
				minIndex = j;
			}
		}
		
		return minIndex==-1?null:(InterestPoint)interest_points2.get(minIndex); 
	}
	
//	public static void main(String argv[]) {
//
//		BufferedImage img;
//		long start = System.currentTimeMillis();
//		int j=0;
//		System.out.println("Going for the first frame...");
//		ImageBuffer ib = new ImageBuffer();
//		try {
//			VideoStreamReader vsr = new VideoStreamReader(argv[0], ib);
//		} catch (Exception ex) {
//
//			ex.printStackTrace();
//		}
//		img = ib.get();
//		do {
//				//Thread.sleep(1000L);
//				j++;
//				// img = ImageIO.read(file);
//				// BufferedImage parent = img;				
//				ISurf mySURF = Surf.createInstance(img, balanceValue,
//						threshold, octaves, img);
//				IDetector detector = mySURF.createDetector();
//				interest_points = detector.generateInterestPoints();
//				IDescriptor descriptor = mySURF
//						.createDescriptor(interest_points);
//				descriptor.generateAllDescriptors();
//
//			drawInterestPoints();
//			drawDescriptors();
//			
//			 File out =new File("/Users/masteranza/Pictures/test/to"+j+".png");
//			  
//			  try { ImageIO.write(img, "PNG", out); } 
//			  catch (IOException ex) 
//			  {
//				  Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex); 
//			  }
//			  long end = System.currentTimeMillis();
//			  System.out.println("Execution time was "+(end-start)+" ms.");
//			  System.out.println("Analyzed " + j + "frames.");
//			  img = ib.get();
//		} 
//		while( img!=null );
//
//		JFrame frame = new JFrame();
//        frame.setDefaultCloseOperation(1);
//        JLabel label = new JLabel(new ImageIcon(img));
//        frame.getContentPane().add(label, BorderLayout.CENTER);
//        frame.pack();
//        frame.setVisible(true);
//		
//		 
//	}

	static void drawInterestPoints(ArrayList interest_points) {

		System.out.println("Drawing Interest Points...");

		for (int i = 0; i < interest_points.size(); i++) {

			InterestPoint IP = (InterestPoint) interest_points.get(i);
			IP.drawPosition(5, new Color(200, 200, 200), "");

		}

	}

	static void drawDescriptors(ArrayList interest_points) {

		System.out.println("Drawing Descriptors...");

		for (int i = 0; i < interest_points.size(); i++) {

			InterestPoint IP = (InterestPoint) interest_points.get(i);
			IP.drawDescriptor(new Color(255, 0, 0));

		}

	}
}
