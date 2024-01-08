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
package com.frojasg1.general.commandline.generic.system;

import com.frojasg1.generic.system.SystemImp;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class DesktopSystem extends SystemImp
{
	protected static DesktopSystem _instance;

	public static void changeInstance( DesktopSystem inst )
	{
		_instance = inst;
	}

	public static DesktopSystem instance()
	{
		if( _instance == null )
			_instance = new DesktopSystem();
		return( _instance );
	}

	@Override
	public int getNumberOfProcessors()
	{
		return( Runtime.getRuntime().availableProcessors() );
	}

	@Override
	public Exception browse(String url)
	{
		Exception result = null;

		if( url != null )
		{
			if( Desktop.isDesktopSupported() )
			{
				try
				{
					Desktop.getDesktop().browse( new URI( url ) );
				}
				catch( Exception ex )
				{
					result = ex;
					ex.printStackTrace();
				}
			}
			else
			{
				Runtime runtime = Runtime.getRuntime();
				String command = null;
				if( System.getProperty("os.name").toLowerCase().indexOf( "win" ) >= 0)
				{
					command = "cmd /k start " + url;
				}
				else if( System.getProperty("os.name").toLowerCase().indexOf( "mac" ) >= 0)
				{
					command = "open " + url;
				}
				else if( System.getProperty("os.name").toLowerCase().indexOf( "nix" ) >= 0 ||
					System.getProperty("os.name").toLowerCase().indexOf( "nux" ) >= 0 )
				{
					String[] browsers = {"epiphany", "firefox", "mozilla", "konqueror",
						"netscape","opera","links","lynx"};

					StringBuffer cmd = new StringBuffer();
					for (int i=0; i<browsers.length; i++)
					cmd.append( (i==0  ? "" : " || " ) + browsers[i] +" \"" + url + "\" ");

					try
					{
						runtime.exec(new String[] { "sh", "-c", cmd.toString() });
					}
					catch (IOException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				if( command != null )
				{
					try
					{
						runtime.exec(command);
					}
					catch (IOException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
        }

		return( result );
	}

	@Override
	public Exception mailTo(String destinationEmailAddress)
	{
		Exception result = null;

		if( ( destinationEmailAddress != null ) && Desktop.isDesktopSupported() )
		{
			try
			{
				Desktop.getDesktop().mail( new URI( "mailto:" + destinationEmailAddress ) );
			}
			catch( Exception ex )
			{
				result = ex;
				ex.printStackTrace();
			}
		}

		return( result );
	}

	public void openDocument( String fileName ) throws IOException
	{
		File file = new File( fileName );
		Desktop dt = Desktop.getDesktop();
		dt.open(file);
	}

}
