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
package com.frojasg1.general.desktop.view.about;

import com.frojasg1.desktop.liblens.graphics.Coordinate2D;
import com.frojasg1.general.string.StringFunctions;
import com.frojasg1.general.desktop.view.editorkits.WrapEditorKit;
import com.frojasg1.general.desktop.view.pdf.ImageJPanel;
import com.frojasg1.desktop.liblens.graphics.lens.LensJPanel;
import com.frojasg1.general.desktop.view.FontFunctions;
import com.frojasg1.general.desktop.view.editorpane.FastColorInversorStaticDocumentJEditorPane;
import com.frojasg1.general.lib3d.components.api.about.animation.AnimationForAbout;
import com.frojasg1.general.desktop.view.text.link.imp.ScrollableJTextComponentUrlLauncher;
import com.frojasg1.general.desktop.view.zoom.mapper.ComponentMapper;
import com.frojasg1.general.desktop.view.zoom.mapper.InternallyMappedComponent;
import com.frojasg1.general.number.IntegerFunctions;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import javax.swing.JToggleButton;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;

/**
 *
 * @author Usuario
 */
public class GenericAboutJPanel extends com.frojasg1.general.desktop.view.panels.CustomJPanelBase  implements InternallyMappedComponent	//,MouseListener, MouseMotionListener
{
//	protected static final String TORUS_IMAGE_RESOURCE = "com/frojasg1/generic/resources/images/torus.png";

	protected static final String SPACES_LINE = "                                                                      " + StringFunctions.RETURN;

	protected AboutJPanelControllerInterface _controller = null;

	protected ImageJPanel _imageJPanel = null;

	protected BufferedImage _image = null;

	protected FastColorInversorStaticDocumentJEditorPane _rtfEditorPane = null;

	protected LensJPanel _lensJPanel = null;

//	protected String _lastURL = null;

	protected GenericAboutJPanel _this = null;

	protected double _zoomFactor = 1.0D;

	protected ScrollableJTextComponentUrlLauncher _scrollableJTextForUrl = new ScrollableJTextComponentUrlLauncher();

	protected Dimension _originalImageJPanelSize = null;

	protected String _downloadFileName = null;

	protected AnimationForAbout _animation = null;

//	protected int _oldScrollYYValue = -1;

	/**
	 * Creates new form GenericAboutJPanel
	 */
	public GenericAboutJPanel( AboutJPanelControllerInterface controller,
								BufferedImage image,
								String downloadFileName,
								double zoomFactor )
	{
		super.init();

		_zoomFactor = zoomFactor;
		_this = this;
		_controller = controller;
		_downloadFileName = downloadFileName;
		setImage( image );

		initComponents();

		initOwnComponents();
	}

	public void setSendEmailButtonText( String text )
	{
		jBtn_sendEmail.setText( text );
	}
/*
	public void setHomePageLabelText( String text )
	{
		jL_homeWebPage.setText( text );
	}
*/
	public void setLensJPanel( LensJPanel ljp )
	{
		_lensJPanel = ljp;

		if( _lensJPanel != null )
		{
			_lensJPanel.M_addComponentNotToPaint( jTa_text );
//			_lensJPanel.M_addComponentNotToPaint( jBtn_exit );
//			_lensJPanel.M_addComponentNotToPaint( jBtn_sendEmail );
			_lensJPanel.M_addComponentNotToPaint( getJTextComponent() );
		}
	}

	public void setImage( BufferedImage image )
	{
		if( ( image == null ) || ( image.getHeight() == 0 ) )
		{
			_image = new BufferedImage( 245, 120, BufferedImage.TYPE_INT_RGB );
			Graphics gc = _image.getGraphics();
			gc.setColor( Color.WHITE );
			gc.fillRect( 0, 0, 245, 120 );
		}
		else
			_image = image;
	}

	public void setText( String text )
	{
		jTa_text.setText( text );
		jTa_text.setSelectionStart(0);
		jTa_text.setSelectionEnd(0);
	}

