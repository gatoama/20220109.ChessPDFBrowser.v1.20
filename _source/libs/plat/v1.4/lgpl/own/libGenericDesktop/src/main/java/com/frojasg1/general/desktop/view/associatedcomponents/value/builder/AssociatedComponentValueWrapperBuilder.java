
package com.frojasg1.general.desktop.view.associatedcomponents.value.builder;

import com.frojasg1.general.desktop.view.associatedcomponents.value.impl.bool.AssociatedComponentBooleanValueUpdaterDefault;
import com.frojasg1.general.desktop.view.associatedcomponents.value.impl.integer.AssociatedComponentIntValueUpdaterDefault;
import java.awt.Component;

/**
 *
 * @author fjavier.rojas
 */
public class AssociatedComponentValueWrapperBuilder
{
	protected static AssociatedComponentValueWrapperBuilder INSTANCE = new AssociatedComponentValueWrapperBuilder();

	public static AssociatedComponentValueWrapperBuilder instance()
	{
		return( INSTANCE );
	}

	public AssociatedComponentIntValueUpdaterDefault createAssociatedIntComponentWrapper( Integer initialValue,
																						Integer minValue,
																						Integer maxValue,
																						Component parent,
																						Component ... components )
	{
		AssociatedComponentIntValueUpdaterDefault result = new AssociatedComponentIntValueUpdaterDefault(initialValue, minValue, maxValue);
		result.init();

		for( Component comp: components )
			result.addComponentToWrap(comp, parent);

		return( result );
	}

	public AssociatedComponentBooleanValueUpdaterDefault createAssociatedIntComponentWrapper( Boolean initialValue,
																						Component parent,
																						Component ... components )
	{
		AssociatedComponentBooleanValueUpdaterDefault result = new AssociatedComponentBooleanValueUpdaterDefault(initialValue);
		result.init();

		for( Component comp: components )
			result.addComponentToWrap(comp, parent);

		return( result );
	}
}
