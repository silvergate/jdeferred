package org.jdeferred.gwt.impl;

import org.jdeferred.AlwaysCallback;
import org.jdeferred.DoneCallback;
import org.jdeferred.DoneFilter;
import org.jdeferred.FailCallback;
import org.jdeferred.FailFilter;
import org.jdeferred.ProgressCallback;
import org.jdeferred.ProgressFilter;
import org.jdeferred.Promise;
import org.jdeferred.impl.DeferredObject;
import org.jdeferred.gwt.NativePromise;

/**
 * <p>
 * Hack to prevent the GWT-Compiler to NPE at com.google.gwt.dev.js.JsInliner$InliningVisitor.endVisit(JsInliner.java:997) when trying to call methods state(), isRejected(), isResolved(), isPending()
 * or progress() on a promise that could possibly be of type NativePromise.
 * </p>
 * 
 * @author ruosss
 */
public class PromiseGwtHack {

    public static interface GwtHackedPromise<D, F, P> extends Promise<D, F, P> {
        Class<?> getImplementationClass();
    };

    /**
     * Call this method somewhere in reachable java-code to install the hack.
     */
    public static void injectHack() {
        Promise<?, ?, ?>[] promise = new Promise<?, ?, ?>[1];
        PromiseGwtHack.wrap(promise[0]);
    }

    /**
     * <p>
     * <strong>Node</strong>: This method is actually not required, calling {@link #injectHack()} is already sufficient to prevent GWT from NPEing.
     * </p>
     * <p>
     * Wrapps a promise with a wrapper and returns the wrapper. The wrapper behaves the same as the passed original promise.
     * </p>
     * 
     * @param original Original promise.
     * @return Wrapped promise - or original promise if already wrapped.
     */
    public static <D, F, P> GwtHackedPromise<D, F, P> wrap(Promise<D, F, P> original) {
        if (original instanceof GwtHackedPromise<?, ?, ?>) {
            /* Already wrapped, no need to wrap again. */
            return (GwtHackedPromise<D, F, P>) original;
        }

        if (original instanceof DeferredObject<?, ?, ?>) {
            return new GwtHackedPromiseDeferredObject<D, F, P>((DeferredObject<D, F, P>) original);
        }

        if (original instanceof NativePromise<?, ?, ?>) {
            return new GwtHackedPromiseNative<D, F, P>((NativePromise<D, F, P>) original);
        }

        if (original != null) {
            throw new IllegalArgumentException("Promise of type " + original.getClass().getName() + " is unknown. Please implement a gwt-hack for this type.");
        }

        return null;
    }

    private static class GwtHackedPromiseDeferredObject<D, F, P> implements GwtHackedPromise<D, F, P> {

        public GwtHackedPromiseDeferredObject(DeferredObject<D, F, P> original) {
            super();
            this.original = original;
        }

        private DeferredObject<D, F, P> original;

        @Override
        public org.jdeferred.Promise.State state() {
            return this.original.state();
        }

        @Override
        public boolean isPending() {
            return this.original.isPending();
        }

        @Override
        public boolean isResolved() {
            return this.original.isResolved();
        }

        @Override
        public boolean isRejected() {
            return this.original.isRejected();
        }

        @Override
        public Promise<D, F, P> then(DoneCallback<D> doneCallback) {
            return this.original.then(doneCallback);
        }

        @Override
        public Promise<D, F, P> then(DoneCallback<D> doneCallback, FailCallback<F> failCallback) {
            return this.original.then(doneCallback, failCallback);
        }

        @Override
        public Promise<D, F, P> then(DoneCallback<D> doneCallback, FailCallback<F> failCallback, ProgressCallback<P> progressCallback) {
            return this.original.then(doneCallback, failCallback, progressCallback);
        }

        @Override
        public <D_OUT, F_OUT, P_OUT> Promise<D_OUT, F_OUT, P_OUT> then(DoneFilter<D, D_OUT> doneFilter) {
            return this.original.then(doneFilter);
        }

