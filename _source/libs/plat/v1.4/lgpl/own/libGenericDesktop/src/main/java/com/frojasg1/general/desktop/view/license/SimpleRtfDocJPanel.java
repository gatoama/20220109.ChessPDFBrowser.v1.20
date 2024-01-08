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
package com.frojasg1.general.desktop.view.license;

import com.frojasg1.general.desktop.view.text.link.imp.ScrollableJTextComponentUrlLauncher;
import com.frojasg1.general.desktop.files.DesktopResourceFunctions;
import com.frojasg1.general.desktop.view.editorkits.WrapEditorKit;
import com.frojasg1.applications.common.components.resizecomp.MapResizeRelocateComponentItem;
import com.frojasg1.applications.common.components.resizecomp.ResizeRelocateItem;
import com.frojasg1.general.desktop.DesktopStreamFunctions;
import com.frojasg1.general.desktop.view.color.components.ColorInversorJEditorPane;
import com.frojasg1.general.desktop.view.editorpane.FastColorInversorStaticDocumentJEditorPane;
import com.frojasg1.general.desktop.view.zoom.mapper.ComponentMapper;
import com.frojasg1.general.desktop.view.zoom.mapper.InternallyMappedComponent;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import javax.swing.JEditorPane;
import javax.swing.text.Document;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class SimpleRtfDocJPanel extends com.frojasg1.general.desktop.view.panels.CustomJPanelBase implements InternallyMappedComponent
{
	protected MapResizeRelocateComponentItem _resizeRelocateInfo = null;
	protected ColorInversorJEditorPane _rtfEditorPane = null;
	protected ScrollableJTextComponentUrlLauncher _urlLauncher = new ScrollableJTextComponentUrlLauncher();

	protected String _lastURL = null;

	/**
	 * Creates new form SimpleRtfDocJPanel
	 */
	public SimpleRtfDocJPanel()
	{
		super.init();


		initComponents();

//		setPanelConfiguration();
		setWindowConfiguration();
	}

	public MapResizeRelocateComponentItem getMapResizeRelocateComponentItem()
	{
		return( _resizeRelocateInfo );
	}

	protected void setWindowConfiguration( )
	{
		_resizeRelocateInfo = new MapResizeRelocateComponentItem();
		MapResizeRelocateComponentItem mapRRCI = _resizeRelocateInfo;
		try
		{
			mapRRCI.putResizeRelocateComponentItem( jScrollPane1, ResizeRelocateItem.FILL_WHOLE_PARENT );
		}
		catch( Throwable th )
		{
			th.printStackTrace();
		}
	}

	public void setUrl( URL url ) throws IOException
	{
		_rtfEditorPane = new FastColorInversorStaticDocumentJEditorPane();
		_rtfEditorPane.setEditable( false );
//		_rtfEditorPane.setEditorKit( new WrapEditorKit() );

		_rtfEditorPane.setPage( url );

//		_urlLauncher.removeListeners();
		jScrollPane1.setViewportView( _rtfEditorPane );
/*
		_rtfEditorPane.setSelectionStart(0);
		_rtfEditorPane.setSelectionEnd(0);

		_urlLauncher.setJTextComponent(_rtfEditorPane);
*/

		getColorInversor().setDarkMode(_rtfEditorPane, isDarkMode(), null);
	}

	public void setUrlStream( InputStream is, String description ) throws IOException
	{
		_rtfEditorPane = new FastColorInversorStaticDocumentJEditorPane();
		_rtfEditorPane.setEditable( false );
		_rtfEditorPane.setEditorKit( new WrapEditorKit() );

		_rtfEditorPane.setContentType( "text/html" );
//		_rtfEditorPane.setText( "<html>Page not found.</html>" );
//		TextFileWrapper

		_rtfEditorPane.read(is, description);

		_urlLauncher.removeListeners();
		jScrollPane1.setViewportView( _rtfEditorPane );
		_rtfEditorPane.setSelectionStart(0);
		_rtfEditorPane.setSelectionEnd(0);

		_urlLauncher.setJTextComponent(_rtfEditorPane);

		getColorInversor().setDarkMode(_rtfEditorPane, isDarkMode(), null);
	}

	public void setRtfDocument( Document doc )
	{
		_rtfEditorPane = new FastColorInversorStaticDocumentJEditorPane( "text/rtf", "" );
		_rtfEditorPane.setEditable( false );
		_rtfEditorPane.setEditorKit( new WrapEditorKit() );

		_rtfEditorPane.setDocument( doc );

		_urlLauncher.removeListeners();
		jScrollPane1.setViewportView( _rtfEditorPane );
		_rtfEditorPane.setSelectionStart(0);
		_rtfEditorPane.setSelectionEnd(0);

		_urlLauncher.setJTextComponent(_rtfEditorPane);
//		addListeners();
//		_rtfEditorPane.addMouseListener( this );
//		_rtfEditorPane.addMouseMotionListener( this );

		getColorInversor().setDarkMode(_rtfEditorPane, isDarkMode(), null);
	}

	public boolean setRtfFile( String fileName )
	{
		boolean result = false;
		try( FileInputStream fis = new FileInputStream( fileName ) )
		{
			setRtfInputStream( fis );
			result = true;
		}
		catch( Exception ex )
		{
			ex.printStackTrace();
		}

		return( result );
	}

	public boolean setRtfResource( String resourceName )
	{
		boolean result = false;
		try( InputStream is = DesktopResourceFunctions.instance().getInputStreamOfResource(resourceName) )
		{
			setRtfInputStream( is );
			result = true;
		}
		catch( Exception ex )
		{
			ex.printStackTrace();
		}

		return( result );
	}

	public boolean setRtfInputStream( InputStream is ) throws IOException
	{
		Document doc = null;
		doc = DesktopStreamFunctions.instance().loadRtfInputStream(is);
		setRtfDocument( doc );

		return( true );
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jEditorPane1 = new javax.swing.JEditorPane();

        setMinimumSize(new java.awt.Dimension(50, 50));
        setLayout(new java.awt.BorderLayout());

        jScrollPane1.setViewportView(jEditorPane1);

        add(jScrollPane1, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JEditorPane jEditorPane1;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables

	@Override
	public void setComponentMapper(ComponentMapper mapper)
	{
		jEditorPane1 = mapper.mapComponent(jEditorPane1);
		jScrollPane1 = mapper.mapComponent(jScrollPane1);
		_rtfEditorPane = mapper.mapComponent(_rtfEditorPane);
		_urlLauncher.setJTextComponent(_rtfEditorPane);

		super.setComponentMapper(mapper);
	}

	public JEditorPane getJEditorPane()
	{
		return( _rtfEditorPane );
	}
}
