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
package com.frojasg1.chesspdfbrowser.view.chess.edittags;

import com.frojasg1.applications.common.configuration.application.BaseApplicationConfigurationInterface;

/**
 *
 * @author Usuario
 */
public interface FilterTAGconfiguration extends BaseApplicationConfigurationInterface
{
	public boolean getMandatoryFilterSelected();
	public boolean getPlayerFilterSelected();
	public boolean getEventFilterSelected();
	public boolean getOpeningFilterSelected();
	public boolean getTimeAndDateFilterSelected();
	public boolean getTimeControlFilterSelected();
	public boolean getGameConclusionFilterSelected();
	public boolean getMiscellaneousFilterSelected();
	public boolean getAlternativeStartingFilterSelected();

	public String getFilterItemForTAGs( int index );
	
	public boolean getEditTAGwindowAlwaysOnTop();
	
	public void setMandatoryFilterSelected( boolean value );
	public void setPlayerFilterSelected( boolean value );
	public void setEventFilterSelected( boolean value );
	public void setOpeningFilterSelected( boolean value );
	public void setTimeAndDateFilterSelected( boolean value );
	public void setTimeControlFilterSelected( boolean value );
	public void setGameConclusionFilterSelected( boolean value );
	public void setMiscellaneousFilterSelected( boolean value );
	public void setAlternativeStartingFilterSelected( boolean value );

	public void setFilterItemForTAGs( int index, String valueOfFilter );

	public void setEditTagWindowAlwaysOnTop( boolean value );
}
