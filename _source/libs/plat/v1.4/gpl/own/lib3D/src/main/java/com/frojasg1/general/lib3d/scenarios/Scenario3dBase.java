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
package com.frojasg1.general.lib3d.scenarios;

import org.jogamp.java3d.Canvas3D;
import org.jogamp.java3d.Transform3D;
import org.jogamp.java3d.utils.universe.SimpleUniverse;
import org.jogamp.vecmath.Quat4f;
import org.jogamp.vecmath.Vector3f;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public abstract class Scenario3dBase
{
	protected final Vector3f yAxis = new Vector3f(0f, 1f, 0f);

	protected Canvas3D _canvas = null;

	protected SimpleUniverse _universe = null;

	protected Vector3f calculateNormalVector( float fi, float zeta )
	{
		return( new Vector3f( 	(float) (Math.cos(zeta)*Math.sin(fi)),
								(float) Math.sin(zeta),
								(float) (Math.cos(zeta)*Math.cos(fi))
								 ) );
	}

	public SimpleUniverse getSimpleUniverse()
	{
		return( _universe );
	}

	protected SimpleUniverse setSimpleUniverse( SimpleUniverse su )
	{
		_universe = su;
		return( _universe );
	}

	protected SimpleUniverse setSimpleUniverse( )
	{
		return( setSimpleUniverse( createSimpleUniverse() ) );
	}

	protected SimpleUniverse createSimpleUniverse()
	{
		return( createSimpleUniverse( getCanvas3D() ) );
	}

	protected SimpleUniverse createSimpleUniverse( Canvas3D canvas )
	{
		return( new SimpleUniverse(canvas) );
	}

	protected Transform3D get3dRotationTransform( Vector3f direction )
	{
		// Get the normalized axis perpendicular to the direction 
        Vector3f axis = new Vector3f();
        axis.cross(yAxis, direction);
        axis.normalize();

        // When the intended direction is a point on the yAxis, rotate on x
        if (Float.isNaN(axis.x) && Float.isNaN(axis.y) && Float.isNaN(axis.z)) 
        {
            axis.x = 1f;
            axis.y = 0f;
            axis.z = 0f;
        }
        // Compute the quaternion transformations
        final float angleX = yAxis.angle(direction);
        final float a = axis.x * (float) Math.sin(angleX / 2f);
        final float b = axis.y * (float) Math.sin(angleX / 2f);
        final float c = axis.z * (float) Math.sin(angleX / 2f);
        final float d = (float) Math.cos(angleX / 2f);

		Transform3D t3d = new Transform3D();
        Quat4f quat = new Quat4f(a, b, c, d);
        t3d.set(quat);

		return( t3d );
	}

	protected void setCanvas3D( Canvas3D canvas3D )
	{
		_canvas = canvas3D;
	}

	public Canvas3D getCanvas3D()
	{
		return( _canvas );
	}

	// the following functoin must be implemented and invoked after initialization.
	protected abstract void setInitialPosition();
}
