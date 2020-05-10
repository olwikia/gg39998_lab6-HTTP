package com.example.gg39998_lab6_http;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class GamesList extends AppCompatActivity {

    private int game;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_games_list);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Bundle extras = getIntent().getExtras();
        game = extras.getInt("gra");

        SwipeRefreshLayout swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        swipeLayout.setOnRefreshListener( new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshGameList();
            }
        });
        refreshGameList();

        //Implement onClick Action - chose game for play
        ListView list = (ListView)findViewById(R.id.listView);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                ProgressBar spinner = (ProgressBar)findViewById(R.id.progressBar1);
                spinner.setVisibility(View.VISIBLE);
                String game_id = arg0.getItemAtPosition(arg2).toString().replace("ID: ","");

                Intent intencja = new Intent(
                        getApplicationContext(),
                        HttpService.class);
                PendingIntent pendingResult = createPendingResult(HttpService.GAME_INFO, new Intent(),0);
                if(game == R.id.inRow){
                    intencja.putExtra(HttpService.URL, HttpService.LINES+game_id);
                }else{
                    //TODO - geting ticTacToe games list
                }
                intencja.putExtra(HttpService.METHOD, HttpService.GET);
                intencja.putExtra(HttpService.RETURN, pendingResult);
                startService(intencja);
            }
        });

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View view) {
                Intent intencja = null;
                switch (game) {
                    case R.id.inRow:
                        intencja = new Intent(getApplicationContext(), inRow.class);
                        intencja.putExtra(inRow.STATUS, inRow.NEW_GAME);
                        intencja.putExtra(inRow.MOVES, "");
                        break;
                        default:
                            //TODO - when gamer choose TicTacToe Game
                            break;
                }
                Toast.makeText(getApplicationContext(), "fabbbb", Toast.LENGTH_LONG).show();
                startActivity(intencja);
            }
        });
    }

    public void refreshGameList(){
        ProgressBar spinner = (ProgressBar)findViewById(R.id.progressBar1);
        spinner.setVisibility(View.VISIBLE);
        Snackbar.make(findViewById(R.id.main_list), getString(R.string.refresh), Snackbar.LENGTH_SHORT) .setAction("Action", null).show();

        ListView list = (ListView)findViewById(R.id.listView);
        TextView emptyText = (TextView)findViewById(android.R.id.empty);

        Intent intencja = new Intent( getApplicationContext(), HttpService.class);
        PendingIntent pendingResult = createPendingResult(HttpService.GAMES_LIST, new Intent(),0);
        if(game == R.id.inRow){ intencja.putExtra(HttpService.URL, HttpService.LINES);
            Toast.makeText(getApplicationContext(), "refresh!", Toast.LENGTH_LONG).show();
        }
        else{
            //TODO - geting ticTacToe games list
        }
        intencja.putExtra(HttpService.METHOD, HttpService.GET);
        intencja.putExtra(HttpService.RETURN, pendingResult);

        startService(intencja);

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == HttpService.GAMES_LIST) {

            //Hide loading spinner
            ProgressBar spinner = (ProgressBar) findViewById(R.id.progressBar1);
            spinner.setVisibility(View.GONE);
            SwipeRefreshLayout swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
            swipeLayout.setRefreshing(false);

            //Get answer as JsonObject
            try {
                //Parse response as JSON
                JSONObject response = new JSONObject(data.getStringExtra(HttpService.RESPONSE));

                if (response.getInt("games_count") > 0) {
                    //hide message "no game"
                    TextView no_game = (TextView) findViewById(R.id.empty);
                    no_game.setVisibility(View.GONE);

                    //get array of games from JSON
                    JSONArray games = new JSONArray(response.getString("games"));
                    ArrayList<String> items = new ArrayList<String>();

                    //Parse String list of games (for adapter)
                    for (int i = 0; i < response.getInt("games_count"); i++) {
                        JSONObject game = games.getJSONObject(i);
                        items.add("ID: " + game.getString("id"));
                    }

                    //Set adapter to list
                    ArrayAdapter<String> gamesAdapter =
                            new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items);
                    ListView list = (ListView) findViewById(R.id.listView);
                    list.setAdapter(gamesAdapter);
                }//if "no game" do nothing
            } catch (Exception ex) {
                ex.printStackTrace();

            }
        } else if (requestCode == HttpService.GAME_INFO) {
            //Hide loading spinner
            ProgressBar spinner = (ProgressBar) findViewById(R.id.progressBar1);
            spinner.setVisibility(View.GONE);

            if (game == R.id.inRow) {
                //Create intent to start game 4inRow
                Intent intencja = new Intent(getApplicationContext(), inRow.class);

                try {
                    //Parse server response
                    JSONObject response = new JSONObject(data.getStringExtra(HttpService.RESPONSE));

                    //Set Game number
                    intencja.putExtra(inRow.GAME_ID, response.getInt("id"));

                    if (response.getInt("status") == 0 && response.getInt("player1") == 2) {
                        //connect to new game
                        intencja.putExtra(inRow.STATUS, inRow.YOUR_TURN);
                    } else if (response.getInt("status") == 1 && response.getInt("player1") == 1) {
                        //time to player1 move
                        intencja.putExtra(inRow.STATUS, inRow.YOUR_TURN);
                    } else if (response.getInt("status") == 2 && response.getInt("player1") == 2) {
                        //time to player2 move
                        intencja.putExtra(inRow.STATUS, inRow.YOUR_TURN);
                    } else
                        intencja.putExtra(inRow.STATUS, inRow.WAIT);

                    //set player number
                    intencja.putExtra(inRow.PLAYER, response.getInt("player1"));
                    //set previous moves
                    intencja.putExtra(inRow.MOVES, response.getString("moves"));
                    //start game
                    startActivity(intencja);

                } catch (Exception ex) {
                    //For JSON Object
                    ex.printStackTrace();

                }
            } else if (game == R.id.ticTac) {
                //TODO - start chosen game for TicTacToe
            }
        }
    }


}
