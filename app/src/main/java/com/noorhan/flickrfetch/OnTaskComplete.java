package com.noorhan.flickrfetch;

import java.util.concurrent.ExecutionException;

/**
 * Created by Noora on 3/7/2016.
 */
public interface OnTaskComplete
{
    public void OnComplete() throws ExecutionException, InterruptedException;
}
