package jain.locationmessenger;

import android.app.Activity;
import android.os.Bundle;

import com.here.android.mpa.common.GeoCoordinate;
import com.here.android.mpa.common.GeoPosition;
import com.here.android.mpa.common.OnEngineInitListener;
import com.here.android.mpa.common.PositioningManager;
import com.here.android.mpa.mapping.Map;
import com.here.android.mpa.mapping.MapFragment;

public class MainActivity extends Activity {

    // map embedded in the map fragment
    private Map map = null;

    // map fragment embedded in this activity
    private MapFragment mapFragment = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // Search for the map fragment to finish setup by calling init().
        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.mapfragment);
        mapFragment.init(new OnEngineInitListener() {
            @Override
            public void onEngineInitializationCompleted(OnEngineInitListener.Error error) {
                if (error == OnEngineInitListener.Error.NONE) {
                    // retrieve a reference of the map from the map fragment
                    map = mapFragment.getMap();

                    PositioningManager positioningManager = PositioningManager.getInstance();
                    positioningManager.start(PositioningManager.LocationMethod.GPS_NETWORK);
                    GeoPosition geoPosition = positioningManager.getLastKnownPosition();
                    final GeoCoordinate geoCoordinate = geoPosition.getCoordinate();
                    double latitude = geoCoordinate.getLatitude();
                    double longitude = geoCoordinate.getLongitude();

                    map.setZoomLevel(map.getMaxZoomLevel() + 2);
                    map.setCenter(geoCoordinate, Map.Animation.LINEAR);

                    // Set the map center coordinate to the Vancouver region (no animation)
                    map.setCenter(geoCoordinate, Map.Animation.LINEAR);
                    //map.setCenter(new GeoCoordinate(49.196261, -123.004773, 0.0), Map.Animation.NONE);
                    // Set the map zoom level to the average between min and max (no animation)
                    //map.setZoomLevel((map.getMaxZoomLevel() + map.getMinZoomLevel()) / );
                } else {
                    System.out.println("ERROR: Cannot initialize Map Fragment");
                }
            }
        });
    }
}