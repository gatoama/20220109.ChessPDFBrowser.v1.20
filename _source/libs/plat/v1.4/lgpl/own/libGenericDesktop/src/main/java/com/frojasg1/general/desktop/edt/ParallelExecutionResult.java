/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.desktop.edt;

import com.frojasg1.general.ExecutionFunctions;
import java.util.concurrent.Semaphore;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class ParallelExecutionResult<RR>
{
	protected volatile RR _result;
	protected Semaphore _semaphore = new Semaphore(0);

	public RR getResult() {
		return _result;
	}

	public void setResult(RR _result) {
		this._result = _result;
	}

	public Semaphore getSemaphore() {
		return _semaphore;
	}

	public void setSemaphore(Semaphore _semaphore) {
		this._semaphore = _semaphore;
	}

	public void semaphoreAcquire()
	{
		ExecutionFunctions.instance().safeMethodExecution( () -> _semaphore.acquire(1) );		
	}
}
