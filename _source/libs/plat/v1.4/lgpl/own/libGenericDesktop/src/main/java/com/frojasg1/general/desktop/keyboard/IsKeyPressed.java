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
package com.frojasg1.general.desktop.keyboard;

/**
 *
 * @author Usuario
 */
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JFrame;

public class IsKeyPressed implements KeyEventDispatcher
{
	protected static Object _mutex = new Object();

	private static Map<Integer, Boolean> sa_mapKeysPressed = new HashMap<Integer, Boolean>();

	private static boolean sa_isLeftShiftPressed = false;
	private static boolean sa_isRightShiftPressed = false;
	private static boolean sa_isLeftCTRLPressed = false;
	private static boolean sa_isRightCTRLPressed = false;

	protected static boolean _hasBeenActivated = false;

	public static boolean isKeyPressed( int keyCode )
	{
        synchronized ( _mutex )
		{
			boolean result = false;
            Boolean value = sa_mapKeysPressed.get( new Integer(keyCode) );
			if( value != null )
				result = value.booleanValue();
			return( result );
        }
    }

	protected static void putKeyStateInMap( int keyCode, boolean isPressed )
	{
		Integer key = new Integer( keyCode );
		if( sa_mapKeysPressed.containsKey( key ) )	sa_mapKeysPressed.remove( key );
		Boolean value = new Boolean( isPressed );
		sa_mapKeysPressed.put(key, value);
	}
	
	protected static void manageKeyPressedKeyReleased( KeyEvent ke, boolean isKeyPressed )
	{
		if (ke.getKeyCode() == KeyEvent.VK_SHIFT)
		{
			if( ke.getKeyLocation() == KeyEvent.KEY_LOCATION_LEFT )		sa_isLeftShiftPressed = isKeyPressed;
			if( ke.getKeyLocation() == KeyEvent.KEY_LOCATION_RIGHT )	sa_isRightShiftPressed = isKeyPressed;
			putKeyStateInMap( KeyEvent.VK_SHIFT, sa_isLeftShiftPressed || sa_isRightShiftPressed );
		}
		else if (ke.getKeyCode() == KeyEvent.VK_CONTROL)
		{
			if( ke.getKeyLocation() == KeyEvent.KEY_LOCATION_LEFT )		sa_isLeftCTRLPressed = isKeyPressed;
			if( ke.getKeyLocation() == KeyEvent.KEY_LOCATION_RIGHT )	sa_isRightCTRLPressed = isKeyPressed;
			putKeyStateInMap( KeyEvent.VK_CONTROL, sa_isLeftCTRLPressed || sa_isRightCTRLPressed );
		}
		else
		{
			putKeyStateInMap( ke.getKeyCode(), isKeyPressed );
		}
	}
	
	@Override
	public boolean dispatchKeyEvent(KeyEvent ke)
	{
		synchronized ( _mutex )
		{
			switch (ke.getID())
			{
				case KeyEvent.KEY_PRESSED:
					manageKeyPressedKeyReleased( ke, true );
//					System.out.println( " Key pressed: " + ke.getKeyCode() );
				break;

				case KeyEvent.KEY_RELEASED:
					manageKeyPressedKeyReleased( ke, false );
//					System.out.println( " Key released: " + ke.getKeyCode() );
				break;
			}
			return false;
		}
    }

	public static void activateKeyEventListening()
	{
		if( ! _hasBeenActivated )
		{
			KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher( new IsKeyPressed() );
			_hasBeenActivated = true;
		}
    }

	public static void main( String args[] )
	{
		activateKeyEventListening();

        JFrame frame = new JFrame();
        frame.setBounds(50, 50, 200, 200);
        frame.setVisible(true);
	}
}