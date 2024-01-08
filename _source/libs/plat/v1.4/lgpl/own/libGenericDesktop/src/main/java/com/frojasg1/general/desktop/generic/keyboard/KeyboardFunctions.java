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
package com.frojasg1.general.desktop.generic.keyboard;

import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.Console;
import java.io.InputStreamReader;

/**
 *
 * @author Usuario
 */
public class KeyboardFunctions
{

	public static char[] readPasswordFromKeyboard()
	{
		char[] result = null;
		
		Console console = System.console();
		
		if( console != null )
			result = console.readPassword();
		else
		{
			String resultStr = readLineFromKeyboard();
			result = resultStr.toCharArray();
		}

		return( result );
	}

	public static String readLineFromKeyboard()
	{
		String result = null;

		BufferedReader br = null;
		InputStreamReader isr = null;
		try
		{
			isr = new InputStreamReader(System.in);
			br = new BufferedReader( isr ); 
			result = br.readLine();
		}
		catch( Throwable th )
		{
			th.printStackTrace();
			result = null;
		}

		return( result );
	}

	public static boolean isCapsOn()
	{
		boolean isOn = Toolkit.getDefaultToolkit().getLockingKeyState(KeyEvent.VK_CAPS_LOCK);
		return( isOn );
	}
}
