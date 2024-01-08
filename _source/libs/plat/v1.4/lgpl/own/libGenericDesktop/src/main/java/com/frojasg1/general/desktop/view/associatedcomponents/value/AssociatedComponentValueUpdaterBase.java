/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.desktop.view.associatedcomponents.value;

import com.frojasg1.general.desktop.view.associatedcomponents.AssociatedComponentEvtBase;
import java.awt.Component;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Consumer;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 * @param <VV>		Value Class
 * @param <SIFZ>	Single wrapper interface
 * @param <EV>		Event Class
 */
public abstract class AssociatedComponentValueUpdaterBase<VV,
														EV extends ValueChangedEvent<VV>,
														WW extends ComponentValueWrapper<VV, EV>>
	extends AssociatedComponentEvtBase<EV, WW>
	implements AssociatedComponentValueUpdater<VV, EV, WW>
{
	protected VV _value;

	protected boolean _modifiedByProgram = false;

	public AssociatedComponentValueUpdaterBase( BiFunction<Component, Component, WW> wrapperBuilder,
												VV initialValue )
	{
		super( wrapperBuilder );

		_value = initialValue;
	}

	@Override
	protected void initChild()
	{
		super.initChild();
	}

	@Override
	protected abstract EV createEmptyEvent();

	@Override
	protected EV createEvent( EV parentEvt )
	{
		EV result = super.createEvent( parentEvt );
		
		if( result != null )
		{
//			result.setSource( createSource( parentEvt.getSource() ) );
			result.setPreviousValue(_value);
			result.setNewValue( getValue() );
		}

		return( result );
	}

	@Override
	protected WW createWrapper( Component component, Component parent )
	{
		return( super.createWrapper(component, parent) );
	}

	@Override
	public synchronized void addComponentEvtWrapper(WW wrapper)
	{
		getComponentValueWrapperList().add( wrapper );

		wrapper.setValue( _value );
		addListener( wrapper );
	}

	@Override
	public VV getValue()
	{
		return( _value );
	}

	@Override
	public synchronized void setValue(VV value)
	{
		_value = value;

		browseWrappers( wrapper -> {
			if( !Objects.equals( wrapper.getValue(), value ) )
				wrapper.setValue( value );
		});
	}

	public synchronized void browseWrappers( Consumer<WW> wrapperConsumer )
	{
		try
		{
			_modifiedByProgram = true;

			for( WW wrapper: _componentValueWrapperList )
				wrapperConsumer.accept( wrapper );
		}
		finally
		{
			_modifiedByProgram = false;
		}
	}

	@Override
	protected void internalValueChangedListener( EV evt )
	{
		EV newEvt = createEvent( evt );

		setValue( evt.getNewValue() );

		getListenersList().notifyEvt( newEvt );
	}
}
