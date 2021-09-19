package com.example.memorygame.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.memorygame.musicService.HomeWatcher;
import com.example.memorygame.InventoryDialog;
import com.example.memorygame.musicService.MusicService;
import com.example.memorygame.interfaces.NotGameInventory;
import com.example.memorygame.R;
import com.google.gson.Gson;

public class MainActivity extends AppCompatActivity implements NotGameInventory {

    private SharedPreferences sp;

    private FragmentManager fm = getSupportFragmentManager();

    private HomeWatcher mHomeWatcher;

    private int cardsNum;
    private int coins;
    private int doubleCoins;
    private int money;
    private boolean musicOn = true;
    private boolean screenBlack;
    private boolean doneTutorial;
    private long btnDuration = 3000;

    private TextView moneyTv;

    private Intent music;

    private ImageButton muteBtn;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        sp = getSharedPreferences("info",MODE_PRIVATE);



///for all stages clear
        /*String response;
        Stage[] stages;
        response = sp.getString("stages","");
        if(response !="")
        {
            Gson gson = new Gson();
            stages = gson.fromJson(response,
                    new TypeToken<Stage[]>(){}.getType());


        }
        else
        {
            stages = new Stage[30];
            for(int i =0; i<30; i++)
            {
                stages[i] = new Stage();
                stages[i].setCleared(true);

            }
            Gson gson = new Gson();
            String json = gson.toJson(stages);
            sp.edit().remove("stages").commit();
            sp.edit().putString("stages",json).commit();


        }*/
        ///done open stages



        Animation floatBtnAnim = AnimationUtils.loadAnimation(this, R.anim.floating);





        Button startBtn = findViewById(R.id.start_btn);


        floatBtnAnim.setDuration(btnDuration);
        startBtn.startAnimation(floatBtnAnim);

        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(doneTutorial)
                {
                    Intent intent = new Intent(MainActivity.this, StageSelectActivity.class);
                    startActivity(intent);
                }
                else
                {
                    Intent intent = new Intent(MainActivity.this, TutorialActivity.class);
                    startActivity(intent);
                }

            }
        });

        Button start2Btn = findViewById(R.id.start2_btn);


        floatBtnAnim.setDuration(btnDuration);
        start2Btn.startAnimation(floatBtnAnim);


        start2Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, TwoPlayersSetting.class);
                intent.putExtra("cardsNum",16);
                intent.putExtra("world",0);
                startActivity(intent);
            }
        });

        Button shopBtn = findViewById(R.id.shop_btn);

        floatBtnAnim.setDuration(btnDuration);
        shopBtn.startAnimation(floatBtnAnim);

        shopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ShopActivity.class);
                intent.putExtra("cardsNum",cardsNum);
                startActivity(intent);
            }
        });


        ImageButton inventoryBtn = findViewById(R.id.inventory_btn);
        inventoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle bundle = new Bundle();

                //dialogFragment.setArguments(bundle);
                Gson gson = new Gson();
                String response = sp.getString("bought items","");

                InventoryDialog inventoryFragment = new InventoryDialog();
                if(response !="")
                {
                    bundle.putString("inventory",response);

                    inventoryFragment.setArguments(bundle);

                }

                // Show DialogFragment
                inventoryFragment.show(fm, "Dialog Fragment");
            }
        });




        doubleCoins = sp.getInt("doubleCoins",0);
        //Toast.makeText(this, "doube coins are "+doubleCoins, Toast.LENGTH_SHORT).show();




        muteBtn = findViewById(R.id.music_btn);


        Button quitBtn = findViewById(R.id.quit_btn);
        floatBtnAnim.setDuration(btnDuration);
        quitBtn.startAnimation(floatBtnAnim);
        quitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });



        //BIND Music Service



        doBindService();
        music = new Intent();
        music.setClass(this, MusicService.class);
        musicOn = sp.getBoolean("musicOn",true);



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




        if(musicOn)
        {

            music.putExtra("worldMusic", 3);
            startService(music);
            muteBtn.setImageDrawable(getResources().getDrawable(R.drawable.music_button));
            muteBtn.setSelected(true);
        }
        else
        {
            muteBtn.setImageDrawable(getResources().getDrawable(R.drawable.music_button));
            muteBtn.setSelected(false);
        }



        muteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(musicOn)
                {

                    mServ.pauseMusic();
                    musicOn = false;
                    muteBtn.setSelected(false);
                }
                else
                {
                    muteBtn.setSelected(true);
                    //mServ.startMusic();
                    musicOn = true;
                    if(mServ.getWorld() !=3)
                    {
                        mServ.pauseMusic();
                        mServ.setWorld(3);
                        mServ.startMusic();
                    }
                    else
                    {
                        mServ.startMusic();
                    }
                }
            }
        });
    }


    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
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
        doubleCoins = sp.getInt("doubleCoins",0);
        money = sp.getInt("money",0);
        doneTutorial = sp.getBoolean("doneTutorial",false);
        moneyTv = findViewById(R.id.money_Tv);
        moneyTv.setText(money+"");
/*        if(mServ !=null)
        {
            Toast.makeText(this, "mserv world is " + mServ.world, Toast.LENGTH_SHORT).show();
        }*/


        if (mServ != null &&musicOn &&mServ.getWorld() !=4 && mServ.getWorld() !=5&& (mServ.getWorld() !=3||screenBlack)) {

            mServ.resumeMusic();
            screenBlack = false;
        }
        if(mServ !=null && mServ.getWorld()==4)
        {
            mServ.setWorld(3);
            mServ.startMusic();
        }
        if(mServ !=null && mServ.getWorld()==5)
        {
            mServ.setWorld(3);
            mServ.resumeMusic();
        }
        if(musicOn)
        {

            muteBtn.setImageDrawable(getResources().getDrawable(R.drawable.music_button));
            muteBtn.setSelected(true);
        }
        else
        {
            muteBtn.setImageDrawable(getResources().getDrawable(R.drawable.music_button));
            muteBtn.setSelected(false);
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
                screenBlack = true;
            }
        }
        sp.edit().putBoolean("musicOn",musicOn).commit();
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

    @Override
    public void doubleCoins() {
        doubleCoins++;
        sp.edit().putInt("doubleCoins",doubleCoins).commit();
    }
}
