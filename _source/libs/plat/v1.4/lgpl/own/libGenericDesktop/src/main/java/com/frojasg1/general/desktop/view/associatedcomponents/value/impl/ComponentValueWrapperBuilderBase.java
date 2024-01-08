/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.desktop.view.associatedcomponents.value.impl;

import com.frojasg1.general.desktop.view.associatedcomponents.ComponentEvtWrapperBuilderBase;
import com.frojasg1.general.desktop.view.associatedcomponents.value.ComponentValueWrapperSimpleDefault;
import java.awt.Component;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public abstract class ComponentValueWrapperBuilderBase<VV>
	extends ComponentEvtWrapperBuilderBase
{
	protected Map<Class<? extends Component>,
					BiFunction<Component, Component,
								ComponentValueWrapperSimpleDefault>> _mapOfBuilders;

	public ComponentValueWrapperBuilderBase init()
	{
		_mapOfBuilders = new HashMap<>();

		fillMap( _mapOfBuilders );

		return( this );
	}

	protected abstract void fillMap( Map<Class<? extends Component>,
				BiFunction<Component, Component,
						ComponentValueWrapperSimpleDefault>> map );

	protected <C extends Component> void put(
		Map map,
		Class<C> componentClass,
		BiFunction<C, Component, ComponentValueWrapperSimpleDefault<C, VV>> wrapperBuilder )
	{
		map.put(componentClass,	wrapperBuilder);
	}

	public ComponentValueWrapperSimpleDefault<? extends Component, VV> apply(Component component,
																				Component parent)
	{
		ComponentValueWrapperSimpleDefault<? extends Component, VV> result = null;

		Class<? extends Component> compClass = getCompClass( component, _mapOfBuilders.keySet() );

		if( compClass != null )
			result = _mapOfBuilders.get(compClass).apply(component, parent);

		if( result != null )
			result.init();

		return( result );
	}

	public Map<Class<? extends Component>,
					BiFunction<Component, Component, ComponentValueWrapperSimpleDefault>> getMapOfBuilders() {
		return _mapOfBuilders;
	}
}
