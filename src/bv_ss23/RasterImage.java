// BV Ue6 SS2023 Vorgabe
//
// Copyright (C) 2023 by Klaus Jung
// All rights reserved.
// Date: 2023-03-23
 		   	  	  		

package bv_ss23;

import java.io.File;
import java.util.Arrays;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

public class RasterImage {
	 		   	  	  		
	private static final int gray  = 0xffa0a0a0;
	    
	public int[] argb;	// pixels represented as ARGB values in scanline order
	public int width;	// image width in pixels
	public int height;	// image height in pixels
	
	public RasterImage(int width, int height) {
		// creates an empty RasterImage of given size
		this.width = width;
		this.height = height;
		argb = new int[width * height];
		Arrays.fill(argb, gray);
	}
	
	public RasterImage(RasterImage src) {
		// copy constructor
		this.width = src.width;
		this.height = src.height;
		argb = src.argb.clone();
	}
	
	public RasterImage(File file) {
		// creates an RasterImage by reading the given file
		Image image = null;
		if(file != null && file.exists()) {
			image = new Image(file.toURI().toString());
		}
		if(image != null && image.getPixelReader() != null) {
			width = (int)image.getWidth();
			height = (int)image.getHeight();
			argb = new int[width * height];
			image.getPixelReader().getPixels(0, 0, width, height, PixelFormat.getIntArgbInstance(), argb, 0, width);
		} else {
			// file reading failed: create an empty RasterImage
			this.width = 256;
			this.height = 256;
			argb = new int[width * height];
			Arrays.fill(argb, gray);
		}
	}
	 		   	  	  		
	public RasterImage(ImageView imageView) {
		// creates a RasterImage from that what is shown in the given ImageView
		Image image = imageView.getImage();
		width = (int)image.getWidth();
		height = (int)image.getHeight();
		argb = new int[width * height];
		image.getPixelReader().getPixels(0, 0, width, height, PixelFormat.getIntArgbInstance(), argb, 0, width);
	}
	
	public void setToView(ImageView imageView) {
		// sets the current argb pixels to be shown in the given ImageView
		if(argb != null) {
			WritableImage wr = new WritableImage(width, height);
			PixelWriter pw = wr.getPixelWriter();
			pw.setPixels(0, 0, width, height, PixelFormat.getIntArgbInstance(), argb, 0, width);
			imageView.setImage(wr);
		}
	}
		
	// image point operations to be added here
	
	public void convertToGray() {
		
		for (int i = 0; i<argb.length; i++) {
			int pixel = argb[i];
			int red =(pixel>>16)&0xff;
			int green=(pixel >>8)&0xff;
			int blue= pixel& 0xff;
			int gray= (red+green+blue)/3;
			argb[i]=(0xff <<24)| (gray <<16)|(gray << 8)|gray;
		
	}
		}
	
	public double getEntropy() {
	    int[] histogram = new int[256];

	    // Calculate the histogram of gray values from errorImage
	    for (int pixel : argb) {
	        int grayValue = (pixel & 0xFF);
	        histogram[grayValue]++;
	    }

	    // Calculate the entropy
	    double entropy = 0.0;
	    int totalPixels = width * height;
	    for (int count : histogram) {
	        if (count != 0) {
	            double probability = (double) count / totalPixels;
	            entropy -= probability * Math.log(probability) / Math.log(2);
	        }
	    }

	    return entropy;
	}



}

 		   	  	  		
	   	  	  		




 		   	  	  		


