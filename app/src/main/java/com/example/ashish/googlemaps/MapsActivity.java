package com.example.ashish.googlemaps;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;

import com.google.android.gms.location.LocationListener;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import static com.example.ashish.googlemaps.R.id.map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener, GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    LocationRequest mLocationRequest;
    MarkerOptions markerOptions, markerOptions2, markerOptions3;
    List<DeliveryBoyModel> deliveryBoys = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow(); // in Activity's onCreate() for instance
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        /*boolean success = googleMap.setMapStyle(new MapStyleOptions(getResources()
                .getString(R.string.map_style_json)));
        mMap = googleMap;
        if (!success) {
            Log.e("Style", "Style parsing failed.");
        }*/

        // Add a marker in Sydney and move the camera
        mMap = googleMap;
        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this,R.raw.style_map));
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);


            }
        }
        else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }
        DeliveryBoyModel deliveryBoyModel = new DeliveryBoyModel("Ashish","Assigned",28.4540583,77.0937073);
        DeliveryBoyModel deliveryBoyModel1 = new DeliveryBoyModel("Vinay","Not Assigned",28.4941690,77.6427130);
        DeliveryBoyModel deliveryBoyModel2 = new DeliveryBoyModel("Deepak","Not Assigned",28.4342510,77.3237180);
        DeliveryBoyModel deliveryBoyModel3 = new DeliveryBoyModel("Akash","Out for delivery",28.4043182,77.9747654);
        DeliveryBoyModel deliveryBoyModel4 = new DeliveryBoyModel("Amit","Assigned",28.4744784,77.3057055);
        deliveryBoys.add(deliveryBoyModel);
        deliveryBoys.add(deliveryBoyModel1);
        deliveryBoys.add(deliveryBoyModel2);
        deliveryBoys.add(deliveryBoyModel3);
        deliveryBoys.add(deliveryBoyModel4);
        for(int i=0; i<deliveryBoys.size();i++){
            LatLng latLngMarker = new LatLng(deliveryBoys.get(i).getLat(), deliveryBoys.get(i).getLang());
            markerOptions = new MarkerOptions();
            markerOptions.position(latLngMarker);
            markerOptions.title(deliveryBoys.get(i).getName()+"(Status:"+ deliveryBoys.get(i).getStatus()+ ")");
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.delivery_boy_final));
            mMap.addMarker(markerOptions);
        }
        /*LatLng latLngMarker = new LatLng(28.4440583, 77.0467073);
         markerOptions = new MarkerOptions();
        markerOptions.position(latLngMarker);
        markerOptions.title("Amit Kumar(Status: Assigned)");
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.delivery_boy_final));
        mMap.addMarker(markerOptions);
        final LatLng latLngMarker2 = new LatLng(28.4460579, 77.0513358);
         markerOptions2 = new MarkerOptions();
        markerOptions2.position(latLngMarker2);
        markerOptions2.title("Vinay Gupta(Status: Out for delivery)");
        markerOptions2.icon(BitmapDescriptorFactory.fromResource(R.mipmap.delivery_boy_final));
        mMap.addMarker(markerOptions2);
        final LatLng latLngMarker3 = new LatLng(28.4436641, 77.0417706);
         markerOptions3 = new MarkerOptions();
        markerOptions3.position(latLngMarker3);
        markerOptions3.title("Ashish Gupta(Status: Not Assigned)");
        markerOptions3.icon(BitmapDescriptorFactory.fromResource(R.mipmap.delivery_boy_final));
        mMap.addMarker(markerOptions3);*/
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }


    @Override
    public void onConnected(Bundle bundle) {

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {

        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }

        //Place current location marker
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

        //mCurrLocationMarker = mMap.addMarker(markerOptions);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));

        //stop location updates
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }

    }




    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    public boolean checkLocationPermission(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted. Do the
                    // contacts-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }

                } else {

                    // Permission denied, Disable the functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other permissions this app might request.
            // You can add here other case statements according to your requirement.
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if(marker.equals(markerOptions)){
            Toast.makeText(getApplicationContext(),"Marker1",Toast.LENGTH_SHORT).show();
        }
        else if(marker.equals(markerOptions2)){
            Toast.makeText(getApplicationContext(),"Marker2",Toast.LENGTH_SHORT).show();
        }
        return true;
    }
}
