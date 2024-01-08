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
package com.frojasg1.general.desktop.completion.base;

import com.frojasg1.general.desktop.generic.view.DesktopViewTextComponent;
import java.awt.Rectangle;
import javax.swing.SwingUtilities;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public abstract class DesktopInputTextCompletionManagerBase extends InputTextCompletionManagerBase< Rectangle >
{
/*
	public DesktopInputTextCompletionManagerBase( CompletionConfiguration conf )//TextCompletionConfiguration conf )
	{
//		super( conf );
		super( conf );
	}
*/
	@Override
	public DesktopViewTextComponent getInputTextComponent()
	{
		return( (DesktopViewTextComponent) _inputTextComponent );
	}

	@Override
	protected void setCaretPositionAndRequestFocus( int caretPosition )
	{
		_inputTextComponent.setCaretPosition( caretPosition );
		SwingUtilities.invokeLater( () ->{
			SwingUtilities.invokeLater( () -> {
				SwingUtilities.invokeLater( () -> {
					if( ! _inputTextComponent.hasFocus() )
						_inputTextComponent.requestFocus();
					});
			});
		});
	}

	@Override
	protected abstract String getCaretWord( String text, int caretPos );
}
