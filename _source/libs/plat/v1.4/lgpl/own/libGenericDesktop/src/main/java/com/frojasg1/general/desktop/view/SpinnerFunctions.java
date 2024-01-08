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
package com.frojasg1.general.desktop.view;

import java.awt.Component;
import java.util.function.Consumer;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.plaf.basic.BasicSpinnerUI;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class SpinnerFunctions
{
	protected static SpinnerFunctions _instance = null;

	public static SpinnerFunctions instance()
	{
		if( _instance == null )
			_instance = new SpinnerFunctions();

		return( _instance );
	}

	public void browseSpinnerButtons( JSpinner spinner, Consumer<Component> consumer )
	{
		if( ( consumer != null ) && ( spinner.getUI() instanceof BasicSpinnerUI ) )
		{
			for (Component component : spinner.getComponents()) {
				if (component.getName() != null && component.getName().endsWith("Button")) {
					consumer.accept(component);
				}
			}
		}
	}

	public void setMaxValue( JSpinner spinner, Integer max )
	{
		modelSetterGen( spinner, (model) -> model.setMaximum(max) );
	}

	public void setMinValue( JSpinner spinner, Integer min )
	{
		modelSetterGen( spinner, (model) -> model.setMinimum(min) );
	}

	public void modelSetterGen( JSpinner spinner, Consumer<SpinnerNumberModel> setter )
	{
		SpinnerNumberModel model = getSpinnerNumberModel( spinner );
		if( model != null )
			setter.accept(model);
	}

	protected SpinnerNumberModel getSpinnerNumberModel( JSpinner spinner )
	{
		SpinnerNumberModel result = null;
		if( spinner.getModel() instanceof SpinnerNumberModel )
			result = (SpinnerNumberModel) spinner.getModel();

		return( result );
	}

	public void limitRange( JSpinner spinner, Integer min, Integer max )
	{
		if( spinner != null )
		{
			this.setMinValue(spinner, min);
			this.setMaxValue(spinner, max);
		}
	}
}
