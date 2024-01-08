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
package com.frojasg1.chesspdfbrowser.application;

import com.frojasg1.applications.common.components.internationalization.window.InternationalizationInitializationEndCallback;
import com.frojasg1.applications.common.components.resizecomp.MapResizeRelocateComponentItem;
import com.frojasg1.chesspdfbrowser.application.tasks.OpenSetPositionWindowAndTrainer;
import com.frojasg1.chesspdfbrowser.configuration.AppStringsConf;
import com.frojasg1.chesspdfbrowser.engine.configuration.ApplicationConfiguration;
import com.frojasg1.chesspdfbrowser.engine.init.ApplicationInitContext;
import com.frojasg1.chesspdfbrowser.engine.io.parsers.raw.inputtext.impl.InputImage;
import com.frojasg1.chesspdfbrowser.engine.io.parsers.tags.TagsExtractor;
import com.frojasg1.chesspdfbrowser.engine.view.chess.interaction.ChessGameControllerInterface;
import com.frojasg1.chesspdfbrowser.engine.view.chess.interaction.ImagePositionController;
import com.frojasg1.chesspdfbrowser.panels.ChessPdfViewerControlPanel;
import com.frojasg1.chesspdfbrowser.panels.api.ChessPdfViewerMaster;
import com.frojasg1.chesspdfbrowser.pdf.textsegmentation.PageSegmentationResult;
import com.frojasg1.general.desktop.image.pixel.impl.PixelComponents;
import com.frojasg1.chesspdfbrowser.pdf.textsegmentation.implementation.PDFPageSegmentator;
import com.frojasg1.chesspdfbrowser.pdf.textsegmentation.implementation.SummarizedPage;
import com.frojasg1.chesspdfbrowser.recognizer.recognizer.result.RecognitionResult;
import com.frojasg1.chesspdfbrowser.threads.LoadChessControllerInterface;
import com.frojasg1.general.FileFunctions;
import com.frojasg1.general.desktop.image.ImageFunctions;
import com.frojasg1.general.desktop.view.ViewFunctions;
import com.frojasg1.general.desktop.view.menus.BaseJPopupMenu;
import com.frojasg1.general.desktop.view.zoom.mapper.ComponentMapper;
import com.frojasg1.general.dialogs.DialogsWrapper;
import com.frojasg1.general.number.IntegerFunctions;
import com.frojasg1.generic.GenericFunctions;
import com.frojasg1.libpdf.api.ImageWrapper;
import com.frojasg1.libpdf.api.PdfDocumentWrapper;
import com.frojasg1.libpdf.threads.LoadPdfControllerInterface;
import com.frojasg1.libpdf.view.PdfViewerWindowBase;
import com.frojasg1.libpdf.view.api.PdfViewerControlView;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Vector;
import java.util.function.Consumer;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

// Minimum Size: [570, 780]

/**
 *
 * @author Usuario
 */
public class PdfViewerWindow extends PdfViewerWindowBase<ApplicationInitContext> implements ChessPdfViewerMaster
{
	public static final String sa_configurationBaseFileName = "PDFviewerWindow";

	protected LoadChessControllerInterface _parent = null;
	protected ChessGameControllerInterface _chessGameController = null;
	protected ImagePositionController _imagePositionController = null;

	protected PDFPageSegmentator _pageSegmentator = null;

	protected boolean _putSummarizedImage = false;

	protected TagsExtractor _tagsExtractor = null;

	protected ContextualMenu _popupMenu = null;

	protected MouseListener _mouseListener = null;

	protected Point _popupMenuLocation = null;

	protected ImageWrapper _imageToRecognize = null;

	protected OpenSetPositionWindowAndTrainer _openSetPositionWindowAndTrainer = null;

	/**
	 * Creates new form MainWindow
	 */
	public PdfViewerWindow( ApplicationInitContext applicationContext,
							Consumer<InternationalizationInitializationEndCallback> initializationEndCallBack )	{
		super( ApplicationConfiguration.instance(), applicationContext, initializationEndCallBack );
	}

