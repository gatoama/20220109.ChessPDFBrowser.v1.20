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
package com.frojasg1.general.desktop.view.splash;

import com.frojasg1.applications.common.configuration.application.BaseApplicationConfigurationInterface;
import com.frojasg1.general.desktop.generic.dialogs.impl.StaticDesktopDialogsWrapper;
import com.frojasg1.general.desktop.files.DesktopResourceFunctions;
import com.frojasg1.general.desktop.view.pdf.ImageJPanel;
import com.frojasg1.general.exceptions.ConfigurationException;
import com.frojasg1.general.number.IntegerFunctions;
import com.frojasg1.applications.common.components.internationalization.InternException;
import com.frojasg1.applications.common.components.internationalization.window.InternationalizedJFrame;
import com.frojasg1.applications.common.components.resizecomp.MapResizeRelocateComponentItem;
import com.frojasg1.applications.common.components.resizecomp.ResizeRelocateItem;
import com.frojasg1.general.desktop.view.colors.Colors;
import com.frojasg1.general.desktop.view.zoom.mapper.ComponentMapper;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.util.Vector;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.plaf.basic.BasicProgressBarUI;

/**
 *
 * @author Usuario
 */
public abstract class GenericBasicSplash extends InternationalizedJFrame implements GenericSplashInterface, MouseMotionListener, MouseListener
{
	protected static GenericBasicSplash _instance = null;

	public static final String CONF_OPENING_CONFIGURATION = "OPENING_CONFIGURATION";
	public static final String CONF_SHOWING_LICENSES = "SHOWING_LICENSES";
	public static final String CONF_IMPORTING_CONFIGURATION = "IMPORTING_CONFIGURATION";
	public static final String CONF_CREATING_MAIN_WINDOW = "CREATING_MAIN_WINDOW";
	public static final String CONF_PROBLEM_LAUNCHING_THE_APPLICATION = "PROBLEM_LAUNCHING_THE_APPLICATION";

	public static final String CONF_LOOKING_FOR_A_NEW_VERSION = "LOOKING_FOR_A_NEW_VERSION";
	public static final String CONF_SHOWING_WHAT_IS_NEW = "SHOWING_WHAT_IS_NEW";

	protected BufferedImage _image = null;
	protected ImageJPanel _imageJPanel = null;

	protected Point _positionOnScreenWhenMousePressed = null;
	protected Point _positionOnScreenOfSplashWhenMousePressed = null;

	protected String _currentActivityLabel = null;

	protected Dimension _originalImageJPanelSize = null;

	/**
	 * Creates new form Splash
	 */
	public GenericBasicSplash( BaseApplicationConfigurationInterface applicationConfiguration )
	{
		super();
//		setImage( image );

//		setPreventFromRepainting(false);

		setUndecorated( true );

		this.setAppliConf(applicationConfiguration);

		initComponents();

		initOwnComponents_Parent();

		_instance = this;
	}

	protected Color getTextColorForProgressBar( Color brightModeColor, Color darkModeColor )
	{
		Color result = brightModeColor;
		if( isDarkMode() )
			result = darkModeColor;
		
		return( result );
	}

	protected final void initOwnComponents_Parent()
	{
		// http://stackoverflow.com/questions/3480125/setting-the-colors-of-a-jprogressbar-text
		jPB_progress.setUI( new BasicProgressBarUI() {
								protected Color getSelectionBackground() { return getTextColorForProgressBar(Color.BLACK, Color.WHITE); }
								protected Color getSelectionForeground() { return getTextColorForProgressBar(Color.BLACK, Color.BLUE); }
							  });
		
		setPreferredSize( jPanel1.getSize() );
		setSize( jPanel1.getSize() );
	}

	protected abstract String getBaseConfigurationFileName();

	protected void setWindowConfiguration( )
	{
		Vector<JPopupMenu> vectorJpopupMenus = null;

		MapResizeRelocateComponentItem mapRRCI = new MapResizeRelocateComponentItem();
		try
		{
			mapRRCI.putResizeRelocateComponentItem( jPanel1, ResizeRelocateItem.FILL_WHOLE_WIDTH + ResizeRelocateItem.FILL_WHOLE_HEIGHT );
			mapRRCI.putResizeRelocateComponentItem( jPanelImage, 0 );
			mapRRCI.putResizeRelocateComponentItem( _imageJPanel, ResizeRelocateItem.FILL_WHOLE_WIDTH + ResizeRelocateItem.FILL_WHOLE_HEIGHT );
		}
		catch( Throwable th )
		{
			th.printStackTrace();
		}

		createInternationalization(	getAppliConf().getConfigurationMainFolder(),
									getAppliConf().getApplicationNameFolder(),
									getAppliConf().getApplicationGroup(),
									getAppliConf().getInternationalPropertiesPathInJar(),
									getBaseConfigurationFileName(),
									this,
									null,
									vectorJpopupMenus,
									true,
									mapRRCI );
	}

