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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
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

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;

import javax.net.ssl.HttpsURLConnection;


public class MainActivity extends Activity {

    private static Image iconImage;

    // map embedded in the map fragment
    private Map map = null;
    private volatile List<ParseObject> peopleList;
    private RequestQueue queue;


    // map fragment embedded in this activity
    private MapFragment mapFragment = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        queue = Volley.newRequestQueue(this);


        //getUberDistance(37.7759792, -122.41823, 37.868165, -122.268611);


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

    private void getUberDistance(double lat1, double lon1, double lat2, double lon2) {


        String url = "https://api.uber.com/v1/estimates/price/";
        try {

            JsonObjectRequest jsObjRequest = new JsonObjectRequest
                    (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                        @Override
                        public HashMap<String, String> getParams() {
                            HashMap<String, String> params = new HashMap<String, String>();
                            params.put("Authorization: Token", "aa2j6E7NRmCPiuc6C6Axzatwxc7cbJK0reBlchej");
                            return params;
                        }

                        @Override
                        public void onResponse(JSONObject response) {
                            Toast.makeText(MainActivity.this, response.toString(), Toast.LENGTH_LONG).show();
                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // TODO Auto-generated method stub

                        }
                    });



            HttpClient client = new DefaultHttpClient();
            HttpGet get = new HttpGet();
            get.setURI(new URI("https://api.uber.com/v1/estimates/price/"));
            get.setHeader("Authorization", "Token aa2j6E7NRmCPiuc6C6Axzatwxc7cbJK0reBlchej");
            get.getParams().setParameter("product_id", "a1111c8c-c720-46c3-8534-2fcdd730040d");
            get.getParams().setParameter("start_latitude", String.valueOf(lat1));
            get.getParams().setParameter("start_longitude", String.valueOf(lon1));
            get.getParams().setParameter("end_latitude", String.valueOf(lat2));
            get.getParams().setParameter("start_latitude", String.valueOf(lon2));
            HttpResponse response = client.execute(get);

            System.out.println("boo!");
            if (response != null) {

            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
