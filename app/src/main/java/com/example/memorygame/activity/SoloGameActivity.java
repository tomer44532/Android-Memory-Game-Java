package com.example.memorygame.activity;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.memorygame.InventoryDialog;
import com.example.memorygame.R;
import com.example.memorygame.adapters.BuyableAdapter;
import com.example.memorygame.fragments.GameFragment;
import com.example.memorygame.interfaces.HandleGameCards;
import com.example.memorygame.interfaces.HaveShop;
import com.example.memorygame.model.Buyable;
import com.example.memorygame.model.Inventory;
import com.example.memorygame.model.InventorySlot;
import com.example.memorygame.model.Shop;
import com.example.memorygame.model.Stage;
import com.example.memorygame.musicService.HomeWatcher;
import com.example.memorygame.musicService.MusicService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

public class SoloGameActivity extends AppCompatActivity implements HandleGameCards, HaveShop {
    private HomeWatcher mHomeWatcher;

    private InventoryDialog inventoryFragment;

    private SharedPreferences sp;
    private androidx.fragment.app.FragmentManager fm = getSupportFragmentManager();
    private Intent music;

    private final int LIFE_PRICE = 2000;

    private int world;
    private int stage;
    private int money;
    private boolean musicOn = true;



    private final String GAME_FRAGMENT_TAG = "game_fragment";

    private TextView turnsShuffle;
    private TextView turnsLeft;
    private TextView stageTxt;
    private TextView doubleCoinsAmount;
    private TextView moneyTv;
    private ImageView doubleCoinsIcon;
    private TextView gameTitle;
    private LinearLayout stageBar;
    private FrameLayout backgroundLayout;
    private ImageView winAnim;
    private ImageView loseAnim;
    private LinearLayout doubleCoinsLayout;


    private GameFragment gameFragment;
    private AlertDialog show;
    private Inventory inventory;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.one_player_activity);
        Button tabTst = findViewById(R.id.tab_tst);
        tabTst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                victoryManager(100);
            }
        });

        Button tstBtn = findViewById(R.id.tab_tst2);
        tstBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleGameover();
            }
        });


        sp = getSharedPreferences("info",MODE_PRIVATE);

        money = sp.getInt("money",0);
        moneyTv = findViewById(R.id.money_Tv);
        moneyTv.setText(money+"");


        stageBar = findViewById(R.id.stage_bar);
        gameTitle = findViewById(R.id.stage_title);
        backgroundLayout = findViewById(R.id.background_layout);




        turnsShuffle = findViewById(R.id.shuffle_turns);
        turnsLeft = findViewById(R.id.turns_left);
        //stageTxt = findViewById(R.id.stage_txt);
        winAnim = findViewById(R.id.win_anim);
        loseAnim = findViewById(R.id.lose_anim);



        world = getIntent().getIntExtra("world",0);
        stage = getIntent().getIntExtra("stage",0);


        gameFragment = GameFragment.newInstance("fragment");
        Bundle bundle = new Bundle();
        bundle.putInt("world", world);
        bundle.putInt("stage", stage);
