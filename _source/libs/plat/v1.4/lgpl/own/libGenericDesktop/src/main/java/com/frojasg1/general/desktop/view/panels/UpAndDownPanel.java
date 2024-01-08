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
package com.frojasg1.general.desktop.view.panels;

import com.frojasg1.applications.common.components.resizecomp.MapResizeRelocateComponentItem;
import com.frojasg1.applications.common.components.resizecomp.ResizeRelocateItem;
import com.frojasg1.general.desktop.view.zoom.mapper.ComponentMapper;
import com.frojasg1.general.desktop.view.zoom.mapper.InternallyMappedComponent;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class UpAndDownPanel extends com.frojasg1.general.desktop.view.panels.CustomJPanelBase implements InformerInterface, InternallyMappedComponent
{
	protected static final String _resourcePath = "com/frojasg1/generic/resources/upanddown";

//	protected ResizableImageJButton _jB_upButton = null;
//	protected ResizableImageJButton _jB_downButton = null;

	protected UpAndDownControllerInterface _controller = null;

	protected MapResizeRelocateComponentItem _mapRRCI = null;

	protected UpAndDownPanel _this = this;

	/**
	 * Creates new form UpAndDownPanel
	 */
	public UpAndDownPanel( UpAndDownControllerInterface controller ) {
		super.init();

		initComponents();

		initOwnComponents();

		setSize( _jB_downButton.getWidth(), _jB_downButton.getY() + _jB_downButton.getHeight() );

		_controller = controller;
		_mapRRCI = createResizeRelocateInfo();
	}

	protected String getResourceName( String shortResourceName )
	{
		return( _resourcePath + "/" + shortResourceName );
	}

	protected void initOwnComponents()
	{
		Insets insets = new Insets( 1, 1, 1, 1 );
/*
		_jB_upButton = new ResizableImageJButton( getResourceName( "up.png" ), insets ){
						public void actionPerformed(ActionEvent e)
						{
							_controller.doUp( _this );
						}
		};

		_jB_downButton = new ResizableImageJButton( getResourceName( "down.png" ), insets ){
						public void actionPerformed(ActionEvent e)
						{
							_controller.doDown( _this );
						}
		};

		jPanel2.add( _jB_upButton );
		_jB_upButton.setBounds( 0, 0, jPanel2.getWidth(), jPanel2.getHeight() );

		jPanel1.add( _jB_downButton );
		_jB_downButton.setBounds( 0, 0, jPanel1.getWidth(), jPanel1.getHeight() );
*/
//		_jB_upButton.setMargin( insets );
//		_jB_downButton.setMargin( insets );

		_jB_upButton.addActionListener( new ActionListener(){
						@Override
						public void actionPerformed(ActionEvent e)
						{
							_controller.doUp( _this );
						}
		});
		_jB_downButton.addActionListener( new ActionListener(){
						@Override
						public void actionPerformed(ActionEvent e)
						{
							_controller.doDown( _this );
						}
		});
	}

	public MapResizeRelocateComponentItem getResizeRelocateInfo()
	{
		if( _mapRRCI == null )
		{
			_mapRRCI = createResizeRelocateInfo();
		}

		return( _mapRRCI );
	}

	protected MapResizeRelocateComponentItem createResizeRelocateInfo()
	{
		MapResizeRelocateComponentItem mapRRCI = new MapResizeRelocateComponentItem();

		try
		{
/*			mapRRCI.putResizeRelocateComponentItem( jPanel1, ResizeRelocateItem.MOVE_LEFT_SIDE_PROPORTIONAL +
																ResizeRelocateItem.MOVE_RIGHT_SIDE_PROPORTIONAL +
																ResizeRelocateItem.MOVE_TOP_SIDE_PROPORTIONAL +
																ResizeRelocateItem.MOVE_BOTTOM_SIDE_PROPORTIONAL );
			mapRRCI.putResizeRelocateComponentItem( jPanel2, ResizeRelocateItem.MOVE_LEFT_SIDE_PROPORTIONAL +
																ResizeRelocateItem.MOVE_RIGHT_SIDE_PROPORTIONAL +
																ResizeRelocateItem.MOVE_TOP_SIDE_PROPORTIONAL +
																ResizeRelocateItem.MOVE_BOTTOM_SIDE_PROPORTIONAL );

			mapRRCI.putResizeRelocateComponentItem( _jB_upButton, ResizeRelocateItem.RESIZE_TO_BOTTOM +
																		ResizeRelocateItem.RESIZE_TO_RIGHT );
			mapRRCI.putResizeRelocateComponentItem( _jB_downButton, ResizeRelocateItem.RESIZE_TO_BOTTOM +
																		ResizeRelocateItem.RESIZE_TO_RIGHT );
*/
			mapRRCI.putResizeRelocateComponentItem( _jB_upButton, ResizeRelocateItem.MOVE_LEFT_SIDE_PROPORTIONAL +
																ResizeRelocateItem.MOVE_RIGHT_SIDE_PROPORTIONAL +
																ResizeRelocateItem.MOVE_TOP_SIDE_PROPORTIONAL +
																ResizeRelocateItem.MOVE_BOTTOM_SIDE_PROPORTIONAL );
			mapRRCI.putResizeRelocateComponentItem( _jB_downButton, ResizeRelocateItem.MOVE_LEFT_SIDE_PROPORTIONAL +
																ResizeRelocateItem.MOVE_RIGHT_SIDE_PROPORTIONAL +
																ResizeRelocateItem.MOVE_TOP_SIDE_PROPORTIONAL +
																ResizeRelocateItem.MOVE_BOTTOM_SIDE_PROPORTIONAL );

//			HintForComponent hfc = new HintForComponent( jB_acceptChanges, "Accept changes" );
//			HintForComponent hfc2 = new HintForComponent( jB_eraseTag, "Erase record" );
//			HintForComponent hfc3 = new HintForComponent( jB_revertChanges, "Revert changes" );
		}
		catch( Throwable th )
		{
			th.printStackTrace();
		}

		return( mapRRCI );
	}
	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        _jB_upButton = new javax.swing.JButton();
        _jB_downButton = new javax.swing.JButton();

        setLayout(null);

        _jB_upButton.setName("name=_jB_upButton,icon=com/frojasg1/generic/resources/upanddown/up.png"); // NOI18N
        add(_jB_upButton);
        _jB_upButton.setBounds(0, 0, 20, 20);

        _jB_downButton.setName("name=_jB_downButton,icon=com/frojasg1/generic/resources/upanddown/down.png"); // NOI18N
        add(_jB_downButton);
        _jB_downButton.setBounds(0, 20, 20, 20);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton _jB_downButton;
    private javax.swing.JButton _jB_upButton;
    // End of variables declaration//GEN-END:variables

	@Override
	public void setComponentMapper(ComponentMapper mapper) {
		_jB_downButton = mapper.mapComponent(_jB_downButton);
		_jB_upButton = mapper.mapComponent(_jB_upButton);

		super.setComponentMapper(mapper);
	}
}