	@Override
	public void init( LoadPdfControllerInterface parent,
						String configurationBaseFileName )
	{
		throw( new RuntimeException( "this init cannot be invoked. use init( LoadChessControllerInterface parent, " +
										"ChessGameControllerInterface cgci, " +
										"TagsExtractor tagsExtractor )   instead" ) );
	}

	public void init( LoadChessControllerInterface parent,
							ChessGameControllerInterface cgci,
							TagsExtractor tagsExtractor,
							ImagePositionController imagePositionController)
	{
		_imagePositionController = imagePositionController;
		_tagsExtractor = tagsExtractor;
		_chessGameController = cgci;
		_parent = parent;

		_popupMenu = new ContextualMenu();

		super.init( parent, sa_configurationBaseFileName );

		_mouseListener = new ClickListener();
		getPdfContentImageJPanel().addMouseListener( _mouseListener );

		_openSetPositionWindowAndTrainer = createOpenSetPositionWindowAndTrainer();
	}

	protected OpenSetPositionWindowAndTrainer createOpenSetPositionWindowAndTrainer()
	{
		OpenSetPositionWindowAndTrainer result = new OpenSetPositionWindowAndTrainer();
		result.init();

		return( result );
	}

	@Override
	protected Vector<JPopupMenu> getJPopupMenuVector()
	{
		Vector<JPopupMenu> result = super.getJPopupMenuVector();
		if( result == null )
			result = new Vector<>();
		
		result.add( getJPopupMenu() );

		return( result );
	}

	@Override
	protected void setWindowConfiguration( )
	{
		super.setWindowConfiguration();
	}

	public JPopupMenu getJPopupMenu()
	{
		return( _popupMenu );
	}

	protected void doPopup( MouseEvent evt )
	{
		_popupMenu.doPopup(evt);
	}

	protected ImageWrapper getImageToRecognize()
	{
		return( _imageToRecognize );
	}

	protected void setImageToRecognize( ImageWrapper image )
	{
		_imageToRecognize = image;
	}

	protected void paintHoverGlyphFrame( Graphics grp )
	{
		// do not paint glyph frame.
	}

	@Override
	protected PdfViewerControlView createPdfViewerControlView()
	{
		ChessPdfViewerControlPanel result = new ChessPdfViewerControlPanel();
		result.init( this );

		return( result );
	}

	@Override
	public boolean setNewPDF( PdfDocumentWrapper pdfDocument )
	{
		boolean documentWasSet = false;
		_pageSegmentator = null;

		documentWasSet = super.setNewPDF( pdfDocument );
		if( documentWasSet )
			setRenderSegmentsOfPages( hasToShowSegments() );

		return( documentWasSet );
	}

	protected String getPdfBaseFileName()
	{
		return( FileFunctions.instance().getBaseName( getPdfFileName() ) );
	}

	public boolean matchesBaseFileName( String baseFileName )
	{
		boolean result = false;

		String pdfBaseFileName = getPdfBaseFileName();
		if( ( pdfBaseFileName != null ) && ( baseFileName != null ) )
			result = pdfBaseFileName.equals( baseFileName );

		return( result );
	}

	public boolean hasToShowSegments()
	{
		return( getAppliConf().getHasToShowSegments() );
	}

	protected void createInternationalization(	String mainFolder,
												String applicationName,
												String group,
												String paquetePropertiesIdiomas,
												String configurationBaseFileName,
												Component parentFrame,
												Component parentParentFrame,
												Vector<JPopupMenu> vPUMenus,
												boolean hasToPutWindowPosition,
												MapResizeRelocateComponentItem mapRRCI,
												boolean adjustSizeOfFrameToContents,
												boolean adjustMinSizeOfFrameToContents,
												double zoomFactor,
												boolean activateUndoRedoForTextComponents,
												boolean enableTextPopupMenu,
												boolean switchToZoomComponents,
												boolean internationalizeFont,
												Integer delayToInvokeCallback
											)
	{
		super.createInternationalization( mainFolder, applicationName, group,
									paquetePropertiesIdiomas, configurationBaseFileName,
									parentFrame, parentParentFrame,
									vPUMenus, hasToPutWindowPosition,
									mapRRCI,
									adjustSizeOfFrameToContents,
									adjustMinSizeOfFrameToContents,
									zoomFactor,
									activateUndoRedoForTextComponents,
									enableTextPopupMenu,
									switchToZoomComponents,
									internationalizeFont,
									delayToInvokeCallback );
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(570, 340));
        setName(""); // NOI18N
        getContentPane().setLayout(null);

