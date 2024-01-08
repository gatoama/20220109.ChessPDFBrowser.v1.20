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
package com.frojasg1.general.desktop.view.text.link.imp;

import com.frojasg1.applications.common.components.internationalization.JFrameInternationalization;
import com.frojasg1.general.desktop.copypastepopup.TextCompPopupManager;
import com.frojasg1.general.desktop.view.text.StringAndPosition;
import com.frojasg1.general.desktop.view.text.link.JTextComponentMouseLinkListener;
import com.frojasg1.general.undoredo.text.TextUndoRedoInterface;
import com.frojasg1.generic.GenericFunctions;
import javax.swing.JViewport;
import javax.swing.text.JTextComponent;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class ScrollableJTextComponentUrlLauncher extends JTextComponentMouseLinkListener
{
	protected StringAndPosition _lastURLorEmailAddress = null;

	protected JFrameInternationalization _intern;

	protected TextCompPopupManager _textPopupManager = null;

	public ScrollableJTextComponentUrlLauncher(  )
	{
	}

	@Override
	protected void changeCursor_internal( JViewport textComponentParent, int oldScrollYYValue )
	{
		super.changeCursor_internal(textComponentParent, oldScrollYYValue);

		if( _textPopupManager != null )
			_textPopupManager.setUrlOrEmailAddress( _lastURLorEmailAddress );
	}

	protected StringAndPosition getCurrentUrlOrEmailAddress( JTextComponent tc, int pos )
	{
		StringAndPosition result = null;
		
		StringAndPosition strAndPos = getMouseText( tc, pos );

		if( strAndPos != null )
		{
			if( GenericFunctions.instance().getSystem().isUrl(strAndPos.getString() ) )
				result = strAndPos;
			else if( GenericFunctions.instance().getSystem().isEmailAddress(strAndPos.getString()) )
				result = strAndPos;
		}

		return( result );
	}

	@Override
	protected void doActions()
	{
		if( _lastURLorEmailAddress != null )
		{
			String address = _lastURLorEmailAddress.getString();
			if( GenericFunctions.instance().getSystem().isUrl(address) )
				GenericFunctions.instance().getSystem().browse(address);
			else if( GenericFunctions.instance().getSystem().isEmailAddress(address) )
				GenericFunctions.instance().getSystem().mailTo(address);
		}
	}
	public void setInternationalization( JFrameInternationalization intern )
	{
		_intern = intern;

		if( _intern != null )
		{
			_textPopupManager = _intern.getTextCompPopupManager( _textComp );

			if( _textPopupManager != null )
				_textPopupManager.setIsUrlTextComponent( true );

			TextUndoRedoInterface undoRedoInterface = _intern.getTextUndoRedoManager( _textComp );

			if( undoRedoInterface != null )
				undoRedoInterface.registerListener( this );
		}
	}

	@Override
	protected boolean hasToShowHand(int pos)
	{
		_lastURLorEmailAddress = getCurrentUrlOrEmailAddress( _textComp, pos );

		return( _lastURLorEmailAddress != null );
	}
}
