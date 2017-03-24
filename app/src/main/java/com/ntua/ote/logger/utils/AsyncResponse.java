package com.ntua.ote.logger.utils;

public interface AsyncResponse<T> {

    void processFinish(T output);
}
