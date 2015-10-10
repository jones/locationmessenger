package jain.locationmessenger;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

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
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

//APP ID: 87lrb1Uvkf3DWxXzUIJQ87lrb1Uvkf3DWxXzUIJQ
// APP CODE tDt4DOhV2jUkWQeg9jBcGA
// license key H4DbgfcenFH8Dc+QfYzyer2+SmpUO/i/jlKT7KQjFpujmK0iXyKUD1Odb5/WSqt8mFH3oRIECp1JC+ekBgzT9X0aCeEL/fovPKrkdFBJs7H2HQMtwtFRIAHMrpXg9PZHcOrB5fS7lTbd0528DPwk23FFA88ztU0slT9g7dii14fVWPrM+AbjJn7h2SM64w9+SFFaJ9zMLinjt0wyfiBt//iSBKJRzORvu2nBBwy0y360JZH5CJf91WhvcgF+gyinyJrvhMQ3zbPK1uEmNujcsApUfiaqOe0c+O+yMapPiCGQWuHigo3e9YIzUglamB8eA9SNNoBbbgOScn2W5vgzKHbak1kT2A/scozId71X6lerxzFaQ26RYZiSgmU9JOXUCnUvs0d9SdnBYeIcc2YlibSIqQsSdjjAbm63lypN//GFWG43kukaLhyCQkYoCiVzDosMzQ4Zr+ESEuHKZeDPVnT4TvH8PMsRNM43+Y3ECwCiFje2Td4jPF878eT9+9FwuZUPCdVh9bGkjjKP8f7MckchZfz1WagFUNZmr52ZmqAFLkBHgjex2me7+QxPyfXHibU+7/TITHbkWBrqVe6dTkF+uQifzWq7jIOcuiPE5lRV/zY8p5/tkxyCTQzVZlabGVeaxNS267+S9F3qV+jzHg0HLc9UbheIcUyVPN0wOew=
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

        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "aCAVE6xQKukRNC6JiMqoIAar2d7mp43TgV6UsWbt", "RipGECXNngnJuD9aXDhIKynjDqTbUkyNwylJXUsz");
        ParseInstallation.getCurrentInstallation().saveInBackground();

    }

    private void setup() {
        person = new ParseObject("People");
        person.put("location", android_id);
        person.put("name", name);
        person.put("requests", new ArrayList<String>());
        person.saveInBackground();
        ids.add(android_id);
        names.add(name);
    }
}
