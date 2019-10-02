package com.example.footballresultsapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class StandingsActivity extends AppCompatActivity {

    private ListView listView;
    private ArrayList<Team> teams = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_standings);

        Intent intent = getIntent();
        String competitionID = intent.getExtras().getString(MainActivity.EXTRA_MESSAGE);
        listView = findViewById(R.id.teamsListView);
        getCompetition(competitionID);
        Log.d("StandingsActivity", competitionID);
    }

    public void getCompetition(String competitionID) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://api.football-data.org/v2/competitions/" + competitionID + "/standings";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray jsonArray = response.getJSONArray("standings");
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    JSONArray tableArray = jsonObject.getJSONArray("table");
                    for (int i = 0; i < tableArray.length(); i++) {
                        JSONObject teamObject = tableArray.getJSONObject(i);
                        int ranking = teamObject.getInt("position");
                        JSONObject innerObjectTeam = teamObject.getJSONObject("team");
                        String teamName = innerObjectTeam.getString("name");
                        int playedGames = teamObject.getInt("playedGames");
                        int wins = teamObject.getInt("won");
                        int draws = teamObject.getInt("draw");
                        int losses = teamObject.getInt("lost");
                        int points = teamObject.getInt("points");
                        Team teamToAdd = new Team(ranking, teamName, playedGames, wins, draws, losses, points);
                        teams.add(teamToAdd);
                    }
                } catch (JSONException e) {
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
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("X-Auth-Token", "5e25ad658d7c4577aec218810b77e937");
                return params;
            }
        };
        queue.add(jsonObjectRequest);
    }

    public void setupView() {
        final ArrayAdapter<Team> adapter;
        adapter = new TeamArrayAdapter(this, teams);
        listView.setAdapter(adapter);
    }
}
