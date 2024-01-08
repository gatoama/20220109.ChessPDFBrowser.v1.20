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
package com.frojasg1.general.desktop.view.zoom.components;

import com.frojasg1.general.desktop.view.IconFunctions;
import com.frojasg1.general.desktop.view.color.CanInvertIcons;
import com.frojasg1.general.desktop.view.color.ColorInversor;
import com.frojasg1.general.desktop.view.color.ColorThemeInvertible;
import com.frojasg1.general.desktop.view.zoom.ZoomIcon;
import com.frojasg1.general.desktop.view.zoom.imp.ZoomIconImp;
import com.frojasg1.general.number.DoubleReference;
import java.util.function.Function;
import javax.swing.Icon;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class ComponentWithIconForZoomOverriden extends ComponentForZoomOverriden
												implements ColorThemeInvertible,
															CanInvertIcons
{
	protected ComponentWithIconForZoomInterface _abstractButtonForZoomComponent = null;

	protected static int DISABLED_ICON_INDEX = 0;
	protected static int DISABLED_SELECTED_ICON_INDEX = 1;
	protected static int ICON_INDEX = 2;
	protected static int PRESSED_ICON_INDEX = 3;
	protected static int ROLL_OVER_ICON_INDEX = 4;
	protected static int ROLL_OVER_SELECTED_ICON_INDEX = 5;
	protected static int SELECTED_ICON_INDEX = 6;

	protected IconData[] zoomIcons;

	protected boolean _hasToInvert = false;
	protected boolean _colorsAreInverted = false;

	protected boolean _canInvertIcons = true;

	public ComponentWithIconForZoomOverriden( ComponentWithIconForZoomInterface component,
									DoubleReference zoomFactor )
	{
		super( component, zoomFactor );
		_abstractButtonForZoomComponent = component;

		zoomIcons = new IconData[] {
			new IconData( but -> but.superGetDisabledIcon() ),
			new IconData( but -> but.superGetDisabledSelectedIcon() ),
			new IconData( but -> but.superGetIcon() ),
			new IconData( but -> but.superGetPressedIcon() ),
			new IconData( but -> but.superGetRolloverIcon() ),
			new IconData( but -> but.superGetRolloverSelectedIcon() ),
			new IconData( but -> but.superGetSelectedIcon() )
		};
	}

	@Override
	public boolean canInvertIcons() {
		return _canInvertIcons;
	}

	@Override
	public void setCanInvertIcons(boolean _canInvertIcons) {
		this._canInvertIcons = _canInvertIcons;
	}

	protected ZoomIconImp createZoomIcon( Icon icon )
	{
		ZoomIconImp result = null;
		
		if( icon instanceof ZoomIconImp )
		{
			result = (ZoomIconImp) icon;
		}
		else if( icon != null )
		{
			result = new ZoomIconImp( icon, _zoomFactor );
//			result.setAdditionalFactor( 1.33D );
		}
		return( result );
	}

	protected Icon getIcon( int index )
	{
		IconData id = zoomIcons[index];
		Icon icon = id._iconGetter.apply(_abstractButtonForZoomComponent);
		Icon originalIcon = getOriginalIcon(id._zoomedIcon);
		if( (id._zoomedIcon == null) ||
			( originalIcon != null ) && ( originalIcon != icon) )
		{
			id._zoomedIcon = createZoomIcon( icon );
		}

		id._zoomedIcon = this.invertZoomIconIfNecessary( id._zoomedIcon );

		return( id._zoomedIcon );
	}

	protected Icon getOriginalIcon( ZoomIcon zi )
	{
		Icon result = null;
		if( zi instanceof ZoomIconImp )
			result = ( (ZoomIconImp) zi).getOriginalIcon();

		return( result );
	}

	public Icon getDisabledIcon()
	{
		return( getIcon( DISABLED_ICON_INDEX ) );
	}

	public Icon getDisabledSelectedIcon()
	{
		return( getIcon( DISABLED_SELECTED_ICON_INDEX ) );
	}

	public Icon getIcon()
	{
		return( getIcon( ICON_INDEX ) );
	}

	public Icon getPressedIcon()
	{
		return( getIcon( PRESSED_ICON_INDEX ) );
	}

	public Icon getRolloverIcon()
	{
		return( getIcon( ROLL_OVER_ICON_INDEX ) );
	}

	public Icon getRolloverSelectedIcon()
	{
		return( getIcon( ROLL_OVER_SELECTED_ICON_INDEX ) );
	}

	public Icon getSelectedIcon()
	{
		return( getIcon( SELECTED_ICON_INDEX ) );
	}

	protected ZoomIcon invertZoomIconIfNecessary( ZoomIcon original )
	{
		ZoomIcon result = original;

		// if icons cannot be inverted, we have to invert them if necessary,
		// as  paint function inverts all component image when inverted
		// and double inversion makes the image be the original one.
		if( ( result != null )  && canInvertIcons() )
		{
			if( hasToInvert() != result.areColorsInverted() )
				result = (ZoomIcon) IconFunctions.instance().invertIconColors(result);
		}

		return( result );
	}

	protected boolean hasToInvert()
	{
		return( _hasToInvert );
	}

	public void invertColors( ColorInversor colorInversor )
	{
		_hasToInvert = !_hasToInvert;
	}

	protected static class IconData
	{
		public Function<ComponentWithIconForZoomInterface, Icon> _iconGetter;
		public ZoomIcon _zoomedIcon;

		public IconData( Function<ComponentWithIconForZoomInterface, Icon> iconGetter )
		{
			_iconGetter = iconGetter;
		}
	}
}
