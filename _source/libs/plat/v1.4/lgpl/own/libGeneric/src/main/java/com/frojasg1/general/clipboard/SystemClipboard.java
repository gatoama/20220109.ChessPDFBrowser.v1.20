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
package com.frojasg1.general.clipboard;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

/**
 *
 * @author Usuario
 * http://www.javapractices.com/topic/TopicAction.do?Id=82
 */
public class SystemClipboard implements ClipboardOwner
{

	protected static SystemClipboard _instance = null;
	
	public static void main(String...  aArguments )
	{
		SystemClipboard textTransfer = new SystemClipboard();

		//display what is currently on the clipboard
		System.out.println("Clipboard contains:" + textTransfer.getClipboardContents());

		//change the contents and then re-display
		textTransfer.setClipboardContents("blah, blah, blah");
		System.out.println("Clipboard contains:" + textTransfer.getClipboardContents());
	}

	public static SystemClipboard instance()
	{
		if( _instance == null )
			_instance = new SystemClipboard();
		
		return( _instance );
	}

	protected SystemClipboard()
	{}

	/**
	* Empty implementation of the ClipboardOwner interface.
	*/
	@Override
	public void lostOwnership(Clipboard aClipboard, Transferable aContents)
	{
		//do nothing
	}

	/**
	* Place a String on the clipboard, and make this class the
	* owner of the Clipboard's contents.
	*/
	public void setClipboardContents(String aString)
	{
		StringSelection stringSelection = new StringSelection(aString);
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		clipboard.setContents(stringSelection, this);
	}

	/**
	* Get the String residing on the clipboard.
	*
	* @return any text found on the Clipboard; if none found, return an
	* empty String.
	*/
	public String getClipboardContents()
	{
		String result = "";
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		//odd: the Object param of getContents is not currently used
		Transferable contents = clipboard.getContents(null);
		boolean hasTransferableText =
			(contents != null) &&
			contents.isDataFlavorSupported(DataFlavor.stringFlavor);

		if (hasTransferableText)
		{
			try
			{
				result = (String)contents.getTransferData(DataFlavor.stringFlavor);
			}
			catch (UnsupportedFlavorException | IOException ex)
			{
				System.out.println(ex);
				ex.printStackTrace();
			}
		}
		return result;
	}
} 

