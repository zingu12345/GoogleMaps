package com.rstintl.docta.deliveryApp.Activities;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;

import com.cs.googlemaproute.DrawRoute;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rstintl.docta.deliveryApp.Models.DeliveryBoyModel;
import com.rstintl.docta.deliveryApp.R;
import com.google.android.gms.location.LocationListener;

import android.location.LocationManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.rstintl.docta.deliveryApp.R.id.map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener, GoogleMap.OnMarkerClickListener, DrawRoute.onDrawRoute{

    private GoogleMap mMap;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    LocationRequest mLocationRequest;
    SharedPreferences sharedPreferences;
    MarkerOptions markerOptions, markerOptions2;
    List<DeliveryBoyModel> deliveryBoys = new ArrayList<>();
    String date;
    double lat1, lang1, lat2, lang2;
    TextView tvTimeDistance;
    String finalDuration, finalDistance;
    DatabaseReference myRef;
    FirebaseDatabase database;
    FloatingActionButton fab;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }
         database = FirebaseDatabase.getInstance();
         myRef = database.getReference("location");
        fab = (FloatingActionButton)findViewById(R.id.fab);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if(getIntent().getExtras() != null){
            lat1 = getIntent().getDoubleExtra("lat1",0.0);
            lat2 = getIntent().getDoubleExtra("lat2",0.0);
            lang1 = getIntent().getDoubleExtra("lang1",0.0);
            lang2= getIntent().getDoubleExtra("lang2",0.0);
        }
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(map);
        mapFragment.getMapAsync(this);
        tvTimeDistance = (TextView)findViewById(R.id.tv_time_distance);

    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this,R.raw.style_map));
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
                mMap.setOnMarkerClickListener(this);


            }
        }
        else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }
        /*DeliveryBoyModel deliveryBoyModel = new DeliveryBoyModel("Ashish","Assigned",28.4540583,77.0937073);
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
            Marker marker = mMap.addMarker(markerOptions);
            marker.setTag(i);
            marker.setDraggable(true);
        }*/
        LatLng latLngMarker = new LatLng(lat1, lang1);
        markerOptions = new MarkerOptions();
        markerOptions.position(latLngMarker);
        markerOptions.title("Delivery Boy");
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.delivery_boy_final));
        mMap.addMarker(markerOptions);

        LatLng latLngMarker2 = new LatLng(lat2, lang2);
        markerOptions2 = new MarkerOptions();
        markerOptions2.position(latLngMarker2);
        markerOptions2.title("Drop Location");
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.location));
        mMap.addMarker(markerOptions2);
        DrawRoute.getInstance(this,MapsActivity.this).setFromLatLong(lat1,lang1)
                .setToLatLong(lat2,lang2).setGmapAndKey("AIzaSyAXJL08SLtzX1hWhi_hTeBVsUQT2f49F1s",mMap).setZoomLevel((float) 10.0).run();

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
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            Log.d("Hi","Started");
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(final Location location) {
        Toast.makeText(getApplicationContext(), location.getLatitude()+" "+ location.getLongitude(),Toast.LENGTH_SHORT).show();
        Log.d("latlang", String.valueOf(location.getLatitude() + location.getLongitude()));
        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }
        myRef.child("6a3f628a3cdf0504").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                   myRef.child("user").child("6a3f628a3cdf0504").child("lat").setValue(location.getLatitude());
                    myRef.child("user").child("6a3f628a3cdf0504").child("lang").setValue(location.getLongitude());
                    // TODO: handle the case where the data already exists
                }
                else {
                    writeNewUser("Ashish Gupta", "Assigned", location.getLatitude(), location.getLongitude());
                    // TODO: handle the case where the data does not yet exist
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });

        Log.d("Location",location.getLatitude()+" "+ location.getLongitude());
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(lat1, lang1)));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(17));
            }
        });

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(new LatLng(lat1,lang1));
        builder.include(new LatLng(lat2,lang2));
        LatLngBounds bounds = builder.build();

        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds,30);
        mMap.animateCamera(cu, new GoogleMap.CancelableCallback(){
            public void onCancel(){}
            public void onFinish(){
                CameraUpdate zout = CameraUpdateFactory.zoomBy((float) -0.0);
                mMap.animateCamera(zout);
            }
        });
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }

    }
    private void writeNewUser(String name, String status, double lat, double lang) {
        DeliveryBoyModel deliveryBoy = new DeliveryBoyModel(name, status,lat,lang);

        myRef.child("users").child(Settings.Secure.getString(getApplicationContext().getContentResolver(),
                Settings.Secure.ANDROID_ID)).setValue(deliveryBoy);
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

                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        /*Intent intent = new Intent(getApplicationContext(),MainActivity.class);
        intent.putExtra("assigned_to",deliveryBoys.get(tag).getName());
        startActivity(intent);*/

        return false;
    }

    @Override
    public void afterDraw(String result) {
            Log.d("Result", result);

        try {
            JSONObject jsonObject = new JSONObject(result);
            JSONArray routes = jsonObject.getJSONArray("routes");
            JSONArray legs = routes.getJSONObject(0).getJSONArray("legs");
            JSONObject duration = legs.getJSONObject(0).getJSONObject("duration");
            finalDuration = duration.getString("text");
            JSONObject distance = legs.getJSONObject(0).getJSONObject("distance");
            finalDistance = distance.getString("text");
            /*JSONObject duration = legs.getJSONObject(2);
            String mDuration = duration.getString("text");*/
            Log.d("Duration", finalDuration);
            tvTimeDistance.setText("("+ finalDistance + ") " + finalDuration);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



}