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
import java.util.ArrayList;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.text.JTextComponent;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class JLabelCopier extends CompCopierBase<JLabel>
{

	@Override
	protected List<CompCopier<JLabel>> createCopiers() {

		List<CompCopier<JLabel>> result = new ArrayList<>();

		result.add( createDisabledIconCopier() );
		result.add( createDisplayedMnemonicCopier() );
		result.add( createDisplayedMnemonicIndexCopier() );
		result.add( createHorizontalAlignmentCopier() );
		result.add( createHorizontalTextPositionCopier() );
		result.add( createIconCopier() );
		result.add( createIconTextGapCopier() );
		result.add( createLabelForCopier() );
		result.add( createVerticalAlignmentCopier() );
		result.add( createVerticalTextPositionCopier() );
		result.add( createTextCopier() );

		return( result );
	}

	protected CompCopier<JLabel> createDisabledIconCopier()
	{
		return( (originalComponent, newComponent) -> copyDisabledIcon( originalComponent, newComponent ) );
	}

	protected CompCopier<JLabel> createDisplayedMnemonicCopier()
	{
		return( (originalComponent, newComponent) -> copyDisplayedMnemonic( originalComponent, newComponent ) );
	}

	protected CompCopier<JLabel> createDisplayedMnemonicIndexCopier()
	{
		return( (originalComponent, newComponent) -> copyDisplayedMnemonicIndex( originalComponent, newComponent ) );
	}

	protected CompCopier<JLabel> createHorizontalAlignmentCopier()
	{
		return( (originalComponent, newComponent) -> copyHorizontalAlignment( originalComponent, newComponent ) );
	}

	protected CompCopier<JLabel> createHorizontalTextPositionCopier()
	{
		return( (originalComponent, newComponent) -> copyHorizontalTextPosition( originalComponent, newComponent ) );
	}

	protected CompCopier<JLabel> createIconCopier()
	{
		return( (originalComponent, newComponent) -> copyIcon( originalComponent, newComponent ) );
	}

	protected CompCopier<JLabel> createIconTextGapCopier()
	{
		return( (originalComponent, newComponent) -> copyIconTextGap( originalComponent, newComponent ) );
	}

	protected CompCopier<JLabel> createLabelForCopier()
	{
		return( (originalComponent, newComponent) -> copyLabelFor( originalComponent, newComponent ) );
	}

	protected CompCopier<JLabel> createVerticalAlignmentCopier()
	{
		return( (originalComponent, newComponent) -> copyVerticalAlignment( originalComponent, newComponent ) );
	}

	protected CompCopier<JLabel> createVerticalTextPositionCopier()
	{
		return( (originalComponent, newComponent) -> copyVerticalTextPosition( originalComponent, newComponent ) );
	}

	protected CompCopier<JLabel> createTextCopier()
	{
		return( (originalComponent, newComponent) -> copyText( originalComponent, newComponent ) );
	}

	@Override
	public Class<JLabel> getParameterClass() {
		return( JLabel.class );
	}

	protected void copyDisabledIcon( JLabel originalComponent, JLabel newComponent )
	{
		newComponent.setDisabledIcon( originalComponent.getDisabledIcon() );
	}

	protected void copyDisplayedMnemonic( JLabel originalComponent, JLabel newComponent )
	{
		newComponent.setDisplayedMnemonic( originalComponent.getDisplayedMnemonic() );
	}

	protected void copyDisplayedMnemonicIndex( JLabel originalComponent, JLabel newComponent )
	{
		newComponent.setDisplayedMnemonicIndex( originalComponent.getDisplayedMnemonicIndex() );
	}

	protected void copyHorizontalAlignment( JLabel originalComponent, JLabel newComponent )
	{
		newComponent.setHorizontalAlignment( originalComponent.getHorizontalAlignment() );
	}

	protected void copyHorizontalTextPosition( JLabel originalComponent, JLabel newComponent )
	{
		newComponent.setHorizontalTextPosition( originalComponent.getHorizontalTextPosition() );
	}

	protected void copyIcon( JLabel originalComponent, JLabel newComponent )
	{
		newComponent.setIcon( originalComponent.getIcon() );
	}

	protected void copyIconTextGap( JLabel originalComponent, JLabel newComponent )
	{
		newComponent.setIconTextGap( originalComponent.getIconTextGap() );
	}

	protected void copyLabelFor( JLabel originalComponent, JLabel newComponent )
	{
		newComponent.setLabelFor( originalComponent.getLabelFor() );
	}

	protected void copyVerticalAlignment( JLabel originalComponent, JLabel newComponent )
	{
		newComponent.setVerticalAlignment( originalComponent.getVerticalAlignment() );
	}

	protected void copyVerticalTextPosition( JLabel originalComponent, JLabel newComponent )
	{
		newComponent.setVerticalTextPosition( originalComponent.getVerticalTextPosition() );
	}

	protected void copyText( JLabel originalComponent, JLabel newComponent )
	{
		newComponent.setText( originalComponent.getText() );
	}
}
