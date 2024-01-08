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
package com.frojasg1.applications.common.components.data;

import com.frojasg1.general.desktop.copypastepopup.TextCompPopupManager;
import com.frojasg1.general.undoredo.text.TextUndoRedoInterface;
import com.frojasg1.applications.common.components.resizecomp.ResizeRelocateItem;
import com.frojasg1.general.desktop.view.color.impl.ColorThemeChangeableBase;
import java.awt.Component;
import com.frojasg1.general.desktop.view.color.ColorThemeChangeableStatus;
import com.frojasg1.general.desktop.view.color.ColorThemeChangeableStatusBuilder;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class ComponentData
{
	protected ResizeRelocateItem _resizeRelocateItem = null;
	protected InfoForResizingPanels _infoForResizingPanels = null;

	protected boolean _resizeParents = false;

//	protected TextUndoRedoInterface _textUndoRedoManager = null;

//	protected double _originalSizeOfText = 1;

	protected TextCompPopupManager _textPopupMenuManager;

	protected ColorThemeChangeableStatus _colorThemeChangeable;

	protected Component _component;

	public ComponentData(Component component)
	{
		_component = component;
	}

	public void setComponent( Component component )
	{
		_component = component;
	}

	public ResizeRelocateItem getResizeRelocateItem()
	{
		return( _resizeRelocateItem );
	}

	public void setResizeRelocateItem( ResizeRelocateItem rri )
	{
		if( ( _resizeRelocateItem != null ) && ( _resizeRelocateItem != rri ) )
		{
			boolean parentListenerWasAdded = _resizeRelocateItem.isParentComponentListenerAdded();
			_resizeRelocateItem.dispose();

			if( ( rri != null ) && parentListenerWasAdded )
				rri.registerListeners();
		}

		_resizeRelocateItem = rri;
	}

	public InfoForResizingPanels getInfoForResizingPanels()
	{
		return( _infoForResizingPanels );
	}

	public void setInfoForResizingPanels( InfoForResizingPanels rri )
	{
		_infoForResizingPanels = rri;
	}

	public boolean getResizeParents()
	{
		return( _resizeParents );
	}

	public void setResizeParents( boolean value )
	{
		_resizeParents = value;
	}

	public TextCompPopupManager getTextCompPopupManager()
	{
		return( _textPopupMenuManager );
	}

	public void setTextCompPopupManager( TextCompPopupManager tcpm )
	{
		_textPopupMenuManager = tcpm;
	}

	protected Component getComponent()
	{
		return( _component );
	}

	protected ColorThemeChangeableStatus createColorThemeChangeable()
	{
		ColorThemeChangeableStatus result = null;
		Component comp = getComponent();
		if( comp instanceof  ColorThemeChangeableStatus )
			result = (ColorThemeChangeableStatus) comp;
		else if( comp instanceof ColorThemeChangeableStatusBuilder )
			result = ( (ColorThemeChangeableStatusBuilder) getComponent()).createColorThemeChangeableStatus();
		else
			result = new ColorThemeChangeableBase();

		return( result );
	}

	public ColorThemeChangeableStatus getColorThemeChangeableStatus()
	{
		if( _colorThemeChangeable == null )
			_colorThemeChangeable = createColorThemeChangeable();

		return( _colorThemeChangeable );
	}

	public void setColorThemeChangeable( ColorThemeChangeableStatus ctc )
	{
		_colorThemeChangeable = ctc;
	}

	public TextUndoRedoInterface getTextUndoRedoManager()
	{
		TextUndoRedoInterface result = null;
		if( getTextCompPopupManager() != null )
			result = getTextCompPopupManager().getUndoRedoManager();

		return( result );
	}
/*
	public void setTextUndoRedoManager( TextUndoRedoInterface turm )
	{
		_textUndoRedoManager = turm;
	}
*/
	public boolean isEmpty()
	{
		boolean result = ( _resizeParents == false ) && ( _resizeRelocateItem == null ) && ( _infoForResizingPanels == null );
		return( result );
	}

/*
	public void setOriginalTextSize( double size )
	{
		_originalSizeOfText = size;
	}

	public int getFactoredTextSize( double factor )
	{
		return( (int) Math.round( _originalSizeOfText * factor ) );
	}
*/
}