// set Fragmentclass Arguments

        gameFragment.setArguments(bundle);

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.boardgame_layout,gameFragment,GAME_FRAGMENT_TAG);
        transaction.addToBackStack(null);
        transaction.commit();


        doubleCoinsIcon = findViewById(R.id.double_coins);
        doubleCoinsAmount = findViewById(R.id.double_coins_amount);
        doubleCoinsLayout = findViewById(R.id.double_coins_layout);
        int dCoinsAmount = sp.getInt("doubleCoins",0);
        if(dCoinsAmount > 0)
        {
            doubleCoinsLayout.setVisibility(View.VISIBLE);
            doubleCoinsIcon.setVisibility(View.VISIBLE);
            doubleCoinsAmount.setVisibility(View.VISIBLE);
            doubleCoinsAmount.setText("X"+dCoinsAmount);
            doubleCoinsAmount.setTag(dCoinsAmount+"");
            gameFragment.setDoubleCoinsAmount(dCoinsAmount);

        }


        ImageButton inventoryBtn = findViewById(R.id.inventory_btn);
        inventoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle bundle = new Bundle();


                Gson gson = new Gson();
                String response = sp.getString("bought items","");

                inventoryFragment = new InventoryDialog();
                if(response !="")
                {
                    bundle.putString("inventory",response);

                    inventoryFragment.setArguments(bundle);

                }

                // Show DialogFragment
                inventoryFragment.show(fm, "Dialog Fragment");
            }
        });

        ImageButton resetBtn = findViewById(R.id.reset_btn);
        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gameFragment.reset();
            }
        });

        ImageButton shopBtn = findViewById(R.id.shop_btn);
        shopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openShop();
            }
        });


        final ImageButton musicBtn = findViewById(R.id.music_btn);
        musicBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(musicOn)
                {
                    mServ.pauseMusic();
                    musicOn = false;
                    musicBtn.setSelected(false);
                }
                else
                {
                    //mServ.startMusic();
                    musicOn = true;
                    musicBtn.setSelected(true);
                    if(mServ.getWorld() !=0)
                    {
                        mServ.pauseMusic();
                        mServ.setWorld(0);
                        mServ.startMusic();
                    }
                    else
                    {
                        mServ.startMusic();
                    }
                }

            }
        });



        updateUI();



        //BIND Music Service

        doBindService();
        music = new Intent();
        music.setClass(this, MusicService.class);
        musicOn = sp.getBoolean("musicOn",true);
        if(musicOn)
        {
            startService(music);

            musicBtn.setSelected(true);
        }
        else
        {

            musicBtn.setSelected(false);
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

    private void updateUI()
    {
        String player1Str = getIntent().getStringExtra("player1");
        String player2Str = getIntent().getStringExtra("player2");

        switch (world)
        {
            case 0:
            {
                stageBar.setBackground(getResources().getDrawable(R.drawable.forest_stage_bar));
                gameTitle.setBackground(getResources().getDrawable(R.drawable.forest_stage_title));
                backgroundLayout.setBackground(getResources().getDrawable(R.drawable.forest_game_background));
                break;
            }
            case 1:
            {
                stageBar.setBackground(getResources().getDrawable(R.drawable.candy_stage_bar));
                gameTitle.setBackground(getResources().getDrawable(R.drawable.candy_stage_title));
                backgroundLayout.setBackground(getResources().getDrawable(R.drawable.candy_game_background));
                break;
            }
            case 2:
            {
                stageBar.setBackground(getResources().getDrawable(R.drawable.city_stage_bar));
                gameTitle.setBackground(getResources().getDrawable(R.drawable.city_stage_title));
                backgroundLayout.setBackground(getResources().getDrawable(R.drawable.city_game_background));
                break;
            }
        }
    }


    @Override
    public void updateShuffle(int curShuffle) {
        if(!gameFragment.isCancelShuffle())
        {
            turnsShuffle.setText(curShuffle +"");
        }

    }

    @Override
    public void updateTurns(int turns) {
        turnsLeft.setText(turns +"");
    }

    @Override
    public void updateStage(int stage) {
        gameTitle.setText(stage+"");
    }


    @Override
    public void handleGameover() {
        loseAnim.setVisibility(View.VISIBLE);
        AnimationDrawable animationDrawable = (AnimationDrawable) loseAnim.getDrawable();
        animationDrawable.start();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                loseDialogManager();

            }
        },2100);
    }

    private void loseDialogManager()
    {
        switch (world){
            case 0:
                forestLoseDialog();
                break;
            case 1:
                candyLoseDialog();
                break;
            case 2:
                cityLoseDialog();
                break;

        }

    }

    @Override
    public void victoryManager(int score) {


        winAnim.setVisibility(View.VISIBLE);
        AnimationDrawable animationDrawable = (AnimationDrawable) winAnim.getDrawable();
        animationDrawable.start();
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
            for(int i =0; i<30; i++)
            {
                stages[i] = new Stage();

            }

        }
        stages[stage-1].setCleared(true);

        if(doubleCoinsAmount.getTag() !=null&&Integer.parseInt(doubleCoinsAmount.getTag().toString())> 0)
        {
            score = score*2;
        }


        final int maxScore;
        boolean newHighScore = false;
        int curStage = ((world ) * 10 +stage) -1;
        //stages[curStage].setCleared(true);
        maxScore = stages[curStage].getBestScore();
        if(score > maxScore)
        {
            stages[curStage].setBestScore(score);

            newHighScore = true;

        }
        final boolean tmpNewHighScore = newHighScore;
        final int tmpScore = score;
        Gson gson = new Gson();
        String json = gson.toJson(stages);
        sp.edit().remove("stages").commit();
        sp.edit().putString("stages",json).commit();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                winDialogManager(tmpScore,maxScore,tmpNewHighScore);

            }
        },2100);

    }



    @Override
    public void closeInventory() {
        inventoryFragment.dismiss();
    }

    @Override
    public void cancelShuffle() {
        turnsShuffle.setText("-");
        gameFragment.setCancelShuffle(true);
    }

    @Override
    public void updateDoubleCoins() {

        int tmpAmount = sp.getInt("doubleCoins",0);
        tmpAmount--;
        if(tmpAmount==0)
        {
            doubleCoinsLayout.setVisibility(View.GONE);
        }
        gameFragment.setDoubleCoinsAmount(tmpAmount);
        doubleCoinsAmount.setText("X"+tmpAmount);
        doubleCoinsAmount.setTag(tmpAmount+"");
        sp.edit().putInt("doubleCoins",tmpAmount).commit();
    }

    private void winDialogManager(int score,int maxScore,boolean newHighScore)
    {
        switch (world){
            case 0:
                forestWinDialog(score,maxScore,newHighScore);
                break;
            case 1:
                candyWinDialog(score,maxScore,newHighScore);
                break;
            case 2:
                cityWinDialog(score,maxScore,newHighScore);
                break;

        }



    }

    private void cityWinDialog(int score, int maxScore, boolean newHighScore) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(SoloGameActivity.this);
        LayoutInflater inflater = ((Activity) SoloGameActivity.this).getLayoutInflater();
        View alertView = inflater.inflate(R.layout.victory_dialog, null);
        alertDialog.setView(alertView);
        alertDialog.setCancelable(false);

        ImageView imageView = alertView.findViewById(R.id.dialog_background);
        imageView.setImageResource(R.drawable.dialog_shape_city);


        TextView scoreTxt = alertView.findViewById(R.id.score_txt);
        scoreTxt.setText(getResources().getString(R.string.your_score)+" "+score);
        TextView highScoreTxt = alertView.findViewById(R.id.max_score);

        highScoreTxt.setText(getResources().getString(R.string.highest_score)+" "+maxScore);
        if(newHighScore)
        {
            TextView newHighTxt = alertView.findViewById(R.id.new_high_score);
            newHighTxt.setVisibility(View.VISIBLE);
        }


        final AlertDialog show = alertDialog.show();

        show.getWindow().getDecorView().setBackgroundColor(getResources().getColor(R.color.transparent_full));


        money = money +score;
        sp.edit().putInt("money",money).commit();
        moneyTv.setText(money+"");



        Button nextBtn=  alertView.findViewById(R.id.next_stage);
        nextBtn.setBackground(getResources().getDrawable(R.drawable.city_play_btn));
        if(stage == 10)
        {
            nextBtn.setVisibility(View.GONE);
        }

        Button restartBtn = alertView.findViewById(R.id.restart_stage);
        restartBtn.setBackground(getResources().getDrawable(R.drawable.city_play_btn));

        restartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //moveStage(tmpV);
                show.dismiss();
                gameFragment.reset();
                winAnim.setVisibility(View.GONE);


            }
        });

        Button quitBtn = alertView.findViewById(R.id.quit_stage);
        quitBtn.setBackground(getResources().getDrawable(R.drawable.city_play_btn));

        quitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show.dismiss();
                Intent intent = new Intent(SoloGameActivity.this, CityStageActivity.class);
                startActivity(intent);
                updateDoubleCoins();
            }
        });


        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show.dismiss();
                if(stage <10)
                {
                    stage++;
                    gameTitle.setText(stage+"");
                    gameFragment.setMaxShuffle(gameFragment.getCurShuffle()-1);
                    gameFragment.setStage(gameFragment.getStage()+1);
                    gameFragment.reset();
                    winAnim.setVisibility(View.GONE);

                }

            }
        });
    }

    private void candyWinDialog(int score, int maxScore, boolean newHighScore) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(SoloGameActivity.this);
        LayoutInflater inflater = ((Activity) SoloGameActivity.this).getLayoutInflater();
        View alertView = inflater.inflate(R.layout.victory_dialog, null);
        alertDialog.setView(alertView);
        alertDialog.setCancelable(false);

        ImageView imageView = alertView.findViewById(R.id.dialog_background);
        imageView.setImageResource(R.drawable.dialog_shape_candy);


        TextView scoreTxt = alertView.findViewById(R.id.score_txt);
        scoreTxt.setText(getResources().getString(R.string.your_score)+" "+score);
        TextView highScoreTxt = alertView.findViewById(R.id.max_score);

        highScoreTxt.setText(getResources().getString(R.string.highest_score)+" "+maxScore);
        if(newHighScore)
        {
            TextView newHighTxt = alertView.findViewById(R.id.new_high_score);
            newHighTxt.setVisibility(View.VISIBLE);
        }



        final AlertDialog show = alertDialog.show();

        show.getWindow().getDecorView().setBackgroundColor(getResources().getColor(R.color.transparent_full));

        money = money +score;
        sp.edit().putInt("money",money).commit();
        moneyTv.setText(money+"");

        Button nextBtn=  alertView.findViewById(R.id.next_stage);
        nextBtn.setBackground(getResources().getDrawable(R.drawable.candy_play_btn));

        Button restartBtn = alertView.findViewById(R.id.restart_stage);
        restartBtn.setBackground(getResources().getDrawable(R.drawable.candy_play_btn));

        Button quitBtn = alertView.findViewById(R.id.quit_stage);
        quitBtn.setBackground(getResources().getDrawable(R.drawable.candy_play_btn));

        quitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show.dismiss();
                Intent intent = new Intent(SoloGameActivity.this, CandylandStageActivity.class);
                startActivity(intent);
                updateDoubleCoins();
            }
        });



        restartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //moveStage(tmpV);
                show.dismiss();
                gameFragment.reset();
                winAnim.setVisibility(View.GONE);


            }
        });

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show.dismiss();
                if(stage <10)
                {
                    stage++;
                    gameTitle.setText(stage+"");
                    gameFragment.setMaxShuffle(gameFragment.getMaxShuffle()-1);
                    gameFragment.setStage(gameFragment.getStage()+1);
                    gameFragment.reset();
                    winAnim.setVisibility(View.GONE);

                }
                else
                {
                    Intent intent = new Intent(SoloGameActivity.this,CityStageActivity.class);

                    music.putExtra("worldMusic", 2);

                    mServ.pauseMusic();



                    startActivity(intent);
                }

            }
        });
    }

    private void forestWinDialog(int score, int maxScore, boolean newHighScore) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(SoloGameActivity.this);
        LayoutInflater inflater = ((Activity) SoloGameActivity.this).getLayoutInflater();
        View alertView = inflater.inflate(R.layout.victory_dialog, null);
        alertDialog.setView(alertView);
        alertDialog.setCancelable(false);

        ImageView imageView = alertView.findViewById(R.id.dialog_background);
        imageView.setImageResource(R.drawable.dialog_shape_forest);

        TextView scoreTxt = alertView.findViewById(R.id.score_txt);
        scoreTxt.setText(getResources().getString(R.string.your_score)+" "+score);
        TextView highScoreTxt = alertView.findViewById(R.id.max_score);

        highScoreTxt.setText(getResources().getString(R.string.highest_score)+" "+maxScore);
        if(newHighScore)
        {
            TextView newHighTxt = alertView.findViewById(R.id.new_high_score);
            newHighTxt.setVisibility(View.VISIBLE);
        }


        final AlertDialog show = alertDialog.show();

        show.getWindow().getDecorView().setBackgroundColor(getResources().getColor(R.color.transparent_full));


        money = money +score;
        sp.edit().putInt("money",money).commit();
        moneyTv.setText(money+"");


        Button nextBtn=  alertView.findViewById(R.id.next_stage);
        nextBtn.setBackground(getResources().getDrawable(R.drawable.forest_play_btn));

        Button restartBtn = alertView.findViewById(R.id.restart_stage);
        restartBtn.setBackground(getResources().getDrawable(R.drawable.forest_play_btn));

        Button quitBtn = alertView.findViewById(R.id.quit_stage);
        quitBtn.setBackground(getResources().getDrawable(R.drawable.forest_play_btn));

        quitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show.dismiss();
                Intent intent = new Intent(SoloGameActivity.this, ForestStageActivity.class);
                startActivity(intent);
                updateDoubleCoins();
            }
        });

        restartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //moveStage(tmpV);
                show.dismiss();
                gameFragment.reset();
                winAnim.setVisibility(View.GONE);


            }
        });

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show.dismiss();
                if(stage <10)
                {

                    stage++;

                    gameTitle.setText(stage+"");
                    gameFragment.setMaxShuffle(gameFragment.getMaxShuffle()-1);
                    gameFragment.setStage(gameFragment.getStage()+1);
                    gameFragment.reset();
                    winAnim.setVisibility(View.GONE);

                }
                else
                {
                    Intent intent = new Intent(SoloGameActivity.this,CandylandStageActivity.class);

                    music.putExtra("worldMusic", 1);
                    //stopService(music);
                    mServ.pauseMusic();

                    // music.putExtra("worldMusic", 3);

                    startActivity(intent);
                }

            }
        });
    }


    private void buyExtraLife()
    {
        if(money >= LIFE_PRICE)
        {
            money = money - LIFE_PRICE;
            sp.edit().putInt("money",money).commit();
            moneyTv.setText(money+"");

            gameFragment.setCurShuffle(gameFragment.getMaxShuffle());
            gameFragment.setTurnsLeft((int)(gameFragment.getCardsNum() * 1.5));
            double scoreFactor = 0.9;
            gameFragment.setScoreFactor(Math.pow(0.9,gameFragment.getTurnsLeft()/2));
            gameFragment.setGameOver(false);
            loseAnim.setVisibility(View.GONE);
            turnsLeft.setText(gameFragment.getTurnsLeft()+"");
            turnsShuffle.setText(gameFragment.getCurShuffle()+"");
            show.dismiss();
        }
        else
        {
            Toast.makeText(this, getResources().getString(R.string.not_enough_money_str), Toast.LENGTH_SHORT).show();
        }

    }


    private void useExtraLife(ArrayList<InventorySlot> slots)
    {
        for (int i = 0; i <slots.size();i++)
        {
            InventorySlot slot = slots.get(i);
            if(slot.getBuyable()!=null&&slot.getBuyable().getId()==0)
            {
                slot.setItemCount(slot.getItemCount()-1);
                if(slot.getItemCount()==0)
                {
                    slots.remove(slot);
                    break;
                }

            }

        }
        gameFragment.setCurShuffle(gameFragment.getMaxShuffle());
        gameFragment.setTurnsLeft((int) (gameFragment.getCardsNum() *1.5));
        gameFragment.setScoreFactor(Math.pow(0.9,gameFragment.getTurnsLeft()/2));
        gameFragment.setGameOver(false);
        loseAnim.setVisibility(View.GONE);
        turnsLeft.setText(gameFragment.getTurnsLeft()+"");
        turnsShuffle.setText(gameFragment.getCurShuffle()+"");

        Gson gson = new Gson();

        String json = gson.toJson(inventory);
        sp.edit().remove("bought items").commit();
        sp.edit().putString("bought items",json).commit();
        show.dismiss();
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
/*
        buyablesArray.add(new Buyable(getResources().getString(R.string.extra_life),R.drawable.extra_life,0,2000,0,1));
        buyablesArray.add(new Buyable(getResources().getString(R.string.double_coins),R.drawable.coins_increase,0,1000,1,1));
        buyablesArray.add(new Buyable(getResources().getString(R.string.cancel_shuffle),R.drawable.unshuffle,0,800,3,1));
        buyablesArray.add(new Buyable(getResources().getString(R.string.skip_stage),R.drawable.skip_stage,0,5000,4,1));
        buyablesArray.add(new Buyable(getResources().getString(R.string.extra_life),R.drawable.extra_life_stack,0,5000,0,3));
        buyablesArray.add(new Buyable(getResources().getString(R.string.double_coins),R.drawable.coins_increase_stack,0,2500,1,3));
        buyablesArray.add(new Buyable(getResources().getString(R.string.cancel_shuffle),R.drawable.unshuffle_stack,0,2000,3,3));
        buyablesArray.add(new Buyable(getResources().getString(R.string.skip_stage),R.drawable.skip_stage_stack,0,12000,4,3));
*/
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

    public void forestLoseDialog(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(SoloGameActivity.this);
        LayoutInflater inflater = ((Activity) SoloGameActivity.this).getLayoutInflater();
        View alertView = inflater.inflate(R.layout.gameover_dialog, null);
        alertDialog.setView(alertView);
        alertDialog.setCancelable(false);

        ImageView imageView = alertView.findViewById(R.id.dialog_background);

        imageView.setImageResource(R.drawable.dialog_shape_forest);


        show = alertDialog.show();

        show.getWindow().getDecorView().setBackgroundColor(getResources().getColor(R.color.transparent_full));



        Button quitBtn=  alertView.findViewById(R.id.quit_btn);
        quitBtn.setBackground(getResources().getDrawable(R.drawable.forest_play_btn));

        Button restartBtn = alertView.findViewById(R.id.restart_stage);
        restartBtn.setBackground(getResources().getDrawable(R.drawable.forest_play_btn));

        restartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //moveStage(tmpV);
                show.dismiss();
                gameFragment.reset();
                loseAnim.setVisibility(View.GONE);


            }
        });

        quitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show.dismiss();
                Intent intent = new Intent(SoloGameActivity.this,ForestStageActivity.class);
                startActivity(intent);
                updateDoubleCoins();
            }
        });


        String response = sp.getString("bought items","");
        boolean gotExtraLife =false;
        int amountLife = 0;
        final ArrayList<InventorySlot> slots;
        if(response !="")
        {

            Gson gson = new Gson();
            inventory = gson.fromJson(response,
                    new TypeToken<Inventory>(){}.getType());
            slots = inventory.getUseables();

            for (InventorySlot slot: slots)
            {

                if(slot.getBuyable()!=null&&slot.getBuyable().getId()==0)
                {
                    gotExtraLife = true;
                    amountLife = slot.getItemCount();

                }
            }
        }
        else
        {
            slots = new ArrayList<InventorySlot>(10);
        }
        if(gotExtraLife)
        {
            TextView  amountLifeTv = alertView.findViewById(R.id.amount_tv);
            amountLifeTv.setText("X"+amountLife);
            amountLifeTv.setVisibility(View.VISIBLE);

            TextView priceTv = alertView.findViewById(R.id.price_tv);
            priceTv.setVisibility(View.GONE);

            Button useBtn = alertView.findViewById(R.id.use_btn);
            useBtn.setVisibility(View.VISIBLE);
            useBtn.setBackground(getResources().getDrawable(R.drawable.forest_play_btn));
            useBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    useExtraLife(slots);
                }
            });
            Button buyBtn = alertView.findViewById(R.id.buy_btn);
            buyBtn.setVisibility(View.GONE);

        }
        else
        {
            TextView  amountLifeTv = alertView.findViewById(R.id.amount_tv);
            amountLifeTv.setVisibility(View.GONE);

            TextView priceTv = alertView.findViewById(R.id.price_tv);
            priceTv.setVisibility(View.VISIBLE);
            priceTv.setText("price: " + LIFE_PRICE);

            Button useBtn = alertView.findViewById(R.id.use_btn);
            useBtn.setVisibility(View.GONE);

            Button buyBtn = alertView.findViewById(R.id.buy_btn);
            buyBtn.setVisibility(View.VISIBLE);
            buyBtn.setBackground(getResources().getDrawable(R.drawable.forest_play_btn));
            buyBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    buyExtraLife();
                }
            });
        }
    }

    public void candyLoseDialog(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(SoloGameActivity.this);
        LayoutInflater inflater = ((Activity) SoloGameActivity.this).getLayoutInflater();
        View alertView = inflater.inflate(R.layout.gameover_dialog, null);
        alertDialog.setView(alertView);
        alertDialog.setCancelable(false);

        ImageView imageView = alertView.findViewById(R.id.dialog_background);
        imageView.setImageResource(R.drawable.dialog_shape_candy);


        show = alertDialog.show();

        show.getWindow().getDecorView().setBackgroundColor(getResources().getColor(R.color.transparent_full));



        Button quitBtn=  alertView.findViewById(R.id.quit_btn);
        quitBtn.setBackground(getResources().getDrawable(R.drawable.candy_play_btn));

        Button restartBtn = alertView.findViewById(R.id.restart_stage);
        restartBtn.setBackground(getResources().getDrawable(R.drawable.candy_play_btn));

        restartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //moveStage(tmpV);
                show.dismiss();
                gameFragment.reset();
                loseAnim.setVisibility(View.GONE);


            }
        });

        quitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show.dismiss();
                Intent intent = new Intent(SoloGameActivity.this,CandylandStageActivity.class);
                startActivity(intent);
                updateDoubleCoins();
            }
        });


        String response = sp.getString("bought items","");
        boolean gotExtraLife =false;
        int amountLife = 0;
        final ArrayList<InventorySlot> slots;
        if(response !="")
        {

            Gson gson = new Gson();
            inventory = gson.fromJson(response,
                    new TypeToken<Inventory>(){}.getType());
            slots = inventory.getUseables();

            for (InventorySlot slot: slots)
            {

                if(slot.getBuyable()!=null&&slot.getBuyable().getId()==0)
                {
                    gotExtraLife = true;
                    amountLife = slot.getItemCount();

                }
            }
        }
        else
        {
            slots = new ArrayList<InventorySlot>(10);
        }
        if(gotExtraLife)
        {
            TextView  amountLifeTv = alertView.findViewById(R.id.amount_tv);
            amountLifeTv.setText("X"+amountLife);
            amountLifeTv.setVisibility(View.VISIBLE);

            TextView priceTv = alertView.findViewById(R.id.price_tv);
            priceTv.setVisibility(View.GONE);

            Button useBtn = alertView.findViewById(R.id.use_btn);
            useBtn.setVisibility(View.VISIBLE);
            useBtn.setBackground(getResources().getDrawable(R.drawable.candy_play_btn));
            useBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    useExtraLife(slots);
                }
            });
            Button buyBtn = alertView.findViewById(R.id.buy_btn);
            buyBtn.setVisibility(View.GONE);
        }
        else
        {
            TextView  amountLifeTv = alertView.findViewById(R.id.amount_tv);
            amountLifeTv.setVisibility(View.GONE);

            TextView priceTv = alertView.findViewById(R.id.price_tv);
            priceTv.setVisibility(View.VISIBLE);

            Button useBtn = alertView.findViewById(R.id.use_btn);
            useBtn.setVisibility(View.GONE);

            Button buyBtn = alertView.findViewById(R.id.buy_btn);
            buyBtn.setVisibility(View.VISIBLE);
            buyBtn.setBackground(getResources().getDrawable(R.drawable.candy_play_btn));
            buyBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    buyExtraLife();
                }
            });
        }
    }

    public void cityLoseDialog(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(SoloGameActivity.this);
        LayoutInflater inflater = ((Activity) SoloGameActivity.this).getLayoutInflater();
        View alertView = inflater.inflate(R.layout.gameover_dialog, null);
        alertDialog.setView(alertView);
        alertDialog.setCancelable(false);

        ImageView imageView = alertView.findViewById(R.id.dialog_background);
        imageView.setImageResource(R.drawable.dialog_shape_city);


        show = alertDialog.show();

        show.getWindow().getDecorView().setBackgroundColor(getResources().getColor(R.color.transparent_full));



        Button quitBtn=  alertView.findViewById(R.id.quit_btn);
        quitBtn.setBackground(getResources().getDrawable(R.drawable.city_play_btn));

        Button restartBtn = alertView.findViewById(R.id.restart_stage);
        restartBtn.setBackground(getResources().getDrawable(R.drawable.city_play_btn));

        restartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //moveStage(tmpV);
                show.dismiss();
                gameFragment.reset();
                loseAnim.setVisibility(View.GONE);


            }
        });

        quitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show.dismiss();
                Intent intent = new Intent(SoloGameActivity.this,CityStageActivity.class);
                startActivity(intent);
                updateDoubleCoins();
            }
        });

        String response = sp.getString("bought items","");
        boolean gotExtraLife =false;
        int amountLife = 0;
        final ArrayList<InventorySlot> slots;
        if(response !="")
        {

            Gson gson = new Gson();
            inventory = gson.fromJson(response,
                    new TypeToken<Inventory>(){}.getType());
            slots = inventory.getUseables();

            for (InventorySlot slot: slots)
            {

                if(slot.getBuyable()!=null&&slot.getBuyable().getId()==0)
                {
                    gotExtraLife = true;
                    amountLife = slot.getItemCount();

                }
            }
        }
        else
        {
            slots = new ArrayList<InventorySlot>(10);
        }
        if(gotExtraLife)
        {
            TextView  amountLifeTv = alertView.findViewById(R.id.amount_tv);
            amountLifeTv.setText("X"+amountLife);
            amountLifeTv.setVisibility(View.VISIBLE);

            TextView priceTv = alertView.findViewById(R.id.price_tv);
            priceTv.setVisibility(View.GONE);

            Button useBtn = alertView.findViewById(R.id.use_btn);
            useBtn.setVisibility(View.VISIBLE);
            useBtn.setBackground(getResources().getDrawable(R.drawable.city_play_btn));
            useBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    useExtraLife(slots);
                }
            });
            Button buyBtn = alertView.findViewById(R.id.buy_btn);
            buyBtn.setVisibility(View.GONE);
        }
        else
        {
            TextView  amountLifeTv = alertView.findViewById(R.id.amount_tv);
            amountLifeTv.setVisibility(View.GONE);

            TextView priceTv = alertView.findViewById(R.id.price_tv);
            priceTv.setVisibility(View.VISIBLE);
            priceTv.setText("price: " + LIFE_PRICE);

            Button useBtn = alertView.findViewById(R.id.use_btn);
            useBtn.setVisibility(View.GONE);

            Button buyBtn = alertView.findViewById(R.id.buy_btn);
            buyBtn.setVisibility(View.VISIBLE);
            buyBtn.setBackground(getResources().getDrawable(R.drawable.city_play_btn));
            buyBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    buyExtraLife();
                }
            });
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
        if (mServ != null&&musicOn ) {//&& world !=0

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
    public void onBackPressed() {
        Intent intent;
        switch (world){
            case 0:
                intent = new Intent(SoloGameActivity.this,ForestStageActivity.class);
                startActivity(intent);
                break;
            case 1:
                intent = new Intent(SoloGameActivity.this,CandylandStageActivity.class);
                startActivity(intent);
                break;
            case 2:
                intent = new Intent(SoloGameActivity.this,CityStageActivity.class);
                startActivity(intent);
                break;
        }



/*        music.putExtra("worldMusic", 3);
        //stopService(music);
        mServ.pauseMusic();

        super.onBackPressed();*/
        //super.onBackPressed();
    }
}
