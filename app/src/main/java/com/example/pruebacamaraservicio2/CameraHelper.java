package com.example.pruebacamaraservicio2;


import android.graphics.ImageFormat;
import android.hardware.Camera;


import java.util.List;

public class CameraHelper {
    private final static String TAG = "CameraHelper";

    private static Camera sCamera;
    private static Camera.Parameters sParameters;
    public synchronized static Camera getCameraInstance(){
        if (sCamera == null) {
            try {
                sCamera = Camera.open(1); // attempt to get a Camera instance
            }
            catch (Exception e) {}
        }

        if (sCamera != null) {
            sParameters = sCamera.getParameters();
            getFormats();
        }
        return sCamera; // returns null if camera is unavailable
    }

    public static void getFormats() {
        List<Camera.Size> picSizes = sParameters.getSupportedPictureSizes();
        Camera.Size s = picSizes.get(1);
        sParameters.setPictureSize(s.width, s.height);
        picSizes = sParameters.getSupportedPreviewSizes();
        Camera.Size previewSize = picSizes.get(picSizes.size() - 1);
        sParameters.setPreviewSize(previewSize.width, previewSize.width);
        sParameters.setPreviewFormat(ImageFormat.JPEG);
    }

    public synchronized static void releaseCamera() {
        if (sCamera != null) {
            sCamera.stopPreview();
            sCamera.release();        // release the camera for other applications
            sCamera = null;
        }
    }
}
