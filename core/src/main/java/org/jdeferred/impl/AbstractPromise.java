/*
 * Copyright 2013 Ray Tsang Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES
 * OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */
package org.jdeferred.impl;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdeferred.AlwaysCallback;
import org.jdeferred.DoneCallback;
import org.jdeferred.DoneFilter;
import org.jdeferred.FailCallback;
import org.jdeferred.FailFilter;
import org.jdeferred.ProgressCallback;
import org.jdeferred.ProgressFilter;
import org.jdeferred.Promise;
import org.jdeferred.impl.ExceptionHandler.Location;

/**
 * @see Promise
 * @author Ray Tsang
 */
public abstract class AbstractPromise<D, F, P> implements Promise<D, F, P> {
    final protected Logger log = Logger.getLogger(AbstractPromise.class.getName());

    protected volatile State state = State.PENDING;

    /**
     * @param exceptionHandler Add a custom exception handler that's to be called if a handler inside a trigger-method fails. <code>null</code> is allowed.
     * @param copyOnWriteLists Set this to <code>true</code> if the list-implementations are copy-on-write lists. Set this to <code>false</code> if the implementations are not copy-on-write lists.
     * <strong>Important</strong>: <code>false</code> is only allowed in a single-threaded environment.
     * @param doneCallbacks List implementation. List has to be empty.
     * @param failCallbacks List implementation. List has to be empty.
     * @param progressCallbacks List implementation. List has to be empty.
     * @param alwaysCallbacks List implementation. List has to be empty.
     */
    public AbstractPromise(ExceptionHandler exceptionHandler, boolean copyOnWriteLists, List<DoneCallback<D>> doneCallbacks, List<FailCallback<F>> failCallbacks, List<ProgressCallback<P>> progressCallbacks, List<AlwaysCallback<D, F>> alwaysCallbacks) {
        this.exceptionHandler = exceptionHandler;
        this.doneCallbacks = doneCallbacks;
        this.failCallbacks = failCallbacks;
        this.progressCallbacks = progressCallbacks;
        this.alwaysCallbacks = alwaysCallbacks;
        this.copyOnWriteLists = copyOnWriteLists;
    }

    protected final List<DoneCallback<D>> doneCallbacks;
    protected final List<FailCallback<F>> failCallbacks;
    protected final List<ProgressCallback<P>> progressCallbacks;
    protected final List<AlwaysCallback<D, F>> alwaysCallbacks;
    private final ExceptionHandler exceptionHandler;
    private final boolean copyOnWriteLists;

    protected D resolveResult;
    protected F rejectResult;

    @Override
    public State state() {
        return state;
    }

    @Override
    public Promise<D, F, P> done(DoneCallback<D> callback) {
        if (this.copyOnWriteLists) {
            doneCallbacks.add(callback);
        }

        final D result;
        final boolean resolved;
        synchronized (this) {
            resolved = isResolved();
            result = resolveResult;
            if (!this.copyOnWriteLists) {
                /* Don't add it to the doneCallbacks to prevent ConcurrentModificationException when triggerDone() */
                if (isPending()) {
                    doneCallbacks.add(callback);
                }
            }
        }
        if (resolved)
            callback.onDone(result);

        return this;
    }

    @Override
    public Promise<D, F, P> fail(FailCallback<F> callback) {
        if (this.copyOnWriteLists) {
            failCallbacks.add(callback);
        }

        final F result;
        final boolean rejected;
        synchronized (this) {
            rejected = isRejected();
            result = rejectResult;
            if (!this.copyOnWriteLists) {
                /* Don't add it to the doneCallbacks to prevent ConcurrentModificationException when triggerDone() */
                if (isPending()) {
                    failCallbacks.add(callback);
                }
            }
        }
        if (rejected)
            callback.onFail(result);

        return this;
    }

    @Override
    public Promise<D, F, P> always(AlwaysCallback<D, F> callback) {
        if (this.copyOnWriteLists) {
            alwaysCallbacks.add(callback);
        }

        final State state;
        final D resolveResult;
        final F rejectResult;
        synchronized (this) {
            state = this.state;
            resolveResult = this.resolveResult;
            rejectResult = this.rejectResult;
            if (!this.copyOnWriteLists) {
                /* Don't add it to the doneCallbacks to prevent ConcurrentModificationException when triggerDone() */
                if (isPending()) {
                    alwaysCallbacks.add(callback);
                }
            }
        }

        if (state != State.PENDING)
            callback.onAlways(state, resolveResult, rejectResult);
        return this;
    }

