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
package com.frojasg1.general.desktop.view.text.utils;

import com.frojasg1.general.ExecutionFunctions;
import com.frojasg1.general.desktop.view.ComponentFunctions;
import java.awt.Point;
import java.awt.Rectangle;
import javax.accessibility.AccessibleContext;
import javax.swing.plaf.basic.BasicTextPaneUI;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class TextViewFunctions
{
	protected static TextViewFunctions _instance = null;

	public static TextViewFunctions instance()
	{
		if( _instance == null )
			_instance = new TextViewFunctions();

		return( _instance );
	}

	public String getText( JTextComponent textComp )
	{
		String result = null;

		if( textComp != null )
		{
			Document doc = textComp.getDocument();
			if( doc != null )
				result = ExecutionFunctions.instance().safeFunctionExecution( () -> doc.getText(0, getLength(textComp) ) );
			else
				result = textComp.getText();
		}

		return( result );
	}

	public int getLength( JTextComponent textComp )
	{
		int result = -1;
		Document doc = textComp.getDocument();
		
		if( doc != null )
		{
			result = doc.getLength();
		}
		else
			result = textComp.getText().length();

		return( result );
	}

	public Rectangle getCharacterBounds(JTextComponent textComp, int index)
	{
		Rectangle result = null;
		
		if( ComponentFunctions.instance().isVisible( textComp ) )
		{
			AccessibleContext accessibleContext = textComp.getAccessibleContext();

			BasicTextPaneUI ui;
			if( accessibleContext instanceof JTextComponent.AccessibleJTextComponent )
			{
				int length = getLength(textComp);
				if( index >= length )
					index = length - 1;

				if( index >= 0 )
				{
					try
					{
						JTextComponent.AccessibleJTextComponent cjtc = (JTextComponent.AccessibleJTextComponent) accessibleContext;
						result = cjtc.getCharacterBounds(index);
					}
					catch( Exception ex )
					{
						ex.printStackTrace();
					}
				}
			}
		}

		return( result );
	}

	public Rectangle getCharacterBoundsOnScreen(JTextComponent textComp, int index)
	{
		Rectangle result = getCharacterBounds(textComp, index);

		if( result != null )
		{
			Point location = textComp.getLocationOnScreen();

			result = new Rectangle( result.x + location.x, result.y + location.y,
									result.width, result.height );
		}

		return( result );
	}
}
