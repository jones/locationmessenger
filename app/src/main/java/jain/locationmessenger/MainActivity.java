package jain.locationmessenger;

import android.app.Activity;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.os.Bundle;
import android.widget.Toast;

import com.here.android.mpa.common.GeoCoordinate;
import com.here.android.mpa.common.GeoPosition;
import com.here.android.mpa.common.OnEngineInitListener;
import com.here.android.mpa.common.PositioningManager;
import com.here.android.mpa.common.ViewObject;
import com.here.android.mpa.mapping.Map;
import com.here.android.mpa.mapping.MapFragment;
import com.here.android.mpa.mapping.MapGesture;
import com.here.android.mpa.mapping.MapMarker;
import com.here.android.mpa.common.Image;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;


public class MainActivity extends Activity {

    private static Image iconImage;

    // map embedded in the map fragment
    private Map map = null;
    private volatile List<ParseObject> peopleList;


    // map fragment embedded in this activity
    private MapFragment mapFragment = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "gQPFmP5lp0JWuE0YsKLznIaxx1R7V2g4XHbmOXpp", "otoRe6CSRkEkY8N2NbsMY7veZskiCqOg2lJYiDox");
        ParseInstallation.getCurrentInstallation().saveInBackground();


        // Search for the map fragment to finish setup by calling init().
        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.mapfragment);
        mapFragment.init(new OnEngineInitListener() {
            @Override
            public void onEngineInitializationCompleted(OnEngineInitListener.Error error) {
                if (error == OnEngineInitListener.Error.NONE) {
                    mapFragment.getMapGesture().addOnGestureListener(new MapGesture.OnGestureListener() {
                        @Override
                        public void onPanStart() {

                        }

                        @Override
                        public void onPanEnd() {

                        }

                        @Override
                        public void onMultiFingerManipulationStart() {

                        }

                        @Override
                        public void onMultiFingerManipulationEnd() {

                        }

                        @Override
                        public boolean onMapObjectsSelected(List<ViewObject> list) {
                            String msg = "selcted " + Arrays.toString(list.toArray());
                            Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();

                            return true;
                        }

                        @Override
                        public boolean onTapEvent(PointF pointF) {
                            return false;
                        }

                        @Override
                        public boolean onDoubleTapEvent(PointF pointF) {
                            return false;
                        }

                        @Override
                        public void onPinchLocked() {

                        }

                        @Override
                        public boolean onPinchZoomEvent(float v, PointF pointF) {
                            return false;
                        }

                        @Override
                        public void onRotateLocked() {

                        }

                        @Override
                        public boolean onRotateEvent(float v) {
                            return false;
                        }

                        @Override
                        public boolean onTiltEvent(float v) {
                            return false;
                        }

                        @Override
                        public boolean onLongPressEvent(PointF pointF) {
                            return false;
                        }

                        @Override
                        public void onLongPressRelease() {

                        }

                        @Override
                        public boolean onTwoFingerTapEvent(PointF pointF) {
                            return false;
                        }
                    });

                    // retrieve a reference of the map from the map fragment
                    map = mapFragment.getMap();

                    PositioningManager positioningManager = PositioningManager.getInstance();
                    positioningManager.start(PositioningManager.LocationMethod.GPS_NETWORK);
                    GeoPosition geoPosition = positioningManager.getLastKnownPosition();
                    final GeoCoordinate geoCoordinate = geoPosition.getCoordinate();
                    double latitude = geoCoordinate.getLatitude();
                    double longitude = geoCoordinate.getLongitude();

                    // Add random mapmaker for yourself
                    MapMarker selfMapMarker = new MapMarker(geoCoordinate, getImageIcon(Color.BLACK));
                    //MapMarker selfMapMarker = new MapMarker();
                    //selfMapMarker.setCoordinate(geoCoordinate);
                    map.addMapObject(selfMapMarker);

                    //map.setZoomLevel(map.getMaxZoomLevel() + 2);
                    // Set the map center coordinate to the Vancouver region (no animation)
                    map.setCenter(geoCoordinate, Map.Animation.LINEAR);
                    //map.setCenter(new GeoCoordinate(49.196261, -123.004773, 0.0), Map.Animation.NONE);
                    // Set the map zoom level to the average between min and max (no animation)
                    //map.setZoomLevel((map.getMaxZoomLevel() + map.getMinZoomLevel()) / );

                    // Contact Parse to add points to the map
                    addPointsToMap();
                } else {
                    System.out.println("ERROR: Cannot initialize Map Fragment");
                }
            }


        });
    }

    private Image getImageIcon(int color) {
        // Load an image
        AssetManager assetManager = getApplicationContext().getAssets();
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeStream(assetManager.open("pin2.png"));
        } catch (Exception e) {
            System.out.println("should handle this");
            bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            Paint paint = new Paint();
            paint.setColor(color);
            canvas.drawRect(0F, 0F, (float) 100, (float) 100, paint);
        }
        final Image resImage = new Image();
        resImage.setBitmap(bitmap);
        return resImage;
    }

    private void addPointsToMap() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("People");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null) {
                    int[] colorlist = {Color.RED, Color.YELLOW, Color.CYAN, Color.GREEN, Color.MAGENTA};
                    int index = 0;
                    for (ListIterator<ParseObject> ec = list.listIterator(); ec.hasNext(); ) {
                        ParseObject currObject = ec.next();
                        double latitude = currObject.getDouble("lat");
                        double longitude = currObject.getDouble("lon");
                        MapMarker mm = new MapMarker(new GeoCoordinate(latitude, longitude), getImageIcon(colorlist[index % colorlist.length]));
                        map.addMapObject(mm);
                        index += 1;

                    }
                } else {
                    Toast.makeText(MainActivity.this, "Sorry A Parse Error Has Occured", Toast.LENGTH_LONG).show();

                }
            }
        });

    }
}
