package com.dhu777.hciapp;

import android.content.Context;
import android.media.MediaPlayer;


/**
 * 持有一系列背景音MediaPlay对象
 */
@SuppressWarnings({"WeakerAccess", "CanBeFinal"})
public class Players {
    private MediaPlayer currentPlayer;

    public static final int WATER1 = 0;
    private MediaPlayer water1p;

    public static final int MARKET1 = 1;
    private  MediaPlayer market1p;

    public static final int WATER0 = 2;
    private MediaPlayer water0p;

    public static final int HORSE0 = 3;
    private MediaPlayer horse0p;

    public static final int MARKET0 = 4;
    private MediaPlayer market0p;

    public Players(Context mContext) {
        water1p = MediaPlayer.create(mContext,R.raw.water1);
        water1p.setLooping(true);

        market1p=MediaPlayer.create(mContext,R.raw.market1);
        market1p.setLooping(true);

        water0p=MediaPlayer.create(mContext,R.raw.water0);
        water0p.setLooping(true);

        horse0p=MediaPlayer.create(mContext,R.raw.horse0);
        horse0p.setLooping(true);

        market0p=MediaPlayer.create(mContext,R.raw.market0);
        market0p.setLooping(true);
    }

    /**
     * @param id 根据不同id进行播放，id见本类public static 字段
     */
    public void play(int id){
        switch (id){
            case WATER1:
                mPlay(water1p);
                break;
            case MARKET1:
                mPlay(market1p);
                break;
            case WATER0:
                mPlay(water0p);
                break;
            case HORSE0:
                mPlay(horse0p);
                break;
            case MARKET0:
                mPlay(market0p);
                break;
        }
    }

    /**
     * 继续播放
     */
    public void play(){
        if(currentPlayer!=null){
            currentPlayer.start();
        }
    }

    /**
     * 暂停播放
     */
    public void pause(){
        if(isPlaying()){
            currentPlayer.pause();
        }
    }

    /**
     * @return 当前是否在播放
     */
    public Boolean isPlaying(){
        return currentPlayer!=null && currentPlayer.isPlaying();
    }

    private void mPlay(MediaPlayer player){
        if(currentPlayer!=player){
            pause();
            currentPlayer = player;
        }
        currentPlayer.start();
    }

    /**
     * 需要进行内存释放以避免Memory Leak
     */
    public void release(){
        mRelease(water0p);
        mRelease(water1p);
        mRelease(market0p);
        mRelease(market1p);
        mRelease(horse0p);
        water0p =null;
        water1p =null;
        market0p=null;
        market1p=null;
        horse0p=null;
    }

    private void mRelease(MediaPlayer player){
        if(player != null){
            player.reset();
            player.release();
        }
    }


}
