package com.rain.crow.impl;

/**
 * @author:duyu
 * @org :   www.yudu233.com
 * @email : yudu233@gmail.com
 * @date :  2019/5/16 15:55
 * @filename : CommonResult
 * @describe :
 */
public interface CommonResult<T> {
    void onSuccess(T data,boolean success);
}
