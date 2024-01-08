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
package com.frojasg1.chesspdfbrowser.enginewrapper.view.configuration.items;

import com.frojasg1.applications.common.components.internationalization.window.exceptions.ValidationException;
import com.frojasg1.applications.common.components.resizecomp.MapResizeRelocateComponentItem;
import com.frojasg1.applications.common.components.resizecomp.ResizeRelocateItem;
import com.frojasg1.chesspdfbrowser.enginewrapper.configuration.items.ValueItem;
import com.frojasg1.general.desktop.generic.view.DesktopViewComponent;
import com.frojasg1.general.desktop.view.zoom.mapper.ComposedComponent;
import com.frojasg1.general.view.ViewComponent;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;
import javax.swing.JPanel;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public abstract class ConfigurationItemJPanelBase<CC, CI extends ValueItem<CC>>
	extends com.frojasg1.general.desktop.view.panels.CustomJPanelBase implements ConfigurationItemView<CC, CI>,
								DesktopViewComponent, ComposedComponent
{
	protected MapResizeRelocateComponentItem _resizeRelocateInfo = null;

	protected void init()
	{
		super.init();

		initComponents_protected();

		initContents();

		setWindowConfiguration();
	}

	protected abstract void initComponents_protected();
	protected abstract void initContents();
	protected abstract JPanel getParentPanel();

	protected void setWindowConfiguration( )
	{
		_resizeRelocateInfo = new MapResizeRelocateComponentItem();
		MapResizeRelocateComponentItem mapRRCI = _resizeRelocateInfo;
		try
		{
			boolean postponeInit = true;
			mapRRCI.putResizeRelocateComponentItem( this, ResizeRelocateItem.FILL_WHOLE_WIDTH, postponeInit );
			mapRRCI.putResizeRelocateComponentItem( getParentPanel(), ResizeRelocateItem.FILL_WHOLE_PARENT );
		}
		catch( Throwable th )
		{
			th.printStackTrace();
		}
	}

	@Override
	public MapResizeRelocateComponentItem getResizeRelocateInfo()
	{
		return( _resizeRelocateInfo );
	}

	@Override
	public Dimension getInternalSize()
	{
		return( getParentPanel().getSize() );
	}

	@Override
	public Rectangle getInternalBounds()
	{
		return( getParentPanel().getBounds() );
	}

	@Override
	public ConfigurationItemJPanelBase<CC, CI> getComponent()
	{
		return( this );
	}

	public void validateChanges() throws ValidationException
	{
	}

	@Override
	public void revert()
	{
		initContents();
	}

	@Override
	public void releaseResources()
	{
		_resizeRelocateInfo = null;
	}
}
