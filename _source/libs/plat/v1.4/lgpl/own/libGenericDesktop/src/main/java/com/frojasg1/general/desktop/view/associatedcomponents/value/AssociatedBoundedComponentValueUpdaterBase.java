/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.desktop.view.associatedcomponents.value;

import java.awt.Component;
import java.util.function.BiFunction;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 * @param <VV>		Value Class
 * @param <SIFZ>	Single wrapper interface
 * @param <EV>		Event Class
 */
public abstract class AssociatedBoundedComponentValueUpdaterBase<VV,
														EV extends ValueChangedEvent<VV>,
														WW extends BoundedComponentValueWrapper<VV, EV>>
	extends AssociatedComponentValueUpdaterBase<VV, EV, WW>
	implements BoundedComponentValueWrapper<VV, EV>
{
	protected VV _min;
	protected VV _max;

	public AssociatedBoundedComponentValueUpdaterBase( BiFunction<Component, Component, WW> wrapperBuilder,
												VV initialValue,
												VV min, VV max)
	{
		super( wrapperBuilder, initialValue );

		_min = min;
		_max = max;
	}

	@Override
	protected void initChild()
	{
		super.initChild();
	}

	@Override
	protected WW createWrapper( Component component, Component parent )
	{
		WW result = super.createWrapper(component, parent);
		
		return( result );
	}

	@Override
	public synchronized void addComponentEvtWrapper(WW wrapper)
	{
		getComponentValueWrapperList().add( wrapper );

		wrapper.setValue( getValue() );
		wrapper.setBounds( getMin(), getMax());
		wrapper.setValue( getValue() );

		addListener( wrapper );
	}

	@Override
	public VV getMin()
	{
		return( _min );
	}

	@Override
	public VV getMax()
	{
		return( _max );
	}

	@Override
	public void setBounds( VV min, VV max )
	{
		_min = min;
		_max = max;

		browseWrappers( wrapper -> wrapper.setBounds(min, max));
	}
}
