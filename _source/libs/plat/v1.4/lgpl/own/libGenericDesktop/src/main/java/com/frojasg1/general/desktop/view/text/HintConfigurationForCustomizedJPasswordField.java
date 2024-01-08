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
package com.frojasg1.general.desktop.view.text;

import com.frojasg1.applications.common.components.hints.HintConfiguration;
import com.frojasg1.general.desktop.view.FontFunctions;
import java.awt.Color;
import java.awt.Font;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class HintConfigurationForCustomizedJPasswordField extends HintConfiguration
{
//	protected static Font DEFAULT_FONT = null;
	protected static final Color DEFAULT_FOREGROUND_COLOR = Color.RED;
	protected static final Color DEFAULT_BACKGROUND_COLOR = new Color( 251, 213, 213 );	// light pink

	protected static HintConfigurationForCustomizedJPasswordField _instance = null;

	public static HintConfigurationForCustomizedJPasswordField instance()
	{
		if( _instance == null )
			_instance = new HintConfigurationForCustomizedJPasswordField();

		return( _instance );
	}

	protected Font createInitialFont()
	{
		Font result = FontFunctions.instance().getResizedFont( FontFunctions.instance().getOriginalToolTipFont(), 14 );
		result = FontFunctions.instance().getStyledFont( result, Font.BOLD );

		return( result );
	}

	public HintConfigurationForCustomizedJPasswordField()
	{
		setOriginalFont( createInitialFont() );
		setBackgroundColor( DEFAULT_BACKGROUND_COLOR );
		setForegroundColor( DEFAULT_FOREGROUND_COLOR );
	}

	public boolean getHintsActivated()
	{
		return( false );
	}
}
