/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.desktop.view.associatedcomponents.value.impl;

import com.frojasg1.general.desktop.view.associatedcomponents.value.AssociatedComponentValueUpdaterBase;
import com.frojasg1.general.desktop.view.associatedcomponents.value.ComponentValueWrapper;
import com.frojasg1.general.desktop.view.associatedcomponents.value.ValueChangedEvent;
import com.frojasg1.general.desktop.view.associatedcomponents.value.ValueChangedEventBase;
import com.frojasg1.general.functional.interfaces.TriFunction;
import java.awt.Component;
import java.util.function.BiFunction;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class AssociatedComponentValueUpdaterDefault<VV,
				WW extends ComponentValueWrapper<VV, ValueChangedEvent<VV>>>
	extends AssociatedComponentValueUpdaterBase<VV, ValueChangedEvent<VV>, WW>
{
/*
	public AssociatedComponentValueUpdaterDefault(
		Class<VV> classForWrapperBuilder,
		VV initialValue) {
		this( ComponentValueWrapperBuilderBase.instance()::apply, classForWrapperBuilder, initialValue);
	}

	public AssociatedComponentValueUpdaterDefault(
		TriFunction<Class<?>, Component, Component, WW> wrapperBuilderByClass,
		Class<VV> classForWrapperBuilder,
		VV initialValue) {
		this( ( comp, parent ) ->
				wrapperBuilderByClass.apply( classForWrapperBuilder, comp, parent ),
			initialValue);
	}
*/

	public AssociatedComponentValueUpdaterDefault(
		BiFunction<Component, Component, WW> wrapperBuilder,
		VV initialValue) {
		super(wrapperBuilder, initialValue);
	}

	@Override
	protected ValueChangedEvent<VV> createEmptyEvent() {
		return( new ValueChangedEventBase<>() );
	}
}
