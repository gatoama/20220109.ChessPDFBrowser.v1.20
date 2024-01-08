/* 
 * Copyright (C) 2021 Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You may obtain a copy of the License at
 *
 *      http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 */
package com.frojasg1.general.executor;

import com.frojasg1.general.ExecutionFunctions;
import com.frojasg1.general.executor.worker.ExecutorWorker;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public interface ExecutorPullInterface
{
	public int getCapacity();

	public ExecutorInterface getNextExecutor(ExecutorWorker executor);
	public void addPendingExecutor( ExecutorInterface executor );
	public void addPendingNonStopableExecutor( Runnable executor );

	public <RR> void addPendingExecutor( ExecutionFunctions.UnsafeFunction<RR> function, Consumer<RR> callback );

	public boolean isFinished();
	public void hasToStop();
	public void stopExecutors();
	public void start();

	public boolean isPending( ExecutorInterface runnable );
	public boolean isBeingExecuted( ExecutorInterface runnable );
	public void stoppingToExecute( ExecutorWorker executor, ExecutorInterface runnable );

	public ExecutorInterface replace( ExecutorInterface runnableToBeReplaced, Supplier<ExecutorInterface> replacementRunnableBuilder );
}
