package com.altfuns.android.venuessearch.core;

import android.os.Handler;
import android.text.TextUtils;

public abstract class BackgroundTask {

    protected Handler handler;

    private Throwable error;

    private String errorMessage;

    public BackgroundTask() {
        handler = new Handler();

        new BackgroundThread().start();
    }

    public abstract void work();

    public abstract void done();

    /**
     * Whether there was an error while working, typically checked in done()
     */
    protected boolean failed() {
        return error != null || errorMessage != null;
    }

    protected void fail(String message) {
        fail(null, message);
    }

    protected void fail(Throwable t) {
        fail(t, null);
    }

    protected void fail(Throwable t, String message) {
        error = t;
        errorMessage = message;
        LogIt.e(this, t, message);
    }

    protected Throwable getException() {
        return error;
    }

    protected String getExceptionMessage() {
        StringBuilder builder = new StringBuilder();
        if (!TextUtils.isEmpty(errorMessage)) {
            builder.append(errorMessage);
        }
        if (error != null) {
            if (builder.length() > 0) {
                builder.append(": ");
            }
            builder.append(error.toString());
        }
        return builder.toString();
    }

    private class BackgroundThread extends Thread {

        @Override
        public void run() {

            try {
                work();
            } catch (Throwable t) {
                fail(t, null);
            }

            handler.post(new Runnable() {

                @Override
                public void run() {
                    try {
                        done();
                    } catch (Throwable t) {
                        LogIt.e(this, t);
                    }
                }
            });
        }
    }
}
