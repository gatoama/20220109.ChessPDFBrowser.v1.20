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
package com.frojasg1.libpdf.view.panels;

import com.frojasg1.applications.common.components.resizecomp.MapResizeRelocateComponentItem;
import com.frojasg1.applications.common.components.resizecomp.ResizeRelocateItem;
import com.frojasg1.applications.common.configuration.InternationalizedStringConf;
import com.frojasg1.applications.common.configuration.imp.InternationalizedStringConfImp;
import com.frojasg1.general.desktop.view.FrameworkComponentFunctions;
import com.frojasg1.general.desktop.view.color.ColorInversor;
import com.frojasg1.libpdf.api.PDFownerInterface;
import com.frojasg1.libpdf.api.PdfDocumentWrapper;
import com.frojasg1.libpdf.viewer.PdfViewer;
import com.frojasg1.general.desktop.view.pdf.ImageJPanel;
import com.frojasg1.general.desktop.view.pdf.ImageJPanelControllerInterface;
import com.frojasg1.general.desktop.view.zoom.mapper.ComponentMapper;
import com.frojasg1.general.desktop.view.zoom.mapper.ComposedComponent;
import com.frojasg1.general.number.DoubleReference;
import com.frojasg1.general.number.IntegerFunctions;
import com.frojasg1.libpdf.api.GlyphWrapper;
import com.frojasg1.libpdf.api.ImageWrapper;
import com.frojasg1.libpdf.color.PdfPageColorInversor;
import com.frojasg1.libpdf.constants.LibPdfConstants;
import com.frojasg1.libpdf.view.api.PdfViewerMaster;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.LinkedList;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.JScrollPane;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class PdfContentPanel extends com.frojasg1.general.desktop.view.panels.CustomJPanelBase
	implements ComposedComponent,
				ImageJPanelControllerInterface, //NavigatorControllerInterface,
				PDFownerInterface, PdfViewer, InternationalizedStringConf
{
	public static final String GLOBAL_CONF_FILE_NAME = "PdfContentPanel.properties";

	protected static final String CONF_ERROR_WHEN_GETTING_PDF_PAGE = "ERROR_WHEN_GETTING_PDF_PAGE";


	protected InternationalizedStringConfImp _internationalizedStringConf = null;

	protected ImageJPanel _pdfImagePanel = null;

	protected int _totalNumberOfPages = 0;
	protected int _currentPageIndex = 0;

	protected PDFownerInterface _pdfOwner = null;

	protected DoubleReference _pdfZoomFactor = null;

	protected PdfDocumentWrapper _pdfDocument = null;

	protected MapResizeRelocateComponentItem _resizeRelocateInfo = null;

	protected PdfViewerMaster _controller = null;

	protected PdfPageColorInversor _pdfPageColorInversor;

	public PdfContentPanel()
	{
		this( new PdfPageColorInversor() );
	}

	/**
	 * Creates new form PdfContentPanel
	 */
	public PdfContentPanel(PdfPageColorInversor pdfPageColorInversor)
	{
		_pdfPageColorInversor = pdfPageColorInversor;
		_internationalizedStringConf = new InternationalizedStringConfImp( GLOBAL_CONF_FILE_NAME,
								LibPdfConstants.sa_PROPERTIES_PATH_IN_JAR );

		registerInternationalizedStrings();
	}

	public void init( PdfViewerMaster controller, PDFownerInterface pdfOwner )
	{
		super.init();

		initComponents();

		initZoomFactor();
		initPanel();

		_controller = controller;
		if( pdfOwner != null )
			_pdfOwner = pdfOwner;
		else
			_pdfOwner = this;

		setWindowConfiguration();
	}

	protected void initZoomFactor()
	{
		_pdfZoomFactor = new DoubleReference( 1.0D );
	}

	public PdfDocumentWrapper getPdfDocument()
	{
		return( _pdfDocument );
	}

	protected JScrollPane getImageScrollPane()
	{
		return( jSP_imageScrollPane );
	}

	protected ImageJPanel createImageJPanel()
	{
		ImageJPanel result = new ImageJPanel( getImageScrollPane(), this );
		result.setCanInvertImageColors(false);
		return( result );
	}

	protected void initPanel()
	{
		try
		{
			_pdfImagePanel = createImageJPanel();
			jSP_imageScrollPane.setViewportView( _pdfImagePanel );
		}
		catch( Throwable th )
		{
			th.printStackTrace();
		}
	}

	public ImageJPanel getImagePanel()
	{
		return( _pdfImagePanel );
	}

	protected void setWindowConfiguration( )
	{
		_resizeRelocateInfo = new MapResizeRelocateComponentItem();
		MapResizeRelocateComponentItem mapRRCI = _resizeRelocateInfo;
		try
		{
			mapRRCI.putResizeRelocateComponentItem( jSP_imageScrollPane, ResizeRelocateItem.FILL_WHOLE_PARENT );
		}
		catch( Throwable th )
		{
			th.printStackTrace();
		}

//		ExecutionFunctions.instance().safeMethodExecution( () -> registerInternationalizedStrings() );
	}

	@Override
	public MapResizeRelocateComponentItem getResizeRelocateInfo()
	{
		return( _resizeRelocateInfo );
	}

	public void setImage( String fileName )
	{
		try
		{
			File file = new File( fileName );
			setPage( ImageIO.read( file ) );
		}
		catch( Throwable th )
		{
			th.printStackTrace();
		}
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jSP_imageScrollPane = new javax.swing.JScrollPane();

        setLayout(null);
        add(jSP_imageScrollPane);
        jSP_imageScrollPane.setBounds(0, 0, 550, 670);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jSP_imageScrollPane;
    // End of variables declaration//GEN-END:variables

	public LinkedList<DoubleReference> getStepsForFactorList()
	{
		return( _pdfImagePanel.getStepsForFactorList() );
	}
/*
	protected DoubleReference getSelectedZoomFactor()
	{
        DoubleReference newFactor = null;
        Object obj = jCBZoomFactor.getSelectedItem();
        if( obj instanceof DoubleReference )
        {
            newFactor = (DoubleReference) obj;
        }
		return( newFactor );
	}

	protected double getSelectedZoomFactorDouble()
	{
		double factor = 1.0D;
		
		DoubleReference dr = getSelectedZoomFactor();
		if( dr != null )
			factor = dr._value;
		
		return( factor );
	}
*/	

	public void setDocument( PdfDocumentWrapper document )
	{
		if( ( _pdfDocument != null ) && ( _pdfDocument != document ) )
		{
			try
			{
				_pdfDocument.close();
			}
			catch( Throwable th )
			{
				th.printStackTrace();
			}
		}

		_pdfDocument = document;
	}

	public void setNumberOfPages( int numberOfPages )
	{
		_totalNumberOfPages = numberOfPages;
		_currentPageIndex = 0;

		updatePage( 0, _pdfZoomFactor );
	}

	public void setNewPDF( PdfDocumentWrapper document )
	{
		setDocument( document );

		setNumberOfPages( _pdfDocument.getNumberOfPages() );
	}

	protected boolean isDarkMode()
	{
		return( FrameworkComponentFunctions.instance().isDarkMode(this) );
	}

	protected BufferedImage invertPageColors( BufferedImage image, double zoomFactor )
	{
		return( _pdfPageColorInversor.invertPdfPageColors( getColorInversor(), image,
					zoomFactor,	getImagesOfPage(), getGlyphsOfPage() )	);
	}

	protected List<ImageWrapper> getImagesOfPage()
	{
		return( _controller.getPdfViewerContext().getImagesOfPage() );
	}

	protected List<GlyphWrapper> getGlyphsOfPage()
	{
		return( _controller.getPdfViewerContext().getGlyphsOfPage() );
	}

	protected void setPage( BufferedImage image, DoubleReference factor, int pageIndex )
	{
		ImageJPanel.InitialVerticalPosition ivp = null;
				
		if( isDarkMode() )
			image = invertPageColors( image, factor._value );


		if( _currentPageIndex < pageIndex )
			ivp = ImageJPanel.InitialVerticalPosition.TOP;
		else if( _currentPageIndex > pageIndex )
			ivp = ImageJPanel.InitialVerticalPosition.BOTTOM;
		else
			ivp = ImageJPanel.InitialVerticalPosition.NOTHING;

		_pdfImagePanel.setImage( image, factor, ivp );
/*
		updateComboBoxValues();

		_flagComboBoxItemSelectedByProgram = true;
		jCBZoomFactor.setSelectedItem( _pdfImagePanel.getZoomFactor() );
		_flagComboBoxItemSelectedByProgram = false;
*/		
		_currentPageIndex = pageIndex;

		_controller.newPageSet(image, factor, pageIndex);
	}

	public DoubleReference getMostSuitableFactor( DoubleReference factor )
	{
		return( _pdfImagePanel.getMostSuitableFactor( factor ) );
	}

	protected double getSelectedZoomFactorDouble(DoubleReference dr)
	{
		double factor = 1.0D;
		
		if( dr != null )
			factor = dr._value;
		
		return( factor );
	}

	protected void setPage( BufferedImage image )
	{
		DoubleReference factor = new DoubleReference( 1.0D );
		setPage(image, factor, _currentPageIndex );
	}

	public void setPageIndex( int pageIndex )
	{
		_currentPageIndex = pageIndex;
	}

	protected void setPage( int pageIndex, DoubleReference zoomFactor )
	{
		BufferedImage image = _pdfOwner.getPage( pageIndex, getSelectedZoomFactorDouble(zoomFactor) );
		if( image != null )
			setPage( image, zoomFactor, pageIndex );		
	}

	public void updatePage( int pageIndex, DoubleReference zoomFactor )
	{
////		if( isVisible() )
		{
			_currentPageIndex = pageIndex;
			setPage(_currentPageIndex, zoomFactor );
////			updateCurrentPageTexts();
		}
	}

	public void setPdfZoomFactor( DoubleReference pdfZoomFactor )
	{
		updatePage( _currentPageIndex, _pdfZoomFactor );
	}

	public DoubleReference getZoomFactor()
	{
		return( _pdfImagePanel.getZoomFactor() );
	}

	public int getStartPage()
	{
		return( 0 );
	}

	public int getEndPage()
	{
		return( _totalNumberOfPages - 1 );
	}

/*
	@Override
	public void navigator_start( InformerInterface panel )
	{
		int pageIndex = 0;
		updatePage( pageIndex, _pdfZoomFactor );
	}

	@Override
	public void navigator_end( InformerInterface panel )
	{
		int pageIndex = _totalNumberOfPages - 1;
		updatePage( pageIndex, _pdfZoomFactor );
	}
*/
	public int incrementCurrentPage()
	{
		int pageIndex = IntegerFunctions.min(_totalNumberOfPages - 1, _currentPageIndex + 1 );
//		_currentPageIndex = IntegerFunctions.min( _totalNumberOfPages - 1, _currentPageIndex + 1 );
		return( pageIndex );
	}
	
	public int decrementCurrentPage()
	{
		int pageIndex = IntegerFunctions.max(0, _currentPageIndex - 1 );
//		_currentPageIndex = IntegerFunctions.max( 0, _currentPageIndex - 1 );
		return( pageIndex );
	}
/*
	@Override
	public void navigator_previous( InformerInterface panel )
	{
		updatePage( decrementCurrentPage(), _pdfZoomFactor );
	}

	@Override
	public void navigator_next( InformerInterface panel )
	{
		updatePage( incrementCurrentPage(), _pdfZoomFactor );
	}
*/

	public void updatePage( int pageIndex )
	{
		updatePage( pageIndex, _pdfZoomFactor );
	}

	public int getCurrentPage()
	{
		return( _currentPageIndex );
	}

	@Override
	public void previousPage()
	{
		updatePage( decrementCurrentPage() );
	}

	@Override
	public void nextPage()
	{
		updatePage( incrementCurrentPage() );
	}

	@Override
	public void setNewPdfZoomFactor( DoubleReference factor )
	{
		if( factor != null )
			_pdfZoomFactor = getMostSuitableFactor( factor );

		setPage(_pdfOwner.getPage(_currentPageIndex, _pdfZoomFactor._value ), _pdfZoomFactor, _currentPageIndex );
	}
	
	@Override
	public BufferedImage getPage( int pageIndex, double factor )
	{
		BufferedImage image = null;

		try
		{
			if( _pdfDocument != null )
				image = _pdfDocument.getPage( pageIndex, factor );
		}
		catch( Exception ex )
		{
			ex.printStackTrace();

			throw( new RuntimeException( getInternationalString( CONF_ERROR_WHEN_GETTING_PDF_PAGE ), ex ) );
		}

		return( image );
	}

	@Override
	public int getNumPages()
	{
		return( _totalNumberOfPages );
	}

	@Override
	public void showPage(int index)
	{
		if( ( index >= 0 ) && ( index < getNumPages() ) )
			updatePage( index , _pdfZoomFactor );
	}

	@Override
	public String getPdfFileName()
	{
		String result = null;
		if( _pdfDocument != null )
			result = _pdfDocument.getFileName();

		return( result );
	}

	@Override
	public void setComponentMapper(ComponentMapper mapper)
	{
		jSP_imageScrollPane = mapper.mapComponent( jSP_imageScrollPane );
		_pdfImagePanel = mapper.mapComponent( _pdfImagePanel );

		super.setComponentMapper(mapper);
	}

	@Override
	public Dimension getInternalSize()
	{
		return( jSP_imageScrollPane.getSize() );
	}

	@Override
	public void changeLanguage(String language) throws Exception {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public void registerInternationalString(String label, String value)
	{
		_internationalizedStringConf.registerInternationalString(label, value);
	}

	@Override
	public String getInternationalString(String label)
	{
		return( _internationalizedStringConf.getInternationalString(label ) );
	}

	@Override
	public String createCustomInternationalString(String label, Object... args) {
		return( _internationalizedStringConf.createCustomInternationalString( label, args ) );
	}

	protected void registerInternationalizedStrings()
	{
		registerInternationalString( CONF_ERROR_WHEN_GETTING_PDF_PAGE, "Error when getting pdf page" );
	}

	@Override
	public Rectangle getInternalBounds()
	{
		return( jSP_imageScrollPane.getBounds() );
	}

	@Override
	protected void invertColorsChild(ColorInversor colorInversor)
	{
		updatePage( getCurrentPage() );
	}
}
