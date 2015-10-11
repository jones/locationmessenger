package jain.locationmessenger;

import android.app.Activity;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;

import com.here.android.mpa.common.GeoCoordinate;
import com.here.android.mpa.common.GeoPosition;
import com.here.android.mpa.common.OnEngineInitListener;
import com.here.android.mpa.common.PositioningManager;
import com.here.android.mpa.mapping.Map;
import com.here.android.mpa.mapping.MapFragment;
import com.here.android.mpa.mapping.MapMarker;
import com.here.android.mpa.common.Image;



public class MainActivity extends Activity {

    // map embedded in the map fragment
    private Map map = null;

    // map fragment embedded in this activity
    private MapFragment mapFragment = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Load an image
        AssetManager assetManager = getApplicationContext().getAssets();
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeStream(assetManager.open("pin.png"));
        } catch (Exception e) {
            System.out.println("should handle this");
            bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            Paint paint = new Paint();
            paint.setColor(Color.GREEN);
            canvas.drawRect(0F, 0F, (float) 100, (float) 100, paint);
        }
        final Bitmap bitmap2 = bitmap;



        // Search for the map fragment to finish setup by calling init().
        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.mapfragment);
        mapFragment.init(new OnEngineInitListener() {
            @Override
            public void onEngineInitializationCompleted(OnEngineInitListener.Error error) {
                if (error == OnEngineInitListener.Error.NONE) {
                    // retrieve a reference of the map from the map fragment
                    map = mapFragment.getMap();

                    final Image foo = new Image();
                    foo.setBitmap(bitmap2);

                    PositioningManager positioningManager = PositioningManager.getInstance();
                    positioningManager.start(PositioningManager.LocationMethod.GPS_NETWORK);
                    GeoPosition geoPosition = positioningManager.getLastKnownPosition();
                    final GeoCoordinate geoCoordinate = geoPosition.getCoordinate();
                    double latitude = geoCoordinate.getLatitude();
                    double longitude = geoCoordinate.getLongitude();

                    // Add random mapmaker for yourself
                    MapMarker selfMapMarker = new MapMarker(geoCoordinate, foo);
                    //MapMarker selfMapMarker = new MapMarker();
                    //selfMapMarker.setCoordinate(geoCoordinate);
                    map.addMapObject(selfMapMarker);

                    //map.setZoomLevel(map.getMaxZoomLevel() + 2);
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
