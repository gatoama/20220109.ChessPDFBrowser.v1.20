/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.desktop.view.panels.evt.map;

import com.frojasg1.general.desktop.view.zoom.mapper.ComponentMapper;
import com.frojasg1.general.desktop.view.zoom.mapper.InternallyMappedComponent;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.AbstractButton;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class ButtonEventEntry<ET> implements InternallyMappedComponent {

	protected ET _eventType;
	protected AbstractButton _button;
	protected String _imageResource;

	protected List<AbstractButton> _associatedButtonsList = new ArrayList<>();

	public ET getEventType() {
		return _eventType;
	}

	public void setEventType(ET _eventType) {
		this._eventType = _eventType;
	}

	public AbstractButton getButton() {
		return _button;
	}

	public void setButton(AbstractButton _button) {
		this._button = _button;
	}

	public String getImageResource() {
		return _imageResource;
	}

	public void setImageResource(String _imageResource) {
		this._imageResource = _imageResource;
	}

	public List<AbstractButton> getAssociatedButtonList()
	{
		return( _associatedButtonsList );
	}

	public void addAssociatedButton( AbstractButton button )
	{
		if( !_associatedButtonsList.contains(button) )
			_associatedButtonsList.add( button );
	}

	@Override
	public void setComponentMapper(ComponentMapper mapper)
	{
		_button = mapper.mapComponent(_button);

		List<AbstractButton> newAssociatedList = getAssociatedButtonList().stream()
			.map( mapper::mapComponent ).collect( Collectors.toList() );

		_associatedButtonsList = newAssociatedList;
	}

	@Override
	public boolean hasBeenAlreadyMapped() {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}
}
