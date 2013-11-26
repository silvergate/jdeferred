package org.jdeferred.impl;

import java.util.List;
import org.jdeferred.AlwaysCallback;
import org.jdeferred.Deferred;
import org.jdeferred.DoneCallback;
import org.jdeferred.FailCallback;
import org.jdeferred.ProgressCallback;
import org.jdeferred.Promise;

/**
 * An abstract implementation of {@link Deferred} interface.
 * 
 * @see DoneCallback
 * @see FailCallback
 * @see ProgressCallback
 * @author Ray Tsang
 */
public abstract class AbstractDeferredObject<D, F, P> extends AbstractPromise<D, F, P> implements Deferred<D, F, P> {

    public AbstractDeferredObject(ExceptionHandler exceptionHandler, boolean copyOnWriteLists, List<DoneCallback<D>> doneCallbacks, List<FailCallback<F>> failCallbacks, List<ProgressCallback<P>> progressCallbacks, List<AlwaysCallback<D, F>> alwaysCallbacks) {
        super(exceptionHandler, copyOnWriteLists, doneCallbacks, failCallbacks, progressCallbacks, alwaysCallbacks);
    }

    @Override
    public Deferred<D, F, P> resolve(final D resolve) {
        final State state;
        synchronized (this) {
            if (!isPending())
                throw new IllegalStateException("Deferred object already finished, cannot resolve again");

            this.state = state = State.RESOLVED;
            this.resolveResult = resolve;
        }
        try {
            triggerDone(resolve);
        } finally {
            triggerAlways(state, resolve, null);
        }
        return this;
    }

    @Override
    public Deferred<D, F, P> notify(final P progress) {
        synchronized (this) {
            if (!isPending())
                throw new IllegalStateException("Deferred object already finished, cannot notify progress");

            state = State.PENDING;
        }
        triggerProgress(progress);
        return this;
    }

    @Override
    public Deferred<D, F, P> reject(final F reject) {
        State state;
        synchronized (this) {
            if (!isPending())
                throw new IllegalStateException("Deferred object already finished, cannot reject again");
            this.state = state = State.REJECTED;
            this.rejectResult = reject;
        }
        try {
            triggerFail(reject);
        } finally {
            triggerAlways(state, null, reject);
        }
        return this;
    }

    public Promise<D, F, P> promise() {
        return this;
    }
}
