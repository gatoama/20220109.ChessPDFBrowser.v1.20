/* 
 * Copyright (C) 2021 Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * You may obtain a copy of the License at
 *
 *      http://www.gnu.org/licenses/gpl-3.0.txt
 *
 */
package com.frojasg1.general.lib3d.animations;

import com.frojasg1.general.lib3d.scenarios.Scenario3dBase;
import org.jogamp.java3d.Canvas3D;
import org.jogamp.java3d.utils.universe.SimpleUniverse;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public abstract class AnimationJava3dBase<SC extends Scenario3dBase>
	extends Animation3dBase
	implements AnimationJava3d<SC>
{
	SC _scenario = null;

	@Override
	public SimpleUniverse getUniverse() {
		SimpleUniverse result = null;
		if( _scenario != null )
		{
			result = _scenario.getSimpleUniverse();
		}
		return( result );
	}

	@Override
	public Canvas3D getCanvas3D()
	{
		Canvas3D result = null;
		if( _scenario != null )
		{
			result = _scenario.getCanvas3D();
		}
		return( result );
	}

	@Override
	public void setScenario(SC scenario)
	{
		_scenario = scenario;
	}

	@Override
	public SC getScenario()
	{
		return( _scenario );
	}

}
