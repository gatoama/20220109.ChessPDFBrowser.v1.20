/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.desktop.view.associatedcomponents.value.impl;

import com.frojasg1.general.desktop.view.associatedcomponents.value.AssociatedBoundedComponentValueUpdaterBase;
import com.frojasg1.general.desktop.view.associatedcomponents.value.BoundedComponentValueWrapper;
import com.frojasg1.general.desktop.view.associatedcomponents.value.ValueChangedEvent;
import com.frojasg1.general.desktop.view.associatedcomponents.value.ValueChangedEventBase;
import java.awt.Component;
import java.util.function.BiFunction;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class AssociatedBoundedComponentValueUpdaterDefault<VV,
				WW extends BoundedComponentValueWrapper<VV, ValueChangedEvent<VV>>>
	extends AssociatedBoundedComponentValueUpdaterBase<VV, ValueChangedEvent<VV>, WW>
{
	public AssociatedBoundedComponentValueUpdaterDefault(
		BiFunction<Component, Component, WW> wrapperBuilder,
		VV initialValue, VV min, VV max) {
		super(wrapperBuilder, initialValue, min, max);
	}

	@Override
	protected ValueChangedEvent<VV> createEmptyEvent() {
		return( new ValueChangedEventBase<>() );
	}
}