	@Override
	public void internationalizationInitializationEndCallback()
	{
		super.internationalizationInitializationEndCallback();

		this.registerInternationalString(CONF_OPENING_CONFIGURATION, "Opening configuration ..." );
		this.registerInternationalString(CONF_SHOWING_LICENSES,	"Showing licenses ..." );
		this.registerInternationalString(CONF_PROBLEM_LAUNCHING_THE_APPLICATION, "Problem launching the application" );
		this.registerInternationalString(CONF_CREATING_MAIN_WINDOW, "Creating main window ..." );
		this.registerInternationalString(CONF_IMPORTING_CONFIGURATION, "Importing configuration ..." );

		this.registerInternationalString(CONF_LOOKING_FOR_A_NEW_VERSION, "Looking for a new version ..." );
		this.registerInternationalString(CONF_SHOWING_WHAT_IS_NEW, "Showing what is new ..." );

		SwingUtilities.invokeLater( () -> {
			zoomImage();
//			repaint();
		} );
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
        jPanelImage = new javax.swing.JPanel();
        jPB_progress = new javax.swing.JProgressBar();
        jL_currentActivity = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);
        getContentPane().setLayout(null);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0))));
        jPanel1.setLayout(null);

        jPanelImage.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanelImage.setLayout(null);
        jPanel1.add(jPanelImage);
        jPanelImage.setBounds(111, 11, 128, 128);

        jPB_progress.setForeground(new java.awt.Color(255, 0, 0));
        jPB_progress.setStringPainted(true);
        jPanel1.add(jPB_progress);
        jPB_progress.setBounds(40, 150, 280, 17);

        jL_currentActivity.setPreferredSize(new java.awt.Dimension(280, 10));
        jPanel1.add(jL_currentActivity);
        jL_currentActivity.setBounds(40, 170, 280, 20);

        getContentPane().add(jPanel1);
        jPanel1.setBounds(0, 0, 350, 210);

        pack();
    }// </editor-fold>//GEN-END:initComponents

	/**
	 * @param args the command line arguments
	 */
//	public static void main(String args[]) {
		/* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
		 * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
		 */
	/*
		try {
			for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					javax.swing.UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (ClassNotFoundException ex) {
			java.util.logging.Logger.getLogger(GenericBasicSplash.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		} catch (InstantiationException ex) {
			java.util.logging.Logger.getLogger(GenericBasicSplash.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		} catch (IllegalAccessException ex) {
			java.util.logging.Logger.getLogger(GenericBasicSplash.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		} catch (javax.swing.UnsupportedLookAndFeelException ex) {
			java.util.logging.Logger.getLogger(GenericBasicSplash.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		}
	*/
        //</editor-fold>
        //</editor-fold>

		/* Create and display the form */
