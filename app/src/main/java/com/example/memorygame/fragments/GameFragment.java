package com.example.memorygame.fragments;

import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.app.Fragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.memorygame.GameCard;
import com.example.memorygame.interfaces.HandleGameCards;
import com.example.memorygame.R;

import java.util.ArrayList;
import java.util.Collections;


public class GameFragment extends Fragment {
     private int world;// 0 = forest, 1 = candyland, 2 = city
     private int stage;
     private int maxShuffle;
     private int curShuffle;
     private int midX;
     private int midY;
     private int score;
     private int coins;
     private int timePassed=0;
     private int turnsLeft;
     private int gameTurn;
     private int cardsNum;
     private int doubleCoinsAmount;
     private double scoreFactor =1;
     private boolean flipping = false;
     private boolean gameStart = true;
     private boolean gameOver = false;
     private boolean shuffeling;
     private boolean canReset = true;
     private boolean canShuffle = true;
     private boolean cardFlipping1;
     private boolean cardFlipping2;
     private boolean cancelShuffle;



    private int[] arrayX;
    private int[] arrayY;
    private int[] originalX;
    private int[] originalY;

    private MediaPlayer flipSound;
    private MediaPlayer correctSound;
    private MediaPlayer victorySound;
    private MediaPlayer gameoverSound;
    private MediaPlayer resetSound;
    private MediaPlayer shuffleSound;


    private Drawable cardBack;


    private GameCard prevCard1;


    //EditText turnsShuffle;
    private GridLayout boardLayout;

    private ArrayList<Integer> cards = new ArrayList<Integer>();
    private ArrayList<Drawable> cardValues = new ArrayList<Drawable>();
    private ArrayList<GameCard> gameCards = new ArrayList<GameCard>();

    public int getDoubleCoinsAmount() {
        return doubleCoinsAmount;
    }

    public void setDoubleCoinsAmount(int doubleCoinsAmount) {
        this.doubleCoinsAmount = doubleCoinsAmount;
    }

    public int getWorld() {
        return world;
    }

    public void setWorld(int world) {
        this.world = world;
    }

    public int getStage() {
        return stage;
    }

    public void setStage(int stage) {
        this.stage = stage;
    }

    public int getCurShuffle() {
        return curShuffle;
    }

    public void setCurShuffle(int curShuffle) {
        this.curShuffle = curShuffle;
    }

    public int getMidX() {
        return midX;
    }

    public void setMidX(int midX) {
        this.midX = midX;
    }

    public int getMidY() {
        return midY;
    }