	public JTextComponent getJTextComponent()
	{
		JTextComponent result = _rtfEditorPane;
		if( result == null )
			result = jTa_text;

		return( result );
	}

	public void setRtfDocument( Document doc )
	{
		if( doc != null )
		{
			jTa_text.setFocusable( false );
			if( _rtfEditorPane != null )
				_lensJPanel.removeComponentNotToPaint( _rtfEditorPane );

			_rtfEditorPane = new FastColorInversorStaticDocumentJEditorPane( "text/rtf", "" );
			_rtfEditorPane.setEditorKit( new WrapEditorKit() );
			_rtfEditorPane.setEditable( false );
			_rtfEditorPane.setDocument( doc );
			jScrollPane1.setViewportView( _rtfEditorPane );
			_rtfEditorPane.setSelectionStart(0);
			_rtfEditorPane.setSelectionEnd(0);

			getColorInversor().setDarkMode(_rtfEditorPane, isDarkMode(), null);

			try
			{
				doc.insertString( doc.getLength(), StringFunctions.RETURN, null);
				doc.insertString( doc.getLength(), SPACES_LINE, null);
				doc.insertString( doc.getLength(), SPACES_LINE, null);
				doc.insertString( doc.getLength(), SPACES_LINE, null);
			}
			catch( Throwable th )
			{
				th.printStackTrace();
			}

			if( _lensJPanel != null )
			{
				_lensJPanel.M_addComponentNotToPaint( _rtfEditorPane );
				_rtfEditorPane.requestFocusInWindow();
			}
		}
		else
		{
			_rtfEditorPane = null;
			jScrollPane1.setViewportView( jTa_text );
			jTa_text.setFocusable( true );
			jTa_text.requestFocusInWindow();
			jTa_text.setSelectionStart(0);
			jTa_text.setSelectionEnd(0);
		}

		_scrollableJTextForUrl.setJTextComponent( _rtfEditorPane );
		_lensJPanel.repaint();
	}

	protected void zoomImage()
	{
		_imageJPanel.zoomImageWithSize( _zoomFactor );

//		_imageJPanel.repaint();
	}

	protected void setAnimation( AnimationForAbout animation )
	{
		_animation = animation;
		jTB_showTorus.setVisible( _animation != null );
		if( _animation != null )
			_animation.animationSetEnclosingDimension( getPreferredSize() );
	}

	public AnimationForAbout getAnimation()
	{
		return( _animation );
	}

	public void initOwnComponents()
	{
		jL_downloadFileName.setText( _downloadFileName );

		jTa_text.setWrapStyleWord(true);
		jTa_text.setLineWrap( true );

		_imageJPanel = new ImageJPanel( _image );
//		_imageJPanel.zoomImage( _zoomFactor );

		setFrameThick( 3 );

		jPanelImage.add( _imageJPanel );

		int width = (int) ( jPanelImage.getHeight() * _image.getWidth() / _image.getHeight() );
		int height = (int) jPanelImage.getHeight();

		_originalImageJPanelSize = new Dimension( width, height );

		Rectangle jpiBounds = jPanelImage.getBounds();
		jPanelImage.setBounds( (int) ( jpiBounds.getX() + ( jpiBounds.getWidth() - width ) / 2 ),
								(int) ( jpiBounds.getY() + ( jpiBounds.getHeight() - height ) / 2 ),
								width, height );
		_imageJPanel.setBounds( 0, 0, width, height );
		_imageJPanel.setPreferredSize( _originalImageJPanelSize );

		jL_homeWebPage.setFont( FontFunctions.instance().getUnderlinedFont( jL_homeWebPage.getFont() ) );
//		jTa_text.addMouseListener(this);
//		jTa_text.addMouseMotionListener(this);

//		jScrollPane1.getViewport().addMouseListener( this );
//		jScrollPane1.getViewport().addMouseMotionListener( this );

		zoomImage();
	}

	public void setForegroundColorOfHomeWebPage( Color color )
	{
		jL_homeWebPage.setForeground( color );
	}

	protected int getZoomedValue( double value )
	{
		int result = IntegerFunctions.roundToInt( value * _zoomFactor );

		return( result );
	}