//		java.awt.EventQueue.invokeLater(new Runnable() {
//			public void run() {
//				new GenericBasicSplash().setVisible(true);
//			}
//		});
//	}

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jL_currentActivity;
    private javax.swing.JProgressBar jPB_progress;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanelImage;
    // End of variables declaration//GEN-END:variables

	protected void setImage( BufferedImage image )
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

		setImageChanges();
	}

	protected void zoomImage()
	{
		_imageJPanel.zoomImageWithSize( getAppliConf().getZoomFactor() );

		_imageJPanel.repaint();
	}

	protected void setImageChanges()
	{
		_imageJPanel = new ImageJPanel( _image );

		int width = (int) ( jPanelImage.getHeight() * _image.getWidth() / _image.getHeight() );
		int height = (int) jPanelImage.getHeight();

//		_imageJPanel.zoomImage( getAppliConf().getZoomFactor() );
		setFrameThick( 3 );

		jPanelImage.add( _imageJPanel );

		_originalImageJPanelSize = new Dimension( width, height );

		Rectangle jpiBounds = jPanelImage.getBounds();
		jPanelImage.setBounds( (int) ( jpiBounds.getX() + ( jpiBounds.getWidth() - width ) / 2 ),
								(int) ( jpiBounds.getY() + ( jpiBounds.getHeight() - height ) / 2 ),
								width, height );
		_imageJPanel.setBounds( 0, 0, width, height );
		_imageJPanel.setPreferredSize( new Dimension( width, height ) );
	}

	public Font getFontForTextLines()
	{
		return( _imageJPanel.getFont() );
	}

	@Override
	public void setCurrentActivity(String currentActivity)
	{
		jL_currentActivity.setText( currentActivity );
//		jL_currentActivity.update(jL_currentActivity.getGraphics());
		jL_currentActivity.paintImmediately(jL_currentActivity.getVisibleRect());

		_currentActivityLabel = null;
	}

	@Override
	public void setCurrentActivityFromLabel( String label )
	{
		if( label != null )
		{
			setCurrentActivity( this.getInternationalString(label) );
			_currentActivityLabel = label;
		}
	}

	@Override
	public void setProgress(int progress)
	{
		progress = IntegerFunctions.min( jPB_progress.getMaximum(),
											IntegerFunctions.max( progress, jPB_progress.getMinimum() ) );

		jPB_progress.setValue( progress );
//		jPB_progress.update(jPB_progress.getGraphics());
	}

	@Override
	public void activateProgress( boolean activate )
	{
		jPB_progress.setVisible( activate );
		jL_currentActivity.setVisible( activate );
	}

	protected int getZoomedValue( double value )
	{
		int result = IntegerFunctions.roundToInt( value * getAppliConf().getZoomFactor() );

		return( result );
	}

	public void setFrameThick( int value )
	{
//		int newThick = getZoomedValue( value );

//		_imageJPanel.setFrameThick(newThick);
		_imageJPanel.setFrameThick( value );
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

	protected BufferedImage getImage( String pathInJarForApplicationImage )
	{
		BufferedImage result = null;
		try
		{
			result = DesktopResourceFunctions.instance().loadResourceImage(pathInJarForApplicationImage);
		}
		catch( Throwable th )
		{
			th.printStackTrace();
		}

		return( result );
	}

	public void setCenteredLocation()
	{
		setLocation(StaticDesktopDialogsWrapper.getCenteredLocationForComponent_static( this ) );
	}

	@Override
	public void mouseDragged(MouseEvent me)
	{
		if( ( _positionOnScreenWhenMousePressed != null ) &&
			( _positionOnScreenOfSplashWhenMousePressed != null ) )
		{
			Point pos = me.getLocationOnScreen();

			int xx = _positionOnScreenOfSplashWhenMousePressed.x + pos.x - _positionOnScreenWhenMousePressed.x;
			int yy = _positionOnScreenOfSplashWhenMousePressed.y + pos.y - _positionOnScreenWhenMousePressed.y;

			System.out.println( String.format( "( %d, %d )", xx, yy ) );

			setLocation( xx, yy );
		}
	}

	@Override
	public void mouseMoved(MouseEvent me)
	{
/*
		if( _positionOnScreenWhenMousePressed != null )
		{
			Point pos = me.getLocationOnScreen();

			int xx = _positionOnScreenOfSplashWhenMousePressed.x + pos.x - _positionOnScreenWhenMousePressed.x;
			int yy = _positionOnScreenOfSplashWhenMousePressed.y + pos.y - _positionOnScreenWhenMousePressed.y;

			System.out.println( String.format( "( %d, %d )", xx, yy ) );

			setLocation( xx, yy );
		}
*/
	}

	@Override
	public void mouseClicked(MouseEvent me)
	{
	}

	@Override
	public void mousePressed(MouseEvent me)
	{
		_positionOnScreenWhenMousePressed = me.getLocationOnScreen();
		_positionOnScreenOfSplashWhenMousePressed = getLocation();
	}

	@Override
	public void mouseReleased(MouseEvent me)
	{
		_positionOnScreenWhenMousePressed = null;
		_positionOnScreenOfSplashWhenMousePressed = null;
	}

	@Override
	public void mouseEntered(MouseEvent me)
	{
	}

	@Override
	public void mouseExited(MouseEvent me)
	{
	}

	protected void setListenersRecursive( Component comp )
	{
		comp.addMouseMotionListener(this);
		comp.addMouseListener(this);

		if( comp instanceof Container )
		{
			Container cont = (Container) comp;

			for( int ii=0; ii<cont.getComponentCount(); ii++ )
				setListenersRecursive( cont.getComponent(ii) );
		}
	}

	@Override
	public void changeLanguage( String language ) throws ConfigurationException, InternException
	{
		super.changeLanguage( language );
		setCurrentActivityFromLabel( _currentActivityLabel );
	}

	public void setImageBackground( Color color )
	{
		_imageJPanel.setBackground(color);
	}

	@Override
	protected void translateMappedComponents(ComponentMapper compMapper)
	{
		jL_currentActivity = compMapper.mapComponent(jL_currentActivity);
		jPB_progress = compMapper.mapComponent(jPB_progress);
		jPanel1 = compMapper.mapComponent(jPanel1);
		jPanelImage = compMapper.mapComponent(jPanelImage);
	}

	@Override
	public void changeZoomFactor( double zoomFactor )
	{
		super.changeZoomFactor(zoomFactor);

		zoomImage();
	}

	public static void free()
	{
		if( _instance != null )
		{
			_instance.setVisible( false );
			_instance.dispose();
			_instance = null;
		}
	}
}
