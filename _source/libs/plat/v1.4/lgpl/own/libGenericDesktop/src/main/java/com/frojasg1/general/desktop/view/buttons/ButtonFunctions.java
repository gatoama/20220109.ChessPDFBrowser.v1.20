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
package com.frojasg1.general.desktop.view.buttons;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.AbstractButton;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class ButtonFunctions
{
	protected static ButtonFunctions _instance;

	public static void changeInstance( ButtonFunctions instance )
	{
		_instance = instance;
	}

	public static ButtonFunctions instance()
	{
		if( _instance == null )
			_instance = new ButtonFunctions();
		
		return( _instance );
	}

	public void fireActionEvent( AbstractButton button, String text )
	{
		ActionEvent event;
		long when;

		when  = System.currentTimeMillis();
		event = new ActionEvent( button, ActionEvent.ACTION_PERFORMED, text, when, 0);

		for (ActionListener listener : button.getActionListeners()) {
			listener.actionPerformed(event);
		}
	}
}
