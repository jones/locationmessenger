package jain.locationmessenger;

import android.app.Activity;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class FriendsActivity extends Activity {

    private EditText search;
    private ArrayAdapter<String> adapter;
    private ListView friendsView;
    private List<ParseObject> people;
    private ArrayList<String> names;
    private String android_id;
    private ParseObject thisGuy;
    private JSONObject friends;
    private boolean[] isFriend;
    private String[] friendMapping;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);
        friendsView = (ListView) findViewById(R.id.friendsView);
        search = (EditText) findViewById(R.id.search);
        friendsView.setTextFilterEnabled(true);
        android_id = Settings.Secure.getString(getApplicationContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);


        ParseQuery<ParseObject> query = ParseQuery.getQuery("People");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null) {
                    people = list;
                    try {
                        display();
                    } catch (Exception e2) {

                    }
                } else {
                    Toast.makeText(FriendsActivity.this, "Sorry An Error Has Occured", Toast.LENGTH_LONG).show();

                }
            }
        });

    }

    public void display() {
        names = new ArrayList<String>();
        for (ParseObject person: people){
            if(person.getString("droid").equals(android_id)){
                thisGuy = person;
                friends = person.getJSONObject("friends");
            }else{
                names.add(person.getString("name"));
            }
        }
        int index = 0;
        isFriend = new boolean[people.size()-1];
        friendMapping = new String[people.size()-1];
        for (ParseObject person: people){
            if(!person.equals(thisGuy)){
                if(friends.has(person.getString("droid"))) {
                    isFriend[index] = true;
                }
                friendMapping[index] = person.getString("droid");
                index++;
            }
        }

        adapter = new ArrayAdapter<String>(FriendsActivity.this, android.R.layout.simple_list_item_multiple_choice, names);
        friendsView.setAdapter(adapter);
        friendsView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        for(int i = 0; i<isFriend.length; i++){
            if(isFriend[i]) friendsView.setItemChecked(i, true);
        }

        friendsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //SparseBooleanArray Checked = list.getCheckedItemPositions();;
                if (isFriend[position]) {
                    isFriend[position] = false;
                    friendsView.setItemChecked(position, false);
                    friends.remove(friendMapping[position]);
                    thisGuy.put("friends", friends);
                    thisGuy.saveInBackground();
                } else {
                    isFriend[position] = true;
                    friendsView.setItemChecked(position, true);
                    try {
                        friends.put(friendMapping[position], names.get(position));
                        thisGuy.put("friends", friends);
                        thisGuy.saveInBackground();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }   
                }
            }
        });

        search.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                // TODO Auto-generated method stub
                adapter.getFilter().filter(arg0);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
                // TODO Auto-generated method stub

            }
        });
    }
}
