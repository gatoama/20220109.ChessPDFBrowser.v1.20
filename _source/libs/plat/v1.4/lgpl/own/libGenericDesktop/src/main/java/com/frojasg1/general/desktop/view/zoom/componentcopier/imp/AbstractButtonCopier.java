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
package com.frojasg1.general.desktop.view.zoom.componentcopier.imp;

import com.frojasg1.general.desktop.view.zoom.componentcopier.CompCopier;
import com.frojasg1.general.desktop.view.zoom.componentcopier.CompCopierBase;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractButton;
import javax.swing.event.ChangeListener;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class AbstractButtonCopier extends CompCopierBase<AbstractButton>
{

	@Override
	protected List<CompCopier<AbstractButton>> createCopiers() {

		List<CompCopier<AbstractButton>> result = new ArrayList<>();

		result.add( createActionListenersListCopier() );
		result.add( createChangeListenersListCopier() );
		result.add( createItemListenersListCopier() );
		result.add( createDisabledIconCopier() );
		result.add( createDisabledSelectedIconCopier() );
		result.add( createHideActionTextCopier() );
		result.add( createHorizontalAlignmentCopier() );
		result.add( createHorizontalTextPositionCopier() );
		result.add( createIconCopier() );
		result.add( createIconTextGapCopier() );
		result.add( createMarginCopier() );
		result.add( createMnemonicCopier() );
		result.add( createModelCopier() );
		result.add( createMultiClickThreshholdCopier() );
		result.add( createPressedIconCopier() );
		result.add( createRolloverIconCopier() );
		result.add( createRolloverSelectedIconCopier() );
		result.add( createSelectedIconCopier() );
		result.add( createVerticalAlignmentCopier() );
		result.add( createVerticalTextPositionCopier() );
		result.add( createBorderPaintedCopier() );
		result.add( createContentAreaFilledCopier() );
		result.add( createFocusPaintedCopier() );
		result.add( createRolloverEnabledCopier() );
		result.add( createTextCopier() );

		return( result );
	}

	protected CompCopier<AbstractButton> createActionListenersListCopier()
	{
		return( (originalComponent, newComponent) -> copyActionListenersList( originalComponent, newComponent ) );
	}

	protected CompCopier<AbstractButton> createChangeListenersListCopier()
	{
		return( (originalComponent, newComponent) -> copyChangeListenersList( originalComponent, newComponent ) );
	}

	protected CompCopier<AbstractButton> createItemListenersListCopier()
	{
		return( (originalComponent, newComponent) -> copyItemListenersList( originalComponent, newComponent ) );
	}

	protected CompCopier<AbstractButton> createDisabledIconCopier()
	{
		return( (originalComponent, newComponent) -> copyDisabledIcon( originalComponent, newComponent ) );
	}

	protected CompCopier<AbstractButton> createDisabledSelectedIconCopier()
	{
		return( (originalComponent, newComponent) -> copyDisabledSelectedIcon( originalComponent, newComponent ) );
	}

	protected CompCopier<AbstractButton> createHideActionTextCopier()
	{
		return( (originalComponent, newComponent) -> copyHideActionText( originalComponent, newComponent ) );
	}

	protected CompCopier<AbstractButton> createHorizontalAlignmentCopier()
	{
		return( (originalComponent, newComponent) -> copyHorizontalAlignment( originalComponent, newComponent ) );
	}

	protected CompCopier<AbstractButton> createHorizontalTextPositionCopier()
	{
		return( (originalComponent, newComponent) -> copyHorizontalTextPosition( originalComponent, newComponent ) );
	}

	protected CompCopier<AbstractButton> createVerticalAlignmentCopier()
	{
		return( (originalComponent, newComponent) -> copyVerticalAlignment( originalComponent, newComponent ) );
	}

	protected CompCopier<AbstractButton> createVerticalTextPositionCopier()
	{
		return( (originalComponent, newComponent) -> copyVerticalTextPosition( originalComponent, newComponent ) );
	}

	protected CompCopier<AbstractButton> createIconCopier()
	{
		return( (originalComponent, newComponent) -> copyIcon( originalComponent, newComponent ) );
	}

	protected CompCopier<AbstractButton> createIconTextGapCopier()
	{
		return( (originalComponent, newComponent) -> copyIconTextGap( originalComponent, newComponent ) );
	}

	protected CompCopier<AbstractButton> createMarginCopier()
	{
		return( (originalComponent, newComponent) -> copyMargin( originalComponent, newComponent ) );
	}

	protected CompCopier<AbstractButton> createMnemonicCopier()
	{
		return( (originalComponent, newComponent) -> copyMnemonic( originalComponent, newComponent ) );
	}

	protected CompCopier<AbstractButton> createModelCopier()
	{
		return( (originalComponent, newComponent) -> copyModel( originalComponent, newComponent ) );
	}

	protected CompCopier<AbstractButton> createMultiClickThreshholdCopier()
	{
		return( (originalComponent, newComponent) -> copyMultiClickThreshhold( originalComponent, newComponent ) );
	}

	protected CompCopier<AbstractButton> createPressedIconCopier()
	{
		return( (originalComponent, newComponent) -> copyPressedIcon( originalComponent, newComponent ) );
	}

	protected CompCopier<AbstractButton> createRolloverIconCopier()
	{
		return( (originalComponent, newComponent) -> copyRolloverIcon( originalComponent, newComponent ) );
	}

	protected CompCopier<AbstractButton> createRolloverSelectedIconCopier()
	{
		return( (originalComponent, newComponent) -> copyRolloverSelectedIcon( originalComponent, newComponent ) );
	}

	protected CompCopier<AbstractButton> createSelectedIconCopier()
	{
		return( (originalComponent, newComponent) -> copySelectedIcon( originalComponent, newComponent ) );
	}

	protected CompCopier<AbstractButton> createBorderPaintedCopier()
	{
		return( (originalComponent, newComponent) -> copyBorderPainted( originalComponent, newComponent ) );
	}

	protected CompCopier<AbstractButton> createContentAreaFilledCopier()
	{
		return( (originalComponent, newComponent) -> copyContentAreaFilled( originalComponent, newComponent ) );
	}

	protected CompCopier<AbstractButton> createFocusPaintedCopier()
	{
		return( (originalComponent, newComponent) -> copyFocusPainted( originalComponent, newComponent ) );
	}

	protected CompCopier<AbstractButton> createRolloverEnabledCopier()
	{
		return( (originalComponent, newComponent) -> copyRolloverEnabled( originalComponent, newComponent ) );
	}

	protected CompCopier<AbstractButton> createTextCopier()
	{
		return( (originalComponent, newComponent) -> copyText( originalComponent, newComponent ) );
	}

	@Override
	public Class<AbstractButton> getParameterClass() {
		return( AbstractButton.class );
	}

	protected void copyActionListenersList( AbstractButton originalComponent, AbstractButton newComponent )
	{
		copyListeners( originalComponent, newComponent,
						ActionListener.class,
						(c) -> c.getActionListeners(),
						(c,l) -> c.addActionListener(l),
						(c,l) -> c.removeActionListener(l) );
	}

	protected void copyChangeListenersList( AbstractButton originalComponent, AbstractButton newComponent )
	{
		copyListeners( originalComponent, newComponent,
						ChangeListener.class,
						(c) -> c.getChangeListeners(),
						(c,l) -> c.addChangeListener(l),
						(c,l) -> c.removeChangeListener(l) );
	}

	protected void copyItemListenersList( AbstractButton originalComponent, AbstractButton newComponent )
	{
		copyListeners( originalComponent, newComponent,
						ItemListener.class,
						(c) -> c.getItemListeners(),
						(c,l) -> c.addItemListener(l),
						(c,l) -> c.removeItemListener(l) );
	}

	protected void copyDisabledIcon( AbstractButton originalComponent, AbstractButton newComponent )
	{
		newComponent.setDisabledIcon( originalComponent.getDisabledIcon() );
	}

	protected void copyDisabledSelectedIcon( AbstractButton originalComponent, AbstractButton newComponent )
	{
		newComponent.setDisabledSelectedIcon( originalComponent.getDisabledSelectedIcon() );
	}

	protected void copyHideActionText( AbstractButton originalComponent, AbstractButton newComponent )
	{
		newComponent.setHideActionText( originalComponent.getHideActionText() );
	}

	protected void copyHorizontalAlignment( AbstractButton originalComponent, AbstractButton newComponent )
	{
		newComponent.setHorizontalAlignment( originalComponent.getHorizontalAlignment() );
	}

	protected void copyVerticalAlignment( AbstractButton originalComponent, AbstractButton newComponent )
	{
		newComponent.setVerticalAlignment( originalComponent.getVerticalAlignment() );
	}

	protected void copyHorizontalTextPosition( AbstractButton originalComponent, AbstractButton newComponent )
	{
		newComponent.setHorizontalTextPosition( originalComponent.getHorizontalTextPosition() );
	}

	protected void copyVerticalTextPosition( AbstractButton originalComponent, AbstractButton newComponent )
	{
		newComponent.setVerticalTextPosition( originalComponent.getVerticalTextPosition() );
	}

	protected void copyIcon( AbstractButton originalComponent, AbstractButton newComponent )
	{
		newComponent.setIcon( originalComponent.getIcon() );
	}

	protected void copyIconTextGap( AbstractButton originalComponent, AbstractButton newComponent )
	{
		newComponent.setIconTextGap( originalComponent.getIconTextGap() );
	}

	protected void copyMargin( AbstractButton originalComponent, AbstractButton newComponent )
	{
		newComponent.setMargin( originalComponent.getMargin() );
	}

	protected void copyMnemonic( AbstractButton originalComponent, AbstractButton newComponent )
	{
		newComponent.setMnemonic( originalComponent.getMnemonic() );
	}

	protected void copyModel( AbstractButton originalComponent, AbstractButton newComponent )
	{
		newComponent.setModel( originalComponent.getModel() );
	}

	protected void copyMultiClickThreshhold( AbstractButton originalComponent, AbstractButton newComponent )
	{
		newComponent.setMultiClickThreshhold( originalComponent.getMultiClickThreshhold() );
	}

	protected void copyPressedIcon( AbstractButton originalComponent, AbstractButton newComponent )
	{
		newComponent.setPressedIcon( originalComponent.getPressedIcon() );
	}

	protected void copyRolloverIcon( AbstractButton originalComponent, AbstractButton newComponent )
	{
		newComponent.setRolloverIcon( originalComponent.getRolloverIcon() );
	}

	protected void copyRolloverSelectedIcon( AbstractButton originalComponent, AbstractButton newComponent )
	{
		newComponent.setRolloverSelectedIcon( originalComponent.getRolloverSelectedIcon() );
	}

	protected void copySelectedIcon( AbstractButton originalComponent, AbstractButton newComponent )
	{
		newComponent.setSelectedIcon( originalComponent.getSelectedIcon() );
	}

	protected void copyBorderPainted( AbstractButton originalComponent, AbstractButton newComponent )
	{
		newComponent.setBorderPainted( originalComponent.isBorderPainted() );
	}

	protected void copyContentAreaFilled( AbstractButton originalComponent, AbstractButton newComponent )
	{
		newComponent.setContentAreaFilled( originalComponent.isContentAreaFilled() );
	}

	protected void copyFocusPainted( AbstractButton originalComponent, AbstractButton newComponent )
	{
		newComponent.setFocusPainted( originalComponent.isFocusPainted() );
	}

	protected void copyRolloverEnabled( AbstractButton originalComponent, AbstractButton newComponent )
	{
		newComponent.setRolloverEnabled( originalComponent.isRolloverEnabled() );
	}

	protected void copyText( AbstractButton originalComponent, AbstractButton newComponent )
	{
		newComponent.setText( originalComponent.getText() );
	}
}
