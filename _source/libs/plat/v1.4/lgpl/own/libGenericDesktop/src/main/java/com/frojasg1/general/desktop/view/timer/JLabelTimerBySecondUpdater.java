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
package com.frojasg1.general.desktop.view.timer;

import com.frojasg1.general.timers.InterruptionBySecond;
import com.frojasg1.general.timers.InterruptionListener;
import javax.swing.JLabel;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class JLabelTimerBySecondUpdater implements InterruptionListener
{
	protected JLabel _label;
	protected InterruptionBySecond _timer;

	public JLabelTimerBySecondUpdater( JLabel label )
	{
		_label = label;
	}

	public void setJLabel( JLabel label )
	{
		_label = label;
	}

	public void init()
	{
		_timer = createInterruptionBySecond();

		new Thread( _timer ).start();
	}

	public void start()
	{
		_timer.start();
	}

	protected InterruptionBySecond createInterruptionBySecond()
	{
		InterruptionBySecond result = new InterruptionBySecond(this);

		return( result );
	}

	public void stop()
	{
		_timer.setHasToStop(true);
	}

	public void release()
	{
		_timer.release();
		_timer = null;
	}

	@Override
	public void newInterruption(Object sender, long timestampInMs, long timeElapsedSinceStart, String elapsedTimeString)
	{
		_label.setText( elapsedTimeString );
	}
}
