package com.example.memorygame.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.example.memorygame.GameCard;
import com.example.memorygame.musicService.HomeWatcher;
import com.example.memorygame.musicService.MusicService;
import com.example.memorygame.R;

import java.util.ArrayList;
import java.util.Collections;

public class TwoPlayerActivity extends Activity {
    private SharedPreferences sp;
    private Intent music;


    private int world;
    private int cardsNum;
    private int score1;
    private int score2;
    private int gameTurn;
    private int midX;
    private int midY;
    private int firstTeam;
    private int secondTeam;
    private GameCard prevCard1;
    private boolean canReset = true;
    private boolean musicOn = true;
    private boolean firstGame = true;
    private boolean gameStart = true;
    private boolean gameover;
    private boolean flipping = false;
    private boolean firstPlayer;
    private boolean cardFlipping1;
    private boolean cardFlipping2;


    private MediaPlayer flipSound;
    private MediaPlayer correctSound;
    private MediaPlayer victorySound;
    private MediaPlayer resetSound;


    private int[] arrayX;
    private int[] arrayY;
    private int[] originalX;
    private int[] originalY;



    private ArrayList<GameCard> gameCards = new ArrayList<GameCard>();
    private ArrayList<Drawable> cardValues = new ArrayList<Drawable>();

    private LinearLayout progressbarLayout;
    private GridLayout boardLayout;
    private LinearLayout.LayoutParams layoutParams;
    private LinearLayout.LayoutParams boardParams;

    private TextView turnTxt;
    private TextView player1Tv;
    private TextView player2Tv;
    private TextView gameTitle;
    private LinearLayout gameBar;
    private FrameLayout backgroundLayout;
    private ImageView winAnim;


    private ProgressBar player1Bar;
    private ProgressBar player2Bar;

    private HomeWatcher mHomeWatcher;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.two_player_activity);
        sp = getSharedPreferences("info",MODE_PRIVATE);

        flipSound = MediaPlayer.create(this,R.raw.card_flip);
        correctSound = MediaPlayer.create(this,R.raw.correct);
        victorySound = MediaPlayer.create(this,R.raw.victory2);
        resetSound = MediaPlayer.create(this,R.raw.reset2);



        progressbarLayout = findViewById(R.id.progressbar_layout);
        player1Bar = findViewById(R.id.player1_bar);
        player2Bar = findViewById(R.id.player2_bar);

