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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.example.memorygame.model.Buyable;
import com.example.memorygame.adapters.BuyableAdapter;
import com.example.memorygame.interfaces.HaveShop;
import com.example.memorygame.musicService.HomeWatcher;
import com.example.memorygame.InventoryDialog;
import com.example.memorygame.musicService.MusicService;
import com.example.memorygame.interfaces.NotGameInventory;
import com.example.memorygame.R;
import com.example.memorygame.model.Shop;
import com.example.memorygame.model.Stage;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

public class CityStageActivity extends AppCompatActivity implements NotGameInventory, HaveShop {
    private SharedPreferences sp;
    private FragmentManager fm = getSupportFragmentManager();
    private HomeWatcher mHomeWatcher;
    private Intent music;

    private TextView moneyTv;



    private boolean musicOn = true;
    private final String REGISTER_FRAGMENT_TAG = "game_fragment";

    private int doubleCoins;
    private int money;

    private String response;
    private Stage[] stages;
    private ArrayList<ImageView> locks = new ArrayList<ImageView>();

    private ArrayList<Button> stageBtns  = new ArrayList<Button>();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.city_stage);

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


        Button sewer1 = findViewById(R.id.sewer1);
        Button sewer2 = findViewById(R.id.sewer2);
        Button sewer3 = findViewById(R.id.sewer3);
        Button sewer4 = findViewById(R.id.sewer4);
        Button sewer5 = findViewById(R.id.sewer5);
        Button sewer6 = findViewById(R.id.sewer6);
        Button sewer7 = findViewById(R.id.sewer7);
        Button sewer8 = findViewById(R.id.sewer8);
        Button sewer9 = findViewById(R.id.sewer9);
        Button sewer10 = findViewById(R.id.sewer10);






        sewer1.setOnClickListener(new CityStageActivity.OnSewerClickListener());
        sewer2.setOnClickListener(new CityStageActivity.OnSewerClickListener());
        sewer3.setOnClickListener(new CityStageActivity.OnSewerClickListener());
        sewer4.setOnClickListener(new CityStageActivity.OnSewerClickListener());
        sewer5.setOnClickListener(new CityStageActivity.OnSewerClickListener());
        sewer6.setOnClickListener(new CityStageActivity.OnSewerClickListener());
        sewer7.setOnClickListener(new CityStageActivity.OnSewerClickListener());
        sewer8.setOnClickListener(new CityStageActivity.OnSewerClickListener());
        sewer9.setOnClickListener(new CityStageActivity.OnSewerClickListener());
        sewer10.setOnClickListener(new CityStageActivity.OnSewerClickListener());

        stageBtns.add(sewer1);
        stageBtns.add(sewer2);
        stageBtns.add(sewer3);
        stageBtns.add(sewer4);
        stageBtns.add(sewer5);
        stageBtns.add(sewer6);
        stageBtns.add(sewer7);
        stageBtns.add(sewer8);
        stageBtns.add(sewer9);
        stageBtns.add(sewer10);


        ImageView lock1 = findViewById(R.id.lock1);
        ImageView lock2 = findViewById(R.id.lock2);
        ImageView lock3 = findViewById(R.id.lock3);
        ImageView lock4 = findViewById(R.id.lock4);
        ImageView lock5 = findViewById(R.id.lock5);
        ImageView lock6 = findViewById(R.id.lock6);
        ImageView lock7 = findViewById(R.id.lock7);
        ImageView lock8 = findViewById(R.id.lock8);
        ImageView lock9 = findViewById(R.id.lock9);
        ImageView lock10 = findViewById(R.id.lock10);



        locks.add(lock1);
        locks.add(lock2);
        locks.add(lock3);
        locks.add(lock4);
        locks.add(lock5);
        locks.add(lock6);
        locks.add(lock7);
        locks.add(lock8);
        locks.add(lock9);
        locks.add(lock10);





        ImageButton inventoryBtn = findViewById(R.id.inventory_btn);
        inventoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle bundle = new Bundle();


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


        ImageButton shopBtn = findViewById(R.id.shop_btn);
        shopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openShop();
            }
        });




        //BIND Music Service



        doBindService();
        music = new Intent();
        music.setClass(this, MusicService.class);
        musicOn = sp.getBoolean("musicOn",true);
        music.putExtra("worldMusic", 2);
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
                    mServ.setWorld(4);
                }
            }
            @Override
            public void onHomeLongPressed() {
                if (mServ != null) {
                    mServ.pauseMusic();
                    mServ.setWorld(4);
                }
            }
        });
        mHomeWatcher.startWatch();



    }

    @Override
    public void doubleCoins() {
        doubleCoins++;
        sp.edit().putInt("doubleCoins",doubleCoins).commit();
    }


    public class OnSewerClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            String response;
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
                for(int  i = 0; i < 30; i++)
                {
                    stages[i] = new Stage();
                }
            }
            if(stages[Integer.parseInt(v.getTag().toString())+20-2].isCleared())
            {
                androidx.appcompat.app.AlertDialog.Builder alertDialog = new androidx.appcompat.app.AlertDialog.Builder(CityStageActivity.this);
                LayoutInflater inflater = ((Activity) CityStageActivity.this).getLayoutInflater();
                View alertView = inflater.inflate(R.layout.enter_game_dialog, null);
                alertDialog.setView(alertView);

                ImageView imageView = alertView.findViewById(R.id.dialog_background);
                imageView.setImageResource(R.drawable.dialog_shape_city);


                ImageView leftpic = alertView.findViewById(R.id.left_title_img);
                ImageView rightpic = alertView.findViewById(R.id.right_title_img);
                TextView titleStage = alertView.findViewById(R.id.stage_title);
                leftpic.setImageResource(R.drawable.taxi_icon);
                rightpic.setImageResource(R.drawable.taxi_icon);
                titleStage.setText("Stage "+v.getTag());
                titleStage.setTextColor(getResources().getColor(R.color.grey));



                TextView scoreTxt = alertView.findViewById(R.id.max_score);
                if(stages[ Integer.parseInt((String) v.getTag())] != null)
                {
                    scoreTxt.setText(stages[Integer.parseInt((String) v.getTag())-1+20].getBestScore()+"");

                }



                final AlertDialog show = alertDialog.show();

                show.getWindow().getDecorView().setBackgroundColor(getResources().getColor(R.color.transparent_full));



                Button plybtn =  alertView.findViewById(R.id.play_stage_btn);
                plybtn.setBackground(getResources().getDrawable(R.drawable.city_play_btn));






                final View tmpV = v;

                plybtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        moveStage(tmpV);
                        show.dismiss();


                    }
                });

            }

        }

    }
    private void lockStages()
    {

        String response;
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
            for(int  i = 0; i < 30; i++)
            {
                stages[i] = new Stage();
            }
        }

        for(int i =19; i< stages.length-1;i++)
        {
            if(stages[i].isCleared())
            {
                locks.get(i-20+1).setVisibility(View.GONE);
                int tag = Integer.parseInt(stageBtns.get(i-20+1).getTag().toString())+20;
                stageBtns.get(i-20+1).setText(tag+"");

            }



        }
    }

    private void moveStage(View v)
    {
        Intent intent = new Intent(CityStageActivity.this, SoloGameActivity.class);
        int pickedStage = Integer.parseInt(v.getTag().toString());
        switch (pickedStage)
        {
            case 1:
            {

                intent.putExtra("stage",1);
                intent.putExtra("world",2);
                startActivity(intent);

                break;
            }
            case 2:
            {
                intent.putExtra("stage",2);
                intent.putExtra("world",2);
                startActivity(intent);

                break;
            }
            case 3:
            {
                intent.putExtra("stage",3);
                intent.putExtra("world",2);
                startActivity(intent);

                break;
            }
            case 4:
            {
                intent.putExtra("stage",4);
                intent.putExtra("world",2);
                startActivity(intent);

                break;
            }
            case 5:
            {
                intent.putExtra("stage",5);
                intent.putExtra("world",2);
                startActivity(intent);

                break;
            }
            case 6:
            {
                intent.putExtra("stage",6);
                intent.putExtra("world",2);
                startActivity(intent);

                break;
            }
            case 7:
            {
                intent.putExtra("stage",7);
                intent.putExtra("world",2);
                startActivity(intent);

                break;
            }
            case 8:
            {
                intent.putExtra("stage",8);
                intent.putExtra("world",2);
                startActivity(intent);

                break;
            }
            case 9:
            {
                intent.putExtra("stage",9);
                intent.putExtra("world",2);
                startActivity(intent);

                break;
            }
            case 10:
            {

                intent.putExtra("stage",10);
                intent.putExtra("world",2);
                startActivity(intent);

                break;
            }
        }
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
        moneyTv = findViewById(R.id.money_Tv);
        moneyTv.setText(money+"");
        lockStages();

        if (mServ != null &&musicOn &&mServ.getWorld() !=2) {

            mServ.resumeMusic();
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
    public void onBackPressed() {
        music.putExtra("worldMusic", 3);

        mServ.pauseMusic();

        Intent intent = new Intent(CityStageActivity.this, StageSelectActivity.class);
        startActivity(intent);
        //super.onBackPressed();

    }

    private void openShop()
    {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View alertView = inflater.inflate(R.layout.shop_dialog, null);
        alertDialog.setView(alertView);
        alertDialog.setCancelable(true);

        ListView listView = alertView.findViewById(R.id.list_view1);
        ArrayList<Buyable> buyablesArray = new ArrayList<>();
/*        buyablesArray.add(new Buyable(getResources().getString(R.string.extra_life),R.drawable.extra_life,0,2000,0,1));
        buyablesArray.add(new Buyable(getResources().getString(R.string.double_coins),R.drawable.coins_increase,0,1000,1,1));
        buyablesArray.add(new Buyable(getResources().getString(R.string.cancel_shuffle),R.drawable.unshuffle,0,800,3,1));
        buyablesArray.add(new Buyable(getResources().getString(R.string.skip_stage),R.drawable.skip_stage,0,5000,4,1));
        buyablesArray.add(new Buyable(getResources().getString(R.string.extra_life),R.drawable.extra_life_stack,0,5000,0,3));
        buyablesArray.add(new Buyable(getResources().getString(R.string.double_coins),R.drawable.coins_increase_stack,0,2500,1,3));
        buyablesArray.add(new Buyable(getResources().getString(R.string.cancel_shuffle),R.drawable.unshuffle_stack,0,2000,3,3));
        buyablesArray.add(new Buyable(getResources().getString(R.string.skip_stage),R.drawable.skip_stage_stack,0,12000,4,3));*/
        buyablesArray = Shop.createShop(this);


        BuyableAdapter buyableAdapter = new BuyableAdapter(buyablesArray,this);



        listView.setAdapter(buyableAdapter);

        AlertDialog show = alertDialog.show();
        show.getWindow().getDecorView().setBackgroundColor(getResources().getColor(R.color.transparent));
    }

    @Override
    public void updateMoney(int money) {
        moneyTv.setText(money+"");
    }

}