        @Override
        public <D_OUT, F_OUT, P_OUT> Promise<D_OUT, F_OUT, P_OUT> then(DoneFilter<D, D_OUT> doneFilter, FailFilter<F, F_OUT> failFilter) {
            return this.original.then(doneFilter, failFilter);
        }

        @Override
        public <D_OUT, F_OUT, P_OUT> Promise<D_OUT, F_OUT, P_OUT> then(DoneFilter<D, D_OUT> doneFilter, FailFilter<F, F_OUT> failFilter, ProgressFilter<P, P_OUT> progressFilter) {
            return this.original.then(doneFilter, failFilter, progressFilter);
        }

        @Override
        public Promise<D, F, P> done(DoneCallback<D> callback) {
            return this.original.done(callback);
        }

        @Override
        public Promise<D, F, P> fail(FailCallback<F> callback) {
            return this.original.fail(callback);
        }

        @Override
        public Promise<D, F, P> always(AlwaysCallback<D, F> callback) {
            return this.original.always(callback);
        }

        @Override
        public Promise<D, F, P> progress(ProgressCallback<P> callback) {
            return this.original.progress(callback);
        }

        @Override
        public Class<?> getImplementationClass() {
            return this.original.getClass();
        }
    }

    private static class GwtHackedPromiseNative<D, F, P> implements GwtHackedPromise<D, F, P> {

        public GwtHackedPromiseNative(NativePromise<D, F, P> original) {
            super();
            this.original = original;
        }

        private NativePromise<D, F, P> original;

        @Override
        public org.jdeferred.Promise.State state() {
            return this.original.state();
        }

        @Override
        public boolean isPending() {
            return this.original.isPending();
        }

        @Override
        public boolean isResolved() {
            return this.original.isResolved();
        }

        @Override
        public boolean isRejected() {
            return this.original.isRejected();
        }

        @Override
        public Promise<D, F, P> then(DoneCallback<D> doneCallback) {
            return this.original.then(doneCallback);
        }

        @Override
        public Promise<D, F, P> then(DoneCallback<D> doneCallback, FailCallback<F> failCallback) {
            return this.original.then(doneCallback, failCallback);
        }

        @Override
        public Promise<D, F, P> then(DoneCallback<D> doneCallback, FailCallback<F> failCallback, ProgressCallback<P> progressCallback) {
            return this.original.then(doneCallback, failCallback, progressCallback);
        }

        @Override
        public <D_OUT, F_OUT, P_OUT> Promise<D_OUT, F_OUT, P_OUT> then(DoneFilter<D, D_OUT> doneFilter) {
            return this.original.then(doneFilter);
        }

        @Override
        public <D_OUT, F_OUT, P_OUT> Promise<D_OUT, F_OUT, P_OUT> then(DoneFilter<D, D_OUT> doneFilter, FailFilter<F, F_OUT> failFilter) {
            return this.original.then(doneFilter, failFilter);
        }

        @Override
        public <D_OUT, F_OUT, P_OUT> Promise<D_OUT, F_OUT, P_OUT> then(DoneFilter<D, D_OUT> doneFilter, FailFilter<F, F_OUT> failFilter, ProgressFilter<P, P_OUT> progressFilter) {
            return this.original.then(doneFilter, failFilter, progressFilter);
        }

        @Override
        public Promise<D, F, P> done(DoneCallback<D> callback) {
            return this.original.done(callback);
        }

        @Override
        public Promise<D, F, P> fail(FailCallback<F> callback) {
            return this.original.fail(callback);
        }

        @Override
        public Promise<D, F, P> always(AlwaysCallback<D, F> callback) {
            return this.original.always(callback);
        }

        @Override
        public Promise<D, F, P> progress(ProgressCallback<P> callback) {
            return this.original.progress(callback);
        }

        @Override
        public Class<?> getImplementationClass() {
            return this.original.getClass();
        }
    }

}