	public void setFrameThick( int value )
	{
		int newThick = getZoomedValue( value );
		
		_imageJPanel.setFrameThick(newThick);
	}
	
	public void setOriginalFontForTextLines( Font font )
	{
//		int newFontSize = getZoomedValue( font.getSize() );
//		Font zoomedFont = font.deriveFont( (float) newFontSize );

//		_imageJPanel.setOriginalFontForTextLines(zoomedFont);
		_imageJPanel.setOriginalFontForTextLines(font);
	}

	public void setForegroundColorForTextLines( Color color )
	{
		_imageJPanel.setForegroundColorForTextLines(color);
	}
	
	public void addTextLine( ImageJPanel.TextLine textLine )
	{
		_imageJPanel.addTextLine(textLine);
	}

	public void clearTextLines()
	{
		_imageJPanel.clearTextLines();
	}

	public Dimension getSizeOfOriginalImageJPanel()
	{
		return( _originalImageJPanelSize );
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jBtn_exit = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTa_text = new javax.swing.JTextArea();
        jBtn_sendEmail = new javax.swing.JButton();
        jPanelImage = new javax.swing.JPanel();
        jL_homeWebPage = new javax.swing.JLabel();
        jL_Version = new javax.swing.JLabel();
        jL_downloadFileName = new javax.swing.JLabel();
        jTB_showTorus = new javax.swing.JToggleButton();

        setPreferredSize(new java.awt.Dimension(650, 405));
        setLayout(null);

        jPanel1.setLayout(null);

        jBtn_exit.setText("Exit");
        jBtn_exit.setName("jBtn_exit"); // NOI18N
        jBtn_exit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtn_exitActionPerformed(evt);
            }
        });
        jPanel1.add(jBtn_exit);
        jBtn_exit.setBounds(540, 120, 100, 20);

        jTa_text.setEditable(false);
        jTa_text.setColumns(20);
        jTa_text.setRows(5);
        jScrollPane1.setViewportView(jTa_text);

        jPanel1.add(jScrollPane1);
        jScrollPane1.setBounds(10, 160, 630, 230);

        jBtn_sendEmail.setText("Send e-mail to:  frojasg1@hotmail.com");
        jBtn_sendEmail.setFocusable(false);
        jBtn_sendEmail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtn_sendEmailActionPerformed(evt);
            }
        });
        jPanel1.add(jBtn_sendEmail);
        jBtn_sendEmail.setBounds(10, 400, 630, 25);

        jPanelImage.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanelImage.setLayout(null);
        jPanel1.add(jPanelImage);
        jPanelImage.setBounds(230, 10, 128, 128);

        jL_homeWebPage.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jL_homeWebPage.setForeground(new java.awt.Color(39, 155, 0));
        jL_homeWebPage.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jL_homeWebPage.setText("https://frojasg1.com");
        jL_homeWebPage.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jL_homeWebPage.setName("name=jL_homeWebPage,url=https://frojasg1.com"); // NOI18N
        jPanel1.add(jL_homeWebPage);
        jL_homeWebPage.setBounds(440, 10, 200, 16);

        jL_Version.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jL_Version.setText("Version :");
        jL_Version.setName("jL_Version"); // NOI18N
        jPanel1.add(jL_Version);
        jL_Version.setBounds(10, 140, 60, 14);

        jL_downloadFileName.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jL_downloadFileName.setForeground(new java.awt.Color(221, 135, 13));
        jL_downloadFileName.setText("Empty");
        jPanel1.add(jL_downloadFileName);
        jL_downloadFileName.setBounds(80, 140, 560, 14);

        jTB_showTorus.setText("press");
        jTB_showTorus.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jTB_showTorus.setMaximumSize(new java.awt.Dimension(100000, 100000));
        jTB_showTorus.setMinimumSize(new java.awt.Dimension(0, 0));
        jTB_showTorus.setName("name=jTB_showTorus,icon=com/frojasg1/generic/resources/images/torus.png"); // NOI18N
        jTB_showTorus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTB_showTorusActionPerformed(evt);
            }
        });
        jPanel1.add(jTB_showTorus);
        jTB_showTorus.setBounds(10, 10, 60, 60);

        add(jPanel1);
        jPanel1.setBounds(0, 0, 650, 435);
    }// </editor-fold>//GEN-END:initComponents

    private void jBtn_exitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtn_exitActionPerformed
        // TODO add your handling code here:

        _controller.exit(this);

    }//GEN-LAST:event_jBtn_exitActionPerformed

    private void jBtn_sendEmailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtn_sendEmailActionPerformed
        // TODO add your handling code here:

		_controller.sendEmailAddress();

    }//GEN-LAST:event_jBtn_sendEmailActionPerformed

    private void jTB_showTorusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTB_showTorusActionPerformed
        // TODO add your handling code here:

		torusToggleButtonActionPerformed();

    }//GEN-LAST:event_jTB_showTorusActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jBtn_exit;
    private javax.swing.JButton jBtn_sendEmail;
    private javax.swing.JLabel jL_Version;
    private javax.swing.JLabel jL_downloadFileName;
    private javax.swing.JLabel jL_homeWebPage;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanelImage;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JToggleButton jTB_showTorus;
    private javax.swing.JTextArea jTa_text;
    // End of variables declaration//GEN-END:variables

	public void changeUrlCursor( int oldScrollYYValue )
	{
		_scrollableJTextForUrl.changeCursor( oldScrollYYValue );
	}

	public void setImageBackground( Color color )
	{
		_imageJPanel.setBackground(color);
	}

	@Override
	public void setComponentMapper(ComponentMapper mapper) {
		jBtn_exit = mapper.mapComponent(jBtn_exit);
		jBtn_sendEmail = mapper.mapComponent(jBtn_sendEmail);
		jL_homeWebPage = mapper.mapComponent(jL_homeWebPage);
		jPanelImage = mapper.mapComponent(jPanelImage);
		jScrollPane1 = mapper.mapComponent(jScrollPane1);
		jTa_text = mapper.mapComponent(jTa_text);
		jL_Version = mapper.mapComponent(jL_Version);
		jL_downloadFileName = mapper.mapComponent(jL_downloadFileName);

		JToggleButton jtb = mapper.mapComponent(jTB_showTorus);
		jTB_showTorus = jtb;
/*
		jTB_showTorus.addComponentListener(
			new ComponentListener() {
			@Override
			public void componentResized(ComponentEvent e) {
				addImageToShowTorusButton( TORUS_IMAGE_RESOURCE );
			}

			@Override
			public void componentMoved(ComponentEvent e) {
			}

			@Override
			public void componentShown(ComponentEvent e) {
			}

			@Override
			public void componentHidden(ComponentEvent e) {
			}
			
		});
*/
//		SwingUtilities.invokeLater( () -> ViewFunctions.instance().addImageToButtonFast( jtb,
//												TORUS_IMAGE_RESOURCE, new Insets(2,2,2,2) ) );

		super.setComponentMapper(mapper);
	}
/*
	protected void addImageToShowTorusButton( String resourceName )
	{
		ViewFunctions.instance().addImageToButtonAccurate( jTB_showTorus,
												resourceName, new Insets(2,2,2,2) );
	}
*/
	protected void torusToggleButtonActionPerformed()
	{
		if( _animation != null )
		{
			if( jTB_showTorus.isSelected() )
			{
				_animation.animationReset();
			}
			else
			{
				_animation.animationStop();
				_lensJPanel.setSprite( null, null );
			}
		}
	}

	protected void nextStepForAnimation()
	{
		if( _animation != null )
		{
			_animation.animationDoStep();

			if( _animation.animationGetPosition() != null )
				_lensJPanel.setSprite( _animation.animationGetFrame(), point2Coordinate2D(_animation.animationGetPosition() ) );
		}
	}

	protected Coordinate2D point2Coordinate2D( Point pnt )
	{
		Coordinate2D result = null;
		if( pnt != null )
			result = new Coordinate2D(pnt.x, pnt.y);

		return( result );
	}
}
