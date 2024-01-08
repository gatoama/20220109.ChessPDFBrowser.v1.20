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
package com.frojasg1.chesspdfbrowser.view.chess;

import com.frojasg1.chesspdfbrowser.engine.model.chess.pieces.Bishop;
import com.frojasg1.chesspdfbrowser.engine.model.chess.pieces.ChessPiece;
import com.frojasg1.chesspdfbrowser.engine.model.chess.pieces.Knight;
import com.frojasg1.chesspdfbrowser.engine.model.chess.pieces.Queen;
import com.frojasg1.chesspdfbrowser.engine.model.chess.pieces.Rook;
import com.frojasg1.chesspdfbrowser.view.chess.images.ChessBoardImages;
import com.frojasg1.general.desktop.image.ImageFunctions;
import com.frojasg1.general.desktop.view.color.ColorInversor;
import com.frojasg1.general.desktop.view.zoom.mapper.ComponentMapper;
import com.frojasg1.general.desktop.view.zoom.mapper.InternallyMappedComponent;
import java.awt.Component;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;

/**
 *
 * @author Usuario
 */
public class PromotionJPanel extends com.frojasg1.general.desktop.view.panels.CustomJPanelBase
							implements MouseListener, InternallyMappedComponent
{
	protected PromotionControllerInterface _controller = null;
	protected int _color = -1;

	protected boolean _isDarkMode = false;
	protected ColorInversor _colorInversor = null;

	/**
	 * Creates new form NewJPanel
	 */
	public PromotionJPanel( PromotionControllerInterface controller )
	{
		super.init();

		_controller = controller;

		initComponents();
		
		customInit();
	}

	public void customInit()
	{
		setBounds( 0, 0, 160, 40 );
		setBorder( javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)) );
	}

	protected void addMouseListenersToButtons()
	{
		jB_Queen.addMouseListener(this);
		jB_Rook.addMouseListener(this);
		jB_Bishop.addMouseListener(this);
		jB_Knight.addMouseListener(this);
	}

	protected void removeMouseListenersFromButtons()
	{
		jB_Queen.removeMouseListener(this);
		jB_Rook.removeMouseListener(this);
		jB_Bishop.removeMouseListener(this);
		jB_Knight.removeMouseListener(this);
	}

	public void setVisible( boolean visible )
	{
		if( visible )
			addMouseListenersToButtons();
		else
			removeMouseListenersFromButtons();

		super.setVisible( visible );
	}
	
	public void setColor( int color )
	{
		_color = color;

		initPromotionButtons( color );		
	}

	protected void addImageToButton( JButton button, String resourceName )
	{
		try
		{
			BufferedImage img = ImageIO.read(ClassLoader.getSystemClassLoader().getResourceAsStream(resourceName));
			int transparentArgbColor = img.getRGB(0, 0);
			BufferedImage resizedImage = ImageFunctions.instance().resizeImage( img, button.getWidth()-2, button.getHeight()-2, transparentArgbColor, 0xFFF8E2E2, null );
			BufferedImage finalImage = resizedImage;
			if( _isDarkMode )
				finalImage = _colorInversor.putOutImageColor( finalImage, 0.5 );

			button.setIcon(new ImageIcon(finalImage));
			button.setMargin(new Insets(1, 1, 1, 1));
		}
		catch (IOException ex)
		{
			ex.printStackTrace();
		}
	}

	protected void initPromotionButtons( int color )
	{
		if( color == ChessPiece.WHITE )
		{
			addImageToButton(jB_Queen, ChessBoardImages.instance().getResourceNameForWhiteQueen());
			addImageToButton(jB_Rook, ChessBoardImages.instance().getResourceNameForWhiteRook());
			addImageToButton(jB_Bishop, ChessBoardImages.instance().getResourceNameForWhiteBishop());
			addImageToButton(jB_Knight, ChessBoardImages.instance().getResourceNameForWhiteKnight());
		}
		else if( color == ChessPiece.BLACK )
		{
			addImageToButton(jB_Queen, ChessBoardImages.instance().getResourceNameForBlackQueen());
			addImageToButton(jB_Rook, ChessBoardImages.instance().getResourceNameForBlackRook());
			addImageToButton(jB_Bishop, ChessBoardImages.instance().getResourceNameForBlackBishop());
			addImageToButton(jB_Knight, ChessBoardImages.instance().getResourceNameForBlackKnight());
		}
	}
	
	public Point getCenterPointOfQueenButton()
	{
		int xx = jB_Queen.getX() + jB_Queen.getWidth()/2;
		int yy = jB_Queen.getY() + jB_Queen.getHeight()/2;

		return( new Point( xx, yy ) );
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jB_Knight = new javax.swing.JButton();
        jB_Queen = new javax.swing.JButton();
        jB_Rook = new javax.swing.JButton();
        jB_Bishop = new javax.swing.JButton();

        setPreferredSize(new java.awt.Dimension(160, 40));
        setLayout(null);

        jB_Knight.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        add(jB_Knight);
        jB_Knight.setBounds(120, 0, 40, 40);

        jB_Queen.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        add(jB_Queen);
        jB_Queen.setBounds(0, 0, 40, 40);

        jB_Rook.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        add(jB_Rook);
        jB_Rook.setBounds(40, 0, 40, 40);

        jB_Bishop.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        add(jB_Bishop);
        jB_Bishop.setBounds(80, 0, 40, 40);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jB_Bishop;
    private javax.swing.JButton jB_Knight;
    private javax.swing.JButton jB_Queen;
    private javax.swing.JButton jB_Rook;
    // End of variables declaration//GEN-END:variables

	@Override
	public void mouseClicked(MouseEvent me)
	{
		Component component = me.getComponent();
		
		String pieceTypeCode = null;
		if( component == jB_Queen )			pieceTypeCode = Queen.PIECE_TYPE_CODE;
		else if( component == jB_Rook )		pieceTypeCode = Rook.PIECE_TYPE_CODE;
		else if( component == jB_Bishop )		pieceTypeCode = Bishop.PIECE_TYPE_CODE;
		else if( component == jB_Knight )		pieceTypeCode = Knight.PIECE_TYPE_CODE;
		
		if( ( pieceTypeCode != null ) && ( _controller != null ) )
		{
			_controller.pieceTopromotePawnChosen(pieceTypeCode);
		}
	}

	@Override
	public void mousePressed(MouseEvent me)
	{
		
	}

	@Override
	public void mouseReleased(MouseEvent me)
	{
		
	}

	@Override
	public void mouseEntered(MouseEvent me)
	{
		
	}

	@Override
	public void mouseExited(MouseEvent me)
	{
		Point mouseLocation = me.getLocationOnScreen();
		Point leftTopCorner = getLocationOnScreen();
		Point rightBottomCorner = new Point( (int) (leftTopCorner.getX() + getWidth()),
												(int) (leftTopCorner.getY() + getHeight()) );
		
		if( ( mouseLocation.getX() <= leftTopCorner.getX() ) ||
			( mouseLocation.getX() >= rightBottomCorner.getX() ) ||
			( mouseLocation.getY() <= leftTopCorner.getY() ) ||
			( mouseLocation.getY() >= rightBottomCorner.getY() ) )
		{
			_controller.pieceTopromotePawnChosen( null );
		}
	}

	@Override
	public void setComponentMapper(ComponentMapper compMapper) {
		jB_Bishop = compMapper.mapComponent( jB_Bishop );
		jB_Knight = compMapper.mapComponent( jB_Knight );
		jB_Queen = compMapper.mapComponent( jB_Queen );
		jB_Rook = compMapper.mapComponent( jB_Rook );

		super.setComponentMapper(compMapper);
	}

	@Override
	public void invertColorsChild( ColorInversor colorInversor )
	{
		_isDarkMode = !_isDarkMode;
		_colorInversor = colorInversor;
	}
}
