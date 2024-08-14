package com.example.harvestsphere;


import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.util.Log;

import org.tensorflow.lite.Interpreter;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.PriorityQueue;

public class Classifier {

    private Interpreter interpreter;
    private List<String> labelList;
    private int inputSize;
    private final int PIXEL_SIZE = 3;
    private final int IMAGE_MEAN = 0;
    private final float IMAGE_STD = 255.0f;
    private final int MAX_RESULTS = 3;
    private final float THRESHOLD = 0.4f;

    public static class Recognition {
        private String id = "";
        private String title = "";
        private float confidence = 0F;

        public Recognition(String id, String title, float confidence) {
            this.id = id;
            this.title = title;
            this.confidence = confidence;
        }

        public String getId() {
            return id;
        }

        public String getTitle() {
            return title;
        }

        public float getConfidence() {
            return confidence;
        }

        @Override
        public String toString() {
            return "Title = " + title + ", Confidence = " + confidence;
        }
    }

    public Classifier(AssetManager assetManager, String modelPath, String labelPath, int inputSize) throws IOException {
        this.inputSize = inputSize;
        interpreter = new Interpreter(loadModelFile(assetManager, modelPath));
        labelList = loadLabelList(assetManager, labelPath);
    }

    private MappedByteBuffer loadModelFile(AssetManager assetManager, String modelPath) throws IOException {
        FileInputStream inputStream = new FileInputStream(assetManager.openFd(modelPath).getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = assetManager.openFd(modelPath).getStartOffset();
        long declaredLength = assetManager.openFd(modelPath).getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    private List<String> loadLabelList(AssetManager assetManager, String labelPath) throws IOException {
        List<String> labels = new ArrayList<>();
        InputStream is = assetManager.open(labelPath);
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));

        String line;
        while ((line = reader.readLine()) != null) {
            labels.add(line);
        }
        reader.close();
        return labels;
    }

    public List<Recognition> recognizeImage(Bitmap bitmap) {
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, inputSize, inputSize, false);
        ByteBuffer byteBuffer = convertBitmapToByteBuffer(scaledBitmap);
        float[][] result = new float[1][labelList.size()];
        interpreter.run(byteBuffer, result);
        return getSortedResult(result);
    }

    private ByteBuffer convertBitmapToByteBuffer(Bitmap bitmap) {
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4 * inputSize * inputSize * PIXEL_SIZE);
        byteBuffer.order(ByteOrder.nativeOrder());
        int[] intValues = new int[inputSize * inputSize];
        bitmap.getPixels(intValues, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());

        int pixel = 0;
        for (int i = 0; i < inputSize; i++) {
            for (int j = 0; j < inputSize; j++) {
                int val = intValues[pixel++];
                byteBuffer.putFloat(((val >> 16) & 0xFF - IMAGE_MEAN) / IMAGE_STD);
                byteBuffer.putFloat(((val >> 8) & 0xFF - IMAGE_MEAN) / IMAGE_STD);
                byteBuffer.putFloat(((val) & 0xFF - IMAGE_MEAN) / IMAGE_STD);
            }
        }
        return byteBuffer;
    }

    private List<Recognition> getSortedResult(float[][] labelProbArray) {
        Log.d("Classifier", String.format(Locale.ENGLISH, "List Size:(%d, %d, %d)", labelProbArray.length, labelProbArray[0].length, labelList.size()));

        PriorityQueue<Recognition> pq = new PriorityQueue<>(
                MAX_RESULTS,
                new Comparator<Recognition>() {
                    @Override
                    public int compare(Recognition o1, Recognition o2) {
                        return Float.compare(o2.getConfidence(), o1.getConfidence());
                    }
                });

        for (int i = 0; i < labelList.size(); i++) {
            float confidence = labelProbArray[0][i];
            if (confidence >= THRESHOLD) {
                pq.add(new Recognition("" + i, labelList.size() > i ? labelList.get(i) : "Unknown", confidence));
            }
        }
        Log.d("Classifier", String.format(Locale.ENGLISH, "pqsize:(%d)", pq.size()));

        List<Recognition> recognitions = new ArrayList<>();
        int recognitionsSize = Math.min(pq.size(), MAX_RESULTS);
        for (int i = 0; i < recognitionsSize; i++) {
            recognitions.add(pq.poll());
        }
        return recognitions;
    }
}