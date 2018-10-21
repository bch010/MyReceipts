package au.edu.usc.myreceipts.android.myreceipts;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MyReceiptsMapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String EXTRA_LATLNG = "au.edu.usc.myreceipts.android.myreceipts_latlng";
    private GoogleMap mGoogleMap;
    private LatLng extraLatLng;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_map);
        extraLatLng = getIntent().getParcelableExtra(EXTRA_LATLNG);
        initMap();
    }

    private void moveCamera(LatLng latLng) {
        final MarkerOptions markerOptions = new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_icon));

        markerOptions.position(latLng);
        mGoogleMap.addMarker(markerOptions);
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(17.0f), 1000, null);

        if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling

            return;
        }
    }

    private void initMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(MyReceiptsMapActivity.this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mGoogleMap = googleMap;
        LatLng latLng = extraLatLng;
        moveCamera(latLng);
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
        googleMap.addMarker(new MarkerOptions()
                .position(latLng));
    }

    public static Intent newIntent(Context context, LatLng latLng) {

        Intent intent = new Intent(context, MyReceiptsMapActivity.class);
        intent.putExtra(EXTRA_LATLNG, latLng);
        return intent;
    }
}