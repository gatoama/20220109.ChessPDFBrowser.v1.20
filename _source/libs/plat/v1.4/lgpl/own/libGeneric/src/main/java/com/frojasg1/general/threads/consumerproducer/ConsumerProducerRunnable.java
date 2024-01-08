
package com.frojasg1.general.threads.consumerproducer;

import com.frojasg1.general.executor.worker.PullOfExecutorWorkers;
import com.frojasg1.general.threads.producer.ProducerRunnable;
import com.frojasg1.general.threads.producer.ProducerSupervisorRunnable;
import java.util.LinkedList;
import java.util.function.Function;

/**
 *
 * @author fjavier.rojas
 */
public class ConsumerProducerRunnable<CC, PP> extends ProducerRunnable<PP>
{
	protected static final int NUM_ADDITIONAL_CONDITIONS = 1;

	protected static final int WAITING_FOR_ITEM_TO_BE_CONSUMED_CONDITION_INDEX = 1;

	protected Function<CC, PP> _translationFunction;

	protected LinkedList<CC> _itemsToBeConsumed;


	public ConsumerProducerRunnable(Function<CC, PP> translationFunction)
	{
		this(translationFunction, null, 0);
	}

	public ConsumerProducerRunnable(Function<CC, PP> translationFunction,
									PullOfExecutorWorkers pull,
									int numAdditionalConditions )
	{
		this(translationFunction, pull, numAdditionalConditions + NUM_ADDITIONAL_CONDITIONS, true);
	}

	protected ConsumerProducerRunnable( Function<CC, PP> translationFunction,
										PullOfExecutorWorkers pull,
										int numAdditionalConditions,
										boolean init )
	{
		super(pull, numAdditionalConditions, false);

		_translationFunction = translationFunction;
		if( init )
			init();
	}

	@Override
	protected void init()
	{
		super.init();

		_itemsToBeConsumed = createLinkedList();
	}

	@Override
	protected void init(ProducerRunnable<PP> that)
	{
		super.init(that);

		ConsumerProducerRunnable<CC, PP> thatCpr = (ConsumerProducerRunnable<CC, PP>) that;
		_itemsToBeConsumed = copyLinkedList(thatCpr._itemsToBeConsumed);
	}

	protected LinkedList<CC> createLinkedList()
	{
		return( new LinkedList<>() );
	}

	protected LinkedList<CC> copyLinkedList( LinkedList<CC> that )
	{
		return( new LinkedList<>(that) );
	}

	public void addItemToBeConsumed( CC item )
	{
		lockProcedure( () -> addItemToBeConsumedInternal( item ) );
	}

	protected void addItemToBeConsumedInternal( CC item )
	{
		_itemsToBeConsumed.add(item);
		ifHasWaitersSignal(WAITING_FOR_ITEM_TO_BE_CONSUMED_CONDITION_INDEX);
	}

	protected LinkedList<CC> getItemsToBeConsumed() {
		return _itemsToBeConsumed;
	}

	@Override
	protected boolean doInit()
	{
		return( true );
	}

	@Override
	protected void doRoutine()
	{
		super.doRoutine();
	}

	@Override
	protected PP createProduct()
	{
		PP result = null;
		CC item = getItemToBeConsumed();
		if( ! getHasToStop() && !isInterrupted() && ( item != null ) )
			result = translate( item );

		return( result );
	}


	public Function<CC, PP> getTranslationFunction() {
		return _translationFunction;
	}

	protected PP translate( CC item )
	{
		return( getTranslationFunction().apply(item) );
	}

	protected CC getItemToBeConsumed()
	{
		return( lockFunction( this::getItemToBeConsumedInternal ) );
	}

	protected CC getItemToBeConsumedInternal()
	{
		while( !isInterrupted() && getItemsToBeConsumed().isEmpty() )
			awaitInterrupted( WAITING_FOR_ITEM_TO_BE_CONSUMED_CONDITION_INDEX, 500 );

		CC result = null;
		if( ! getItemsToBeConsumed().isEmpty() )
			result = getItemsToBeConsumed().removeFirst();

		return( result );
	}
}
