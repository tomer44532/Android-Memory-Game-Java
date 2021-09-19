package com.example.memorygame.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.example.memorygame.GameCard;
import com.example.memorygame.musicService.HomeWatcher;
import com.example.memorygame.musicService.MusicService;
import com.example.memorygame.R;

import java.util.ArrayList;
import java.util.Collections;

public class TutorialActivity extends Activity {
    private SharedPreferences sp;
    private HomeWatcher mHomeWatcher;
    private boolean musicOn = true;
    private Intent music;

    private GridLayout gridLayout;
    private TextView guideTxt;
    private int stage;
    private int guesses;
    private int midX;
    private int midY;
    private ArrayList<ImageView> cursors = new ArrayList<ImageView>();
    private ArrayList<GameCard> gameCards = new ArrayList<GameCard>();
    private ArrayList<FrameLayout> frameLayouts = new ArrayList<FrameLayout>();

    private TextView shuffleTxt;
    private TextView turnsTxt;
    private ImageView shuffleCursor;
    private ImageView resetCursor;
    private ImageButton resetBtn;

    private int[] arrayX = new int[16];
    private int[] arrayY = new int[16];
    private int[] originalX = new int[16];
    private int[] originalY = new int[16];


    private MediaPlayer flipSound;
    private MediaPlayer correctSound;
    private MediaPlayer resetSound;
    private MediaPlayer shuffleSound;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tutorial_layout);
        sp = getSharedPreferences("info",MODE_PRIVATE);

        flipSound = MediaPlayer.create(this,R.raw.card_flip);
        correctSound= MediaPlayer.create(this,R.raw.correct);
        resetSound = MediaPlayer.create(this,R.raw.reset2);
        shuffleSound = MediaPlayer.create(this,R.raw.shuffle);

        gridLayout = findViewById(R.id.boardgame_layout);
        guideTxt = findViewById(R.id.guide_txt);
        turnsTxt = findViewById(R.id.turns_left);
        shuffleTxt = findViewById(R.id.shuffle_turns);
        shuffleCursor = findViewById(R.id.shuffle_cursor);
        resetCursor = findViewById(R.id.reset_cursor);
        resetBtn  = findViewById(R.id.reset_btn);
        resetBtn.setSoundEffectsEnabled(false);

        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(stage == 7)
                {
                    reset();
                    stage++;
                    resetCursor.setVisibility(View.GONE);
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            guideTxt.setText(getResources().getString(R.string.ready_to_play_str));
                            Handler handler2 = new Handler();
                            handler2.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    sp.edit().putBoolean("doneTutorial",true).commit();
                                    Intent intent = new Intent(TutorialActivity.this,StageSelectActivity.class);
                                    startActivity(intent);
                                }
                            },1500);
                        }
                    },2000);
                }

            }
        });

        createBoard();




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

    private void createBoard()
    {
        guideTxt.setText(getResources().getString(R.string.press_card_str));
        int scale = (int) getResources().getDisplayMetrics().density;
        for(int i = 0; i <16; i++)
        {
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            FrameLayout frameLayout = new FrameLayout(this);
            frameLayout.setLayoutParams(params);
            //frameLayout.setPadding(scale * 3,scale * 3,scale * 3,scale * 3);



            final GameCard gameCard = new GameCard(this,-1,0);
            gameCard.setImageDrawable(gameCard.getBack());
            gameCard.setTag(i+"");
            gameCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cardClick(Integer.parseInt(gameCard.getTag().toString()));
                }
            });
            gameCard.setSoundEffectsEnabled(false);
            gameCards.add(gameCard);


            gameCard.setPadding(scale * 3,scale * 3,scale * 3,scale * 3);
            frameLayout.addView(gameCard);
            if(i==1 ||i==7)
            {
                gameCard.setFront(getResources().getDrawable(R.drawable.siberian_husky));
            }
            else if(i==4)
            {
                gameCard.setFront(getResources().getDrawable(R.drawable.crab));
            }
            else if(i==10)
            {
                gameCard.setFront(getResources().getDrawable(R.drawable.bat));
            }
            else if(i==13)
            {
                gameCard.setFront(getResources().getDrawable(R.drawable.frog));
            }





                ImageView imageView = new ImageView(this);
                imageView.setImageDrawable(getResources().getDrawable(R.drawable.cursor_anim));
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);

                AnimationDrawable animationDrawable = (AnimationDrawable) imageView.getDrawable();
                animationDrawable.start();
                frameLayout.addView(imageView);
                FrameLayout.LayoutParams gravity = (FrameLayout.LayoutParams) imageView.getLayoutParams();
                gravity.gravity = Gravity.CENTER;
                imageView.setTag(i+"");
                imageView.setLayoutParams(gravity);
                cursors.add(imageView);
                //frameLayout.addView(imageView);

            if(i!=1)
            {
                imageView.setVisibility(View.INVISIBLE);
            }

            int[] location = new int[2];
            frameLayout.getLocationOnScreen(location);
            arrayX[i] = location[0];
            arrayY[i] = location[1];
            frameLayouts.add(frameLayout);
            gridLayout.addView(frameLayout);
        }

        gridLayout.post(new Runnable() {
            @Override
            public void run() {
                final GameCard first = gameCards.get(0);
                final ImageView second =(ImageView)gameCards.get(16-1);
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

    private void cardClick(int tag)
    {
        if(stage==0 && tag==1)
        {
            playSound(flipSound);
            cursors.get(tag).setVisibility(View.INVISIBLE);
            gameCards.get(tag).flipFrontAnimation();

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    cursors.get(13).setVisibility(View.VISIBLE);
                    stage++;//1
                    guideTxt.setText(getResources().getString(R.string.press_scond_card_str));
                }
            },1000);

        }
        else if(stage ==1 && tag ==13)
        {
            playSound(flipSound);
            cursors.get(tag).setVisibility(View.INVISIBLE);
            gameCards.get(tag).flipFrontAnimation();
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    gameCards.get(1).flipBackAnimation();
                    gameCards.get(13).flipBackAnimation();
                    turnsTxt.setText("9");
                    shuffleTxt.setText("1");
                    Handler handler2 = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            stage++;//2
                            cursors.get(7).setVisibility(View.VISIBLE);
                            guideTxt.setText(getResources().getString(R.string.press_card_str));
                        }
                    },1000);
                }
            },2000);
        }
        else if(stage ==2 && tag==7)
        {
            playSound(flipSound);
            cursors.get(tag).setVisibility(View.INVISIBLE);
            gameCards.get(tag).flipFrontAnimation();
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    guideTxt.setText(getResources().getString(R.string.find_second_card_str));
                    stage++;//3
                }
            },1000);
        }
        else if(stage==3&& tag ==1)
        {
            playSound(flipSound);
            cursors.get(tag).setVisibility(View.INVISIBLE);
            gameCards.get(tag).flipFrontAnimation();
            final Handler handler  = new Handler();
            cursors.get(1).setVisibility(View.GONE);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    playSound(correctSound);
                    gameCards.get(1).fadeAnimation();
                    gameCards.get(7).fadeAnimation();
                    Handler handler2 = new Handler();
                    handler2.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            guideTxt.setText(getResources().getString(R.string.guide_turns_left_str));
                            final ImageView turnCursor = findViewById(R.id.turns_cursor);
                            turnCursor.setVisibility(View.VISIBLE);
                            Handler handler3 = new Handler();
                            handler3.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    turnCursor.setVisibility(View.GONE);
                                    Handler handler4 = new Handler();
                                    handler4.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {

                                            shuffleCursor.setVisibility(View.VISIBLE);
                                            guideTxt.setText(getResources().getString(R.string.look_shuffle_str));
                                            Handler handler5 = new Handler();
                                            handler5.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    shuffleCursor.setVisibility(View.GONE);
                                                    guideTxt.setText(getResources().getString(R.string.press_card_str));
                                                    cursors.get(10).setVisibility(View.VISIBLE);
                                                    stage++;//5
                                                }
                                            },2000);

                                        }
                                    },100);
                                }
                            },2000);
                        }
                    },1000);

                }
            },2000);
            stage++;//4
        }
        else if(stage ==3 &&tag !=1 &&guesses==0)
        {
            guideTxt.setText(getResources().getString(R.string.not_the_right_card));
            guesses++;
        }
        else if(stage ==3 &&tag !=1 &&tag!=7 &&guesses==1)
        {
            guideTxt.setText(getResources().getString(R.string.ur_close));
            guesses++;
        }
