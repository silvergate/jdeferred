/*
 * Copyright 2013 by Swiss Post, Information Technology Services (Laurent Bovet)                  
 *                   
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jdeferred.gwt;

import org.jdeferred.AlwaysCallback;
import org.jdeferred.DoneCallback;
import org.jdeferred.DoneFilter;
import org.jdeferred.FailCallback;
import org.jdeferred.FailFilter;
import org.jdeferred.ProgressCallback;
import org.jdeferred.ProgressFilter;
import org.jdeferred.Promise;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * Bridges a javascript promise complying with done(), fail(), progress() contract.
 *
 */
public final class NativePromise<D, F, P> extends JavaScriptObject implements Promise<D, F, P> {
	
	protected NativePromise() { 
		
	};
	
	private native Promise<D, F, P> internalPromise() /*-{
      if(!this.__jdeferred) {
		  this.__jdeferred = @org.jdeferred.impl.DeferredObject::new()();
		  this.done(function(result) {
		  	this.__jdeferred.@org.jdeferred.Deferred::resolve(Ljava/lang/Object;)(result);
		  })
		  .fail(function(result) {
		  	this.__jdeferred.@org.jdeferred.Deferred::reject(Ljava/lang/Object;)(result);
		  })
		  .progress(function(progress) {
		  	this.__jdeferred.@org.jdeferred.Deferred::notify(Ljava/lang/Object;)(progress);
		  });
      }
      return this.__jdeferred;
	}-*/;
	
	/**
	 * Not explicitly documented.
	 * @see org.jdeferred.Promise#state()
	 */
	@Override
	public Promise.State state() {
		return internalPromise().state();
	}

	/**
	 * Not explicitly documented.
	 * @see org.jdeferred.Promise#isPending()
	 */
	@Override
	public boolean isPending() {
		return internalPromise().isPending();
	}

	/**
	 * Not explicitly documented.
	 * @see org.jdeferred.Promise#isResolved()
	 */
	@Override
	public boolean isResolved() {
		return internalPromise().isResolved();
	}

	/**
	 * Not explicitly documented.
	 * @see org.jdeferred.Promise#isRejected()
	 */
	@Override
	public boolean isRejected() {
		return internalPromise().isRejected();
	}

	/**
	 * Not explicitly documented.
	 * @see org.jdeferred.Promise#then(org.jdeferred.DoneCallback)
	 */
	@Override
	public Promise<D, F, P> then(DoneCallback<D> doneCallback) {
		return internalPromise().then(doneCallback);
	}

	/**
	 * Not explicitly documented.
	 * @see org.jdeferred.Promise#then(org.jdeferred.DoneCallback, org.jdeferred.FailCallback)
	 */
	@Override
	public Promise<D, F, P> then(DoneCallback<D> doneCallback,
			FailCallback<F> failCallback) {
		return internalPromise().then(doneCallback, failCallback);
	}

	/**
	 * Not explicitly documented.
	 * @see org.jdeferred.Promise#then(org.jdeferred.DoneCallback, org.jdeferred.FailCallback, org.jdeferred.ProgressCallback)
	 */
	@Override
	public Promise<D, F, P> then(DoneCallback<D> doneCallback,
			FailCallback<F> failCallback, ProgressCallback<P> progressCallback) {
		return internalPromise().then(doneCallback, failCallback, progressCallback);
	}

	/**
	 * Not explicitly documented.
	 * @see org.jdeferred.Promise#then(org.jdeferred.DoneFilter)
	 */
	@Override
	public <D_OUT, F_OUT, P_OUT> Promise<D_OUT, F_OUT, P_OUT> then(
			DoneFilter<D, D_OUT> doneFilter) {
		return internalPromise().then(doneFilter);
	}

	/**
	 * Not explicitly documented.
	 * @see org.jdeferred.Promise#then(org.jdeferred.DoneFilter, org.jdeferred.FailFilter)
	 */
	@Override
	public <D_OUT, F_OUT, P_OUT> Promise<D_OUT, F_OUT, P_OUT> then(
			DoneFilter<D, D_OUT> doneFilter, FailFilter<F, F_OUT> failFilter) {
		return internalPromise().then(doneFilter, failFilter);
	}

	/**
	 * Not explicitly documented.
	 * @see org.jdeferred.Promise#then(org.jdeferred.DoneFilter, org.jdeferred.FailFilter, org.jdeferred.ProgressFilter)
	 */
	@Override
	public <D_OUT, F_OUT, P_OUT> Promise<D_OUT, F_OUT, P_OUT> then(
			DoneFilter<D, D_OUT> doneFilter, FailFilter<F, F_OUT> failFilter,
			ProgressFilter<P, P_OUT> progressFilter) {
		return internalPromise().then(doneFilter, failFilter, progressFilter);
	}

	/**
	 * Not explicitly documented.
	 * @see org.jdeferred.Promise#done(org.jdeferred.DoneCallback)
	 */
	@Override
	public Promise<D, F, P> done(DoneCallback<D> callback) {
		return internalPromise().done(callback);
	}

	/**
	 * Not explicitly documented.
	 * @see org.jdeferred.Promise#fail(org.jdeferred.FailCallback)
	 */
	@Override
	public Promise<D, F, P> fail(FailCallback<F> callback) {
		return internalPromise().fail(callback);
	}

	/**
	 * Not explicitly documented.
	 * @see org.jdeferred.Promise#always(org.jdeferred.AlwaysCallback)
	 */
	@Override
	public Promise<D, F, P> always(AlwaysCallback<D, F> callback) {
		return internalPromise().always(callback);
	}

	/**
	 * Not explicitly documented.
	 * @see org.jdeferred.Promise#progress(org.jdeferred.ProgressCallback)
	 */
	@Override
	public Promise<D, F, P> progress(ProgressCallback<P> callback) {
		return internalPromise().progress(callback);
	}	
}
