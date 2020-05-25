package com.example.gg39998_lab6_http;

import androidx.appcompat.app.AppCompatActivity;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

public class inTic extends AppCompatActivity {

    //Constant variables for communicate whit other componets
    public static final String STATUS = "Status";
    public static final String MOVES = "Moves";
    public static final String GAME_ID = "Game_id";
    public static final String PLAYER = "Player";
    public static final int NEW_GAME = 0;
    public static final int YOUR_TURN = 1;
    public static final int WAIT = 2;
    public static final int ERROR = 3;
    public static final int CONNECTION = 4;
    public static final int NETWORK_ERROR = 5;
    public static final int WIN = 6;
    public static final int LOSE = 7;

    private int status;
    private int game_id;
    private String moves;
    private int player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_tic);

        status=getIntent().getIntExtra(inTic.STATUS, inTic.NEW_GAME);
        game_id=getIntent().getIntExtra(inTic.GAME_ID, inTic.NEW_GAME);
        player=getIntent().getIntExtra(inTic.PLAYER, 1);
        hints(status);

        GridView gv = (GridView) findViewById(R.id.gridView2);
        moves = getIntent().getStringExtra(inTic.MOVES);
        gv.setAdapter(new inTicBoard(this, moves));

        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                if(status!=inTic.WAIT)
                {
                    //Toast.makeText(inTic.this, "wiersz "+arg2, Toast.LENGTH_SHORT).show();
                    //Toast.makeText(inTic.this, "kolumna "+arg3, Toast.LENGTH_SHORT).show();
                    status = inTic.WAIT;
                    hints(inTic.CONNECTION);

                    GridView gv = (GridView) findViewById(R.id.gridView2);
                    inTicBoard game = (inTicBoard)gv.getAdapter();
                    //Make Move
                    //TODO - zrobić ruch w tablicy
                    //arg2 jest numerem pola od 0 do 8
                    //if(game.add(arg3)!=null)
                    if(game.add(arg2)!=null)
                        gv.setAdapter(game);
                    else
                        hints(inTic.ERROR);

                    Intent intencja = new Intent(getApplicationContext(), HttpService.class);
                    PendingIntent pendingResult = createPendingResult(HttpService.IN_ROW, new Intent(),0);

                    if(game_id == inTic.NEW_GAME)
                    {
                        intencja.putExtra(HttpService.URL, HttpService.XO);
                        intencja.putExtra(HttpService.METHOD, HttpService.POST);
                    }
                    else
                    {
                        intencja.putExtra(HttpService.URL, HttpService.XO+game_id);
                        intencja.putExtra(HttpService.METHOD, HttpService.PUT);
                    }
                    intencja.putExtra(HttpService.PARAMS, "moves=" + moves + arg2 );
                    Toast.makeText(inTic.this, "historia "+moves, Toast.LENGTH_SHORT).show();
                    intencja.putExtra(HttpService.RETURN, pendingResult);

                    startService(intencja);
                }

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == HttpService.IN_ROW) {
            try {
                JSONObject response = new JSONObject(data.getStringExtra(HttpService.RESPONSE));
                if (resultCode == 200) {

                    if (game_id == 0)
                        game_id = response.getInt("game_id");


                    GridView gv = (GridView) findViewById(R.id.gridView2);
                    inTicBoard game = (inTicBoard) gv.getAdapter();
                    int game_status = game.checkWin();
                    if (game_status == 0)
                        hints(inTic.WAIT);
                    else {
                        if (game_status == player)
                            hints(inTic.WIN);
                        else
                            hints(inTic.LOSE);
                    }

                } else {
                    if (resultCode == 500)
                        hints(inTic.NETWORK_ERROR);
                    else
                        hints(inTic.ERROR);
                    Log.d("DEBUG", response.getString("http_status"));
                }
                Thread.sleep(5000);
                refresh(null);

            } catch (Exception ex) {
                hints(inTic.ERROR);
                ex.printStackTrace();
            }

        } else if (requestCode == HttpService.REFRESH) {
            Toast.makeText(this, "Refreszuje", Toast.LENGTH_SHORT).show();

            try {
                JSONObject response = new JSONObject(data.getStringExtra(HttpService.RESPONSE));
                GridView gv = (GridView) findViewById(R.id.gridView2);

                moves = response.getString("moves");
                inTicBoard game = new inTicBoard(this, moves);
                gv.setAdapter(game);

                if (response.getInt("status") == player) {
                    hints(inTic.LOSE);
                    if (game.checkWin() == player) {
                        hints(inTic.WIN);
                    }
                    else if (game.checkWin() == 0) {
                        status = inTic.YOUR_TURN;
                        hints(status);
                    }
                    else {
                        Toast.makeText(this, "przegrałeś", Toast.LENGTH_SHORT).show();
                        hints(inTic.LOSE);
                    }

                }
                else if(response.getInt("status") != player && game.checkWin() != player && game.checkWin() != 0){
                    hints(inTic.LOSE);
                }
                else {
                    //Toast.makeText(this, "Refreszuje", Toast.LENGTH_SHORT).show();
                    Thread.sleep(5000);
                    refresh(null);
                }

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private void hints(int status){
        TextView hint = (TextView)findViewById(R.id.inRowHint);
        switch(status){
            case inTic.YOUR_TURN:
                hint.setText(getString(R.string.your_turn));
                break;
            case inTic.WAIT:
                Toast.makeText(getApplicationContext(), "wait", Toast.LENGTH_LONG).show();
                hint.setText(getString(R.string.wait));
                break;
            case inTic.ERROR:
                hint.setText(getString(R.string.error));
                break;
            case inTic.CONNECTION:
                hint.setText(getString(R.string.connection));
                break;
            case inTic.NETWORK_ERROR:
                hint.setText(getString(R.string.network_error));
                break;
            case inTic.WIN:
                hint.setText(getString(R.string.win));
                break;
            case inTic.LOSE:
                hint.setText(getString(R.string.lose));
                break;
            default:
                hint.setText(getString(R.string.new_game));
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.game_menu, menu);
        return true;
    }

    public void refresh(MenuItem item){
        Intent intencja = new Intent(
                getApplicationContext(),
                HttpService.class);
        PendingIntent pendingResult = createPendingResult(HttpService.REFRESH, new Intent(),0);
        intencja.putExtra(HttpService.URL, HttpService.XO+game_id);
        intencja.putExtra(HttpService.METHOD, HttpService.GET);
        intencja.putExtra(HttpService.RETURN, pendingResult);
        startService(intencja);
    }

}


