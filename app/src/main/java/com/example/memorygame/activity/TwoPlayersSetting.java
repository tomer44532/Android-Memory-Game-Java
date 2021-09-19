package com.example.memorygame.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.memorygame.musicService.HomeWatcher;
import com.example.memorygame.musicService.MusicService;
import com.example.memorygame.R;

import java.util.ArrayList;

public class TwoPlayersSetting extends Activity {
    private HomeWatcher mHomeWatcher;
    private SharedPreferences sp;
    private Intent music;


    private int cardsNum = 16;
    private int world = 0;
    private int player1Team = 0;
    private int player2Team = 1;

    private boolean musicOn = true;
    private boolean sameColor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.two_players_setting);
        sp = getSharedPreferences("info",MODE_PRIVATE);



        final ArrayList<String> spinnerArrayCards = new ArrayList<String>();
        String s1 = getResources().getString(R.string.easy_str) +"";
        String s2 = getResources().getString(R.string.medium_str) +"";
        String s3 = getResources().getString(R.string.hard_str) +"";

        spinnerArrayCards.add(s1);
        spinnerArrayCards.add(s2);
        spinnerArrayCards.add(s3);

        ArrayAdapter<String> arrayAdapterCards = new ArrayAdapter<String>(this,R.layout.custom_spinner,spinnerArrayCards);
        Spinner cardsSpinner = findViewById(R.id.cards_spinner);
        cardsSpinner.setAdapter(arrayAdapterCards);

        cardsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position)
                {
                    case 0:
                    {
                        cardsNum =16;
                        break;
                    }
                    case 1:
                    {
                        cardsNum =20;
                        break;
                    }
                    case 2:
                    {
                        cardsNum =24;
                        break;
                    }


                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        final ArrayList<String> spinnerArrayWorld = new ArrayList<String>();
        String s4 = getResources().getString(R.string.forest_str) +"";
        String s5 = getResources().getString(R.string.food_str) +"";
        String s6 = getResources().getString(R.string.city_str) +"";

        spinnerArrayWorld.add(s4);
        spinnerArrayWorld.add(s5);
        spinnerArrayWorld.add(s6);

        ArrayAdapter<String> arrayAdapterWorld = new ArrayAdapter<String>(this,R.layout.custom_spinner,spinnerArrayWorld);
        Spinner worldSpinner = findViewById(R.id.world_spinner);
        worldSpinner.setAdapter(arrayAdapterWorld);

        worldSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position)
                {
                    case 0:
                    {
                        world =0;
                        break;
                    }
                    case 1:
                    {
                        world =1;
                        break;
                    }
                    case 2:
                    {
                        world =2;
                        break;
                    }


                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });




        final EditText player1Et = findViewById(R.id.player1_et);
        final EditText player2Et = findViewById(R.id.player2_et);

        Button playBtn  =findViewById(R.id.play_btn);
        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String player1Str = player1Et.getText().toString();
                String player2Str = player2Et.getText().toString();
                if(player1Str.length() >9||player2Str.length()>9)
                {
                    Toast.makeText(TwoPlayersSetting.this, getResources().getString(R.string.name_too_long), Toast.LENGTH_SHORT).show();
                }
                else if(!sameColor)
                {
                    if(player1Str.length()==0)
                    {
                        //Toast.makeText(TwoPlayersSetting.this, player1Str, Toast.LENGTH_SHORT).show();
                        player1Str = "Player 1";
                    }
                    if(player2Str.length()==0)
                    {
                        player2Str = "Player 2";
                    }

                    mServ.pauseMusic();
                    Intent intent = new Intent(TwoPlayersSetting.this, TwoPlayerActivity.class);
                    intent.putExtra("cardsNum",cardsNum);
                    intent.putExtra("world",world);
                    intent.putExtra("player1",player1Str);
                    intent.putExtra("player2",player2Str);
                    intent.putExtra("team1",player1Team);
                    intent.putExtra("team2",player2Team);
                    startActivity(intent);
                }





            }
        });

        final Button backBtn = findViewById(R.id.back_btn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TwoPlayersSetting.this, MainActivity.class);
                startActivity(intent);
            }
        });





        final ArrayList<String> spinnerPlayer1Colors = new ArrayList<String>();
        String s7 = getResources().getString(R.string.red_str) +"";
        String s8 = getResources().getString(R.string.blue_str) +"";
        String s9 = getResources().getString(R.string.green_str) +"";
        String s10 = getResources().getString(R.string.yellow_str) +"";

        spinnerPlayer1Colors.add(s7);
        spinnerPlayer1Colors.add(s8);
        spinnerPlayer1Colors.add(s9);
        spinnerPlayer1Colors.add(s10);




        Spinner player1Spinner = findViewById(R.id.player1_spinner);
        Spinner player2Spinner = findViewById(R.id.player2_spinner);
        final TextView sameColorTv = findViewById(R.id.same_color_tv);

        ArrayAdapter<String> arrayAdapterColor = new ArrayAdapter<String>(this,R.layout.custom_spinner,spinnerPlayer1Colors);

        player1Spinner.setAdapter(arrayAdapterColor);
        player2Spinner.setAdapter(arrayAdapterColor);

        player2Spinner.setSelection(1);

        player1Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                player1Team = position;
                if(player1Team==player2Team)
                {
                    sameColor = true;
                    sameColorTv.setVisibility(View.VISIBLE);
                }
                else
                {
                    sameColor = false;
                    sameColorTv.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        player2Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                player2Team = position;
                if(player1Team==player2Team)
                {
                    sameColor = true;
                    sameColorTv.setVisibility(View.VISIBLE);
                }
                else
                {
                    sameColor = false;
                    sameColorTv.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });















        //BIND Music Service



        doBindService();
        music = new Intent();
        music.setClass(this, MusicService.class);
        musicOn = sp.getBoolean("musicOn",true);
        //music.putExtra("worldMusic", 3);
        if(musicOn)
        {

            startService(music);

        }


        //Start HomeWatcher
        mHomeWatcher = new HomeWatcher(this);
        mHomeWatcher.setOnHomePressedListener(new HomeWatcher.OnHomePressedListener() {
            @Override
            public void onHomePressed() {
                if (mServ != null) {
                    mServ.pauseMusic();
                    mServ.setWorld(5);
                }
            }
            @Override
            public void onHomeLongPressed() {
                if (mServ != null) {
                    mServ.pauseMusic();
                    mServ.setWorld(5);
                }
            }
        });
        mHomeWatcher.startWatch();

    }






    //Bind/Unbind music service
    private boolean mIsBound = false;
    private MusicService mServ;
    private ServiceConnection Scon =new ServiceConnection(){

        public void onServiceConnected(ComponentName name, IBinder
                binder) {
            mServ = ((MusicService.ServiceBinder)binder).getService();
        }

        public void onServiceDisconnected(ComponentName name) {
            mServ = null;
        }
    };

    void doBindService(){
        bindService(new Intent(this,MusicService.class),
                Scon, Context.BIND_AUTO_CREATE);
        mIsBound = true;
    }

    void doUnbindService()
    {
        if(mIsBound)
        {
            unbindService(Scon);
            mIsBound = false;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        musicOn = sp.getBoolean("musicOn",true);



        if (mServ != null &&musicOn) {

            mServ.resumeMusic();
            if(mServ.getWorld() !=3)
            {
                mServ.pauseMusic();
                mServ.setWorld(3);
                mServ.startMusic();
            }
        }









    }

    @Override
    protected void onPause() {
        super.onPause();

        //Detect idle screen
        PowerManager pm = (PowerManager)
                getSystemService(Context.POWER_SERVICE);
        boolean isScreenOn = false;
        if (pm != null) {
            isScreenOn = pm.isScreenOn();
        }

        if (!isScreenOn) {
            if (mServ != null) {
                mServ.pauseMusic();
            }
        }
    }


    @Override
    protected void onDestroy() {
        //UNBIND music service
        doUnbindService();
        Intent music = new Intent();
        music.setClass(this,MusicService.class);
        stopService(music);
        mHomeWatcher.stopWatch();
        super.onDestroy();



    }
}
