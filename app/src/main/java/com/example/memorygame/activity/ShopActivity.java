package com.example.memorygame.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
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
import com.google.gson.Gson;

import java.util.ArrayList;

public class ShopActivity extends AppCompatActivity implements NotGameInventory, HaveShop {
    private SharedPreferences sp;
    private HomeWatcher mHomeWatcher;
    private FragmentManager fm = getSupportFragmentManager();

    private int money;
    private int doubleCoins;
    private boolean musicOn = true;

    private String response;
    private Gson gson;

    private TextView moneyTv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shop_layout);
        sp = getSharedPreferences("info",MODE_PRIVATE);

        doubleCoins = sp.getInt("doubleCoins",0);

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



        final LinearLayout layout1 = findViewById(R.id.layout_1);
        final LinearLayout layout2 = findViewById(R.id.layout_2);

        gson = new Gson();
        final ListView listView2 = findViewById(R.id.list_view2);




        Gson gson = new Gson();






        response = sp.getString("bought items","");







        ArrayList<Buyable> buyablesArray = new ArrayList<>();
/*        buyablesArray.add(new Buyable(getResources().getString(R.string.extra_life),R.drawable.extra_life,0,20,0,1));
        buyablesArray.add(new Buyable(getResources().getString(R.string.double_coins),R.drawable.coins_increase,0,10,1,1));
        buyablesArray.add(new Buyable(getResources().getString(R.string.cancel_shuffle),R.drawable.unshuffle,0,8,3,1));
        buyablesArray.add(new Buyable(getResources().getString(R.string.skip_stage),R.drawable.skip_stage,0,50,4,1));
        buyablesArray.add(new Buyable(getResources().getString(R.string.extra_life),R.drawable.extra_life_stack,0,50,0,3));
        buyablesArray.add(new Buyable(getResources().getString(R.string.double_coins),R.drawable.coins_increase_stack,0,25,1,3));
        buyablesArray.add(new Buyable(getResources().getString(R.string.cancel_shuffle),R.drawable.unshuffle_stack,0,20,3,3));
        buyablesArray.add(new Buyable(getResources().getString(R.string.skip_stage),R.drawable.skip_stage_stack,0,120,4,3));*/

        buyablesArray = Shop.createShop(this);

        BuyableAdapter buyableAdapter = new BuyableAdapter(buyablesArray,this);

        ListView listView1 = findViewById(R.id.list_view1);

        listView1.setAdapter(buyableAdapter);

        //response = sp.getString("bought items","");





        //BIND Music Service



        doBindService();
        Intent music = new Intent();
        music.setClass(this, MusicService.class);
        musicOn = sp.getBoolean("musicOn",true);
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


        if (mServ != null &&musicOn) {
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
    public void doubleCoins() {
        doubleCoins++;
        sp.edit().putInt("doubleCoins",doubleCoins).commit();
    }

    @Override
    public void updateMoney(int money) {
        moneyTv.setText(money+"");
    }
}
