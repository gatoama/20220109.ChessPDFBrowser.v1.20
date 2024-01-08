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
package com.frojasg1.general.desktop.view.zoom.componentcopier.imp;

import com.frojasg1.general.desktop.view.zoom.componentcopier.CompCopier;
import com.frojasg1.general.desktop.view.zoom.componentcopier.CompCopierBase;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class JScrollPaneCopier extends CompCopierBase<JScrollPane>
{
	@Override
	protected List<CompCopier<JScrollPane>> createCopiers() {

		List<CompCopier<JScrollPane>> result = new ArrayList<>();

		result.add( createColumnHeaderCopier() );
		result.add( createCornersCopier() );
//		result.add( createHorizontalScrollBarCopier() );
		result.add( createHorizontalScrollBarPolicyCopier() );
		result.add( createRowHeaderCopier() );
		result.add( createVerticalScrollBarCopier() );
//		result.add( createVerticalScrollBarPolicyCopier() );
		result.add( createViewportCopier() );
		result.add( createViewportBorderCopier() );
		result.add( createWheelScrollingEnabledCopier() );

		return( result );
	}

	protected CompCopier<JScrollPane> createColumnHeaderCopier()
	{
		return( (originalComponent, newComponent) -> copyColumnHeader( originalComponent, newComponent ) );
	}

	protected CompCopier<JScrollPane> createCornersCopier()
	{
		return( (originalComponent, newComponent) -> copyCorners( originalComponent, newComponent ) );
	}

	protected CompCopier<JScrollPane> createHorizontalScrollBarCopier()
	{
		return( (originalComponent, newComponent) -> copyHorizontalScrollBar( originalComponent, newComponent ) );
	}

	protected CompCopier<JScrollPane> createVerticalScrollBarCopier()
	{
		return( (originalComponent, newComponent) -> copyVerticalScrollBar( originalComponent, newComponent ) );
	}

	protected CompCopier<JScrollPane> createHorizontalScrollBarPolicyCopier()
	{
		return( (originalComponent, newComponent) -> copyHorizontalScrollBarPolicy( originalComponent, newComponent ) );
	}

	protected CompCopier<JScrollPane> createVerticalScrollBarPolicyCopier()
	{
		return( (originalComponent, newComponent) -> copyVerticalScrollBarPolicy( originalComponent, newComponent ) );
	}

	protected CompCopier<JScrollPane> createRowHeaderCopier()
	{
		return( (originalComponent, newComponent) -> copyRowHeader( originalComponent, newComponent ) );
	}

	protected CompCopier<JScrollPane> createViewportCopier()
	{
		return( (originalComponent, newComponent) -> copyViewport( originalComponent, newComponent ) );
	}

	protected CompCopier<JScrollPane> createViewportBorderCopier()
	{
		return( (originalComponent, newComponent) -> copyViewportBorder( originalComponent, newComponent ) );
	}

	protected CompCopier<JScrollPane> createWheelScrollingEnabledCopier()
	{
		return( (originalComponent, newComponent) -> copyWheelScrollingEnabled( originalComponent, newComponent ) );
	}

	@Override
	public Class<JScrollPane> getParameterClass() {
		return( JScrollPane.class );
	}

	protected void copyColumnHeader( JScrollPane originalComponent, JScrollPane newComponent )
	{
		newComponent.setColumnHeader( originalComponent.getColumnHeader() );
	}

	protected void copyCorner( JScrollPane originalComponent, JScrollPane newComponent, String cornerKey )
	{
		newComponent.setCorner(cornerKey, newComponent.getCorner(cornerKey) );
	}

	protected void copyCorners( JScrollPane originalComponent, JScrollPane newComponent )
	{
		copyCorner( originalComponent, newComponent, ScrollPaneConstants.LOWER_LEFT_CORNER );
		copyCorner( originalComponent, newComponent, ScrollPaneConstants.LOWER_LEADING_CORNER );
		copyCorner( originalComponent, newComponent, ScrollPaneConstants.LOWER_TRAILING_CORNER );
		copyCorner( originalComponent, newComponent, ScrollPaneConstants.LOWER_RIGHT_CORNER );
		copyCorner( originalComponent, newComponent, ScrollPaneConstants.UPPER_LEFT_CORNER );
		copyCorner( originalComponent, newComponent, ScrollPaneConstants.UPPER_LEADING_CORNER );
		copyCorner( originalComponent, newComponent, ScrollPaneConstants.UPPER_TRAILING_CORNER );
		copyCorner( originalComponent, newComponent, ScrollPaneConstants.UPPER_RIGHT_CORNER );
	}

	protected void copyHorizontalScrollBar( JScrollPane originalComponent, JScrollPane newComponent )
	{
		newComponent.setHorizontalScrollBar( originalComponent.getHorizontalScrollBar() );
	}

	protected void copyVerticalScrollBar( JScrollPane originalComponent, JScrollPane newComponent )
	{
		newComponent.setVerticalScrollBar( originalComponent.getVerticalScrollBar() );
	}

	protected void copyHorizontalScrollBarPolicy( JScrollPane originalComponent, JScrollPane newComponent )
	{
		newComponent.setHorizontalScrollBarPolicy( originalComponent.getHorizontalScrollBarPolicy() );
	}

	protected void copyVerticalScrollBarPolicy( JScrollPane originalComponent, JScrollPane newComponent )
	{
		newComponent.setVerticalScrollBarPolicy( originalComponent.getVerticalScrollBarPolicy() );
	}

	protected void copyRowHeader( JScrollPane originalComponent, JScrollPane newComponent )
	{
		newComponent.setRowHeader( originalComponent.getRowHeader() );
	}

	protected void copyViewport( JScrollPane originalComponent, JScrollPane newComponent )
	{
		newComponent.setViewport( originalComponent.getViewport() );
	}

	protected void copyViewportBorder( JScrollPane originalComponent, JScrollPane newComponent )
	{
		newComponent.setViewportBorder( originalComponent.getViewportBorder() );
	}

	protected void copyWheelScrollingEnabled( JScrollPane originalComponent, JScrollPane newComponent )
	{
		newComponent.setWheelScrollingEnabled( originalComponent.isWheelScrollingEnabled() );
	}
}
