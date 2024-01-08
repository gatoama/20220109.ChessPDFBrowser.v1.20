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
package com.frojasg1.general.lib3d.factory.impl;

import com.frojasg1.general.lib3d.components.api.about.animation.torus.TorusAnimationInitContext;
import com.frojasg1.general.lib3d.components.Canvas3dJPanel;
import com.frojasg1.general.lib3d.factory.Factory3dApi;
import com.frojasg1.general.lib3d.scenarios.Scenario3dBase;
import com.frojasg1.general.lib3d.scenarios.api.Scenario3dApi;
import com.frojasg1.general.lib3d.scenarios.impl.TorusScenario;
import java.awt.Color;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class Factory3dImpl implements Factory3dApi
{

	@Override
	public TorusScenario createTorus(TorusAnimationInitContext initContext) {
		TorusScenario result = new TorusScenario();
		result.init( initContext );

		return( result );
	}

	@Override
	public Canvas3dJPanel createCanvas3dJPanel(Scenario3dApi scenario) {
		Canvas3dJPanel result = null;

		if( scenario instanceof Scenario3dBase )
		{
			result = new Canvas3dJPanel();

			result.init( (Scenario3dBase) scenario );
		}
		else
		{
			throw( new IllegalArgumentException( "scenario was not an instance of Scenario3dBase. cannot create Canvas3dJPanel" ) );
		}

		return( result );
	}
}
