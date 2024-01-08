/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.desktop.view.associatedcomponents.value.impl.bool;

import com.frojasg1.general.desktop.view.associatedcomponents.value.impl.*;
import com.frojasg1.general.desktop.view.associatedcomponents.value.ComponentValueWrapperSimpleDefault;
import java.awt.Component;
import java.util.Map;
import java.util.function.BiFunction;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JToggleButton;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class ComponentBoolValueWrapperBuilder
	extends ComponentValueWrapperBuilderBase<Boolean>
{
	protected static final ComponentBoolValueWrapperBuilder INSTANCE = new ComponentBoolValueWrapperBuilder().init();

	public static ComponentBoolValueWrapperBuilder instance()
	{
		return( INSTANCE );
	}

	public ComponentBoolValueWrapperBuilder init()
	{
		super.init();

		return( this );
	}

	protected void fillMap( Map<Class<? extends Component>,
				BiFunction<Component, Component,
						ComponentValueWrapperSimpleDefault>> map )
	{
		put( map, JCheckBoxMenuItem.class, JCheckBoxMenuItemWrapperDefault::new );
		put( map, JCheckBox.class, JCheckBoxWrapperDefault::new );
		put( map, JToggleButton.class, ToggleButtonWrapperDefault::new );
	}

	public ComponentValueWrapperSimpleDefault<? extends Component, Boolean> apply(Component component,
																				Component parent)
	{
		return( super.apply(component, parent) );
	}
}
