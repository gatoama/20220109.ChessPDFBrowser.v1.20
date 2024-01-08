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
package com.frojasg1.general.desktop.view.zoom.componentcopier.factory;

import com.frojasg1.general.desktop.view.zoom.componentcopier.CompCopier;
import com.frojasg1.general.desktop.view.zoom.componentcopier.imp.AbstractButtonCopier;
import com.frojasg1.general.desktop.view.zoom.componentcopier.imp.BasicArrowButtonCopier;
import com.frojasg1.general.desktop.view.zoom.componentcopier.imp.ComponentCopier;
import com.frojasg1.general.desktop.view.zoom.componentcopier.imp.ContainerCopier;
import com.frojasg1.general.desktop.view.zoom.componentcopier.imp.JButtonCopier;
import com.frojasg1.general.desktop.view.zoom.componentcopier.imp.JCheckBoxCopier;
import com.frojasg1.general.desktop.view.zoom.componentcopier.imp.JCheckBoxMenuItemCopier;
import com.frojasg1.general.desktop.view.zoom.componentcopier.imp.JComboBoxCopier;
import com.frojasg1.general.desktop.view.zoom.componentcopier.imp.JComponentCopier;
import com.frojasg1.general.desktop.view.zoom.componentcopier.imp.JEditorPaneCopier;
import com.frojasg1.general.desktop.view.zoom.componentcopier.imp.JLabelCopier;
import com.frojasg1.general.desktop.view.zoom.componentcopier.imp.JMenuCopier;
import com.frojasg1.general.desktop.view.zoom.componentcopier.imp.JMenuItemCopier;
import com.frojasg1.general.desktop.view.zoom.componentcopier.imp.JPasswordFieldCopier;
import com.frojasg1.general.desktop.view.zoom.componentcopier.imp.JRadioButtonCopier;
import com.frojasg1.general.desktop.view.zoom.componentcopier.imp.JRadioButtonMenuItemCopier;
import com.frojasg1.general.desktop.view.zoom.componentcopier.imp.JScrollPaneCopier;
import com.frojasg1.general.desktop.view.zoom.componentcopier.imp.JSliderCopier;
import com.frojasg1.general.desktop.view.zoom.componentcopier.imp.JSpinnerCopier;
import com.frojasg1.general.desktop.view.zoom.componentcopier.imp.JTextComponentCopier;
import com.frojasg1.general.desktop.view.zoom.componentcopier.imp.JTextFieldCopier;
import com.frojasg1.general.desktop.view.zoom.componentcopier.imp.JTextPaneCopier;
import com.frojasg1.general.desktop.view.zoom.componentcopier.imp.JToggleButtonCopier;
import com.frojasg1.general.desktop.view.zoom.componentcopier.imp.MetalScrollButtonCopier;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class ComponentCopierFactory
{
	public CompCopier createComponentCopier()
	{
		return( new ComponentCopier() );
	}

	public CompCopier createContainerCopier()
	{
		return( new ContainerCopier() );
	}

	public CompCopier createJComponentCopier()
	{
		return( new JComponentCopier() );
	}

	public CompCopier createJTextComponentCopier()
	{
		return( new JTextComponentCopier() );
	}

	public CompCopier createJEditorPaneCopier()
	{
		return( new JEditorPaneCopier() );
	}
	
	public CompCopier createJTextPaneCopier()
	{
		return( new JTextPaneCopier() );
	}

	public CompCopier createAbstractButtonCopier()
	{
		return( new AbstractButtonCopier() );
	}

	public CompCopier createJToggleButtonCopier()
	{
		return( new JToggleButtonCopier() );
	}

	public CompCopier createJCheckBoxCopier()
	{
		return( new JCheckBoxCopier() );
	}

	public CompCopier createJRadioButtonCopier()
	{
		return( new JRadioButtonCopier() );
	}

	public CompCopier createJScrollPaneCopier()
	{
		return( new JScrollPaneCopier() );
	}

	public CompCopier createJSliderCopier()
	{
		return( new JSliderCopier() );
	}

	public CompCopier createJButtonCopier()
	{
		return( new JButtonCopier() );
	}

	public CompCopier createBasicArrowButtonCopier()
	{
		return( new BasicArrowButtonCopier() );
	}

	public CompCopier createMetalScrollButtonCopier()
	{
		return( new MetalScrollButtonCopier() );
	}

	public CompCopier createJTextFieldCopier()
	{
		return( new JTextFieldCopier() );
	}

	public CompCopier createJPasswordFieldCopier()
	{
		return( new JPasswordFieldCopier() );
	}

	public CompCopier createJLabelCopier()
	{
		return( new JLabelCopier() );
	}

	public CompCopier createJMenuItemCopier()
	{
		return( new JMenuItemCopier() );
	}

	public CompCopier createJCheckBoxMenuItemCopier()
	{
		return( new JCheckBoxMenuItemCopier() );
	}

	public CompCopier createJRadioButtonMenuItemCopier()
	{
		return( new JRadioButtonMenuItemCopier() );
	}

	public CompCopier createJSpinnerCopier()
	{
		return( new JSpinnerCopier() );
	}

	public CompCopier createJMenuCopier()
	{
		return( new JMenuCopier() );
	}

	public CompCopier createJComboBoxCopier()
	{
		return( new JComboBoxCopier() );
	}
}