    public void setMidY(int midY) {
        this.midY = midY;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getCoins() {
        return coins;
    }

    public void setCoins(int coins) {
        this.coins = coins;
    }

    public int getTimePassed() {
        return timePassed;
    }

    public void setTimePassed(int timePassed) {
        this.timePassed = timePassed;
    }

    public int getTurnsLeft() {
        return turnsLeft;
    }

    public void setTurnsLeft(int turnsLeft) {
        this.turnsLeft = turnsLeft;
    }

    public int getGameTurn() {
        return gameTurn;
    }

    public void setGameTurn(int gameTurn) {
        this.gameTurn = gameTurn;
    }

    public int getCardsNum() {
        return cardsNum;
    }

    public void setCardsNum(int cardsNum) {
        this.cardsNum = cardsNum;
    }

    public double getScoreFactor() {
        return scoreFactor;
    }

    public void setScoreFactor(double scoreFactor) {
        this.scoreFactor = scoreFactor;
    }

    public boolean isFlipping() {
        return flipping;
    }

    public void setFlipping(boolean flipping) {
        this.flipping = flipping;
    }

    public boolean isGameStart() {
        return gameStart;
    }

    public void setGameStart(boolean gameStart) {
        this.gameStart = gameStart;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }

    public boolean isShuffeling() {
        return shuffeling;
    }

    public void setShuffeling(boolean shuffeling) {
        this.shuffeling = shuffeling;
    }

    public boolean isCanReset() {
        return canReset;
    }

    public void setCanReset(boolean canReset) {
        this.canReset = canReset;
    }

    public boolean isCanShuffle() {
        return canShuffle;
    }

    public void setCanShuffle(boolean canShuffle) {
        this.canShuffle = canShuffle;
    }

    public boolean isCardFlipping1() {
        return cardFlipping1;
    }

    public void setCardFlipping1(boolean cardFlipping1) {
        this.cardFlipping1 = cardFlipping1;
    }

    public boolean isCardFlipping2() {
        return cardFlipping2;
    }

    public void setCardFlipping2(boolean cardFlipping2) {
        this.cardFlipping2 = cardFlipping2;
    }

    public boolean isCancelShuffle() {
        return cancelShuffle;
    }

    public void setCancelShuffle(boolean cancelShuffle) {
        this.cancelShuffle = cancelShuffle;
    }

    public int getMaxShuffle() {
        return maxShuffle;
    }

    public void setMaxShuffle(int maxShuffle) {
        this.maxShuffle = maxShuffle;
    }






    public static GameFragment newInstance(String username)
    {
        GameFragment gameFragment = new GameFragment();
        Bundle bundle = new Bundle();
        //bundle.putString("user_name",username);
        gameFragment.setArguments(bundle);


        return gameFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.game_fragment,container,false);
        //TextView textView = rootView.findViewById(R.id.username_output);
       // textView.setText(getArguments().getString("user_name"));


        flipSound = MediaPlayer.create(getActivity(),R.raw.card_flip);
        correctSound = MediaPlayer.create(getActivity(),R.raw.correct);
        victorySound = MediaPlayer.create(getActivity(),R.raw.victory2);
        gameoverSound = MediaPlayer.create(getActivity(),R.raw.gameover);
        resetSound = MediaPlayer.create(getActivity(),R.raw.reset2);
        shuffleSound = MediaPlayer.create(getActivity(),R.raw.shuffle);

        //victorySound.start();



        boardLayout = rootView.findViewById(R.id.boardgame_layout);
        //turnsShuffle = rootView.findViewById(R.id.shuffle_turns);
        //world = 0;


        world = getArguments().getInt("world");
        stage = getArguments().getInt("stage");

        createBoard();

        return  rootView;
    }

