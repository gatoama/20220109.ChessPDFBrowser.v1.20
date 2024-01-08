/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.desktop.view.componentwrapper.evt;

import com.frojasg1.general.map.multimap.MultiMap;
import com.frojasg1.general.map.multimap.MultiMapMapEntry;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class MultiMapForBuildingJComponentWrapperEvents
	extends MultiMap<ComponentWrapperEventBuildingContext>
{
	protected static final String EVENT_TYPE = "EVENT_TYPE";
	protected static final String EVENT_CLASS = "EVENT_CLASS";

	public MultiMapForBuildingJComponentWrapperEvents() {
		this( true );
	}

	public MultiMapForBuildingJComponentWrapperEvents(boolean hasToInit) {
		if( hasToInit )
			init();
	}

	@Override
	protected Map<String, MultiMapMapEntry<?, ComponentWrapperEventBuildingContext>> fillMultiMapMapEntryMap()
	{
		Map<String, MultiMapMapEntry<?, ComponentWrapperEventBuildingContext>> result = new HashMap<>();

		addMultiMapMapEntry( result, EVENT_TYPE, Long.class,
							ComponentWrapperEventBuildingContext::getEventType );
		addMultiMapMapEntry( result, EVENT_CLASS, Class.class,
							ComponentWrapperEventBuildingContext::getEventClass );

		return( result );
	}

	protected ComponentWrapperEventBuildingContext get( Long eventType )
	{
		return( get(EVENT_TYPE, eventType) );
	}

	protected <EVT> ComponentWrapperEventBuildingContext<EVT> get( Class<EVT> eventClass )
	{
		return( get(EVENT_CLASS, eventClass) );
	}

	public <EVT> Supplier<EVT> getSupplier( Class<EVT> eventClass )
	{
		Supplier<EVT> result = null;
		ComponentWrapperEventBuildingContext<EVT> ctx = get(eventClass);
		if( ctx != null )
			result = ctx.getConstructor();

		return( result );
	}

	public Supplier getSupplier( Long eventType )
	{
		Supplier result = null;
		ComponentWrapperEventBuildingContext ctx = get(eventType);
		if( ctx != null )
			result = ctx.getConstructor();

		return( result );
	}

	protected <EVT> ComponentWrapperEventBuildingContext<EVT> createContext(
		Long eventType, Class<EVT> eventClass, Supplier<EVT> constructor )
	{
		ComponentWrapperEventBuildingContext<EVT> result = new ComponentWrapperEventBuildingContext<>();
		result.setEventType(eventType);
		result.setEventClass(eventClass);
		result.setConstructor(constructor);

		return( result );
	}

	public <EVT> void put( Long eventType, Class<EVT> eventClass,
							Supplier<EVT> constructor )
	{
		put( createContext( eventType, eventClass, constructor ) );
	}
}
