package com.rain.library.observer;

import com.rain.library.bean.MediaData;

import java.util.Observable;

/**
 * @author:duyu
 * @org :   www.yudu233.com
 * @email : yudu233@gmail.com
 * @date :  2019/8/2 19:56
 * @filename : UpdateUIObserver
 * @describe :
 */
public class UpdateUIObserver extends Observable {

    private static UpdateUIObserver instance = new UpdateUIObserver();


    public static UpdateUIObserver getInstance() {
        if (instance == null) {
            synchronized (UpdateUIObserver.class) {
                if (instance == null) {
                    instance = new UpdateUIObserver();
                }
            }
        }
        return instance;
    }


    public void sendUpdateUIMessage(int position, MediaData mediaData,boolean isChecked) {
        setChanged();
        notifyObservers(new NotifyCmd(position, mediaData,isChecked));
    }


    public static class NotifyCmd {
        public int position;
        public MediaData mediaData;
        public boolean isChecked;

        public NotifyCmd(int position, MediaData mediaData,boolean isChecked) {
            this.position = position;
            this.mediaData = mediaData;
            this.isChecked = isChecked;
        }
    }
}
