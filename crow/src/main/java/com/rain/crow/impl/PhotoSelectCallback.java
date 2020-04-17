package com.rain.crow.impl;

import com.rain.crow.bean.MediaData;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author:duyu
 * @org :   www.yudu233.com
 * @email : yudu233@gmail.com
 * @date :  2019/5/16 18:56
 * @filename : PhotoSelectCallback
 * @describe :
 */
public interface PhotoSelectCallback extends Serializable {
    void selectResult(ArrayList<MediaData> photos);
}
