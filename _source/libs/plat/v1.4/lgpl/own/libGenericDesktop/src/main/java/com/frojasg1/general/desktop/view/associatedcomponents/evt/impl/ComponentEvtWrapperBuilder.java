/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.desktop.view.associatedcomponents.evt.impl;

import com.frojasg1.general.desktop.view.associatedcomponents.ComponentEvtWrapperBuilderBase;
import com.frojasg1.general.desktop.view.associatedcomponents.evt.ComponentEvtWrapperSimpleDefaultBase;
import java.awt.Component;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import javax.swing.AbstractButton;
import javax.swing.JMenuItem;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class ComponentEvtWrapperBuilder extends ComponentEvtWrapperBuilderBase
{
	protected Map<Class<? extends Component>,
					BiFunction<Component, Component, ComponentEvtWrapperSimpleDefaultBase>> _mapOfBuilders;

	protected static final ComponentEvtWrapperBuilder INSTANCE = new ComponentEvtWrapperBuilder().init();

	public static ComponentEvtWrapperBuilder instance()
	{
		return( INSTANCE );
	}

	public ComponentEvtWrapperBuilder init()
	{
		_mapOfBuilders = new HashMap<>();

		fillMap( _mapOfBuilders );

		return( this );
	}

	protected void fillMap( Map map )
	{
		put( map, AbstractButton.class, AbstractButtonEvtWrapper::new );
		put( map, JMenuItem.class, MenuItemEvtWrapper::new );
	}

	protected <C extends Component> void put(
		Map<Class<? extends Component>,	BiFunction> map,
		Class<C> componentClass,
		BiFunction<C, Component, ComponentEvtWrapperSimpleDefaultBase<C>> wrapperBuilder )
	{
		map.put(componentClass, wrapperBuilder);
	}

	public ComponentEvtWrapperSimpleDefaultBase<? extends Component> apply(Component component, Component parent)
	{
		ComponentEvtWrapperSimpleDefaultBase<? extends Component> result = null;
		Class<? extends Component> compClass = getCompClass( component, getMapOfBuilders().keySet() );

		if( compClass != null )
			result = getMapOfBuilders().get(compClass).apply(component, parent);

		if( result != null )
			result.init();

		return( result );
	}

	public Map<Class<? extends Component>,
					BiFunction<Component, Component, ComponentEvtWrapperSimpleDefaultBase>> getMapOfBuilders() {
		return _mapOfBuilders;
	}
}
