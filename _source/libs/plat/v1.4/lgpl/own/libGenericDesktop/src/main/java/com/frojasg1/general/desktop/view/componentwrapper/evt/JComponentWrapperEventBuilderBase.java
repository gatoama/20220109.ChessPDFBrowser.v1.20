/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.desktop.view.componentwrapper.evt;

import com.frojasg1.general.ExecutionFunctions;
import java.util.Map;
import java.util.function.Supplier;
import javax.swing.JComponent;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public abstract class JComponentWrapperEventBuilderBase<JC extends JComponent, EV extends JComponentWrapperEvent>
	implements JComponentWrapperEventBuilder<EV>
{
	protected JC _jComponent;

	protected MultiMapForBuildingJComponentWrapperEvents _multiMap;

	protected JComponentWrapperEventBuilderBase( JC jComponent )
	{
		_jComponent = jComponent;
	}

	protected void init()
	{
		_multiMap = createMultiMap();
	}

	protected MultiMapForBuildingJComponentWrapperEvents createMultiMap()
	{
		MultiMapForBuildingJComponentWrapperEvents result = createEmptyMultiMap();
		fillMultiMap(result);

		return( result );
	}

	protected MultiMapForBuildingJComponentWrapperEvents createEmptyMultiMap()
	{
		return( new MultiMapForBuildingJComponentWrapperEvents() );
	}

	protected abstract void fillMultiMap( MultiMapForBuildingJComponentWrapperEvents multiMap );

	@Override
	public EV buildEvent(long eventType)
	{
		return( (EV) buildEvent( _multiMap.getSupplier(eventType) ) );
	}

	@Override
	public <EVT extends EV> EVT buildEvent(Class<EVT> eventClass )
	{
		return( buildEvent( _multiMap.getSupplier(eventClass) ) );
	}

	public <EVT extends EV> EVT buildEvent(Supplier<EVT> constructor)
	{
		EVT result = null;
		if( constructor != null )
		{
			result = constructor.get();
			result.setjComponent(_jComponent);
		}

		return( result );
	}

	protected <CC> CC safeFunctionExecution(ExecutionFunctions.UnsafeFunction<CC> run)
	{
		return( ExecutionFunctions.instance().safeFunctionExecution(run) );
	}
}
