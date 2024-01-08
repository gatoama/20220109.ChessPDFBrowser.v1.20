/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.desktop.view.associatedcomponents.value.impl.bool;

import com.frojasg1.general.desktop.view.associatedcomponents.value.ComponentValueWrapper;
import com.frojasg1.general.desktop.view.associatedcomponents.value.ValueChangedEvent;
import com.frojasg1.general.desktop.view.associatedcomponents.value.impl.AssociatedComponentValueUpdaterDefault;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class AssociatedComponentBooleanValueUpdaterDefault
	extends AssociatedComponentValueUpdaterDefault<Boolean,
													ComponentValueWrapper<Boolean, ValueChangedEvent<Boolean>>>
{
	public AssociatedComponentBooleanValueUpdaterDefault(Boolean initialValue) {
		super(ComponentBoolValueWrapperBuilder.instance()::apply,
			initialValue);
	}
}
