package com.example.pruebacamaraservicio2;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    int MY_PERMISSIONS_REQUEST_CAMERA=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
        {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA))
            {
                iniciar();
            }
            else
            {
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST_CAMERA );
                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an-
                // app-defined int constant. The callback method gets the
                // result of the request.
                iniciar();
            }
        } else {
            iniciar();
        }



    }

    private void iniciar() {
        //Intent service = new Intent(this, ServicioCamera.class);
        Intent service = new Intent(this, ServicioTextureCamara.class);
        startService(service);
    }
}