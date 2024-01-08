
package com.frojasg1.general.threads.producer;

import com.frojasg1.general.executor.ExecutorPullInterface;
import com.frojasg1.general.executor.worker.PullOfExecutorWorkers;
import com.frojasg1.general.threads.RunnableBase;
import com.frojasg1.general.threads.ThreadFunctions;
import java.lang.ref.SoftReference;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.UnaryOperator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author fjavier.rojas
 */
public class ProducerSupervisorRunnable<PR extends ProducerRunnable> extends RunnableBase
{
	private static final Logger LOGGER = LoggerFactory.getLogger(ProducerSupervisorRunnable.class);

	protected static final int DEFAULT_DELAY_MS = 1000;
	protected static final int DEFAULT_MAX_TIMES = 2;

	protected int _delayMs;
	protected int _maxTimes;
	protected long _lastTimestamp;
	protected SoftReference<ExecutorPullInterface> _pull;
	protected int _times;

	protected UnaryOperator<PR> _producerBuilder;

	protected AtomicReference<PR> _producerReference;

	public ProducerSupervisorRunnable( PR supervisedProducer, ExecutorPullInterface pull, UnaryOperator<PR> producerBuilder )
	{
		this(supervisedProducer, pull, producerBuilder, DEFAULT_DELAY_MS, DEFAULT_MAX_TIMES );
	}

	public ProducerSupervisorRunnable( PR supervisedProducer, ExecutorPullInterface pull, UnaryOperator<PR> producerBuilder,
										int delayMs, int maxTimes )
	{
		_producerReference = new AtomicReference<>(supervisedProducer);
		_pull = new SoftReference<>(pull);
		_delayMs = delayMs;
		_maxTimes = maxTimes;
		_producerBuilder = producerBuilder;
		
		pull.addPendingExecutor(this);
	}

	protected boolean isExpected()
	{
		return( _lastTimestamp >= ( System.currentTimeMillis() - _delayMs ) );
	}

	public void newProductProduced( Object product )
	{
		_lastTimestamp = System.currentTimeMillis();
	}

	protected boolean check()
	{
		boolean result = true;
		if( isExpected() )
			resetTimes();
		else
			result = ( _times++ < _maxTimes );

		return( result );
	}

	@Override
	protected boolean doInit()
	{
		return( true );
	}

	protected PR getProducer()
	{
		return( _producerReference.get() );
	}

	protected void setProducer( PR producer )
	{
		_producerReference.set(producer);
	}

	@Override
	protected void doRoutine()
	{
		if( !check() )
		{
			PR producer = getProducer();
			PR newProducer = (PR) _pull.get().replace( getProducer(), this::createNewProducer );
			if( producer != newProducer )
				setProducer( newProducer );
			else
				LOGGER.info( "{} check not valid, but could not change runnable", getClass().getSimpleName() );

			resetTimes();
		}

		sleep();
	}

	public ExecutorPullInterface getPull() {
		return _pull.get();
	}

	protected void resetTimes()
	{
		_times = 0;
	}

	protected PR createNewProducer()
	{
		return( _producerBuilder.apply( getProducer() ) );
	}

	protected void sleep()
	{
		ThreadFunctions.instance().sleep(_delayMs );
	}

	@Override
	public void doEnd()
	{

	}

	@Override
	protected void hasToStopAssociatedTasks()
	{

	}

	@Override
	protected void doBeforePause()
	{

	}

	@Override
	protected void doAfterPause()
	{

	}

	public AtomicReference<PR> getProducerReference()
	{
		return( _producerReference );
	}
}
