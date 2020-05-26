package com.example.gg39998_lab6_http;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

public class inTicBoard extends BaseAdapter {

    private Context context;
    private int player;
    private int[][] board = new int[3][3];

    public inTicBoard(Context cont, String moves) {
        context = cont;
        int mvs = 0;
        for (String move : moves.split("(?!^)")) {
            if (move != "")
                this.move(Integer.parseInt(move), mvs++ % 2);
        }
        player = mvs % 2;
    }

    private boolean move(int pole, int player) {
        int row = 0;

        try { //TODO - zrobić ruch w tablicy
            /*while (board[row][1] != 0)
                row++;*/
            switch (pole) {
                case 0:
                    board[2][0] = player + 1;
                    break;
                case 1:
                    board[2][1] = player + 1;
                    break;
                case 2:
                    board[2][2] = player + 1;
                    break;
                case 3:
                    board[1][0] = player + 1;
                    break;
                case 4:
                    board[1][1] = player + 1;
                    break;
                case 5:
                    board[1][2] = player + 1;
                    break;
                case 6:
                    board[0][0] = player + 1;
                    break;
                case 7:
                    board[0][1] = player + 1;
                    break;
                case 8:
                    board[0][2] = player + 1;
                    break;
            }

        } catch (Exception ex) {
            return false;
        }
        return true;
    }
    //TODO - zrobić ruch w tablicy
    /*public inTicBoard add(long col) {
        if (this.move((int) col, player))
            return this;
        return null;
    }*/

    public inTicBoard add(int pole) {
        if (this.move((int) pole, player++%2))
            return this;
        return null;
    }

    @Override
    public int getCount() {
        return 3 * 3;
    }

    @Override
    public Object getItem(int position) {
        return position % 3;
    }

    @Override
    public long getItemId(int position) {
        return position % 3;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView iv = new ImageView(context);

        int col = position % 3;
        int row = 2 - position / 3;
        switch (board[row][col]) {
            case 0:
                iv.setImageResource(R.drawable.rectangle);
                break;
            case 1:
                iv.setImageResource(R.drawable.player11);
                break;
            case 2:
                iv.setImageResource(R.drawable.player22);
                break;
        }
        iv.setLayoutParams(new LinearLayout.LayoutParams(140, 140));
        return iv;
    }

    public int checkWin() {
       if (board[0][0]==board[0][1]&&board[0][1]==board[0][2]&&board[0][0]!=0){
           return board[0][0];
       }
       else if (board[1][0]==board[1][1]&&board[1][1]==board[1][2]&&board[1][0]!=0){
            return board[1][0];
        }
       else if (board[2][0]==board[2][1]&&board[2][1]==board[2][2]&&board[2][0]!=0){
           return board[2][0];
       }
       else if (board[0][0]==board[1][0]&&board[1][0]==board[2][0]&&board[0][0]!=0){
           return board[0][0];
       }
       else if (board[0][1]==board[1][1]&&board[1][1]==board[2][1]&&board[0][1]!=0){
           return board[0][1];
       }
       else if (board[0][2]==board[1][2]&&board[1][2]==board[2][2]&&board[0][2]!=0){
           return board[2][2];
       }
       else if (board[0][0]==board[1][1]&&board[1][1]==board[2][2]&&board[0][0]!=0){
           return board[0][0];
       }
       else if (board[0][2]==board[1][1]&&board[1][1]==board[2][0]&&board[0][2]!=0){
           return board[0][2];
       }
        return 0;
    }

}
