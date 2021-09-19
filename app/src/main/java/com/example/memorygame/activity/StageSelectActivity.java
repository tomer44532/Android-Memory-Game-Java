package com.example.memorygame.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.memorygame.pagerTabs.CandylandTab;
import com.example.memorygame.pagerTabs.CityTab;
import com.example.memorygame.interfaces.ClickInterface;
import com.example.memorygame.pagerTabs.ForestTab;
import com.example.memorygame.musicService.HomeWatcher;
import com.example.memorygame.musicService.MusicService;
import com.example.memorygame.adapters.PagerAdapter;
import com.example.memorygame.R;
import com.example.memorygame.model.Stage;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class StageSelectActivity extends AppCompatActivity implements ClickInterface {
    private SharedPreferences sp;
    private HomeWatcher mHomeWatcher;

    private int selected;// 0 = forest, 1 = candyland, 2 = city
    private int curTab;
    private String response;

    private ViewPager pager;
    private PagerAdapter pagerAdapter;

    boolean musicOn = true;

    private Stage[] stages;

    private Intent music;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stage_pick_layout);

        sp = getSharedPreferences("info",MODE_PRIVATE);
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
            for(int i =0; i<30;i++)
            {
                stages[i] = new Stage();
            }

        }









        List<Fragment> list  = new ArrayList<>();
        list.add(new ForestTab());
        list.add(new CandylandTab());
        list.add(new CityTab());


        pager = findViewById(R.id.stage_pager);
        pagerAdapter = new PagerAdapter(getSupportFragmentManager(),list);
        pager.setAdapter(pagerAdapter);







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

    @Override
    public void buttonClicked(int world) {
        switch (world)
        {
            case 0:
            {
                music.putExtra("worldMusic", 0);
                //stopService(music);
                mServ.pauseMusic();

/*                Gson gson = new Gson();
                String json = gson.toJson(stages);
                sp.edit().remove("stages").commit();
                sp.edit().putString("stages",json).commit();*/

                Intent intent = new Intent(StageSelectActivity.this, ForestStageActivity.class);
                //intent.putExtra("cardsNum",cardsNum);
                startActivity(intent);
                break;
            }
            case 1:
            {
                music.putExtra("worldMusic", 1);
                //stopService(music);
                mServ.pauseMusic();

/*                Gson gson = new Gson();
                String json = gson.toJson(stages);
                sp.edit().remove("stages").commit();
                sp.edit().putString("stages",json).commit();*/


                Intent intent = new Intent(StageSelectActivity.this, CandylandStageActivity.class);
                //intent.putExtra("cardsNum",cardsNum);
                startActivity(intent);
                break;
            }
            case 2:
            {
                music.putExtra("worldMusic", 2);
                //stopService(music);
                mServ.pauseMusic();

/*                Gson gson = new Gson();
                String json = gson.toJson(stages);
                sp.edit().remove("stages").commit();
                sp.edit().putString("stages",json).commit();*/

                Intent intent = new Intent(StageSelectActivity.this, CityStageActivity.class);
                //intent.putExtra("cardsNum",cardsNum);
                startActivity(intent);
                break;
            }
        }

    }



    public void defaultTab()
    {
/*        if(pager != null)
        {

            pager.post(new Runnable() {
                @Override
                public void run() {


                    Toast.makeText(StageSelectActivity.this, "stage tab is " +curTab, Toast.LENGTH_SHORT).show();
                    pager.setCurrentItem(curTab);
                }
            });
        }*/
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(StageSelectActivity.this, MainActivity.class);
        intent.putExtra("same music",true);
        startActivity(intent);
        super.onBackPressed();
    }
}
