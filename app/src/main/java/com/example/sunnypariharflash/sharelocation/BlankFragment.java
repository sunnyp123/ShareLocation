package com.example.sunnypariharflash.sharelocation;


import android.annotation.SuppressLint;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;


/**
 * A simple {@link Fragment} subclass.
 */
public class BlankFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener {

    public MapView mapView;
    private GoogleMap Map;
    TextView textView;
    boolean mLocationPermissionGranted = false;
Location mLastKnownLocation;
private GeoDataClient mGeoDataClient;
private FusedLocationProviderClient mFusedLocationProviderClient;
private PlaceDetectionClient mPlaceDetectionClient;
@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View vv = inflater.inflate(R.layout.fragment_blank, container, false);
        mapView = vv.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        textView = vv.findViewById(R.id.textsssssss);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) && ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION)) {

                } else {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION}, 1889);
                }
            } else {
                mLocationPermissionGranted = true;
                //Your permission has already been checked.
                Log.d("Data", "Your Location has been already granted");
            }
        }
        GoogleApiClient
    googleApiClient = new GoogleApiClient.Builder(getActivity())
            .addApi(LocationServices.API).addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .build();
    googleApiClient.connect();

    LocationRequest locationRequest  = LocationRequest.create();
    locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    locationRequest.setInterval(30 * 1000);
    locationRequest.setFastestInterval(5 * 1000);
    LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest);
builder.setAlwaysShow(true);
final PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient,  builder.build());
result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
    @Override
    public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
        final Status status = locationSettingsResult.getStatus();
        final LocationSettingsStates state  = locationSettingsResult.getLocationSettingsStates();
        switch  (status.getStatusCode()){
            case LocationSettingsStatusCodes.SUCCESS:
                //All Location Settings are satisfied.
                break;
            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                try{
status.startResolutionForResult(getActivity(),1000);
                }catch(Exception e){
                    e.printStackTrace();
                }
                break;
            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                //location settings are not satisfied.
                break;
        }
    }
});
        mGeoDataClient = Places.getGeoDataClient(getActivity(),null);
        mPlaceDetectionClient = Places.getPlaceDetectionClient(getActivity(),null);
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        mapView.onResume();
        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {

            e.printStackTrace();
        }
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                Map = googleMap;
                updateLocationUI();
                getDeviceLocation();

            }

        });
     return vv;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        if (requestCode == 1889) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.e("Map", "Permission");
                mLocationPermissionGranted = true;
            } else {
                Toast.makeText(getActivity(), "You have denied the permission.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void getDeviceLocation() {
try{
    if(mLocationPermissionGranted){
Task locationResult = mFusedLocationProviderClient.getLastLocation();
    locationResult.addOnCompleteListener(getActivity(), new OnCompleteListener() {
        @Override
        public void onComplete(@NonNull Task task) {
       if(task.isSuccessful()){
           mLastKnownLocation = (Location) task.getResult();
       Map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastKnownLocation.getLatitude(),mLastKnownLocation.getLongitude()),15));
           textView.setText("Last Updated a min ago at :"+mLastKnownLocation.getLatitude()+" and "+mLastKnownLocation.getLongitude() );

       }
       else{
           Log.d("Location","NULL here");
           Toast.makeText(getActivity(), "No data to be found", Toast.LENGTH_SHORT).show();
       }
        }
    });
    }
}catch(SecurityException e){
    e.printStackTrace();
}
    }

    @SuppressLint("MissingPermission")
    private void updateLocationUI() {
        if (Map == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                     return;
                }
                Map.setMyLocationEnabled(true);
                Map.getUiSettings().setMyLocationButtonEnabled(true);
            }
            else{
                Map.setMyLocationEnabled(false);
                Map.getUiSettings().setMyLocationButtonEnabled(false);
      mLastKnownLocation = null;
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
