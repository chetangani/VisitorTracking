package com.tvd.visitortracking.goojprt;

/**
 * Created by tvd on 08/17/2017.
 */

public interface IOCallBack {
    void OnOpen();

    void OnOpenFailed();

    void OnClose();
}
