package jain.locationmessenger;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class InterestsActivity extends Activity {
    private ParseObject person;
    private String android_id;
    private ListView listView;
    private List interests;
    private ArrayList<String> interestList;
    private Context context;
    private ArrayAdapter<String> adapter;
    private EditText newInterest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interests);
        context = this;
        newInterest = (EditText) findViewById(R.id.newInterest);

        android_id = Settings.Secure.getString(getApplicationContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);

        ParseQuery<ParseObject> query = ParseQuery.getQuery("People").whereMatches("droid", android_id);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null) {
                    person = list.get(0);
                    try {
                        showInterest();
                    } catch (Exception e2) {

                    }
                } else {
                    Toast.makeText(InterestsActivity.this, "Sorry An Error Has Occured", Toast.LENGTH_LONG).show();

                }
            }
        });

    }
    public void addInterest (View view){
        String interest = newInterest.getText().toString();
        if(!interestList.contains(interest)) {
            interestList.add(interest);
            interests.add(interest);
            person.put("interests", interests);
            person.saveInBackground();
            adapter.notifyDataSetChanged();
        }
    }
    public void showInterest() throws JSONException {
        listView = (ListView) findViewById(R.id.interests);
        interests = person.getList("interests");
        interestList = new ArrayList<>();
        for(int i = 0; i<interests.size(); i++){
            interestList.add(interests.get(i).toString());
        }
        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1,
                interestList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                final int index = position;
                new AlertDialog.Builder(context)
                        .setTitle("Delete entry")
                        .setMessage("Are you sure you want to delete this entry?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // continue with delete
                                interestList.remove(index);
                                interests.remove(index);
                                person.put("interests", interests);
                                person.saveInBackground();
                                runOnUiThread(new Runnable() {
                                                  @Override
                                                  public void run() {
                                                      adapter.notifyDataSetChanged();
                                                  }
                                              }
                                );
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });

    }

}
