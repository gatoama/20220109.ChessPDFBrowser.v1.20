/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.desktop.view.panels;

import com.frojasg1.applications.common.configuration.application.BaseApplicationConfigurationInterface;
import com.frojasg1.general.desktop.generic.view.DesktopViewComponent;
import com.frojasg1.general.desktop.generic.view.imp.DesktopViewFacilitiesImp;
import com.frojasg1.general.desktop.view.FrameworkComponentFunctions;
import com.frojasg1.general.desktop.view.color.ColorInversor;
import java.awt.Color;
import javax.swing.JPanel;
import com.frojasg1.general.desktop.view.color.ColorThemeInvertible;
import com.frojasg1.general.desktop.view.zoom.mapper.ComponentMapper;
import com.frojasg1.general.desktop.view.zoom.mapper.InternallyMappedComponent;
import com.frojasg1.general.desktop.view.zoom.mapper.ContainerOfInternallyMappedComponent;
import com.frojasg1.general.desktop.view.zoom.mapper.ContainerOfInternallyMappedComponentBase;
import com.frojasg1.general.view.ReleaseResourcesable;
import java.util.List;
import javax.swing.JPopupMenu;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public abstract class CustomJPanelBase extends JPanel
									implements ColorThemeInvertible,
												ReleaseResourcesable,
												InternallyMappedComponent,
												DesktopViewComponent,
												ContainerOfInternallyMappedComponent
{
	protected boolean _doNotInvertColors = false;
	protected boolean _alreadyMapped = false;

	protected ContainerOfInternallyMappedComponentBase _containreOfinternallyMappedComponent =
		new ContainerOfInternallyMappedComponentBase();

	protected BaseApplicationConfigurationInterface _appliConf;

	public CustomJPanelBase()
	{
		this(false);
	}

	public CustomJPanelBase(boolean doNotInvertColors )
	{
		this(null, doNotInvertColors);
	}

	public CustomJPanelBase( BaseApplicationConfigurationInterface appliConf, boolean doNotInvertColors )
	{
		super();

		_appliConf = appliConf;
		_doNotInvertColors = doNotInvertColors;
	}

	protected void init()
	{
	}

	protected BaseApplicationConfigurationInterface getAppliConf()
	{
		return( _appliConf );
	}

	protected void invertColorsChild(ColorInversor colorInversor)
	{
		// If children want to change the behaviour, they have to override
	}

	protected void invertSinglePanelColors(ColorInversor colorInversor)
	{
		colorInversor.invertSingleColorsGen(this);
	}

	@Override
	public void invertColors( ColorInversor colorInversor)
	{
		if( ! _doNotInvertColors )
		{
			invertSinglePanelColors(colorInversor);
			invertColorsChild(colorInversor);
		}
	}

	@Override
	public void setBackground( Color bc )
	{
		super.setBackground(bc);
	}

	protected ColorInversor getColorInversor()
	{
		return( FrameworkComponentFunctions.instance().getColorInversor(this) );
	}

	protected boolean isDarkMode()
	{
		return( FrameworkComponentFunctions.instance().isDarkMode(this) );
	}

	protected boolean wasLatestModeDark()
	{
		return( FrameworkComponentFunctions.instance().wasLatestModeDark(this) );
	}

	@Override
	public void setComponentMapper(ComponentMapper compMapper)
	{
		for( InternallyMappedComponent imc: getInternallyMappedComponentListCopy() )
			imc.setComponentMapper(compMapper);

		_alreadyMapped = true;
	}

	@Override
	public boolean hasBeenAlreadyMapped()
	{
		return( _alreadyMapped );
	}

	@Override
	public void releaseResources()
	{
		// intentionally left blank
	}

	@Override
	public CustomJPanelBase getComponent()
	{
		return( this );
	}

	@Override
	public void setVisible( boolean value )
	{
		super.setVisible(value);
	}

	@Override
	public void requestFocus()
	{
		super.requestFocus();
	}

	@Override
	public boolean isFocusable()
	{
		return( super.isFocusable() );
	}

	@Override
	public boolean hasFocus()
	{
		return( super.hasFocus() );
	}

	@Override
	public DesktopViewComponent getParentViewComponent()
	{
		return( DesktopViewFacilitiesImp.instance().getParentViewComponent( this ) );
	}

	protected List<InternallyMappedComponent> getInternallyMappedComponentListCopy() {
		return( _containreOfinternallyMappedComponent.getInternallyMappedComponentListCopy() );
	}

	@Override
	public void addInternallyMappedComponent( InternallyMappedComponent im )
	{
		_containreOfinternallyMappedComponent.addInternallyMappedComponent(im);
	}

	@Override
	public void addPopupMenu( JPopupMenu jPopupMenu )
	{
		_containreOfinternallyMappedComponent.addPopupMenu(jPopupMenu);
	}

	protected List<JPopupMenu> getJPopupMenuListCopy()
	{
		return( _containreOfinternallyMappedComponent.getJPopupMenuListCopy() );
	}
}
