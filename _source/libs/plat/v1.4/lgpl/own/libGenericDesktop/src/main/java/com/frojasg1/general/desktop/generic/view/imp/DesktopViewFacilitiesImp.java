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
package com.frojasg1.general.desktop.generic.view.imp;

import com.frojasg1.general.desktop.undoredo.DesktopTextUndoRedoImp;
import com.frojasg1.general.desktop.generic.view.DesktopViewComponent;
import com.frojasg1.general.desktop.generic.view.DesktopViewFacilities;
import com.frojasg1.general.desktop.generic.view.DesktopViewTextComponent;
import com.frojasg1.general.desktop.generic.view.SimpleViewComponent;
import com.frojasg1.general.desktop.generic.view.SimpleViewTextComponent;
import com.frojasg1.general.undoredo.text.TextUndoRedoInterface;
import com.frojasg1.general.view.ViewComponent;
import com.frojasg1.general.view.ViewTextComponent;
import java.awt.Component;
import javax.swing.text.JTextComponent;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class DesktopViewFacilitiesImp implements DesktopViewFacilities
{
	protected static DesktopViewFacilitiesImp _instance;

	public static void changeInstance( DesktopViewFacilitiesImp inst )
	{
		_instance = inst;
	}

	public static DesktopViewFacilitiesImp instance()
	{
		if( _instance == null )
			_instance = new DesktopViewFacilitiesImp();
		return( _instance );
	}

	@Override
	public TextUndoRedoInterface createTextUndoRedoObject( ViewTextComponent view )
	{
		DesktopTextUndoRedoImp result = null;
		if( view instanceof DesktopViewTextComponent )
		{
			DesktopViewTextComponent view2 = (DesktopViewTextComponent) view;
			result = new DesktopTextUndoRedoImp( view2 );
			result.initilize();
		}
		else
		{
			throw( new RuntimeException( "Error: view parameter was not of DesktopViewTextComponent." ) );
		}

		return( result );
	}

	@Override
	public DesktopViewTextComponent createTextViewComponent( JTextComponent textComp )
	{
		DesktopViewTextComponent result = null;
		if( textComp != null )
			result = new SimpleViewTextComponent( textComp );

		return( result );
	}

	@Override
	public DesktopViewComponent createViewComponent( Component comp )
	{
		DesktopViewComponent result = null;
		if( comp != null )
			result = new SimpleViewComponent( comp );

		return( result );
	}

	@Override
	public DesktopViewComponent getParentViewComponent( ViewComponent vc )
	{
		DesktopViewComponent result = null;

		if( vc instanceof DesktopViewComponent )
		{
			DesktopViewComponent dvc = (DesktopViewComponent) vc;

			Component comp = dvc.getComponent();
			if( comp instanceof DesktopViewComponent )
				result = (DesktopViewComponent) comp;
			else
			{
				if( comp.getParent() != null )
					result = createViewComponent( comp.getParent() );
			}
		}

		return( result );
	}

}
