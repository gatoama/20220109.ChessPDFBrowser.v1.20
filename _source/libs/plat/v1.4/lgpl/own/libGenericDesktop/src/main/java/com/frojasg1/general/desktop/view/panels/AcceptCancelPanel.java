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
import com.frojasg1.general.desktop.view.ViewFunctions;
import com.frojasg1.general.desktop.view.zoom.mapper.ComponentMapper;
import com.frojasg1.general.desktop.view.zoom.mapper.InternallyMappedComponent;
import javax.swing.JButton;

/**
 *
 * @author Usuario
 */
public class AcceptCancelPanel extends com.frojasg1.general.desktop.view.panels.CustomJPanelBase
									implements // ResizableComponentInterface,
												InformerInterface,
												AcceptCancelControllerInterface,
												InternallyMappedComponent
{
	protected static final String _resourcePath = "com/frojasg1/generic/resources/acceptcancel";

	protected AcceptCancelControllerInterface _controller = null;

	protected MapResizeRelocateComponentItem _mapRRCI = null;


	/**
	 * Creates new form AcceptCancelRevertPanel
	 */
	public AcceptCancelPanel( AcceptCancelControllerInterface controller )
	{
		super.init();


		_controller = controller;

		initComponents();

		setBounds( 0, 0, jPanel1.getWidth(), jPanel1.getHeight() );
		
//		initNavigatorButtons();

		_mapRRCI = createResizeRelocateInfo();
	}

	protected String getResourceName( String shortResourceName )
	{
		return( _resourcePath + "/" + shortResourceName );
	}

	public JButton getCancelButton()
	{
		return( jB_cancel );
	}
/*
	protected void initNavigatorButtons()
	{
		Insets insets = new Insets( 1, 1, 1, 1 );

		ViewFunctions.instance().addImageToButtonAccurate( jB_cancel, getResourceName( "erase.png" ), insets );
		ViewFunctions.instance().addImageToButtonAccurate( jB_acceptChanges, getResourceName( "accept.png" ), insets );
		ViewFunctions.instance().addImageToButtonAccurate( jB_revertChanges, getResourceName( "revert.png" ), insets );
	}
*/
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
			mapRRCI.putResizeRelocateComponentItem( jPanel1, ResizeRelocateItem.FILL_WHOLE_PARENT );
			mapRRCI.putResizeRelocateComponentItem( jB_acceptChanges, ResizeRelocateItem.MOVE_LEFT_SIDE_PROPORTIONAL +
																		ResizeRelocateItem.MOVE_RIGHT_SIDE_PROPORTIONAL +
																		ResizeRelocateItem.MOVE_TOP_SIDE_PROPORTIONAL +
																		ResizeRelocateItem.MOVE_BOTTOM_SIDE_PROPORTIONAL );
			mapRRCI.putResizeRelocateComponentItem(jB_cancel, ResizeRelocateItem.MOVE_LEFT_SIDE_PROPORTIONAL +
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

	public void setAcceptButtonEnabled( boolean value )
	{
		jB_acceptChanges.setEnabled( value );
	}

	public void setEraseButtonEnabled( boolean value )
	{
		jB_cancel.setEnabled( value );
	}

	public void setCompoundNameForAcceptButton( String name )
	{
		this.jB_acceptChanges.setName( name );
	}

	public void setCompoundNameForCancelButton( String name )
	{
		this.jB_cancel.setName( name );
	}

	public void setNameForAcceptButton( String name )
	{
		ViewFunctions.instance().setNameForComponent( jB_acceptChanges, name );
	}

	public void setNameForCancelButton( String name )
	{
		ViewFunctions.instance().setNameForComponent( jB_cancel, name );
	}

	public String getCompoundNameForAcceptButton()
	{
		return( jB_acceptChanges.getName() );
	}

	public String getCompoundNameForCancelButton()
	{
		return( jB_cancel.getName() );
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
        jB_cancel = new javax.swing.JButton();
        jB_acceptChanges = new javax.swing.JButton();

        setMinimumSize(new java.awt.Dimension(90, 50));
        setPreferredSize(new java.awt.Dimension(90, 50));
        setLayout(null);

        jPanel1.setLayout(null);

        jB_cancel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jB_cancel.setMaximumSize(new java.awt.Dimension(30, 30));
        jB_cancel.setMinimumSize(new java.awt.Dimension(30, 30));
        jB_cancel.setName("name=jB_cancel,icon=com/frojasg1/generic/resources/acceptcancel/cancel.png"); // NOI18N
        jB_cancel.setPreferredSize(new java.awt.Dimension(30, 30));
        jB_cancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jB_cancelActionPerformed(evt);
            }
        });
        jPanel1.add(jB_cancel);
        jB_cancel.setBounds(10, 10, 30, 30);

        jB_acceptChanges.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jB_acceptChanges.setMaximumSize(new java.awt.Dimension(30, 30));
        jB_acceptChanges.setMinimumSize(new java.awt.Dimension(30, 30));
        jB_acceptChanges.setName("name=jB_acceptChanges,icon=com/frojasg1/generic/resources/acceptcancel/accept.png"); // NOI18N
        jB_acceptChanges.setPreferredSize(new java.awt.Dimension(30, 30));
        jB_acceptChanges.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jB_acceptChangesActionPerformed(evt);
            }
        });
        jPanel1.add(jB_acceptChanges);
        jB_acceptChanges.setBounds(50, 10, 30, 30);

        add(jPanel1);
        jPanel1.setBounds(0, 0, 90, 50);
    }// </editor-fold>//GEN-END:initComponents

    private void jB_cancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jB_cancelActionPerformed

		cancel( this );

    }//GEN-LAST:event_jB_cancelActionPerformed

    private void jB_acceptChangesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jB_acceptChangesActionPerformed

		accept( this );

    }//GEN-LAST:event_jB_acceptChangesActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jB_acceptChanges;
    private javax.swing.JButton jB_cancel;
    private javax.swing.JPanel jPanel1;
    // End of variables declaration//GEN-END:variables

/*
	@Override
	public void doTasksAfterResizingComponent()
	{
//		initNavigatorButtons();		// to resize the images.
	}
*/
	@Override
	public void accept(InformerInterface panel)
	{
		if( _controller != null )
			_controller.accept( this );
	}

	@Override
	public void cancel(InformerInterface panel)
	{
		if( _controller != null )
			_controller.cancel( this );
	}

	@Override
	public void setComponentMapper(ComponentMapper mapper)
	{
		jB_acceptChanges = mapper.mapComponent(jB_acceptChanges);
		jB_cancel = mapper.mapComponent(jB_cancel);
		jPanel1 = mapper.mapComponent(jPanel1);

		super.setComponentMapper(mapper);
	}
}
