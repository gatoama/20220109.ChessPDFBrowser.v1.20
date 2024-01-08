/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.desktop.view.panels.volume.equalizer;

import com.frojasg1.applications.common.components.resizecomp.MapResizeRelocateComponentItem;
import com.frojasg1.applications.common.components.resizecomp.ResizeRelocateItem;
import com.frojasg1.general.desktop.view.panels.volume.ChangeVolumeEvent;
import com.frojasg1.general.desktop.view.panels.volume.VolumeJPanel;
import com.frojasg1.general.desktop.view.zoom.mapper.ComposedComponent;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.lang.ref.WeakReference;
import java.util.function.Consumer;
import javax.swing.JPanel;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class EqualizerJPanel extends com.frojasg1.general.desktop.view.panels.CustomJPanelBase
		implements ComposedComponent
{
	protected MapResizeRelocateComponentItem _mapRRCI = null;

	protected EqualizerBandObject[] _bandObjects = null;

	protected Consumer<ChangeEqualizerBandLevelEvent> _listener;

	protected ChangeEqualizerBandLevelEvent[] _latestValues;

	protected VolumeJPanel[] _volumeJPanels;

	/**
	 * Creates new form EqualizerJPanel
	 */
	public EqualizerJPanel(Consumer<ChangeEqualizerBandLevelEvent> listener,
							ChangeEqualizerBandLevelEvent ... bandsAndValues) {
		super();

		_listener = listener;

		initComponents();

		initOwnComponents(bandsAndValues);

		_mapRRCI = createResizeRelocateInfo();
	}

	protected void initOwnComponents(ChangeEqualizerBandLevelEvent ... bandsAndValues)
	{
		createEqualizerBands( bandsAndValues );
	}

	protected void createEqualizerBands(ChangeEqualizerBandLevelEvent ... bandsAndValues)
	{
		_bandObjects = new EqualizerBandObject[ bandsAndValues.length ];
		_latestValues = new ChangeEqualizerBandLevelEvent[ bandsAndValues.length ];
		_volumeJPanels = new VolumeJPanel[bandsAndValues.length];

		int ii = 0;
		Dimension singleSize = null;
		for( ChangeEqualizerBandLevelEvent bandAndValue: bandsAndValues )
		{
			EqualizerBandIdentifier id = bandAndValue.getEqBandIdentifier();
			EqualizerBandObject ebo = new EqualizerBandObject();
			VolumeJPanel volumeJPanel = new VolumeJPanel( evt -> notifyChangeEqualizerBandLevelListener( id, evt ),
														-20, 0 );
			JPanel container = new JPanel();
			container.setLayout(null);
			container.add( volumeJPanel );
			singleSize = volumeJPanel.getInternalSize();

			volumeJPanel.setBounds(0, 0, (int) singleSize.getWidth(), (int) singleSize.getHeight() );

			jPanel1.add(container);
			container.setBounds(singleSize.width * ii, 0,
								singleSize.width, singleSize.height );

			ebo.setEqualizerBandIdentifier(id);
			ebo.setContainer(container);
			ebo.setVolumeJPanel(volumeJPanel);

			volumeJPanel.setVolumeIndB( bandAndValue.getVolumeEvent().getVolumeIndB() );

			_volumeJPanels[ii] = volumeJPanel;
			_bandObjects[ii++] = ebo;
		}

		if( singleSize != null )
			jPanel1.setBounds( 0, 0, singleSize.width * ii, singleSize.height );

		initBandValues();
	}

	public int getNumBands()
	{
		return( _volumeJPanels.length );
	}

	protected void initBandValues()
	{
		for( EqualizerBandObject ebo: _bandObjects )
			setBandValue( ebo.getEqualizerBandIdentifier(), 0, false );
	}

	@Override
	public MapResizeRelocateComponentItem getResizeRelocateInfo()
	{
		if( _mapRRCI == null )
		{
			_mapRRCI = createResizeRelocateInfo();
		}

		return( _mapRRCI );
	}

	@Override
	public Dimension getInternalSize()
	{
		return( jPanel1.getSize() );
	}

	@Override
	public Rectangle getInternalBounds()
	{
		return( jPanel1.getBounds() );
	}

	protected MapResizeRelocateComponentItem createResizeRelocateInfo()
	{
		MapResizeRelocateComponentItem mapRRCI = new MapResizeRelocateComponentItem();

		try
		{
			boolean postponeInitialization = true;
			mapRRCI.putResizeRelocateComponentItem( this, ResizeRelocateItem.FILL_WHOLE_PARENT, postponeInitialization );
			mapRRCI.putResizeRelocateComponentItem( jPanel1, ResizeRelocateItem.FILL_WHOLE_PARENT );

			for( EqualizerBandObject ebo: _bandObjects )
			{
				mapRRCI.putResizeRelocateComponentItem( ebo.getContainer(), ResizeRelocateItem.MOVE_ALL_SIDES_PROPORTIONAL );
				mapRRCI.putAll( ebo.getVolumeJPanel().getResizeRelocateInfo() );
			}
		}
		catch( Throwable th )
		{
			th.printStackTrace();
		}

		return( mapRRCI );
	}

	public void setBandValue( EqualizerBandIdentifier ebi, int value, boolean isMuted )
	{
		setBandValue( ebi, createChangeVolumeEvent( value, isMuted ) );
	}

	protected ChangeVolumeEvent createChangeVolumeEvent( int value, boolean isMuted )
	{
		return( new ChangeVolumeEvent().setVolumeIndB( value ).setIsMuted( isMuted ) );
	}

	public void setBandValue( EqualizerBandIdentifier ebi, ChangeVolumeEvent cve )
	{
		setBandValue( createChangeEqualizerBandLevelEvent( ebi, cve ) );
	}

	public void setBandValue( ChangeEqualizerBandLevelEvent evt )
	{
		int index = updateLatestBandValue( evt );
		setBandValue( index, evt.getVolumeEvent() );
	}

	public void setBandValue( int index, ChangeVolumeEvent volumeEvt )
	{
		if( index > -1 )
		{
			_latestValues[index].setVolumeEvent( volumeEvt );
			_volumeJPanels[index].setVolumeIndB( volumeEvt.getVolumeIndB() );
		}
	}

	public void setBandValue( int index, int value, boolean isMuted )
	{
		setBandValue( index, createChangeVolumeEvent( value, isMuted ) );
	}

	public int updateLatestBandValue( ChangeEqualizerBandLevelEvent evt )
	{
		int index = getIndex( evt );
		if( index > -1 )
		{
			_volumeJPanels[index].setVolumeIndB( evt.getVolumeEvent().getVolumeIndB() );
			_latestValues[index] = evt;
		}

		return( index );
	}

	protected int getIndex( ChangeEqualizerBandLevelEvent evt )
	{
		int result = -1;
		if( evt != null )
			result = getIndex( evt.getEqBandIdentifier() );

		return( result );
	}

	protected int getIndex( EqualizerBandIdentifier ebi )
	{
		int result = -1;
		if( ebi != null )
		{
			int ii=0;
			for( EqualizerBandObject ebo: _bandObjects )
			{
				if( ebi.equals( ebo.getEqualizerBandIdentifier() ) )
				{
					result = ii;
					break;
				}
				ii++;
			}
		}

		return( result );
	}

	public ChangeEqualizerBandLevelEvent[] getValues()
	{
		return( _latestValues );
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

        setLayout(null);

        jPanel1.setLayout(null);
        add(jPanel1);
        jPanel1.setBounds(0, 0, 400, 260);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    // End of variables declaration//GEN-END:variables


	protected void notifyChangeEqualizerBandLevelListener(EqualizerBandIdentifier bandId,
														ChangeVolumeEvent volumeEvent)
	{
		if( _listener != null )
			_listener.accept( createChangeEqualizerBandLevelEvent(bandId, volumeEvent) );

		setBandValue( bandId, volumeEvent );
	}

	protected ChangeEqualizerBandLevelEvent createChangeEqualizerBandLevelEvent( EqualizerBandIdentifier bandId,
																				ChangeVolumeEvent volumeEvent )
	{
		return( new ChangeEqualizerBandLevelEvent()
						.setEqBandIdentifier(bandId)
						.setVolumeEvent(volumeEvent) );
	}

	protected static class EqualizerBandObject
	{
		protected WeakReference<JPanel> _container;
		protected WeakReference<VolumeJPanel> _volumeJPanel;
		protected EqualizerBandIdentifier _equalizerBandIdentifier;

		protected <CC> WeakReference<CC> createWeakReference( CC obj )
		{
			return( new WeakReference<>(obj) );
		}

		public JPanel getContainer() {
			return _container.get();
		}

		public void setContainer(JPanel _container) {
			this._container = createWeakReference( _container );
		}

		public VolumeJPanel getVolumeJPanel() {
			return _volumeJPanel.get();
		}

		public void setVolumeJPanel(VolumeJPanel _volumeJPanel) {
			this._volumeJPanel = createWeakReference(_volumeJPanel);
		}

		public EqualizerBandIdentifier getEqualizerBandIdentifier() {
			return _equalizerBandIdentifier;
		}

		public void setEqualizerBandIdentifier(EqualizerBandIdentifier _equalizerBandIdentifier) {
			this._equalizerBandIdentifier = _equalizerBandIdentifier;
		}
	}
}
