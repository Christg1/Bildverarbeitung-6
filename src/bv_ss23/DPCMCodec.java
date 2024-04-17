// BV Ue6 SS2023 Vorgabe
//
// Copyright (C) 2023 by Klaus Jung
// All rights reserved.
// Date: 2023-03-23
 		   	  	  		

package bv_ss23;


public class DPCMCodec {
    
    public enum PredictionType {
        A("A (horizontal)"),
        B("B (vertical)"),
        C("C (diagonal)"),
        ABC("A+B-C"),
        AB_MEAN("(A+B)/2"),
        ADAPTIVE("adaptive");

        private final String name;

        private PredictionType(String s) {
            name = s;
        }

        public String toString() {
            return this.name;
        }
    }

    public void processDPCM(RasterImage originalImage, RasterImage errorImage, RasterImage reconstructedImage, double quantizationDelta, PredictionType type) {
        // Encode the originalImage with DPCM using the given prediction type
        int width = originalImage.width;
        int height = originalImage.height;

        // Initialize the error image and reconstructed image
        for (int i = 0; i < width * height; i++) {
            errorImage.argb[i] = 0;
            reconstructedImage.argb[i] = 0;
        }

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixelIndex = y * width + x;

                // Get the neighboring pixel values A, B, C
                int A = (x > 0) ? reconstructedImage.argb[pixelIndex - 1] & 0xFF : 128;
                int B = (y > 0) ? reconstructedImage.argb[pixelIndex - width] & 0xFF : 128;
                int C = ((x > 0) && (y > 0)) ? originalImage.argb[pixelIndex - width - 1] & 0xFF : 128;

                // Calculate the prediction based on the specified type
                int P = 0;
                if (type == PredictionType.A) {
                    P = A;
                } else if (type == PredictionType.B) {
                    P = B;
                } else if (type == PredictionType.C) {
                    P = C;
                } else if (type == PredictionType.ABC) {
                    P = A + B - C;
                } else if (type == PredictionType.AB_MEAN) {
                    P = (A + B) / 2;
                } else if (type == PredictionType.ADAPTIVE) {
                    P = (Math.abs(A - C) < Math.abs(B - C)) ? B : A;
                }

                // Calculate the prediction error
                int originalPixel = originalImage.argb[pixelIndex] & 0xFF;
                int predictionError = originalPixel - P;

                // Adjust the quantization process
                if (quantizationDelta > 0) {
                    predictionError = (int) Math.round(predictionError / quantizationDelta) * (int) quantizationDelta;
                    predictionError = Math.min(255, Math.max(0, predictionError)); // Clamp the value between 0 and 255
                }

                // Visualize the prediction error in the error image (add 128 to make it visible)
                errorImage.argb[pixelIndex] = (255 << 24) | ((predictionError + 128) << 16) | ((predictionError + 128) << 8) | (predictionError + 128);

               
             // Decode the prediction error into the reconstructed image with clamping
                int reconstructedPixel = P + predictionError;
                reconstructedPixel = Math.min(255, Math.max(0, reconstructedPixel)); // Clamp the value between 0 and 255
                reconstructedImage.argb[pixelIndex] = (255 << 24) | (reconstructedPixel << 16) | (reconstructedPixel << 8) | reconstructedPixel;

            }
        }
    }

    

    public double getMSE(RasterImage originalImage, RasterImage reconstructedImage) {
        int width = originalImage.width;
        int height = originalImage.height;

        double sumSquaredError = 0.0;

        for (int i = 0; i < width * height; i++) {
            int originalPixel = originalImage.argb[i] & 0xFF;
            int reconstructedPixel = reconstructedImage.argb[i] & 0xFF;

            double error = originalPixel - reconstructedPixel;
            sumSquaredError += error * error;
        }

        return sumSquaredError / (width * height);
    }
}