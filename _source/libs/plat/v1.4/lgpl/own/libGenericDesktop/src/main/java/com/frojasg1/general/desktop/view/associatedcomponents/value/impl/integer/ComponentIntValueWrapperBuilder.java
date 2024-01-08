/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.desktop.view.associatedcomponents.value.impl.integer;

import com.frojasg1.general.desktop.view.associatedcomponents.value.BoundedComponentValueWrapperSimpleDefault;
import com.frojasg1.general.desktop.view.associatedcomponents.value.impl.*;
import java.awt.Component;
import java.util.Map;
import java.util.function.BiFunction;
import javax.swing.JSlider;
import javax.swing.JSpinner;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class ComponentIntValueWrapperBuilder
	extends BoundedComponentValueWrapperBuilderBase<Integer>
{
	protected static final ComponentIntValueWrapperBuilder INSTANCE = new ComponentIntValueWrapperBuilder().init();

	public static ComponentIntValueWrapperBuilder instance()
	{
		return( INSTANCE );
	}

	public ComponentIntValueWrapperBuilder init()
	{
		super.init();

		return( this );
	}

	protected void fillMap( Map<Class<? extends Component>,
				BiFunction<Component, Component,
						BoundedComponentValueWrapperSimpleDefault>> map )
	{
		put( map, JSlider.class, JSliderWrapperDefault::new );
		put( map, JSpinner.class, JSpinnerWrapperDefault::new );
	}

	public BoundedComponentValueWrapperSimpleDefault<? extends Component, Integer> apply(Component component,
																				Component parent)
	{
		return( super.apply(component, parent) );
	}
}
