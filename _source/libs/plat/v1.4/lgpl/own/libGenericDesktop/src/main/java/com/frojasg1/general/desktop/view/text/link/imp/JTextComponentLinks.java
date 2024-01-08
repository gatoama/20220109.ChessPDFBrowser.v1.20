/* 
 * Copyright (C) 2021 Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You may obtain a copy of the License at
 *
 *      http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 */
package com.frojasg1.general.desktop.view.text.link.imp;

import com.frojasg1.general.containers.DoubleComparator;
import com.frojasg1.general.containers.OrderedArrayMap;
import com.frojasg1.general.desktop.view.text.link.JTextComponentMouseLinkListener;
import com.frojasg1.general.listeners.ListOfListenersImp;
import com.frojasg1.general.listeners.Notifier;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class JTextComponentLinks<KK extends Comparable<KK>, VV>
								extends JTextComponentMouseLinkListener
								implements LinkServer
{
	
	protected VV _lastLinkDetected = null;

	protected Notifier< LinkListener<VV> > _notifier = ( lis ) -> lis.linkDetected(this, _lastLinkDetected);

	protected ListOfLinkListenersImp _listeners = null;

	protected OrderedArrayMap< KK, VV, Integer > _linkMap = null;

	protected JTextComponentLinks<KK, VV> _this = this;

	protected Class<VV> _vClass = null;

	public JTextComponentLinks( DoubleComparator<Integer, KK> dc,
								Class<VV> vClass )
	{
		_vClass = vClass;
		_listeners = createListOfListeners();
		_linkMap = createLinkMap(dc);
		
	}

	protected ListOfLinkListenersImp createListOfListeners()
	{
		return( new ListOfLinkListenersImp() );
	}

	protected OrderedArrayMap< KK, VV, Integer > createLinkMap(DoubleComparator<Integer, KK> dc)
	{
		return( new OrderedArrayMap<>(dc, _vClass) );
	}

	@Override
	protected void doActions()
	{
		if( _lastLinkDetected != null )
		{
			_listeners.notifyListeners(_notifier);
		}
	}

	@Override
	protected boolean hasToShowHand(int pos)
	{

		_lastLinkDetected = getLink( pos );
		boolean result = ( _lastLinkDetected != null );

		return( result );
	}

	public VV getLink( int pos )
	{
		VV result = _linkMap.getFirstValueFromSimplifiedKey(pos);

		return( result );
	}

	public KK getKey( int pos )
	{
		KK result = _linkMap.getKeyFromSimplifiedKey(pos);

		return( result );
	}

	@Override
	public void addLinkListener(LinkListener listener)
	{
		_listeners.add( listener );
	}

	@Override
	public void removeLinkListener(LinkListener listener)
	{
		_listeners.remove(listener);
	}

	public void addLink( KK link, VV linkObject )
	{
		_linkMap.put( link,linkObject );
	}

	protected class ListOfLinkListenersImp extends ListOfListenersImp< LinkListener<VV> >
	{
	}

	public void clear()
	{
		_linkMap.clear();
	}
}