        pack();
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables

	@Override
	public BufferedImage getPage( int pageIndex, double factor )
	{
		BufferedImage image = null;

		try
		{
			image = super.getPage( pageIndex, factor );

			if( _putSummarizedImage )
			{
				BufferedImage tmpImage = createImageFromSummarizedPage( _pageSegmentator.getSummarizedPage(image, pageIndex) );

				if( tmpImage != null )	image = tmpImage;

				_putSummarizedImage = false;
			}
			else if( hasToShowSegments() && ( _pageSegmentator != null ) )
			{
				PageSegmentationResult result = _pageSegmentator.getSegments( pageIndex );

//				if( result.getCouldBeValidated() )
				{
					image = renderSegments( image, result.geListOfSegmentedRegions(), factor );
				}
			}
		}
		catch( Throwable th )
		{
			th.printStackTrace();
			GenericFunctions.instance().getDialogsWrapper().showMessageDialog( this,
											getAppStrConf().getProperty( AppStringsConf.CONF_ERROR_LOADING_PAGE_FROM ) +
											getTitle() +
											". " + getAppStrConf().getProperty( AppStringsConf.CONF_ERROR ) + ": " +
											th.getMessage(),
											getAppStrConf().getProperty( AppStringsConf.CONF_ERROR_LOADING_PAGE ),
											DialogsWrapper.ERROR_MESSAGE );
		}

		return( image );
	}

	protected BufferedImage renderSegments( BufferedImage image, List<Rectangle> segments, double factor )
	{
/*		BufferedImage result = new BufferedImage( (int) image.getWidth(),
													(int) image.getHeight(),
													BufferedImage.TYPE_INT_ARGB );
*/
		BufferedImage result = image;
		Graphics2D grph = result.createGraphics();

//		grph.drawImage( image, null, null );

		int index = 1;
		for( Rectangle rect: segments )
		{
			rect.setRect( rect.getX() * factor,
							rect.getY() * factor,
							rect.getWidth() * factor,
							rect.getHeight() * factor );

			renderSegment( grph, rect, index );
			index++;
		}
		
		return( result );
	}

	protected void renderSegment( Graphics grph, Rectangle rect, int index )
	{
		grph.setColor( Color.RED );

		int xx = (int) rect.getX()-3;
		int yy = (int) rect.getY()-3;
		int width = (int) rect.getWidth()+6;
		int height = (int) rect.getHeight()+6;
		for( int ii=0; ii<7; ii++ )
		{
			grph.drawRect( xx, yy, width, height );
			xx++;
			yy++;
			width -= 2;
			height -= 2;
		}

//		Integer fontSize = IntegerFunctions.parseInt( jTF_fontSize.getText() );
		Integer fontSize = (int) getPdfDocument().getDpi( 1.0D );
		if( fontSize == null ) fontSize = 72;
		Font font = new Font( "Lucida", Font.BOLD, IntegerFunctions.min( fontSize, IntegerFunctions.min( (int) rect.getWidth(), (int) rect.getHeight() ) ) );
		grph.setFont(font);
		ImageFunctions.instance().paintStringCentered( grph, font, String.valueOf( index ), Color.RED, rect, null );
	}

