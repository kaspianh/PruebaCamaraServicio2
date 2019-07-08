package com.example.pruebacamaraservicio2;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Environment;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ServicioCamera extends Service {

    private Camera.Size _previewSize;
    private final static String TAG = "CameraServiceTexture";

    public ServicioCamera() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new PictureThread().start();
        return START_STICKY;
    }

    private class PictureThread extends Thread {
        SurfaceTexture texture;
        private byte[] callbackBuffer;

        public void run() {

            System.out.println("Preparing to take photo");
            Camera camera = null;

            int cameraCount = 0;
            Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
            cameraCount = Camera.getNumberOfCameras();

            SystemClock.sleep(1000);

            Camera.getCameraInfo(1, cameraInfo);

            try {
                camera = Camera.open(1);
            } catch (RuntimeException e) {
                System.out.println("Camera not available: " + 1);
                camera = null;
                //e.printStackTrace();
            }

            try {

                if (null == camera) {
                    System.out.println("Could not get camera instance");
                } else {
                    System.out.println("Got the camera, creating the dummy surface texture");
                    //SurfaceTexture dummySurfaceTextureF = new SurfaceTexture(0);
                    try {
                        //camera.setPreviewTexture(dummySurfaceTextureF);
                        camera.setPreviewTexture(new SurfaceTexture(0));
                        Camera.Parameters parameters = camera.getParameters();
                        _previewSize = parameters.getPreviewSize();
                        camera.setPreviewCallbackWithBuffer(previewCallback);
                        camera.startPreview();
                    } catch (Exception e) {
                        System.out.println("Could not set the surface preview texture");
                        e.printStackTrace();
                    }
                }

            } catch (Exception e) {
                camera.release();
            }









            /*mCamera = CameraHelper.getCameraInstance();

            //texture = new SurfaceTexture(textures[0]);
            texture = new SurfaceTexture(10);
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


            time = System.currentTimeMillis();*/
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
            @Override
            public void onPreviewFrame(byte[] data, Camera camera) {
                long currentTime = System.currentTimeMillis();
                Log.i(TAG, "onPreviewFrame " + data.length + " " + (currentTime - time));
                time = currentTime;
                camera.addCallbackBuffer(callbackBuffer);


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
}
