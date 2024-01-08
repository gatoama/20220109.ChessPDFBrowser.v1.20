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
import javax.swing.JSpinner;
import javax.swing.event.ChangeListener;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class JSpinnerCopier extends CompCopierBase<JSpinner>
{

	@Override
	protected List<CompCopier<JSpinner>> createCopiers() {

		List<CompCopier<JSpinner>> result = new ArrayList<>();

		result.add( createChangeListenersCopier() );
//		result.add( createEditorCopier() );
		result.add( createModelCopier() );
		result.add( createValueCopier() );

		return( result );
	}

	protected CompCopier<JSpinner> createChangeListenersCopier()
	{
		return( (originalComponent, newComponent) -> copyChangeListeners( originalComponent, newComponent ) );
	}

	protected CompCopier<JSpinner> createEditorCopier()
	{
		return( (originalComponent, newComponent) -> copyEditor( originalComponent, newComponent ) );
	}

	protected CompCopier<JSpinner> createModelCopier()
	{
		return( (originalComponent, newComponent) -> copyModel( originalComponent, newComponent ) );
	}

	protected CompCopier<JSpinner> createValueCopier()
	{
		return( (originalComponent, newComponent) -> copyValue( originalComponent, newComponent ) );
	}

	@Override
	public Class<JSpinner> getParameterClass() {
		return( JSpinner.class );
	}

	protected void copyChangeListeners( JSpinner originalComponent, JSpinner newComponent )
	{
		copyListeners( originalComponent, newComponent,
						ChangeListener.class,
						(c) -> c.getChangeListeners(),
						(c,l) -> c.addChangeListener(l),
						(c,l) -> c.removeChangeListener(l) );
	}

	protected void copyEditor( JSpinner originalComponent, JSpinner newComponent )
	{
//		newComponent.setEditor( originalComponent.getEditor() );
	}

	protected void copyModel( JSpinner originalComponent, JSpinner newComponent )
	{
		newComponent.setModel( originalComponent.getModel() );
	}

	protected void copyValue( JSpinner originalComponent, JSpinner newComponent )
	{
		newComponent.setValue( originalComponent.getValue() );
	}
}