    public void createBoard()
    {
        cancelShuffle = false;

        maxShuffle = 11 - stage;
        curShuffle = maxShuffle;
        ((HandleGameCards)getActivity()).updateShuffle(curShuffle);((HandleGameCards)getActivity()).updateShuffle(curShuffle);

        //turnsShuffle.setText(curShuffle+"");

        gameOver = false;
        timePassed = 0;
        score = 0;
        gameStart = true;

        //Toast.makeText(getActivity(), "cards num "+cardsNum, Toast.LENGTH_SHORT).show();
        gameTurn = 0;
        ((HandleGameCards)getActivity()).updateStage((world*10) + stage);

        //turnTxt.setText(turnsLeft + "");
        if(world==0)//forest
        {
            arrayX = new int[16];
            arrayY = new int[16];
            originalX = new int[16];
            originalY = new int[16];
            cardBack = getResources().getDrawable(R.drawable.back_card_forest);
            boardLayout.setRowCount(4);
            boardLayout.setColumnCount(4);
            cardsNum = 16;
            turnsLeft = (int) (cardsNum*1.5);
            ((HandleGameCards)getActivity()).updateTurns(turnsLeft);
            createPictures();
            boardLayout.removeAllViews();
            Collections.shuffle(cardValues);
            for (int i = 1; i <= 8; i++) {
                cards.add((Integer) i);
                cards.add((Integer) i);
            }
            Collections.shuffle(cards);
            for (int i = 0; i < 16; i++) {
                int scale = (int) getResources().getDisplayMetrics().density;
                GameCard gameCard = new GameCard(getActivity(), cards.get(i),0);
                //gameCard.setOnClickListener(new GameActivity.OnCardClickListener());
                gameCard.setOnClickListener(new OnCardClickListener());
                gameCard.setSoundEffectsEnabled(false);
                int padding = (int) ( 2*scale);
                gameCard.setPadding(padding,padding,padding,padding);
                gameCard.setPaddingRelative(padding,padding,padding,padding);
                Drawable drawable = cardValues.get(i);
                gameCard.setFront(drawable);
                //gameCard.setBack(cardBack);
                gameCard.setImageDrawable(gameCard.getBack());
                boardLayout.addView(gameCard);
                gameCards.add(gameCard);
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
        else if(world == 1)
        {
            arrayX = new int[20];
            arrayY = new int[20];
            originalX = new int[20];
            originalY = new int[20];
            cardBack = getResources().getDrawable(R.drawable.back_card_candy);
            boardLayout.setRowCount(5);
            boardLayout.setColumnCount(4);
            cardsNum = 20;
            turnsLeft = (int) (cardsNum*1.5);
            ((HandleGameCards)getActivity()).updateTurns(turnsLeft);
            createPictures();
            boardLayout.removeAllViews();
            Collections.shuffle(cardValues);
            for (int i = 1; i <= 10; i++) {
                cards.add((Integer) i);
                cards.add((Integer) i);
            }
            Collections.shuffle(cards);
            for (int i = 0; i < 20; i++) {
                int scale = (int) getResources().getDisplayMetrics().density;
                GameCard gameCard = new GameCard(getActivity(), cards.get(i),1);
                //gameCard.setOnClickListener(new GameActivity.OnCardClickListener());
                gameCard.setOnClickListener(new OnCardClickListener());
                gameCard.setSoundEffectsEnabled(false);
                int padding = (int) ( 2*scale);
                gameCard.setPadding(padding,padding,padding,padding);
                gameCard.setPaddingRelative(padding,padding,padding,padding);
                Drawable drawable = cardValues.get(i);
                gameCard.setFront(drawable);
                //gameCard.setBack(cardBack);
                //gameCard.setBack(cardBack);
                gameCard.setImageDrawable(gameCard.getBack());
                boardLayout.addView(gameCard);
                gameCards.add(gameCard);
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
        else if(world ==2)
        {
            arrayX = new int[24];
            arrayY = new int[24];
            originalX = new int[24];
            originalY = new int[24];

            cardBack = getResources().getDrawable(R.drawable.back_card_city);
            boardLayout.setRowCount(6);
            boardLayout.setColumnCount(4);
            cardsNum = 24;
            turnsLeft = (int) (cardsNum*1.5);
            ((HandleGameCards)getActivity()).updateTurns(turnsLeft);
            createPictures();
            boardLayout.removeAllViews();
            Collections.shuffle(cardValues);
            for (int i = 1; i <= cardsNum/2; i++) {
                cards.add((Integer) i);
                cards.add((Integer) i);
            }
            Collections.shuffle(cards);
            for (int i = 0; i < cardsNum; i++) {
                int scale = (int) getResources().getDisplayMetrics().density;
                GameCard gameCard = new GameCard(getActivity(), cards.get(i),2);
                //gameCard.setOnClickListener(new GameActivity.OnCardClickListener());
                gameCard.setOnClickListener(new OnCardClickListener());
                gameCard.setSoundEffectsEnabled(false);
                int padding = (int) ( 2*scale);
                gameCard.setPadding(padding,padding,padding,padding);
                gameCard.setPaddingRelative(padding,padding,padding,padding);
                Drawable drawable = cardValues.get(i);
                gameCard.setFront(drawable);
                //gameCard.setBack(cardBack);
                //gameCard.setBack(cardBack);

                gameCard.setImageDrawable(gameCard.getBack());
                boardLayout.addView(gameCard);
                gameCards.add(gameCard);
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

    private class OnCardClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if(!gameOver&&((GameCard)v).isFlipped()==false && !flipping &&!shuffeling && canReset )
            {
                //canReset = false;

                //cardSound(flipSound);



                gameTurn++;//check if need to set to zero at restart
                int value = ((GameCard)v).getValue();
                Drawable imgValue = ((GameCard)v).getFront();
                playSound(flipSound);
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
                        score++;


                        Handler handler2 = new Handler();
                        handler2.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                playSound(correctSound);
                                Handler handler = new Handler();
                                Animation animation = AnimationUtils.loadAnimation(getActivity(),R.anim.fade);

                                prevCard1.startAnimation(animation);
                                ((GameCard)tmpV).startAnimation(animation);
                                prevCard1.fadeAnimation();
                                ((GameCard)tmpV).fadeAnimation();

                                handler.postDelayed(new Runnable() {
                                    public void run() {

                                        cardFlipping2 = false;
                                        prevCard1.setVisibility(View.INVISIBLE);
                                        tmpV.setVisibility(View.INVISIBLE);
                                        gameTurn = 0;
                                        if(cardsNum/2 == score)
                                        {
                                            score = (int) (scoreFactor *cardsNum * 50 *((stage+10)/10));

                                            playSound(victorySound);
                                            victorySound.start();
                                            ((HandleGameCards)getActivity()).victoryManager(score);
                                            coins = coins +score;
                                            prevCard1.clearAnimation();
                                            ((GameCard)tmpV).clearAnimation();




                                        }

                                    }
                                },500);
                            }
                        },1000);





                    }
                    else//not equal
                    {
                        playSound(flipSound);
                        //Toast.makeText(getContext(), "can reset is "+canReset, Toast.LENGTH_SHORT).show();
                        scoreFactor = scoreFactor * 0.9;



                       // turnsShuffle.setText(curShuffle+"");
                        Handler handler3 = new Handler();
                        handler3.postDelayed(new Runnable() {
                            public void run() {
                                prevCard1.flipBackAnimation();
                                tmpV.flipBackAnimation();
                                prevCard1.setFlipped(false);
                                tmpV.setFlipped(false);
                                gameTurn = 0;
                                turnsLeft--;
                                if(!cancelShuffle)
                                {
                                    curShuffle--;
                                }

                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        cardFlipping2 = false;
                                    }
                                },1200);
                                ((HandleGameCards)getActivity()).updateShuffle(curShuffle);
                                ((HandleGameCards)getActivity()).updateTurns(turnsLeft);
                                if(turnsLeft == 0)
                                {
                                    playSound(gameoverSound);
                                    gameOver = true;
                                    ((HandleGameCards)getActivity()).handleGameover();
                                }
                                if(curShuffle==0 &&!gameOver)
                                {

                                    flipping = true;
                                    Handler handler2 = new Handler();
                                    handler2.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            curShuffle = maxShuffle;
                                            flipping = false;
                                            //turnsShuffle.setText(curShuffle+"");
                                            ((HandleGameCards)getActivity()).updateShuffle(curShuffle);
                                            shuffle();

                                        }
                                    },1500);

                                }

                                //turnTxt.setText(turnsLeft +"");
                            }
                        },1000);

                    }

                }



            }
        }
    }

    public void reset()
    {
        if(canReset && !shuffeling&&(!cardFlipping1&&!cardFlipping2))
        {
            playSound(resetSound);
            if(doubleCoinsAmount>0)
            {
                ((HandleGameCards)getActivity()).updateDoubleCoins();
            }
            cancelShuffle = false;
            canShuffle = false;
            canReset = false;
            curShuffle = maxShuffle;
            scoreFactor = 1;

            gameOver = false;
            timePassed = 0;
            score = 0;
            gameStart = true;
            turnsLeft = (int) (cardsNum*1.5);
            gameTurn = 0;
            ((HandleGameCards)getActivity()).updateShuffle(curShuffle);
            ((HandleGameCards)getActivity()).updateTurns(turnsLeft);
            ((HandleGameCards)getActivity()).updateStage((world*10) + stage);
            //turnTxt.setText(turnsLeft + "");
            createPictures();


            //if(world==0)
            // {
            boolean needToFlip=false;
            for (GameCard gamecard: gameCards)
            {
                if(gamecard.getVisibility() == View.VISIBLE  && (gamecard.getDrawable() != gamecard.getBack()))
                {
                    gamecard.flipBackAnimation();
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
                                canShuffle = true;
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
                        canShuffle = true;
                    }
                },2000);
            }



        }

    }


    private void shuffle()
    {
        if(canShuffle && !cancelShuffle)
        {
            playSound(shuffleSound);
            Collections.shuffle(gameCards);
            shuffeling = true;

            for (int i =0; i <cardsNum/2; i++) {
                final GameCard first = gameCards.get(i);
                final ImageView second =(ImageView)gameCards.get(cardsNum-i-1);

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


                first.animate().translationXBy(location2[0]-location1[0]).setDuration(2000).withLayer().start();
                first.animate().translationYBy(location2[1]-location1[1]).setDuration(2000).withLayer().start();

                second.animate().translationXBy(location1[0]-location2[0]).setDuration(2000).withLayer().start();
                second.animate().translationYBy(location1[1]-location2[1]).setDuration(2000).withLayer().start();
                first.postInvalidateOnAnimation();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        shuffeling = false;
                    }
                },2100);


            }





        }

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
