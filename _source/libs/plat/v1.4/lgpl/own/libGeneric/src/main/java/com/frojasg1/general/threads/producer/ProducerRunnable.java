
package com.frojasg1.general.threads.producer;

import com.frojasg1.general.executor.worker.PullOfExecutorWorkers;
import com.frojasg1.general.listeners.consumer.ListOfConsumerListenersBase;
import com.frojasg1.general.threads.runnablewithlock.RunnableWithLockAndSimpleConditionBase;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

/**
 *
 * @author fjavier.rojas
 */
public abstract class ProducerRunnable<PP> extends RunnableWithLockAndSimpleConditionBase
{
	protected ListOfConsumerListenersBase<PP> _consumersOfProduct;

	protected ProducerSupervisorRunnable _supervisor;
	protected PullOfExecutorWorkers _pull;

	public ProducerRunnable( int numExtraConditions )
	{
		this( (PullOfExecutorWorkers) null, numExtraConditions);
	}

	public ProducerRunnable( PullOfExecutorWorkers pull, int numExtraConditions )
	{
		this(pull, numExtraConditions, true);
	}

	protected ProducerRunnable( PullOfExecutorWorkers pull, int numExtraConditions, boolean init )
	{
		super(numExtraConditions);

		_pull = pull;

		if( init )
			init();
	}

	public ProducerRunnable( ProducerRunnable that, boolean init )
	{
		super(that);

		if( init )
			init(that);
	}

//	@Override
	protected void init()
	{
//		super.init();

		_consumersOfProduct = createConsumerListeners();
		if( _pull != null )
			_supervisor = createSupervisor( _pull );
	}

	protected void init(ProducerRunnable<PP> that)
	{
		_consumersOfProduct = copyConsumerListeners( that._consumersOfProduct );
		_pull = that._pull;
		_supervisor = that._supervisor;
	}

	protected ProducerSupervisorRunnable createSupervisor(PullOfExecutorWorkers pull)
	{
		return( null );
	}

	public ProducerSupervisorRunnable getSupervisor() {
		return _supervisor;
	}

	public AtomicReference getAtomicReference()
	{
		return( _supervisor.getProducerReference() );
	}

	protected ListOfConsumerListenersBase<PP> createConsumerListeners()
	{
		return( new ListOfConsumerListenersBase<>() );
	}

	protected ListOfConsumerListenersBase<PP> copyConsumerListeners(ListOfConsumerListenersBase<PP> that)
	{
		return( new ListOfConsumerListenersBase<>(that) );
	}

	public void notifyEvt( PP product )
	{
		_consumersOfProduct.notifyEvt(product);
	}

	public void addListener( Consumer<PP> listener )
	{
		_consumersOfProduct.add( listener );
	}

	public void removeListener( Consumer<PP> listener )
	{
		_consumersOfProduct.remove( listener );
	}

	@Override
	protected boolean doInit()
	{
		return( true );
	}

	@Override
	protected void doRoutine()
	{
		PP product = createProduct();
		
		notifySupervisor( product );
		
		notifyEvt( product );
	}

	protected void notifySupervisor( PP product )
	{
		ProducerSupervisorRunnable svisor = getSupervisor();
		if( svisor != null )
			svisor.newProductProduced(product);
	}

	protected abstract PP createProduct();

	@Override
	public void doEnd() {
		releaseResources();
	}

	@Override
	protected void hasToStopAssociatedTasks() {

	}

	@Override
	protected void doBeforePause() {

	}

	@Override
	protected void doAfterPause() {

	}

	protected ListOfConsumerListenersBase<PP> getConsumersOfProduct() {
		return _consumersOfProduct;
	}

	protected void setConsumersOfProduct(ListOfConsumerListenersBase<PP> _consumersOfProduct) {
		this._consumersOfProduct = _consumersOfProduct;
	}

	protected void releaseResources()
	{
		getConsumersOfProduct().clear();
		setConsumersOfProduct(null);
	}
}
