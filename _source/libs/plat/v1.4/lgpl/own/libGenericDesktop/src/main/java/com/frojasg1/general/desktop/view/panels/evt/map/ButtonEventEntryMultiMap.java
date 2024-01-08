/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.desktop.view.panels.evt.map;

import com.frojasg1.general.desktop.view.zoom.mapper.ComponentMapper;
import com.frojasg1.general.desktop.view.zoom.mapper.InternallyMappedComponent;
import com.frojasg1.general.map.multimap.MultiMap;
import com.frojasg1.general.map.multimap.MultiMapMapEntry;
import java.util.HashMap;
import java.util.Map;
import javax.swing.AbstractButton;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class ButtonEventEntryMultiMap<ET>
	extends MultiMap<ButtonEventEntry<ET>>
	implements InternallyMappedComponent
{
	public static final String EVENT_TYPE = "EVENT_TYPE";
	public static final String IMAGE_RESOURCE = "IMAGE_RESOURCE";

	protected Class<ET> _eventTypeClass;

	public ButtonEventEntryMultiMap( Class<ET> eventTypeClass )
	{
		_eventTypeClass = eventTypeClass;
	}

	@Override
	public void init()
	{
		super.init();
	}

	@Override
	protected Map<String, MultiMapMapEntry<?, ButtonEventEntry<ET>>> fillMultiMapMapEntryMap() {
		Map<String, MultiMapMapEntry<?, ButtonEventEntry<ET>>> result = new HashMap<>();

		addMultiMapMapEntry( result, EVENT_TYPE, _eventTypeClass, ButtonEventEntry::getEventType );
		addMultiMapMapEntry( result, IMAGE_RESOURCE, String.class,
							ButtonEventEntry::getImageResource );

		return( result );
	}

	public ButtonEventEntry<ET> getEntryOfImageResource( String imageResource )
	{
		return( get( IMAGE_RESOURCE, imageResource ) );
	}

	public ButtonEventEntry<ET> getEntryOfEventType( ET eventType )
	{
		return( get( EVENT_TYPE, eventType ) );
	}

	public void put( String imageResource, ET eventType, AbstractButton button )
	{
		put( createEntry( imageResource, eventType, button ) );
	}

	protected ButtonEventEntry<ET> createEmptyEntry()
	{
		return( new ButtonEventEntry<>() );
	}

	protected ButtonEventEntry<ET> createEntry( String imageResource, ET eventType, AbstractButton button )
	{
		ButtonEventEntry<ET> result = createEmptyEntry();

		result.setImageResource(imageResource);
		result.setEventType(eventType);
		result.setButton(button);

		return( result );
	}

	@Override
	public void setComponentMapper(ComponentMapper mapper)
	{
		for( ButtonEventEntry<ET> entry: getMapByName(EVENT_TYPE, _eventTypeClass).values() )
			entry.setComponentMapper(mapper);
	}

	@Override
	public boolean hasBeenAlreadyMapped()
	{
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}
}
