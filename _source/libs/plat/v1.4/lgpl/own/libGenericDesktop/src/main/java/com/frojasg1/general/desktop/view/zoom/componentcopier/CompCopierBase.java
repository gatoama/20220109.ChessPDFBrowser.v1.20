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
package com.frojasg1.general.desktop.view.zoom.componentcopier;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public abstract class CompCopierBase<CC extends Component> implements CompCopier<CC>
{
	protected List<CompCopier<CC>> _list = new ArrayList<>();

	public CompCopierBase()
	{
		_list = createCopiers();
	}

	protected abstract List<CompCopier<CC>> createCopiers();


	public void addComponentCopier( CompCopier<CC> copier )
	{
		_list.add( copier );
	}

	protected boolean isInnerClass( Object obj )
	{
		boolean result = false;

		if( obj != null )
			result = obj.getClass().getName().contains( "$" );

		return( result );
	}

	protected boolean isClassOfJdk( Object obj )
	{
		boolean result = false;
		if( obj != null )
		{
			String className = obj.getClass().getName();
			result = ( className.startsWith( "javax.swing" ) ) ||
					( className.startsWith( "java.awt" ) );
		}

		return( result );
	}

	@Override
	public void copy( CC orig, CC output )
	{
		if( ( orig != null ) && ( output != null ) )
		{
			for( CompCopier<CC> copier: _list )
			{
				try
				{
					copier.copy( orig, output );
				}
				catch( Exception ex )
				{
					ex.printStackTrace();
				}
			}
		}
	}

	protected <LL> void copyListeners( CC orig, CC output,
										Class<LL> clazz,
										GetListeners<CC, LL> getListenersFunction,
										BiConsumer<CC, LL> setter, BiConsumer<CC, LL> remover )
	{
		if( ( orig != null ) && ( output != null ) )
		{
			LL[] listeners = getListenersFunction.getListeners(orig );
			if( listeners != null )
			{
				for( LL listener: listeners )
				{
					if( remover != null )
						remover.accept(orig, listener);

					if( !isClassOfJdk( listener ) )
						setter.accept(output, listener );
				}
			}
		}
	}

	protected interface GetListeners<CC, LL>
	{
		public LL[] getListeners( CC comp );
	}
}
