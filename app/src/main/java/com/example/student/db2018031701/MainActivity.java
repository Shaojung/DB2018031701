package com.example.student.db2018031701;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    LocationManager lm;
    Location currentLoc;
    RequestQueue queue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        queue = Volley.newRequestQueue(MainActivity.this);
        lm = (LocationManager) getSystemService(LOCATION_SERVICE);

    }

    public void click1(View v) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Log.d("LOC", "沒有權限");
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 123);

            return;
        }
        Log.d("LOC", "有權限");
        turnOnGPS();
    }

    private void turnOnGPS()
    {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new MyListener());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 123) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("LOC", "剛剛得到權限");
                turnOnGPS();
            }
        }
    }
    public void click2(View v)
    {
        String str = "https://maps.googleapis.com/maps/api/directions/json?origin=" + currentLoc.getLatitude() + "," + currentLoc.getLongitude() + "&destination=Taipei101&key=AIzaSyB03rADXr_cx1UI3qvEe6BVR6Vda4XxQs0";
        Log.d("LOC", str);
        StringRequest request = new StringRequest(str, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("LOC", response);
                try {
                    JSONObject obj = new JSONObject(response);
                    JSONArray routes = obj.getJSONArray("routes");
                    JSONObject obj1 = routes.getJSONObject(0);
                    JSONArray legs = obj1.getJSONArray("legs");
                    JSONObject obj2 = legs.getJSONObject(0);
                    JSONObject distance = obj2.getJSONObject("distance");
                    String str = distance.getString("text");
                    Log.d("LOC", "distance:" + str);



                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        queue.add(request);
        queue.start();

    }

    class MyListener implements LocationListener
    {

        @Override
        public void onLocationChanged(Location location) {
            currentLoc = location;
            Log.d("LOC", "Change!!");
            Log.d("LOC", "" + location.getLatitude() + "," + location.getLongitude());
            Location loc101 = new Location("MyLoc");
            loc101.setLatitude(25.0339639);
            loc101.setLongitude(121.5644722);
            double dist = loc101.distanceTo(location);
            Log.d("LOC", "Distance to Taipei 101 = " + dist);

            Geocoder geocoder = new Geocoder(MainActivity.this);
            try {
                List<Address> list = geocoder.getFromLocation(25.0339, 121.5644, 1);
                Address addr = list.get(0);
                Log.d("LOC", addr.getAddressLine(0));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    }
}