    protected void setRenderSegmentsOfPages( boolean hasToSetSegments )
	{
		PdfDocumentWrapper pdfDocument = getPdfDocument();
		if( hasToSetSegments && ( pdfDocument != null ) )
		{
			if( _pageSegmentator == null )
				_pageSegmentator = new PDFPageSegmentator( getAppliConf() );

			_pageSegmentator.setChessViewConfiguration( getAppliConf() );

			if( _pageSegmentator.getPDDocument() != pdfDocument )
			{
				_parent.startLoading();

				try
				{
					_pageSegmentator.initialize( pdfDocument, 3, 30, null );
				}
				catch( Throwable th )
				{
					th.printStackTrace();

					_pageSegmentator = new PDFPageSegmentator( getAppliConf() );
//					jCB_renderSegments.setSelected( false );

					GenericFunctions.instance().getDialogsWrapper().showMessageDialog( this,
													getAppStrConf().getProperty( AppStringsConf.CONF_ERROR_INITIALIZING_SEGMENTATOR ) + th,
													getAppStrConf().getProperty( AppStringsConf.CONF_ERROR_INITIALIZING_SEGMENTATOR2 ),
													DialogsWrapper.ERROR_MESSAGE );
				}

				_parent.endLoading();
			}
			_putSummarizedImage = true;
		}

		updatePage( getCurrentPageIndex() );
    }                                                  

	protected BufferedImage createImageFromSummarizedPage( SummarizedPage sumPage )
	{
		PixelComponents[][] pixels = null;

		BufferedImage result = null;
		
		if( sumPage != null )
		{
			pixels = sumPage.getSummarizedPixels();

			result = new BufferedImage( pixels.length, pixels[0].length, BufferedImage.TYPE_INT_RGB );

			for( int xx=0; xx<pixels.length; xx++ )
				for( int yy=0; yy<pixels[xx].length; yy++ )
				{
					PixelComponents pixel = pixels[xx][yy];
					int pixelValue = 0;
					if( pixel != null )
					{
						pixelValue = ( ( (int) pixel.getRed() ) << 16 ) |
									( ( (int) pixel.getGreen() ) << 8 ) |
									( (int) pixel.getBlue() );
					}
					result.setRGB( xx, yy, pixelValue );
				}
		}

		return( result );
	}

	@Override
	public void block()
	{
		super.block();
	}

	@Override
	public void unblock()
	{
		super.unblock();
	}

	@Override
	public ApplicationConfiguration getAppliConf()
	{
		return( (ApplicationConfiguration) super.getAppliConf() );
	}

	protected void scanPDFDocumentForGames()
	{
		PDFScanProgressJDial scanProgressJDial = new PDFScanProgressJDial( this, true,
																			_parent, getPdfDocument(),
																			_tagsExtractor,
																			getPdfBaseFileName(),
																			_chessGameController,
																			_imagePositionController,
																			getApplicationContext().getChessBoardRecognizerWhole() );

//		PDFScanProgressWindow scanProgressJFrame = new PDFScanProgressWindow( this, _parent, _pdfDocument );

		scanProgressJDial.setAlwaysOnTop(true);
		scanProgressJDial.setVisibleWithLock( true );
	}

	@Override
	public void closeWindow()
	{
		super.closeWindow();
	}

	public AppStringsConf getAppStrConf()
	{
		return( AppStringsConf.instance() );
	}

	@Override
	public void formWindowClosingEvent( )
	{
		super.formWindowClosingEvent();
	}

	@Override
	protected void translateMappedComponents(ComponentMapper compMapper)
	{
		super.translateMappedComponents(compMapper);
	}

	@Override
	public void scanPDFforGamesActionPerformed()
	{
        if( _chessGameController.checkToSaveCurrentPGN() )
			scanPDFDocumentForGames();
	}

	protected boolean canRecognize()
	{
		return( getApplicationContext().getChessBoardRecognizerWhole().getChessBoardRecognitionTrainingThread().isEmpty() &&
				!getApplicationContext().getChessBoardRecognizerWhole().getChessBoardRecognizerCopyFenThread().isRecognizing() );
	}

