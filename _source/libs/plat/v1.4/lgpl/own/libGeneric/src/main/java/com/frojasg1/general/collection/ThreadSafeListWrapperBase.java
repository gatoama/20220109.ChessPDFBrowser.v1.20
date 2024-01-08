/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.collection;

import com.frojasg1.general.CollectionFunctions;
import com.frojasg1.general.threads.SynchronizedFunctions;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 * @param <RR>	- Record
 * @param <CC>	- Container
 */
public class ThreadSafeListWrapperBase<RR, CC extends List<RR>>
	extends SynchronizedFunctions
	implements ModifiedStatus, ThreadSafeListWrapper<RR, CC>
{
	protected boolean _hasBeenModified = false;
	protected Supplier<CC> _emptyListBuilder;
	protected CC _list;

	public ThreadSafeListWrapperBase( Supplier<CC> emptyListBuilder )
	{
		_emptyListBuilder = emptyListBuilder;
	}

	protected void init()
	{
		_list = createEmptyList();
	}

	protected CC createEmptyList()
	{
		return( _emptyListBuilder.get() );
	}

	@Override
	public synchronized void setHasBeenModified( boolean value )
	{
		_hasBeenModified = value;
	}

	@Override
	public synchronized boolean hasBeenModified()
	{
		return( _hasBeenModified );
	}

	public Supplier<CC> getEmptyListBuilder() {
		return _emptyListBuilder;
	}

	@Override
	public CC getList()
	{
		return( _list );
	}

	@Override
	public synchronized CC getListCopy()
	{
		return( getList( ThreadSafeListWrapperBase.this.getList() ) );
	}

	protected CC getList(Collection<? extends RR> collection)
	{
		return( getColFun().copyToList( collection, getEmptyListBuilder() ) );
	}

	@Override
	public void add(RR elem) {
		threadSafeMethod(ThreadSafeListWrapperBase.this.getList(), lst -> lst.add(elem) );
	}

	@Override
	public void addAll(Collection<? extends RR> collection) {
		CC listToCopy = getList(collection);
		addAllInternal( listToCopy );
	}

	@Override
	public <R extends RR> void addAll( ThreadSafeListWrapper<R, ? extends List<? extends R>> listWrapper )
	{
		List<? extends R> listToCopy = getListCopy(listWrapper);
		addAllInternal( listToCopy );
	}

	public void addAllInternal(Collection<? extends RR> collection) {
		threadSafeMethodSetModified( ths -> ths.getList().addAll(collection) );
	}

	@Override
	public synchronized boolean remove(RR elem)
	{
		boolean result = threadSafeFunction(ths -> ths.getList().remove(elem) );
		if( result )
			this.setHasBeenModified(true);

		return( result );
	}

	@Override
	public int size()
	{
		return( threadSafeFunction(ths -> ths.getList().size() ) );
	}

	protected CollectionFunctions getColFun()
	{
		return( CollectionFunctions.instance() );
	}

	@Override
	public void add(int index, RR elem)
	{
		threadSafeMethodSetModified(ths -> ths.getList().add(index, elem) );
	}

	@Override
	public void addAll(int index, Collection<? extends RR> collection)
	{
		CC listToCopy = getList(collection);
		addAllInternal(index, listToCopy);
	}

	protected void addAllInternal(int index, Collection<? extends RR> list)
	{
		if( ( list != null ) && !list.isEmpty() )
			threadSafeMethodSetModified(ths -> ths.getList().addAll(index, list) );
	}

	@Override
	public <R extends RR> void addAll( int index,
									ThreadSafeListWrapper<R, ? extends List<? extends R>> listWrapper )
	{
		List<? extends R> listToCopy = getListCopy(listWrapper);
		addAllInternal(index, listToCopy);
	}

	protected <R extends RR> List<? extends R> getListCopy(ThreadSafeListWrapper<R, ? extends List<? extends R>> listWrapper)
	{
		List<? extends R> result = null;
		if( listWrapper != null )
			result = listWrapper.getListCopy();

		return( result );
	}

	@Override
	public RR get(int index)
	{
		return( threadSafeFunction(ths -> ths.getList().get(index) ) );
	}

	@Override
	public RR remove(int index)
	{
		return( threadSafeFunction(ths -> ths.getList().remove(index) ) );
	}

	@Override
	public CC remove(int index, int numElems)
	{
		return( threadSafeFunctionSetModified(ths -> removeInternal(index, numElems) ) );
	}

	// comes from synchronized
	protected CC removeInternal(int index, int numElems)
	{
		CC resultList = createEmptyList();

		for( int ii=0; ii<numElems; ii++ )
			resultList.add( remove(index) );
		
		return( resultList );
	}

	public <RRR> RRR threadSafeFunction( Function<ThreadSafeListWrapperBase<RR, CC>, RRR> function )
	{
		return( threadSafeFunction( this, function ) );
	}

	public void threadSafeMethod( Consumer<ThreadSafeListWrapperBase<RR, CC>> method )
	{
		threadSafeMethod( this, method );
	}

	public synchronized <RRR> RRR threadSafeFunctionSetModified( Function<ThreadSafeListWrapperBase<RR, CC>, RRR> function )
	{
		RRR result = threadSafeFunction( function );
		setHasBeenModified( true );

		return( result );
	}

	public synchronized void threadSafeMethodSetModified( Consumer<ThreadSafeListWrapperBase<RR, CC>> method )
	{
		threadSafeMethod( method );
		setHasBeenModified( true );
	}

	@Override
	public void clear() {
		threadSafeMethodSetModified( ths -> ths.getList().clear() );
	}

	public synchronized boolean contains( RR record )
	{
		return( getList().contains(record) );
	}

	protected boolean equals( CC list )
	{
		boolean result = false;
		if( ( _list != null ) && ( list != null ) )
			result = Arrays.equals( _list.toArray(), list.toArray() );

		return( result );
	}

	@Override
	public synchronized void setList(CC list) {
		if( !equals(list) )
			threadSafeMethodSetModified(ths -> _list = list );
	}
}
