package org.jdeferred.impl;

public interface ExceptionHandler {

    public static enum Location {
        doneCallback, failCallback, progressCallback, alwaysCallback
    }

    void onException(Location localtion, Exception exception, Object handler);
}
