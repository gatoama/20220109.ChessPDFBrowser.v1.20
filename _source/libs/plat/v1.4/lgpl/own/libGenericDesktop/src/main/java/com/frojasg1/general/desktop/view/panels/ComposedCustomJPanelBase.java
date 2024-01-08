/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.desktop.view.panels;

import com.frojasg1.applications.common.components.resizecomp.MapResizeRelocateComponentItem;
import com.frojasg1.general.desktop.view.zoom.mapper.ComposedComponent;
import java.awt.Dimension;
import java.awt.Rectangle;
import javax.swing.JPanel;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public abstract class ComposedCustomJPanelBase extends CustomJPanelBase
									implements ComposedComponent
{
	protected MapResizeRelocateComponentItem _resizeRelocateInfo = null;

	protected abstract MapResizeRelocateComponentItem createMapResizeRelocateComponentItem();

	@Override
	public void init()
	{
		super.init();
		
		initComponentsChild();
		setWindowConfiguration();
	}

	protected abstract void initComponentsChild();

	protected void setWindowConfiguration( )
	{
		_resizeRelocateInfo = createMapResizeRelocateComponentItem();
		if( _resizeRelocateInfo != null )
			_resizeRelocateInfo.addAllPopupMenus( getJPopupMenuListCopy() );
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

	protected abstract JPanel getParentPanel();
}