    protected void triggerDone(D resolved) {
        for (DoneCallback<D> callback : doneCallbacks) {
            try {
                callback.onDone(resolved);
            } catch (Exception e) {
                invokeOnException(Location.doneCallback, e, callback);
                log.log(Level.SEVERE, "an uncaught exception occured in a DoneCallback", e);
            }
        }
    }

    protected void triggerFail(F rejected) {
        for (FailCallback<F> callback : failCallbacks) {
            try {
                callback.onFail(rejected);
            } catch (Exception e) {
                invokeOnException(Location.failCallback, e, callback);
                log.log(Level.SEVERE, "an uncaught exception occured in a FailCallback", e);
            }
        }
    }

    protected void triggerProgress(P progress) {
        for (ProgressCallback<P> callback : progressCallbacks) {
            try {
                callback.onProgress(progress);
            } catch (Exception e) {
                invokeOnException(Location.progressCallback, e, callback);
                log.log(Level.SEVERE, "an uncaught exception occured in a ProgressCallback", e);
            }
        }
    }

    protected void triggerAlways(State state, D resolve, F reject) {
        for (AlwaysCallback<D, F> callback : alwaysCallbacks) {
            try {
                callback.onAlways(state, resolve, reject);
            } catch (Exception e) {
                invokeOnException(Location.alwaysCallback, e, callback);
                log.log(Level.SEVERE, "an uncaught exception occured in a AlwaysCallback", e);
            }
        }
    }

    private void invokeOnException(Location location, Exception exception, Object handler) {
        if (this.exceptionHandler != null) {
            try {
                this.exceptionHandler.onException(location, exception, handler);
            } catch (Exception ex) {
                /* Catch exceptions the exception handler has thrown */
                log.log(Level.SEVERE, "A exception handler has thrown an exception.", ex);
            }
        }
    }

    @Override
    public Promise<D, F, P> progress(ProgressCallback<P> callback) {
        progressCallbacks.add(callback);
        return this;
    }

    @Override
    public Promise<D, F, P> then(DoneCallback<D> callback) {
        return done(callback);
    }

    @Override
    public Promise<D, F, P> then(DoneCallback<D> doneCallback, FailCallback<F> failCallback) {
        done(doneCallback);
        fail(failCallback);
        return this;
    }

    @Override
    public Promise<D, F, P> then(DoneCallback<D> doneCallback, FailCallback<F> failCallback, ProgressCallback<P> progressCallback) {
        done(doneCallback);
        fail(failCallback);
        progress(progressCallback);
        return this;
    }

    @Override
    public <D_OUT, F_OUT, P_OUT> Promise<D_OUT, F_OUT, P_OUT> then(DoneFilter<D, D_OUT> doneFilter) {
        return new FilteredPromise<D, F, P, D_OUT, F_OUT, P_OUT>(this, doneFilter, null, null);
    }

    @Override
    public <D_OUT, F_OUT, P_OUT> Promise<D_OUT, F_OUT, P_OUT> then(DoneFilter<D, D_OUT> doneFilter, FailFilter<F, F_OUT> failFilter) {
        return new FilteredPromise<D, F, P, D_OUT, F_OUT, P_OUT>(this, doneFilter, failFilter, null);
    }

    @Override
    public <D_OUT, F_OUT, P_OUT> Promise<D_OUT, F_OUT, P_OUT> then(DoneFilter<D, D_OUT> doneFilter, FailFilter<F, F_OUT> failFilter, ProgressFilter<P, P_OUT> progressFilter) {
        return new FilteredPromise<D, F, P, D_OUT, F_OUT, P_OUT>(this, doneFilter, failFilter, progressFilter);
    }

    @Override
    public boolean isPending() {
        return state == State.PENDING;
    }

    @Override
    public boolean isResolved() {
        return state == State.RESOLVED;
    }

    @Override
    public boolean isRejected() {
        return state == State.REJECTED;
    }
}