	@Override
	public void releaseResources()
	{
		getPdfContentImageJPanel().removeMouseListener( _mouseListener );

		super.releaseResources();
	}

	public void newImagePositionDetected(RecognitionResult result, InputImage image)
	{
		_openSetPositionWindowAndTrainer.newImagePositionDetected( this, getAppliConf(),
																	result, image,
																	getApplicationContext().getChessBoardRecognizerWhole(),
																	getApplicationContext().getChessGameController(),
																	OpenSetPositionWindowAndTrainer.ActionOnFenDetection.ASK_TO_OPEN_EDIT_POSITION_WINDOW );
	}

	protected Point getLocationOfImageToRecognize()
	{
		double zoomFactor = 1.0D / getPdfContentImageJPanel().getZoomFactor()._value;
		Point result = ViewFunctions.instance().getNewPoint(_popupMenuLocation, _newZoomFactor );

		return( result );
	}

	protected InputImage createInputImageToRecognize()
	{
		InputImage result = new InputImage( getImageToRecognize(),
											getLocationOfImageToRecognize(),
											null );

		return( result );
	}

	protected void tryToRecognizeAndCopyFen()
	{
		boolean finishThreadAtEnd = false;
		getApplicationContext().getChessBoardRecognizerWhole().getChessBoardRecognizerCopyFenThread().recognize(
			(pos, im) -> newImagePositionDetected(pos, im), createInputImageToRecognize(),
			getApplicationContext().getChessBoardRecognizerWhole().getStore(), finishThreadAtEnd );
	}

	protected class ClickListener extends com.frojasg1.general.desktop.controller.ClickListener
	{
		public ClickListener()
		{
		}

		@Override
		public void rightClick( MouseEvent evt )
		{
			Component comp = (Component) evt.getSource();

			_popupMenuLocation = evt.getPoint();
			setImageToRecognize( getHoverImage() );

			if( canRecognize() && ( getHoverImage() != null ) )
				doPopup( evt );
		}
	}

	protected class ContextualMenu extends BaseJPopupMenu
	{
		JMenuItem _menuItem_tryToRecognizeAndCopyFen = null;

		public ContextualMenu()
		{
			super(PdfViewerWindow.this);

			_menuItem_tryToRecognizeAndCopyFen = new JMenuItem( "Try to recognize position and copy Fen" );
			_menuItem_tryToRecognizeAndCopyFen.setName( "_menuItem_tryToRecognizeAndCopyFen" );

			addMenuComponent( _menuItem_tryToRecognizeAndCopyFen );

			_menuItem_tryToRecognizeAndCopyFen.setName( "_menuItem_tryToRecognizeAndCopyFen" );

			addMouseListenerToAllComponents();
		}

		protected void preparePopupMenuItems()
		{
			setAllEnabled(true);

			setTryToRecognizeAndCopyFenEnabled( canRecognize() );
		}

		public void setAllEnabled( boolean value )
		{
			_menuItem_tryToRecognizeAndCopyFen.setEnabled(value);
		}

		public void setTryToRecognizeAndCopyFenEnabled( boolean value )
		{
			_menuItem_tryToRecognizeAndCopyFen.setEnabled(value);
		}

		@Override
		public void mouseEntered(MouseEvent me)
		{
			Component comp = (Component) me.getSource();
		}

		@Override
		public void actionPerformed( ActionEvent evt )
		{
			try
			{
				Component comp = (Component) evt.getSource();

				if( comp == _menuItem_tryToRecognizeAndCopyFen )
					tryToRecognizeAndCopyFen();
			}
			finally
			{
				setVisible(false);
			}
		}

		@Override
		public void mouseExited(MouseEvent me)
		{
			Component comp = (Component) me.getSource();

//			_table.repaint();

			super.mouseExited(me);
		}

		@Override
		public void setComponentMapper(ComponentMapper mapper)
		{
			_menuItem_tryToRecognizeAndCopyFen = mapper.mapComponent(_menuItem_tryToRecognizeAndCopyFen);

			super.setComponentMapper(mapper);
		}
	}
}
