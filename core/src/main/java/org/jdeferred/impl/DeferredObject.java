/*
 * Copyright 2013 Ray Tsang Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES
 * OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */
package org.jdeferred.impl;

import java.util.ArrayList;
import org.jdeferred.AlwaysCallback;
import org.jdeferred.Deferred;
import org.jdeferred.DoneCallback;
import org.jdeferred.FailCallback;
import org.jdeferred.ProgressCallback;
import org.jdeferred.Promise;

/**
 * An implementation of {@link Deferred} interface for GWT.
 * 
 * <pre>
 * <code>
 * final {@link Deferred} deferredObject = new {@link DeferredObject}
 * 
 * {@link Promise} promise = deferredObject.promise();
 * promise
 *   .done(new DoneCallback() { ... })
 *   .fail(new FailCallback() { ... })
 *   .progress(new ProgressCallback() { ... });
 *   
 * {@link Runnable} runnable = new {@link Runnable}() {
 *   public void run() {
 *     int sum = 0;
 *     for (int i = 0; i < 100; i++) {
 *       // something that takes time
 *       sum += i;
 *       deferredObject.notify(i);
 *     }
 *     deferredObject.resolve(sum);
 *   }
 * }
 * // submit the task to run
 * 
 * </code>
 * </pre>
 * 
 * @see DoneCallback
 * @see FailCallback
 * @see ProgressCallback
 * @author Ray Tsang
 */
public class DeferredObject<D, F, P> extends AbstractDeferredObject<D, F, P> {

    private static ExceptionHandler exceptionHandler;

    public DeferredObject() {
        super(exceptionHandler, false, new ArrayList<DoneCallback<D>>(), new ArrayList<FailCallback<F>>(), new ArrayList<ProgressCallback<P>>(), new ArrayList<AlwaysCallback<D, F>>());
    }

    /**
     * Set a custom exception handler. The exception handler {@link ExceptionHandler#onException(org.jdeferred.impl.ExceptionHandler.Location, Exception, Object)} method is called if a done, fail,
     * always or progress handler throws an exception.
     * 
     * @param exceptionHandler
     */
    public static void setExceptionHandler(ExceptionHandler exceptionHandler) {
        DeferredObject.exceptionHandler = exceptionHandler;
    }
}
