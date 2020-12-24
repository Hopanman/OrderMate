package hopanman.android.ordermate;

import android.Manifest;
import android.content.Context;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.geometry.LatLngBounds;
import com.naver.maps.map.CameraAnimation;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.MapView;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.UiSettings;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.OverlayImage;
import com.naver.maps.map.util.FusedLocationSource;

import java.util.ArrayList;
import java.util.List;


public class CustomerMainFragment extends Fragment implements OnMapReadyCallback {

    private MapView mapView;
    private NaverMap naverMap;
    private FusedLocationSource locationSource;
    private final int REQUEST_LOCATION_CODE = 101;
    private final int REQUEST_LOCATION_SETTING_CODE = 102;
    private boolean isSpecified = false;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Handler handler = new Handler(Looper.getMainLooper());

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.fragment_customer_main, container, false);

        mapView = rootView.findViewById(R.id.customer_main_map_view);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        locationSource = new FusedLocationSource(this, REQUEST_LOCATION_CODE);
        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_LOCATION_CODE);

        FloatingActionButton locationButton = rootView.findViewById(R.id.customer_main_my_location_button);
        locationButton.setOnClickListener(v -> {
            boolean hasLocationPermission = (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                    && (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED);

            if (!hasLocationPermission) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_LOCATION_CODE);
                return;
            }

            LocationManager locationManager = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);
            boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            boolean isNWProviderEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (isGPSEnabled || isNWProviderEnabled) {
                Location location = null;
                if (isGPSEnabled) location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                else location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                naverMap.moveCamera(CameraUpdate.scrollTo(new LatLng(location.getLatitude(), location.getLongitude()))
                        .animate(CameraAnimation.Linear));
            } else {
                LocationRequest locationRequest = LocationRequest.create();
                LocationSettingsRequest locationSettingsRequest = new LocationSettingsRequest.Builder()
                        .addLocationRequest(locationRequest)
                        .setAlwaysShow(true).build();
                LocationServices.getSettingsClient(getActivity())
                        .checkLocationSettings(locationSettingsRequest)
                        .addOnFailureListener(e -> {
                            ResolvableApiException rae = (ResolvableApiException)e;

                            switch (rae.getStatusCode()) {
                                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                    try {
                                        rae.startResolutionForResult(getActivity(), REQUEST_LOCATION_SETTING_CODE);
                                    } catch (IntentSender.SendIntentException sendIntentException) {
                                        sendIntentException.printStackTrace();
                                    }
                            }
                        });
            }
        });

        return rootView;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (locationSource.onRequestPermissionsResult(requestCode, permissions, grantResults)) {
            if (locationSource.isActivated()) {
                naverMap.setLocationTrackingMode(LocationTrackingMode.NoFollow);
            } else {
                naverMap.setLocationTrackingMode(LocationTrackingMode.None);
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mapView.onDestroy();
    }

    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        this.naverMap = naverMap;

        naverMap.setMinZoom(15);
        naverMap.setMaxZoom(19.0);
        naverMap.setExtent(new LatLngBounds(new LatLng(31.94, 123.74), new LatLng(38.82, 131.87)));
        naverMap.setLocationSource(locationSource);
        naverMap.addOnLocationChangeListener(location -> {
            if (!isSpecified) {
                naverMap.moveCamera(CameraUpdate.scrollAndZoomTo(new LatLng(location.getLatitude(), location.getLongitude()), 15)
                .animate(CameraAnimation.Linear));
                isSpecified = true;
            }
        });

        UiSettings uiSettings = naverMap.getUiSettings();
        uiSettings.setScaleBarEnabled(false);
        uiSettings.setZoomControlEnabled(false);
        uiSettings.setLogoGravity(Gravity.TOP|Gravity.RIGHT);
        uiSettings.setTiltGesturesEnabled(false);
        uiSettings.setRotateGesturesEnabled(false);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        float density = displayMetrics.density;
        uiSettings.setLogoMargin(0, (int)(10 * density), (int)(10 * density), 0);

        setMarker();
    }

    private void setMarker() {
        new Thread(() -> {
            db.collection("stores")
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            List<Marker> markers = new ArrayList<>();

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (document.get("storeCoordinates") != null) {
                                    GeoPoint geoPoint = (GeoPoint) document.get("storeCoordinates");
                                    Marker marker = new Marker();
                                    marker.setIcon(OverlayImage.fromResource(R.drawable.ic_marker_store_closed));
                                    marker.setPosition(new LatLng(geoPoint.getLatitude(), geoPoint.getLongitude()));
                                    markers.add(marker);
                                }
                            }

                            handler.post(() -> {
                                for (Marker marker : markers) marker.setMap(naverMap);
                            });
                        } else {
                            handler.post(() -> {
                                Toast.makeText(getContext(), "가게정보를 불러오는데 실패하였습니다.", Toast.LENGTH_LONG).show();
                            });
                        }
                    });
        }).start();
    }
}