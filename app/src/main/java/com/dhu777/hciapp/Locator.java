package com.dhu777.hciapp;

import android.graphics.PointF;
import android.graphics.RectF;

/**
 * 定位当前视点并调用相应背景音进行播放
 */
@SuppressWarnings({"WeakerAccess", "CanBeFinal"})
public class Locator {
    /**城门坐标*/
    public static final PointF PCityGate = new PointF(5000,700);
    /**桥坐标*/
    public static final PointF PBridge = new PointF(13800,500);
    /**有马蹄声与人声的区域*/
    private static final RectF horse1 = new RectF(5300,200,7400,1100);
    /**有水声的区域1*/
    private static final RectF river1 = new RectF(11000,100,13400,700);
    /**有水声的区域2*/
    private static final RectF river2 = new RectF(15600,450,19000,1100);
    /**有水声的区域0*/
    private static final RectF river0 = new RectF(14300,450,15599,800);
    /**人声的区域*/
    private static final RectF city0 = new RectF(8600,500,10999,900);

    private Players mPlayers;

    /**
     * @param mPlayers 一系列背景音播放器
     */
    public Locator(Players mPlayers) {
        this.mPlayers = mPlayers;
    }

    /**
     * 定位并播放背景音
     * @param pf 当前视点坐标
     */
    void locateVoice(PointF pf){
        if(city0.contains(pf.x,pf.y)){
            mPlayers.play(Players.MARKET1);
        }else if(horse1.contains(pf.x,pf.y)){
            mPlayers.play(Players.HORSE0);
        } else if(river0.contains(pf.x,pf.y)){
            mPlayers.play(Players.WATER0);
        }else if(river1.contains(pf.x,pf.y)||river2.contains(pf.x,pf.y)){
            mPlayers.play(Players.WATER1);
        }else{
            mPlayers.play(Players.MARKET0);
//            mPlayers.pause();
        }
    }
}
