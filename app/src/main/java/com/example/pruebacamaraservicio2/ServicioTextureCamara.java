package com.example.pruebacamaraservicio2;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.opengl.GLES20;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ServicioTextureCamara extends Service {
    private final static String TAG = "CameraServiceTexture";
    private Camera mCamera;
    private Bitmap _currentFrame;
    public static final String FACEDETECTIONTHREAD_TAG = "FaceDetectionThread_Tag";

    @Override public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override public int onStartCommand(Intent intent, int flags, int startId) {
        new PictureThread().start();
        return START_STICKY;
    }



    private class PictureThread extends Thread {
        SurfaceTexture texture;
        private byte[] callbackBuffer;
        public void run() {
            mCamera = CameraHelper.getCameraInstance();

            /*int textures[] = new int[1];
            GLES20.glGenTextures(1, textures, 0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[0]);
            int width  = 4; // size of preview
            int height = 4;  // size of preview
            GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, width,
                    height, 0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);*/

            //texture = new SurfaceTexture(textures[0]);
            texture = new SurfaceTexture(0);
            texture.setDefaultBufferSize(4, 4);

            try {
                mCamera.setPreviewTexture(texture);
            } catch (IOException e1) {
                e1.printStackTrace();
            }

            Camera.Size previewSize=mCamera.getParameters().getPreviewSize();
            int dataBufferSize=(int)(previewSize.height*previewSize.width*
                    (ImageFormat.getBitsPerPixel(mCamera.getParameters().getPreviewFormat())/8.0));
            callbackBuffer = new byte[dataBufferSize];

            mCamera.addCallbackBuffer(callbackBuffer);
            //mCamera.setPreviewCallback(previewCallback);
            mCamera.setPreviewCallbackWithBuffer(previewCallback);

            mCamera.startPreview();


            time = System.currentTimeMillis();
        }

        private int mMaxCount = 100;
        private int mCount = 0;
        private long time;

        /*private final Camera.PictureCallback jpegCallBack = new Camera.PictureCallback() {
            @Override public void onPictureTaken(byte[] data, Camera camera) {
                long currentTime = System.currentTimeMillis();
                Log.i(TAG, "onPictureTaken " + data.length + " " + (currentTime - time));
                time = currentTime;
                texture.updateTexImage();
                mCamera.startPreview();
                ++mCount;
                if (mCount < mMaxCount) {
                    mCamera.takePicture(null, null, jpegCallBack);
                }
            }
        };*/

        private final Camera.PreviewCallback previewCallback = new Camera.PreviewCallback() {
            @Override public void onPreviewFrame(byte[] data, Camera camera) {
                long currentTime = System.currentTimeMillis();
                Log.i(TAG, "onPreviewFrame " + data.length + " " + (currentTime - time));
                time = currentTime;
                mCamera.addCallbackBuffer(callbackBuffer);

                /*Bitmap bitmap = BitmapFactory.decodeByteArray(data , 0, data.length);
                _currentFrame = bitmap;

                // Rotate the so it siuts our portrait mode
                Matrix matrix = new Matrix();
                matrix.postRotate(90);
                matrix.preScale(-1, 1);
                // We rotate the same Bitmap
                _currentFrame = Bitmap.createBitmap(_currentFrame, 0, 0,
                        bitmap.getWidth(), bitmap.getHeight(), matrix, false);

                _currentFrame = convert(_currentFrame, Bitmap.Config.RGB_565);

                _currentFrame = getResizedBitmap(_currentFrame, 640, 480);

                if (_currentFrame == null) {
                    Log.e(FACEDETECTIONTHREAD_TAG, "Could not decode Image");
                    return;
                }*/



                File pictureFileDir = getDir(Environment.DIRECTORY_PICTURES, Context.MODE_PRIVATE);
                if (!pictureFileDir.exists() && !pictureFileDir.mkdirs()) {
                    return;
                }
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyymmddhhmmss");
                String date = dateFormat.format(new Date());
                String photoFile = "PictureFront_" + "_" + date + ".jpg";
                String filename = pictureFileDir.getPath() + File.separator + photoFile;
                File mainPicture = new File(filename);
                //addImageFile(mainPicture);

                try {
                    FileOutputStream fos = new FileOutputStream(mainPicture);
                    fos.write(data);
                    fos.close();
                    System.out.println("image saved");
                } catch (Exception error) {
                    System.out.println("Image could not be saved");
                }
            }
        };

    }

    private Bitmap convert(Bitmap bitmap, Bitmap.Config config) {
        Bitmap convertedBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), config);
        Canvas canvas = new Canvas(convertedBitmap);
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        canvas.drawBitmap(bitmap, 0, 0, paint);
        return convertedBitmap;
    }

    public static Bitmap getResizedBitmap(Bitmap image, int newHeight, int newWidth) {
        int width = image.getWidth();
        int height = image.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // create a matrix for the manipulation
        Matrix matrix = new Matrix();
        // resize the bit map
        matrix.postScale(scaleWidth, scaleHeight);
        // recreate the new Bitmap
        Bitmap resizedBitmap = Bitmap.createBitmap(image, 0, 0, width, height,
                matrix, false);
        return resizedBitmap;
    }
}
