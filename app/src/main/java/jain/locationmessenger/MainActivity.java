package jain.locationmessenger;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.here.android.mpa.common.GeoCoordinate;
import com.here.android.mpa.common.OnEngineInitListener;
import com.here.android.mpa.mapping.Map;
import com.here.android.mpa.mapping.MapFragment;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    // map embedded in the map fragment
    private Map map = null;

    // map fragment embedded in this activity
    private MapFragment mapFragment = null;

    private ListView listView;
    private String android_id;
    private ArrayList<String> names;
    private ArrayList<String> ids;
    private String name = "default";
    private List<ParseObject> people;
    private ParseObject person;

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
                    // Set the map center coordinate to the Vancouver region (no animation)
                    map.setCenter(new GeoCoordinate(49.196261, -123.004773, 0.0), Map.Animation.NONE);
                    // Set the map zoom level to the average between min and max (no animation)
                    map.setZoomLevel((map.getMaxZoomLevel() + map.getMinZoomLevel()) / 2);
                } else {
                    System.out.println("ERROR: Cannot initialize Map Fragment");
                }
            }
        });

        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "gQPFmP5lp0JWuE0YsKLznIaxx1R7V2g4XHbmOXpp", "otoRe6CSRkEkY8N2NbsMY7veZskiCqOg2lJYiDox");
        ParseInstallation.getCurrentInstallation().saveInBackground();

        android_id = Settings.Secure.getString(getApplicationContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);

    }

    private void connect2(){
        ids = new ArrayList<String>();
        names = new ArrayList<String>();
        boolean presence = false;
        for(int i = 0; i<people.size();i++){
            String droid = people.get(i).getString("droid");
            if(droid.equals(android_id)){
                person = people.get(i);
                presence = true;
            }
            ids.add(droid);
            names.add(people.get(i).getString("name"));
        }
        if(!presence){
            setup();
        }
        listView = (ListView) findViewById(R.id.list);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, names);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                ParseObject curr = (people.size() == position || people.isEmpty()) ? person : people.get(position);
                List<Object> req = curr.getList("requests");
                req.add(person.getString("name"));
                curr.saveInBackground();
            }
        });
        NoobTask t1 = new NoobTask();
        t1.execute();
    }

    public void connect(View view) {
        EditText getName = (EditText) findViewById(R.id.name);
        name = getName.getText().toString();
    }

    private void setup() {
        person = new ParseObject("People");
        person.put("droid", android_id);
        person.put("name", name);
        person.put("lat", 3.2);
        person.put("long", 3.2);
        person.saveInBackground();
        ids.add(android_id);
        names.add(name);
    }

    public void getPeople(double lat, double lon){
        ParseQuery<ParseObject> query = ParseQuery.getQuery("People");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null) {
                    people = list;
                    connect2();
                } else {
                    Toast.makeText(MainActivity.this, "Sorry An Error Has Occured", Toast.LENGTH_LONG).show();

                }
            }
        });
    }

    public void updateLocation(double lat, double lon){
        person.put("lat", lat);
        person.put("long", lon);
    }

    private class NoobTask extends AsyncTask<Void, String, Void> {


        @Override
        protected Void doInBackground(Void... params) {
            while(true) {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                person.fetchInBackground(new GetCallback<ParseObject>() {
                    @Override
                    public void done(ParseObject parseObject, ParseException e) {
                        if(e == null){
                            List<Object> req = person.getList("requests");
                            for(int i = 0; i<req.size(); i++){
                                try{
                                    final String msg = (String) req.get(i);
                                    MainActivity.this.runOnUiThread(new Runnable() {
                                        public void run() {
                                            Toast.makeText(MainActivity.this, msg + " has given you a " +
                                                    "shoutout!", Toast.LENGTH_LONG).show();

                                        }
                                    });
                                }catch (Exception e5){

                                }
                            }
                            person.put("requests", new ArrayList<String>());
                            person.saveInBackground();
                        }
                    }
                });
            }
        }
    }

}