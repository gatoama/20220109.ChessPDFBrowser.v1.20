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
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JEditorPane;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.EditorKit;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class JEditorPaneCopier extends CompCopierBase<JEditorPane>
{

	@Override
	protected List<CompCopier<JEditorPane>> createCopiers() {

		List<CompCopier<JEditorPane>> result = new ArrayList<>();

		result.add( createContentTypeCopier() );
		result.add( createEditorKitCopier() );
		result.add( createPageCopier() );
		result.add( createHyperlinkListenersListCopier() );

		return( result );
	}

	protected CompCopier<JEditorPane> createContentTypeCopier()
	{
		return( (originalComponent, newComponent) -> copyContentType( originalComponent, newComponent ) );
	}

	protected CompCopier<JEditorPane> createEditorKitCopier()
	{
		return( (originalComponent, newComponent) -> copyEditorKit( originalComponent, newComponent ) );
	}

	protected CompCopier<JEditorPane> createPageCopier()
	{
		return( (originalComponent, newComponent) -> copyPage( originalComponent, newComponent ) );
	}

	protected CompCopier<JEditorPane> createHyperlinkListenersListCopier()
	{
		return( (originalComponent, newComponent) -> copyHyperlinkListenersList( originalComponent, newComponent ) );
	}

	@Override
	public Class<JEditorPane> getParameterClass() {
		return( JEditorPane.class );
	}

	protected void copyContentType( JEditorPane originalComponent, JEditorPane newComponent )
	{
		newComponent.setContentType( originalComponent.getContentType() );
	}

	protected void copyEditorKit( JEditorPane originalComponent, JEditorPane newComponent )
	{
		EditorKit editor = originalComponent.getEditorKit();
		if( !isClassOfJdk( editor ) )
			newComponent.setEditorKit( editor );
	}

	protected void copyPage( JEditorPane originalComponent, JEditorPane newComponent )
	{
		try
		{
			URL page = originalComponent.getPage();
			if( page != null )
				newComponent.setPage( page );
		}
		catch( Exception ex )
		{
			ex.printStackTrace();
		}
	}

	protected void copyHyperlinkListenersList( JEditorPane originalComponent, JEditorPane newComponent )
	{
		copyListeners( originalComponent, newComponent,
						HyperlinkListener.class,
						(c) -> c.getHyperlinkListeners(),
						(c,l) -> c.addHyperlinkListener(l),
						(c,l) -> c.removeHyperlinkListener(l) );
	}
}
