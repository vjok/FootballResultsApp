package com.example.footballresultsapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_MESSAGE = "com.example.footballresultsapp";
    public final String[] areaCodes = {"2001", "2002", "2014", "2015", "2019", "2021"};
    private ListView listView;
    private final ArrayList<League> leagues = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Leagues");
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(false);
        listView = findViewById(R.id.leagueListView);

        // Load leagues to listView
        for (String s : areaCodes) {
            makeRequest(s);
        }

        // When user clicks some league item -> Open another activity and pass the competitionID to that activity
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                League selected = leagues.get(position);
                String codeToPass = selected.getCompetitionID();
                Intent intent = new Intent(getApplicationContext(), StandingsActivity.class);
                intent.putExtra(EXTRA_MESSAGE, codeToPass);
                startActivity(intent);
            }
        });
    }

    // Volley Request to load JSON-data
    public void makeRequest(String code) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://api.football-data.org/v2/competitions/" + code;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String leagueName = (String) response.get("name");
                    int competitionID = (Integer) response.get("id");
                    League leagueToAdd = new League(leagueName, Integer.toString(competitionID));
                    leagues.add(leagueToAdd);

                } catch (Exception e) {
                    Log.d("Error", "Error loading Volley data!");
                }
                setupView();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Error", "Error loading Volley data!");
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("X-Auth-Token", "5e25ad658d7c4577aec218810b77e937");
                return params;
            }
        };
        queue.add(jsonObjectRequest);
    }

    // Creates adapter which is passed to the list view
    public void setupView() {
        final ArrayAdapter<League> adapter;
        adapter = new LeagueArrayAdapter(this, leagues);
        listView.setAdapter(adapter);
    }
}