/*        player1Bar.setIndeterminate(false);
        player2Bar.setIndeterminate(false);*/
        player1Bar.setSecondaryProgress(0);



        boardLayout = findViewById(R.id.boardgame_layout);
        ImageButton resetBtn = findViewById(R.id.reset_btn);
        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reset();
            }
        });

        gameBar = findViewById(R.id.stage_bar);
        gameTitle = findViewById(R.id.stage_title);
        backgroundLayout = findViewById(R.id.background_layout);

        player1Tv = findViewById(R.id.player1_tv);
        player2Tv = findViewById(R.id.player2_tv);

        winAnim = findViewById(R.id.win_anim);

        world = getIntent().getIntExtra("world",0);






        createBoard();

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
                    if(mServ.getWorld() !=world)
                    {
                        mServ.pauseMusic();
                        mServ.setWorld(world);
                        mServ.startMusic();
                    }
                    else
                    {
                        mServ.startMusic();
                    }
                }

            }
        });




        //BIND Music Service



        doBindService();
        music = new Intent();
        music.putExtra("worldMusic", world);
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

    private void createBoard()
    {
        //cardsNum =16;
        progressbarLayout.invalidate();
        score1 = 0;
        score2 = 0;
        player1Bar.setProgressDrawable(null);
        player2Bar.setProgressDrawable(null);

/*        player1Bar.setProgressDrawable(getResources().getDrawable(R.drawable.score_bar_red));
        player2Bar.setProgressDrawable(getResources().getDrawable(R.drawable.score_bar_blue));*/


        player1Bar.setProgress(0);
        player2Bar.setProgress(0);
        float scale = getResources().getDisplayMetrics().density;
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, (int) (30 * scale),1);
        params.setMargins(0,0,0,0);
        player1Bar.setLayoutParams(params);
        player1Bar.setSelected(true);
        player2Bar.setSelected(false);


        gameover = false;
        gameStart = true;
        firstPlayer = true;
        gameTurn = 0;
        cardsNum = getIntent().getIntExtra("cardsNum",0);
        firstTeam = getIntent().getIntExtra("team1",0);
        secondTeam = getIntent().getIntExtra("team2",1);

        player1Bar.setMax(cardsNum/4);
        player2Bar.setMax(cardsNum/4);
        updateUI();
        createPictures();
        updateScorebarColor();
        if(cardsNum==16)
        {
            arrayX = new int[16];
            arrayY = new int[16];
            originalX = new int[16];
            originalY = new int[16];
        }
        else if(cardsNum==20)
        {
            arrayX = new int[20];
            arrayY = new int[20];
            originalX = new int[20];
            originalY = new int[20];
        }
        else if(cardsNum==24)
        {
            arrayX = new int[24];
            arrayY = new int[24];
            originalX = new int[24];
            originalY = new int[24];
        }

        ArrayList<Drawable> curCardValues = new ArrayList<Drawable>(cardValues.subList(0, cardsNum));
        int k=0;





        boardLayout.setColumnCount(4);
        boardLayout.setRowCount(cardsNum/4);
        for (int i = 0; i <cardsNum; i++)
        {
            //int scale = (int) getResources().getDisplayMetrics().density;
            GameCard gameCard = new GameCard(TwoPlayerActivity.this,k,world);
            gameCard.setLayoutParams(new GridLayout.LayoutParams());

            gameCard.setOnClickListener(new OnCardClickListener());
            gameCard.setSoundEffectsEnabled(false);
            int padding = (int) ( 2*scale);
            gameCard.setPadding(padding,padding,padding,padding);
            gameCard.setPaddingRelative(padding,padding,padding,padding);
            Drawable drawable = cardValues.get(i);
            gameCard.setFront(drawable);
            gameCard.setImageDrawable(gameCard.getBack());
            //boardLayout.addView(gameCard);

            if(i<cardsNum/2)
            {
                gameCard.setTeam(firstTeam);
            }
            else
            {
                gameCard.setTeam(secondTeam);
            }
            gameCards.add(gameCard);
        }

        Collections.shuffle(gameCards);
        for(int i = 0; i < cardsNum; i++)
        {
            GameCard gameCard = gameCards.get(i);
            boardLayout.addView(gameCard);
            int[] location = new int[2];
            gameCard.getLocationOnScreen(location);
            arrayX[i] = location[0];
            arrayY[i] = location[1];
        }
        boardLayout.post(new Runnable() {
            @Override
            public void run() {
                final GameCard first = gameCards.get(0);
                final ImageView second =(ImageView)gameCards.get(cardsNum-1);
                int[] location1 = new int[2];
                int[] location2 = new int[2];
                first.getLocationOnScreen(location1);
                second.getLocationOnScreen(location2);



                midX = (location1[0] + location2[0])/2;
                midY = (location1[1] + location2[1])/2;
                //Toast.makeText(GameActivity.this, midX+","+midY, Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void updateScorebarColor()
    {
        switch (firstTeam)
        {
            case 0:
            {
                player1Bar.setProgressDrawable(getResources().getDrawable(R.drawable.score_bar_red));
                break;
            }
            case 1:
            {
                player1Bar.setProgressDrawable(getResources().getDrawable(R.drawable.score_bar_blue));
                break;
            }
            case 2:
            {
                player1Bar.setProgressDrawable(getResources().getDrawable(R.drawable.score_bar_green));
                break;
            }
            case 3:
            {
                player1Bar.setProgressDrawable(getResources().getDrawable(R.drawable.score_bar_yellow));
                break;
            }
        }

        switch (secondTeam)
        {
            case 0:
            {
                player2Bar.setProgressDrawable(getResources().getDrawable(R.drawable.score_bar_red));
                break;
            }
            case 1:
            {
                player2Bar.setProgressDrawable(getResources().getDrawable(R.drawable.score_bar_blue));
                break;
            }
            case 2:
            {
                player2Bar.setProgressDrawable(getResources().getDrawable(R.drawable.score_bar_green));
                break;
            }
            case 3:
            {
                player2Bar.setProgressDrawable(getResources().getDrawable(R.drawable.score_bar_yellow));
                break;
            }
        }
    }

    private void createPictures() {
        if(world==0)//forest
        {
            Drawable pic1 = getResources().getDrawable(R.drawable.bat);
            Drawable pic2 = getResources().getDrawable(R.drawable.butterfly);
            Drawable pic3 = getResources().getDrawable(R.drawable.cat);
            Drawable pic4 = getResources().getDrawable(R.drawable.siberian_husky);
            Drawable pic5 = getResources().getDrawable(R.drawable.cow);
            Drawable pic6 = getResources().getDrawable(R.drawable.crab);
            Drawable pic7 = getResources().getDrawable(R.drawable.duck);
            Drawable pic8 = getResources().getDrawable(R.drawable.frog);
            Drawable pic9 = getResources().getDrawable(R.drawable.lion);
            Drawable pic10 = getResources().getDrawable(R.drawable.macaw);
            Drawable pic11 = getResources().getDrawable(R.drawable.monkey);
            Drawable pic12 = getResources().getDrawable(R.drawable.pig);



            cardValues.add(pic1);
            cardValues.add(pic1);
            cardValues.add(pic2);
            cardValues.add(pic2);
            cardValues.add(pic3);
            cardValues.add(pic3);
            cardValues.add(pic4);
            cardValues.add(pic4);
            cardValues.add(pic5);
            cardValues.add(pic5);
            cardValues.add(pic6);
            cardValues.add(pic6);
            cardValues.add(pic7);
            cardValues.add(pic7);
            cardValues.add(pic8);
            cardValues.add(pic8);
            cardValues.add(pic9);
            cardValues.add(pic9);
            cardValues.add(pic10);
            cardValues.add(pic10);
            cardValues.add(pic11);
            cardValues.add(pic11);
            cardValues.add(pic12);
            cardValues.add(pic12);
        }
        else if(world ==1)//candyland
        {
            Drawable pic1 = getResources().getDrawable(R.drawable.donuts);
            Drawable pic2 = getResources().getDrawable(R.drawable.cupcake);
            Drawable pic3 = getResources().getDrawable(R.drawable.icecream);
            Drawable pic4 = getResources().getDrawable(R.drawable.cake);
            Drawable pic5 = getResources().getDrawable(R.drawable.candy);
            Drawable pic6 = getResources().getDrawable(R.drawable.cheese);
            Drawable pic7 = getResources().getDrawable(R.drawable.choclate);
            Drawable pic8 = getResources().getDrawable(R.drawable.cookie);
            Drawable pic9 = getResources().getDrawable(R.drawable.lolipop);
            Drawable pic10 = getResources().getDrawable(R.drawable.pancakes);
            Drawable pic11 = getResources().getDrawable(R.drawable.croissant);
            Drawable pic12 = getResources().getDrawable(R.drawable.watermelon);



            cardValues.add(pic1);
            cardValues.add(pic1);
            cardValues.add(pic2);
            cardValues.add(pic2);
            cardValues.add(pic3);
            cardValues.add(pic3);
            cardValues.add(pic4);
            cardValues.add(pic4);
            cardValues.add(pic5);
            cardValues.add(pic5);
            cardValues.add(pic6);
            cardValues.add(pic6);
            cardValues.add(pic7);
            cardValues.add(pic7);
            cardValues.add(pic8);
            cardValues.add(pic8);
            cardValues.add(pic9);
            cardValues.add(pic9);
            cardValues.add(pic10);
            cardValues.add(pic10);
            cardValues.add(pic11);
            cardValues.add(pic11);
            cardValues.add(pic12);
            cardValues.add(pic12);
        }
        else if(world==2)//city
        {
            Drawable pic1 = getResources().getDrawable(R.drawable.bench);
            Drawable pic2 = getResources().getDrawable(R.drawable.building);
            Drawable pic3 = getResources().getDrawable(R.drawable.camera);
            Drawable pic4 = getResources().getDrawable(R.drawable.chairs);
            Drawable pic5 = getResources().getDrawable(R.drawable.food_stall);
            Drawable pic6 = getResources().getDrawable(R.drawable.fountain);
            Drawable pic7 = getResources().getDrawable(R.drawable.hydrant);
            Drawable pic8 = getResources().getDrawable(R.drawable.phone);
            Drawable pic9 = getResources().getDrawable(R.drawable.school);
            Drawable pic10 = getResources().getDrawable(R.drawable.taxi);
            Drawable pic11 = getResources().getDrawable(R.drawable.train);
            Drawable pic12 = getResources().getDrawable(R.drawable.crane);




            cardValues.add(pic1);
            cardValues.add(pic1);
            cardValues.add(pic2);
            cardValues.add(pic2);
            cardValues.add(pic3);
            cardValues.add(pic3);
            cardValues.add(pic4);
            cardValues.add(pic4);
            cardValues.add(pic5);
            cardValues.add(pic5);
            cardValues.add(pic6);
            cardValues.add(pic6);
            cardValues.add(pic7);
            cardValues.add(pic7);
            cardValues.add(pic8);
            cardValues.add(pic8);
            cardValues.add(pic9);
            cardValues.add(pic9);
            cardValues.add(pic10);
            cardValues.add(pic10);
            cardValues.add(pic11);
            cardValues.add(pic11);
            cardValues.add(pic12);
            cardValues.add(pic12);

        }

    }

    private void updateUI()
    {
        String player1Str = getIntent().getStringExtra("player1");
        String player2Str = getIntent().getStringExtra("player2");
        player1Tv.setText(player1Str);
        player2Tv.setText(player2Str);
        switch (world)
        {
            case 0:
            {
                gameBar.setBackground(getResources().getDrawable(R.drawable.forest_stage_bar));
                gameTitle.setBackground(getResources().getDrawable(R.drawable.forest_stage_title));
                backgroundLayout.setBackground(getResources().getDrawable(R.drawable.forest_game_background));
                break;
            }
            case 1:
            {
                gameBar.setBackground(getResources().getDrawable(R.drawable.candy_stage_bar));
                gameTitle.setBackground(getResources().getDrawable(R.drawable.candy_stage_title));
                backgroundLayout.setBackground(getResources().getDrawable(R.drawable.candy_game_background));
                break;
            }
            case 2:
            {
                gameBar.setBackground(getResources().getDrawable(R.drawable.city_stage_bar));
                gameTitle.setBackground(getResources().getDrawable(R.drawable.city_stage_title));
                backgroundLayout.setBackground(getResources().getDrawable(R.drawable.city_game_background));
                break;
            }
        }
    }

    private class OnCardClickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {

            if(((GameCard)v).isFlipped()==false && !flipping &&!gameover&&canReset )
            {
                //Toast.makeText(TwoPlayerGame.this, "player 1 score " +score1, Toast.LENGTH_SHORT).show();

                playSound(flipSound);
                gameTurn++;
                int value = ((GameCard)v).getValue();
                Drawable imgValue = ((GameCard)v).getFront();
                if(gameTurn ==1)
                {
                    ((GameCard)v).setFlipped(true);
                    prevCard1 = ((GameCard)v);
                    prevCard1.flipFrontAnimation();
                    cardFlipping1 = true;
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            cardFlipping1 = false;
                        }
                    },1000);
                }
                if(gameTurn == 2)
                {
                    ((GameCard)v).setFlipped(true);

                    final GameCard tmpV = (GameCard)v;
                    ((GameCard)v).flipFrontAnimation();
                    cardFlipping2 = true;
                    if(((GameCard)v).getFront()== prevCard1.getFront())
                    {



                        Handler handler2 = new Handler();
                        handler2.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                playSound(correctSound);
                                Handler handler = new Handler();
                                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade);
                                prevCard1.startAnimation(animation);
                                ((GameCard)tmpV).startAnimation(animation);
                                prevCard1.fadeAnimation();
                                ((GameCard)tmpV).fadeAnimation();
                                handler.postDelayed(new Runnable() {
                                    @RequiresApi(api = Build.VERSION_CODES.N)
                                    public void run() {
                                        cardFlipping2 = false;
                                        prevCard1.setVisibility(View.INVISIBLE);
                                        tmpV.setVisibility(View.INVISIBLE);
                                        gameTurn = 0;
                                        if(prevCard1.getTeam()== firstTeam)
                                        {
                                            score1++;
                                            player1Bar.setProgress(score1,true);

                                            player1Bar.post(new Runnable() {
                                                @Override
                                                public void run() {

                                                    player1Bar.invalidate();

                                                }
                                            });


                                            //player1Tv.setText(getResources().getString(R.string.player_turn) +"1" + getResources().getString(R.string.score) +":" +score1);
                                            //player1Bar.setProgress(score1,true);
                                            if(cardsNum/4 ==score1)
                                            {
                                                playSound(victorySound);

                                                gameover = true;
                                                victoryManager();
                                            }
                                        }
                                        else
                                        {
                                            score2++;

                                            player2Bar.setProgress(score2,true);
                                            player2Bar.post(new Runnable() {
                                                @Override
                                                public void run() {

                                                    player2Bar.invalidate();

                                                }
                                            });

                                            //player2Tv.setText(getResources().getString(R.string.player_turn) +"2" + getResources().getString(R.string.score) +":" +score2);
                                            //player2Bar.setProgress(score2,true);
                                            if(cardsNum/4 ==score2)
                                            {
                                                playSound(victorySound);
                                                gameover = true;
                                                victoryManager();
                                            }
                                        }

                                        if(firstPlayer)
                                        {


                                            player1Bar.setSelected(false);
                                            player2Bar.setSelected(true);



                                            float scale = getResources().getDisplayMetrics().density;
                                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, (int) (25 * scale),1);
                                            params.setMargins(params.leftMargin, (int) (2*scale), params.rightMargin,params.bottomMargin);
                                            player1Bar.setLayoutParams(params );

                                            params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, (int) (30 * scale),1);
                                            params.setMargins(0,0,0,0);
                                            player2Bar.setLayoutParams(params);

                                        }
                                        else
                                        {

                                            player2Bar.setSelected(false);
                                            player1Bar.setSelected(true);


                                            float scale = getResources().getDisplayMetrics().density;
                                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, (int) (25 * scale),1);
                                            params.setMargins(params.leftMargin, (int) (2*scale), params.rightMargin,params.bottomMargin);
                                            player2Bar.setLayoutParams(params );

                                            params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, (int) (30 * scale),1);
                                            params.setMargins(0,0,0,0);
                                            player1Bar.setLayoutParams(params);
                                        }
                                        firstPlayer = !firstPlayer;
                                    }
                                },500);
                            }
                        },1000);




                    }
                    else
                    {
                        playSound(flipSound);
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @RequiresApi(api = Build.VERSION_CODES.Q)
                            public void run() {
                                prevCard1.flipBackAnimation();
                                tmpV.flipBackAnimation();
                                prevCard1.setFlipped(false);
                                tmpV.setFlipped(false);
                                gameTurn = 0;
                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        cardFlipping2 = false;
                                    }
                                },1200);
                                if(firstPlayer)
                                {
                                    //turnTxt.setText(getResources().getString(R.string.player_turn)+ "2");
                                    //turnTxt.setTextColor(Color.BLUE);
                                    player1Bar.setSelected(false);
                                    player2Bar.setSelected(true);

                                    float scale = getResources().getDisplayMetrics().density;
                                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, (int) (25 * scale),1);
                                    params.setMargins(params.leftMargin, (int) (2*scale), params.rightMargin,params.bottomMargin);
                                    player1Bar.setLayoutParams(params );

                                    params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, (int) (30 * scale),1);
                                    params.setMargins(0,0,0,0);
                                    player2Bar.setLayoutParams(params);

                                }
                                else
                                {
                                    //turnTxt.setText(getResources().getString(R.string.player_turn)+ "1");
                                    //turnTxt.setTextColor(Color.RED);
                                    player2Bar.setSelected(false);
                                    player1Bar.setSelected(true);

                                    float scale = getResources().getDisplayMetrics().density;
                                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, (int) (25 * scale),1);
                                    params.setMargins(params.leftMargin, (int) (2*scale), params.rightMargin,params.bottomMargin);
                                    player2Bar.setLayoutParams(params );

                                    params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, (int) (30 * scale),1);
                                    params.setMargins(0,0,0,0);
                                    player1Bar.setLayoutParams(params);
                                }
                                firstPlayer = !firstPlayer;
                            }
                        },1000);

                    }

                    //firstPlayer = !firstPlayer;

                }


            }


        }
    }


    public void reset()
    {
        if(canReset &&(!cardFlipping1&&!cardFlipping2))
        {
            playSound(resetSound);
            progressbarLayout.invalidate();
            score1 = 0;
            score2 = 0;



/*            player1Bar.setProgressDrawable(getResources().getDrawable(R.drawable.score_bar_red));
            player2Bar.setProgressDrawable(getResources().getDrawable(R.drawable.score_bar_blue));*/

            player1Bar.setSelected(true);
            player2Bar.setSelected(true);
            player1Bar.setProgress(0);
            player2Bar.setProgress(0);
            player1Bar.setSelected(false);
            player2Bar.setSelected(false);
            player1Bar.setProgress(0);
            player2Bar.setProgress(0);


            player1Bar.setProgress(0);
            player2Bar.setProgress(0);
            player1Bar.setSecondaryProgress(0);
            player2Bar.setSecondaryProgress(0);
            float scale = getResources().getDisplayMetrics().density;
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, (int) (30 * scale),1);
            params.setMargins(0,0,0,0);
            player1Bar.setLayoutParams(params);
            player1Bar.setSelected(true);
            player2Bar.setSelected(false);


/*            player1Bar.setProgressDrawable(null);
            player2Bar.setProgressDrawable(null);*/
            updateScorebarColor();

            player1Bar.post(new Runnable() {
                @Override
                public void run() {
                    player1Bar.invalidate();
                }
            });

            player2Bar.post(new Runnable() {
                @Override
                public void run() {
                    player2Bar.invalidate();
                }
            });


            canReset = false;
            gameover = false;
            firstPlayer = true;
            //timePassed = 0;
            gameStart = true;
            gameTurn = 0;
            //turnTxt.setText(turnsLeft + "");
            createPictures();


            //if(world==0)
            // {
            boolean needToFlip=false;
            for (GameCard gamecard: gameCards)
            {
                if(gamecard.getVisibility() == View.VISIBLE  && (gamecard.getDrawable() != gamecard.getBack()))
                {
                    gamecard.removeFrame();
                    gamecard.setImageDrawable(gamecard.getFront());
                    gamecard.flipBackAnimation();
                    gamecard.setFlipped(false);
                    needToFlip = true;
                }
            }
            if(needToFlip)
            {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        final ArrayList<GameCard> shuffledDeck = new ArrayList<>();
                        for(int i = 0; i < cardsNum; i++)
                        {
                            GameCard gameCard = gameCards.get(i);
                            int[] location = new int[2];
                            gameCard.getLocationOnScreen(location);
                            originalX[i] = location[0];
                            originalY[i] = location[1];

                            shuffledDeck.add(gameCard);

                        }

                        for(int i = 0; i <cardsNum;i++)
                        {
                            GameCard gameCard = shuffledDeck.get(i);
                            gameCard.animate().translationXBy(midX - originalX[i]).setDuration(1000).start();
                            gameCard.animate().translationYBy(midY - originalY[i]).setDuration(1000).start();
                        }

                        Collections.shuffle(shuffledDeck);
                        for(int i = 0; i <cardsNum/2; i++)
                        {
                            GameCard first = shuffledDeck.get(i);
                            GameCard second = shuffledDeck.get(cardsNum-i-1);
                            int[] location1 = new int[2];
                            int[] location2 = new int[2];
                            first.getLocationOnScreen(location1);
                            second.getLocationOnScreen(location2);

                            arrayX[i] = location1[0];
                            arrayY[i] = location1[1];

                            arrayX[cardsNum-i-1] = location2[0];
                            arrayY[cardsNum-i-1] = location2[1];
                        }
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                for (int i =0; i < cardsNum; i++)
                                {
                                    GameCard gameCard = gameCards.get(i);
                                    gameCard.setVisibility(View.VISIBLE);
                                    gameCard.setImageDrawable(gameCard.getBack());
                                    gameCard.animate().alpha(1).setDuration(0).start();
                                    gameCard.setFlipped(false);


                                }
                                for(int i = 0; i<cardsNum;i++)
                                {
                                    GameCard gameCard = gameCards.get(i);

                                    gameCard.animate().translationXBy(arrayX[i] - midX).setDuration(1000).start();
                                    gameCard.animate().translationYBy(arrayY[i] - midY).setDuration(1000).start();
                                }
                            }
                        },1000);
                        Handler handler2 = new Handler();
                        handler2.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                canReset = true;
                            }
                        },2000);
                    }
                },1100);
            }
            else
            {
                final ArrayList<GameCard> shuffledDeck = new ArrayList<>();
                for(int i = 0; i < cardsNum; i++)
                {
                    GameCard gameCard = gameCards.get(i);
                    int[] location = new int[2];
                    gameCard.getLocationOnScreen(location);
                    originalX[i] = location[0];
                    originalY[i] = location[1];

                    shuffledDeck.add(gameCard);

                }

                for(int i = 0; i <cardsNum;i++)
                {
                    GameCard gameCard = shuffledDeck.get(i);
                    gameCard.animate().translationXBy(midX - originalX[i]).setDuration(1000).start();
                    gameCard.animate().translationYBy(midY - originalY[i]).setDuration(1000).start();
                }

                Collections.shuffle(shuffledDeck);
                for(int i = 0; i <cardsNum/2; i++)
                {
                    GameCard first = shuffledDeck.get(i);
                    GameCard second = shuffledDeck.get(cardsNum-i-1);
                    int[] location1 = new int[2];
                    int[] location2 = new int[2];
                    first.getLocationOnScreen(location1);
                    second.getLocationOnScreen(location2);

                    arrayX[i] = location1[0];
                    arrayY[i] = location1[1];

                    arrayX[cardsNum-i-1] = location2[0];
                    arrayY[cardsNum-i-1] = location2[1];
                }
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        for (int i =0; i < cardsNum; i++)
                        {
                            GameCard gameCard = gameCards.get(i);
                            gameCard.setVisibility(View.VISIBLE);
                            gameCard.setImageDrawable(gameCard.getBack());
                            gameCard.animate().alpha(1).setDuration(0).start();
                            gameCard.setFlipped(false);


                        }
                        for(int i = 0; i<cardsNum;i++)
                        {
                            GameCard gameCard = gameCards.get(i);

                            gameCard.animate().translationXBy(arrayX[i] - midX).setDuration(1000).start();
                            gameCard.animate().translationYBy(arrayY[i] - midY).setDuration(1000).start();
                        }
                    }
                },1000);
                Handler handler2 = new Handler();
                handler2.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        canReset = true;
                    }
                },2000);
            }





        }

    }


    private void victoryManager()
    {
        winAnim.setVisibility(View.VISIBLE);
        AnimationDrawable animationDrawable = (AnimationDrawable) winAnim.getDrawable();
        animationDrawable.start();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                winAnim.setVisibility(View.GONE);
                reset();
            }
        },3500);
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
        player1Bar.setSelected(true);
        player2Bar.setSelected(true);
        player1Bar.setProgress(0);
        player2Bar.setProgress(0);
        player1Bar.setSelected(false);
        player2Bar.setSelected(false);
        player1Bar.setProgress(0);
        player2Bar.setProgress(0);
        super.onDestroy();



    }


    @Override
    public void onBackPressed() {
        music.putExtra("worldMusic", 3);
        //stopService(music);
        mServ.pauseMusic();

        super.onBackPressed();
        super.onBackPressed();
    }

    private void playSound(MediaPlayer mediaPlayer)
    {
        if(mediaPlayer.isPlaying())
        {
            mediaPlayer.pause();
        }

        mediaPlayer.start();
    }
}
