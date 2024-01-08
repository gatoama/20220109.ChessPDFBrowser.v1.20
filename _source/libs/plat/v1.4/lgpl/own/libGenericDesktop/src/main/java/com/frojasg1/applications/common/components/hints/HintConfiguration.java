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
package com.frojasg1.applications.common.components.hints;

import com.frojasg1.general.desktop.view.FontFunctions;
import java.awt.Color;
import java.awt.Font;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

/**
 *
 * @author Usuario
 */
public class HintConfiguration
{
	protected static final Font DEFAULT_FONT = new Font( "Arial", Font.BOLD, 13 );
	protected static final Color DEFAULT_FOREGROUND_COLOR = Color.BLACK;
	protected static final Color DEFAULT_BACKGROUND_COLOR = new Color( 248, 251, 154 );	// pale yellow

	protected Font _fontForHint = null;
	protected Color _backgroundColor = null;
	protected Color _foregroundColor = null;

	protected int _millisecondsToWaitBeforeShowingHint = 1000;

	protected boolean _hintHidesWhenMouseMoved = true;
	protected boolean _hintHidesWhenFocusGained = true;
	protected boolean _hintHidesWhenMouseExit = true;
	protected boolean _showHintIfComponentDisabled = false;

	protected static HintConfiguration _defaultConfiguration = null;

	List<HintForComponent> _listOfHintForComponentToUpdate = new Vector<HintForComponent>();

	protected boolean _imageAttributeshaveChanged = false;
	protected boolean _timerToWaitBeforeShowingHint_hasChanged = false;

	protected boolean _hintsActivated = true;
	
	protected int _originalSizeforFont = -1;

	public static HintConfiguration getDefault()
	{
		if( _defaultConfiguration == null )
			_defaultConfiguration = new HintConfiguration();

		return( _defaultConfiguration );
	}

	protected void activateImageAttributeChange()
	{
		if( _listOfHintForComponentToUpdate.size() > 0 )
		{
			_imageAttributeshaveChanged = true;
			notifyChanges();
		}
	}

	public void setActivateHints( boolean value )
	{
		_hintsActivated = value;
	}

	public boolean getHintsActivated()
	{
		return( _hintsActivated );
	}

	public synchronized void setOriginalFont( Font font )
	{
		setFont( font );

		if( font != null )
			_originalSizeforFont = font.getSize();
		else
			_originalSizeforFont = -1;
	}

	protected synchronized void setFont( Font font )
	{
		if( _fontForHint != font )
		{
			_fontForHint = font;
			activateImageAttributeChange();
		}
	}

	public void changeZoomFactor( double zoomFactor )
	{
		int newSize = FontFunctions.instance().getZoomedFontSize(_originalSizeforFont, zoomFactor);
		setFont( FontFunctions.instance().getResizedFont( getFont(), newSize ) );
	}

	public Font getFont()
	{
		Font result = _fontForHint;
		if( result == null )
			result = DEFAULT_FONT;

		return( result );
	}

	public synchronized void setForegroundColor( Color color )
	{
		if( _foregroundColor != color )
		{
			_foregroundColor = color;
			activateImageAttributeChange();
		}
	}

	public Color getForegroundColor()
	{
		Color result = _foregroundColor;

		if( result == null )
			result = DEFAULT_FOREGROUND_COLOR;

		return( result );
	}

	public synchronized void setBackgroundColor( Color color )
	{
		if( _backgroundColor != color )
		{
			_backgroundColor = color;
			activateImageAttributeChange();
		}
	}

	public Color getBackgroundColor()
	{
		Color result = _backgroundColor;

		if( result == null )
			result = DEFAULT_BACKGROUND_COLOR;

		return( result );
	}

	public boolean getHintHidesWhenMouseMoved()
	{
		return( _hintHidesWhenMouseMoved );
	}

	public void setHintHidesWhenMouseMoved( boolean value )
	{
		_hintHidesWhenMouseMoved = value;
	}

	public boolean getHintHidesWhenFocusGained()
	{
		return( _hintHidesWhenFocusGained );
	}

	public void setHintHidesWhenFocusGained( boolean value )
	{
		_hintHidesWhenFocusGained = value;
	}

	public boolean getHintHidesWhenMouseExit()
	{
		return( _hintHidesWhenMouseExit );
	}

	public void setHintHidesWhenMouseExit( boolean value )
	{
		_hintHidesWhenMouseExit = value;
	}

	public boolean getShowHintIfcomponentDisabled()
	{
		return( _showHintIfComponentDisabled );
	}

	public void setShowHintIfComponentDisabled( boolean value )
	{
		_showHintIfComponentDisabled = value;
	}

	public int getMillisecondsBeforeShowingHint()
	{
		return( _millisecondsToWaitBeforeShowingHint );
	}

	public synchronized void setMillisecondsBeforeShowingHint( int value )
	{
		_millisecondsToWaitBeforeShowingHint = value;
		
		if( _listOfHintForComponentToUpdate.size() > 0 )
		{
			_timerToWaitBeforeShowingHint_hasChanged = true;
			notifyChanges();
		}
	}

	public void add( HintForComponent hfc )
	{
		_listOfHintForComponentToUpdate.add( hfc );
	}

	public void remove( HintForComponent hfc )
	{
		_listOfHintForComponentToUpdate.remove( hfc );
	}

	public synchronized void notifyChanges()
	{
		if( _imageAttributeshaveChanged ||  _timerToWaitBeforeShowingHint_hasChanged )
		{
			Iterator< HintForComponent > it = _listOfHintForComponentToUpdate.iterator();

			while( it.hasNext() )
			{
				try
				{
					HintForComponent hfc = it.next();
					if( _imageAttributeshaveChanged )
						hfc.refreshConfiguration();

					if( _timerToWaitBeforeShowingHint_hasChanged )
						hfc.updateTimerToShowHint(_millisecondsToWaitBeforeShowingHint);
				}
				catch( Exception ex )
				{
					ex.printStackTrace();
				}
			}
			_imageAttributeshaveChanged = false;
			_timerToWaitBeforeShowingHint_hasChanged = false;
		}
	}
}
