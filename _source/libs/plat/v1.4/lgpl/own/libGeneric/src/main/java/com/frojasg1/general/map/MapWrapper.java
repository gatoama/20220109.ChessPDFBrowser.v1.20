/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.map;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public abstract class MapWrapper<KK, VV>
{
	protected Map<KK, VV> _map;

	protected void init()
	{
		_map = createMap();
	}

	protected Map<KK, VV> createMap()
	{
		return( new ConcurrentHashMap<>() );
	}

	public VV get( KK key )
	{
		return( _map.computeIfAbsent(key, this::create ) );
	}

	protected abstract VV create( KK key );
}
