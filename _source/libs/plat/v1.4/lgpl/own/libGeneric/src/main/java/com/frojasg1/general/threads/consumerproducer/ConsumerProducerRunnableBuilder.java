
package com.frojasg1.general.threads.consumerproducer;

/**
 *
 * @author fjavier.rojas
 */
public abstract class ConsumerProducerRunnableBuilder<CC, PP>
{
	public ConsumerProducerRunnable<CC, PP> createConsumerProducer()
	{
		return( new ConsumerProducerRunnable<>(this::translate) );
	}

	protected abstract PP translate( CC item );
}
