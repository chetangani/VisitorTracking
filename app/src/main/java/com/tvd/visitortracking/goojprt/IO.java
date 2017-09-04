package com.tvd.visitortracking.goojprt;

import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by tvd on 08/17/2017.
 */

public class IO {
    private final ReentrantLock locker = new ReentrantLock();

    public IO() {
    }

    public int Write(byte[] buffer, int offset, int count) {
        return -1;
    }

    public int Read(byte[] buffer, int offset, int count, int timeout) {
        return -1;
    }

    public void SkipAvailable() {
    }

    public boolean IsOpened() {
        return false;
    }

    protected void Lock() {
        this.locker.lock();
    }

    protected void Unlock() {
        this.locker.unlock();
    }
}
