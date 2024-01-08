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
import javax.swing.JSlider;
import javax.swing.event.ChangeListener;
import javax.swing.event.MenuDragMouseListener;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class JSliderCopier extends CompCopierBase<JSlider>
{

	@Override
	protected List<CompCopier<JSlider>> createCopiers() {

		List<CompCopier<JSlider>> result = new ArrayList<>();

		result.add( createChangeListenersListCopier() );
		result.add( createExtentCopier() );
		result.add( createInvertedCopier() );
		result.add( createLabelTableCopier() );
		result.add( createMajorTickSpacingCopier() );
		result.add( createMaximumCopier() );
		result.add( createMinimumCopier() );
		result.add( createMinorTickSpacingCopier() );
		result.add( createModelCopier() );
		result.add( createOrientationCopier() );
		result.add( createPaintLabelsCopier() );
		result.add( createPaintTicksCopier() );
		result.add( createPaintTrackCopier() );
		result.add( createSnapToTicksCopier() );
		result.add( createValueCopier() );

		return( result );
	}

	protected CompCopier<JSlider> createChangeListenersListCopier()
	{
		return( (originalComponent, newComponent) -> copyChangeListenersList( originalComponent, newComponent ) );
	}

	protected CompCopier<JSlider> createExtentCopier()
	{
		return( (originalComponent, newComponent) -> copyExtent( originalComponent, newComponent ) );
	}

	protected CompCopier<JSlider> createInvertedCopier()
	{
		return( (originalComponent, newComponent) -> copyInverted( originalComponent, newComponent ) );
	}

	protected CompCopier<JSlider> createLabelTableCopier()
	{
		return( (originalComponent, newComponent) -> copyLabelTable( originalComponent, newComponent ) );
	}

	protected CompCopier<JSlider> createMajorTickSpacingCopier()
	{
		return( (originalComponent, newComponent) -> copyMajorTickSpacing( originalComponent, newComponent ) );
	}

	protected CompCopier<JSlider> createMaximumCopier()
	{
		return( (originalComponent, newComponent) -> copyMaximum( originalComponent, newComponent ) );
	}

	protected CompCopier<JSlider> createMinimumCopier()
	{
		return( (originalComponent, newComponent) -> copyMinimum( originalComponent, newComponent ) );
	}

	protected CompCopier<JSlider> createMinorTickSpacingCopier()
	{
		return( (originalComponent, newComponent) -> copyMinorTickSpacing( originalComponent, newComponent ) );
	}

	protected CompCopier<JSlider> createModelCopier()
	{
		return( (originalComponent, newComponent) -> copyModel( originalComponent, newComponent ) );
	}

	protected CompCopier<JSlider> createOrientationCopier()
	{
		return( (originalComponent, newComponent) -> copyOrientation( originalComponent, newComponent ) );
	}

	protected CompCopier<JSlider> createPaintLabelsCopier()
	{
		return( (originalComponent, newComponent) -> copyPaintLabels( originalComponent, newComponent ) );
	}

	protected CompCopier<JSlider> createPaintTicksCopier()
	{
		return( (originalComponent, newComponent) -> copyPaintTicks( originalComponent, newComponent ) );
	}

	protected CompCopier<JSlider> createPaintTrackCopier()
	{
		return( (originalComponent, newComponent) -> copyPaintTrack( originalComponent, newComponent ) );
	}

	protected CompCopier<JSlider> createSnapToTicksCopier()
	{
		return( (originalComponent, newComponent) -> copySnapToTicks( originalComponent, newComponent ) );
	}

	protected CompCopier<JSlider> createValueCopier()
	{
		return( (originalComponent, newComponent) -> copyValue( originalComponent, newComponent ) );
	}

	@Override
	public Class<JSlider> getParameterClass() {
		return( JSlider.class );
	}

	protected void copyChangeListenersList( JSlider originalComponent, JSlider newComponent )
	{
		copyListeners( originalComponent, newComponent,
						ChangeListener.class,
						(c) -> c.getChangeListeners(),
						(c,l) -> c.addChangeListener(l),
						(c,l) -> c.removeChangeListener(l) );
	}

	protected void copyExtent( JSlider originalComponent, JSlider newComponent )
	{
		newComponent.setExtent( originalComponent.getExtent() );
	}

	protected void copyInverted( JSlider originalComponent, JSlider newComponent )
	{
		newComponent.setInverted( originalComponent.getInverted() );
	}

	protected void copyLabelTable( JSlider originalComponent, JSlider newComponent )
	{
		newComponent.setLabelTable( originalComponent.getLabelTable() );
	}

	protected void copyMajorTickSpacing( JSlider originalComponent, JSlider newComponent )
	{
		newComponent.setMajorTickSpacing( originalComponent.getMajorTickSpacing() );
	}

	protected void copyMaximum( JSlider originalComponent, JSlider newComponent )
	{
		newComponent.setMaximum( originalComponent.getMaximum() );
	}

	protected void copyMinimum( JSlider originalComponent, JSlider newComponent )
	{
		newComponent.setMinimum( originalComponent.getMinimum() );
	}

	protected void copyMinorTickSpacing( JSlider originalComponent, JSlider newComponent )
	{
		newComponent.setMinorTickSpacing( originalComponent.getMinorTickSpacing() );
	}

	protected void copyModel( JSlider originalComponent, JSlider newComponent )
	{
		newComponent.setModel( originalComponent.getModel() );
	}

	protected void copyOrientation( JSlider originalComponent, JSlider newComponent )
	{
		newComponent.setOrientation( originalComponent.getOrientation() );
	}

	protected void copyPaintLabels( JSlider originalComponent, JSlider newComponent )
	{
		newComponent.setPaintLabels( originalComponent.getPaintLabels() );
	}

	protected void copyPaintTicks( JSlider originalComponent, JSlider newComponent )
	{
		newComponent.setPaintTicks( originalComponent.getPaintTicks() );
	}

	protected void copyPaintTrack( JSlider originalComponent, JSlider newComponent )
	{
		newComponent.setPaintTrack( originalComponent.getPaintTrack() );
	}

	protected void copySnapToTicks( JSlider originalComponent, JSlider newComponent )
	{
		newComponent.setSnapToTicks( originalComponent.getSnapToTicks() );
	}

	protected void copyValue( JSlider originalComponent, JSlider newComponent )
	{
		newComponent.setValue( originalComponent.getValue() );
	}
}