/*        else if(stage ==3 &&tag !=1&&tag!=7 &&guesses==2)
        {
            guideTxt.setText(getResources().getString(R.string.almost_there_str));
            guesses++;
        }*/
        else if(stage ==3 && tag!=1&&tag!=7 &&guesses==2&&gameCards.get(1).getDrawable()!=gameCards.get(1).getFront())
        {
            guideTxt.setText(getResources().getString(R.string.clue_str));
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    cursors.get(1).setVisibility(View.VISIBLE);
                }
            },500);

        }
        else if(stage ==5 && tag ==10)
        {
            playSound(flipSound);
            gameCards.get(tag).flipFrontAnimation();
            cursors.get(tag).setVisibility(View.GONE);
            Handler handler =  new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    stage++;//6
                    cursors.get(4).setVisibility(View.VISIBLE);
                    guideTxt.setText(getResources().getString(R.string.find_second_card_str));
                }
            },1000);
        }
        else if(stage ==6 && tag == 4)
        {
            playSound(flipSound);
            gameCards.get(tag).flipFrontAnimation();
            cursors.get(tag).setVisibility(View.GONE);
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    gameCards.get(4).flipBackAnimation();
                    gameCards.get(10).flipBackAnimation();
                    Handler handler2 = new Handler();
                    handler2.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            shuffleTxt.setText("0");
                            turnsTxt.setText("8");
                            guideTxt.setText(getResources().getString(R.string.look_shuffle_str));
                            shuffleCursor.setVisibility(View.VISIBLE);
                            Handler handler3 = new Handler();
                            handler3.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    shuffle();
                                    Handler handler4 = new Handler();
                                    handler4.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            shuffleCursor.setVisibility(View.GONE);

                                            Handler handler5 = new Handler();
                                            handler5.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    resetCursor.setVisibility(View.VISIBLE);
                                                    AnimationDrawable animationDrawable = (AnimationDrawable) resetCursor.getDrawable();
                                                    animationDrawable.start();
                                                    guideTxt.setText(getResources().getString(R.string.press_reset_str));
                                                    stage++;//7
                                                }
                                            },800);
                                        }
                                    },1000);
                                }
                            },2000);

                        }
                    },2000);
                    //shuffle();
                }
            },2000);
        }

    }


    private void shuffle() {

        playSound(shuffleSound);
        Collections.shuffle(frameLayouts);


        for (int i = 0; i < 16; i++) {
            final FrameLayout first = frameLayouts.get(i);
            final FrameLayout second = frameLayouts.get(16 - i - 1);

            int[] location1 = new int[2];
            int[] location2 = new int[2];
            first.getLocationOnScreen(location1);
            second.getLocationOnScreen(location2);

            int[] firstProp = new int[4];
            int[] secondProp = new int[4];

            firstProp[0] = first.getLeft();
            firstProp[1] = first.getTop();
            firstProp[2] = first.getRight();
            firstProp[3] = first.getBottom();

            secondProp[0] = second.getLeft();
            secondProp[1] = second.getTop();
            secondProp[2] = second.getRight();
            secondProp[3] = second.getBottom();


            first.animate().translationXBy(location2[0] - location1[0]).setDuration(2000).withLayer().start();
            first.animate().translationYBy(location2[1] - location1[1]).setDuration(2000).withLayer().start();

            second.animate().translationXBy(location1[0] - location2[0]).setDuration(2000).withLayer().start();
            second.animate().translationYBy(location1[1] - location2[1]).setDuration(2000).withLayer().start();
            first.postInvalidateOnAnimation();

        }
    }

    private void reset()
    {
        playSound(resetSound);
        turnsTxt.setText("10");
        shuffleTxt.setText("2");

        final ArrayList<FrameLayout> shuffledDeck = new ArrayList<>();
        for(int i = 0; i < 16; i++)
        {
            FrameLayout frameLayout = frameLayouts.get(i);
            int[] location = new int[2];
            frameLayout.getLocationOnScreen(location);
            originalX[i] = location[0];
            originalY[i] = location[1];

            shuffledDeck.add(frameLayout);

        }

        for(int i = 0; i <16;i++)
        {
            FrameLayout frameLayout = shuffledDeck.get(i);
            frameLayout.animate().translationXBy(midX - originalX[i]).setDuration(1000).start();
            frameLayout.animate().translationYBy(midY - originalY[i]).setDuration(1000).start();
        }

        Collections.shuffle(shuffledDeck);
        for(int i = 0; i <8; i++)
        {
            FrameLayout first = shuffledDeck.get(i);
            FrameLayout second = shuffledDeck.get(16-i-1);
            int[] location1 = new int[2];
            int[] location2 = new int[2];
            first.getLocationOnScreen(location1);
            second.getLocationOnScreen(location2);

            arrayX[i] = location1[0];
            arrayY[i] = location1[1];

            arrayX[16-i-1] = location2[0];
            arrayY[16-i-1] = location2[1];
        }
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                for (int i =0; i < 16; i++)
                {
                    GameCard gameCard = gameCards.get(i);
                    gameCard.setVisibility(View.VISIBLE);
                    gameCard.setImageDrawable(gameCard.getBack());
                    gameCard.animate().alpha(1).setDuration(0).start();
                    gameCard.setFlipped(false);


                }
                for(int i = 0; i<16;i++)
                {
                    FrameLayout frameLayout = frameLayouts.get(i);

                    frameLayout.animate().translationXBy(arrayX[i] - midX).setDuration(1000).start();
                    frameLayout.animate().translationYBy(arrayY[i] - midY).setDuration(1000).start();
                }
            }
        },1000);

    }


    private void playSound(MediaPlayer mediaPlayer)
    {
        if(mediaPlayer.isPlaying())
        {
            mediaPlayer.pause();
        }

        mediaPlayer.start();
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
