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
package com.frojasg1.general.view;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public interface ViewTextComponent< LL > extends ViewComponent
{
	public int getLength();

	public String getText();
	public void setText( String text );

	public void setEmptyText();

	public void replaceText( int pos, int length, String strToReplaceTo );
	public void replaceText( int pos, String strToReplaceFrom, String strToReplaceTo );

	public int getSelectionStart();
	public int getSelectionEnd();
	public void setSelectionBounds( int start, int length );
	public String getSelectedText();

	public int getCaretPosition();
	public void setCaretPosition( int position );

	public boolean isEnabled();
	public boolean isEditable();
	public void setEnabled( boolean value );
	public void setEditable( boolean value );

	public LL getCharacterBounds( int index );
}
