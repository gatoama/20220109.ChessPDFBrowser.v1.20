/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.collection;

import java.util.Collection;
import java.util.List;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 * @param <RR>	- Record
 * @param <CC>	- Container
 */
public interface ThreadSafeListWrapper<RR, CC extends List<RR>> {
	
	public CC getList();
	public CC getListCopy();
	public void setList( CC list );

	public void clear();

	public void add( RR elem );
	public void add( int index, RR elem );

	public void addAll( Collection<? extends RR> list );
	public void addAll( int index, Collection<? extends RR> list);
	public <R extends RR> void addAll( ThreadSafeListWrapper<R, ? extends List<? extends R>> list );
	public <R extends RR> void addAll( int index,
									ThreadSafeListWrapper<R, ? extends List<? extends R>> list );

	public RR get(int index);

	public CC remove( int index, int numElems );
	public RR remove( int index );
	public boolean remove( RR elem );

	public int size();

	public boolean contains( RR record );
}
