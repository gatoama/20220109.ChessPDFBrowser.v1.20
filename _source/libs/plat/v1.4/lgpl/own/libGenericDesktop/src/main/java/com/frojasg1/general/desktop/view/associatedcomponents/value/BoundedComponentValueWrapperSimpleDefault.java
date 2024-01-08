/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.desktop.view.associatedcomponents.value;

import java.awt.Component;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public abstract class BoundedComponentValueWrapperSimpleDefault<CC extends Component, VV>
	extends ComponentValueWrapperSimpleDefault<CC, VV>
	implements BoundedComponentValueWrapper<VV, ValueChangedEvent<VV>>
{
	public BoundedComponentValueWrapperSimpleDefault( CC component, Component parent )
	{
		super( component, parent );
	}

	@Override
	public void init()
	{
		super.init();
	}

	@Override
	protected void initChild()
	{
		super.initChild();
	}
}
