package com.ntua.ote.logger.utils;

import com.ntua.ote.logger.models.AsyncResponseDetails;

public interface AsyncResponse {

    void processFinish(AsyncResponseDetails output);
}
