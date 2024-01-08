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
package com.frojasg1.general.lib3d.figures;

import com.frojasg1.general.lib3d.components.api.about.animation.torus.TorusAnimationInitContext;
import java.awt.Color;
import org.jogamp.java3d.Appearance;
import org.jogamp.java3d.ColoringAttributes;
import org.jogamp.java3d.Geometry;
import org.jogamp.java3d.GeometryArray;
import org.jogamp.java3d.IndexedQuadArray;
import org.jogamp.java3d.LineAttributes;
import org.jogamp.java3d.LineStripArray;
import org.jogamp.java3d.Material;
import org.jogamp.java3d.PolygonAttributes;
import org.jogamp.java3d.Shape3D;
import org.jogamp.java3d.Transform3D;
import org.jogamp.java3d.utils.geometry.GeometryInfo;
import org.jogamp.java3d.utils.geometry.NormalGenerator;
import org.jogamp.vecmath.Color3f;
import org.jogamp.vecmath.Point3d;
import org.jogamp.vecmath.Point3f;




// https://github.com/lemtzas/Java3D-Project-1/blob/master/Project%201/src/objects/Torus.java

/**
 *
 * @author https://github.com/lemtzas/Java3D-Project-1/blob/master/Project%201/src/objects/Torus.java
 */
public class Torus extends Shape3D {
	public Torus(TorusAnimationInitContext initContext, boolean isSolid)
	{
		if( isSolid )
		{
			createSolid(initContext);
		}
		else
		{
			createLineShape(initContext);
		}
	}

	protected void createSolid(TorusAnimationInitContext initContext)
	{
		float majorRadius = initContext.getMajorRadius();
		float minorRadius = initContext.getMinorRadius();
		int majorSamples = initContext.getMajorSamples();
		int minorSamples = initContext.getMinorSamples();
		Color color = initContext.getColor();

		setGeometry(createGeometry(majorRadius, minorRadius, majorSamples, minorSamples));
		
		Appearance meshApp = new Appearance();
		Material meshMat = new Material();
		meshMat.setDiffuseColor(color.getRed()/255f, color.getGreen()/255f, color.getBlue()/255f );
		meshApp.setMaterial(meshMat);
		meshApp.setColoringAttributes(new ColoringAttributes(0f, 0f, 0f, ColoringAttributes.SHADE_GOURAUD));
		setAppearance(meshApp);
	}

	protected Point3f[] getVertices( float majorRadius, float minorRadius, int majorSamples, int minorSamples )
	{
		Point3f[] vertices = new Point3f[majorSamples * minorSamples];
		for (int i = 0; i < minorSamples; i++)
			vertices[i] = new Point3f((float)Math.cos(i * 2 * Math.PI / minorSamples) * minorRadius + majorRadius, (float)Math.sin(i * 2 * Math.PI / minorSamples) * minorRadius, 0);

		for (int i = 1; i < majorSamples; i++) {
			Transform3D t3d = new Transform3D();
			t3d.rotY(i * 2 * Math.PI / majorSamples);
			for (int j = 0; j < minorSamples; j++) {
				vertices[i * minorSamples + j] = new Point3f();
				t3d.transform(vertices[j], vertices[i * minorSamples + j]);
			}
		}

		return( vertices );
	}

	protected Geometry createGeometry(float majorRadius, float minorRadius, int majorSamples, int minorSamples) {
		IndexedQuadArray geometry = new IndexedQuadArray(majorSamples * minorSamples, GeometryArray.COORDINATES, 4 * majorSamples * minorSamples);

		Point3f[] vertices = getVertices( majorRadius, minorRadius, majorSamples, minorSamples );

		int[] quadIndices = new int[4 * majorSamples * minorSamples];
		for (int i = 0; i < majorSamples; i++)
			for (int j = 0; j < minorSamples; j++)
			{
				quadIndices[4 * (i * minorSamples + j)] = i * minorSamples + j;
				quadIndices[4 * (i * minorSamples + j) + 1] = (i + 1) % majorSamples * minorSamples + j;
				quadIndices[4 * (i * minorSamples + j) + 2] = (i + 1) % majorSamples * minorSamples + (j + 1) % minorSamples;
				quadIndices[4 * (i * minorSamples + j) + 3] = i * minorSamples + (j + 1) % minorSamples;
			}

		geometry.setCoordinates(0, vertices);
		geometry.setCoordinateIndices(0, quadIndices);

		// Utility code to automatically generate normals.
		GeometryInfo gInfo = new GeometryInfo(geometry);
		new NormalGenerator().generateNormals(gInfo);
		return gInfo.getIndexedGeometryArray();
	}

	protected int[] getStripLengths( int majorSamples, int minorSamples )
	{
		int[] result = new int[majorSamples + minorSamples ];

		for( int ii=0; ii<majorSamples; ii++ )
			result[ii] = minorSamples+1;

		for( int ii=0; ii< minorSamples; ii++ )
			result[ii+majorSamples] = majorSamples+1;

		return( result );
	}

	// http://forum.jogamp.org/file/n4037894/BasicBox.java
	public void createLineShape (TorusAnimationInitContext initContext) {
		float majorRadius = initContext.getMajorRadius();
		float minorRadius = initContext.getMinorRadius();
		int majorSamples = initContext.getMajorSamples();
		int minorSamples = initContext.getMinorSamples();
		Color color = initContext.getColor();

		Appearance app = createLineAppearance(color, 2.0);
		int stripLengths[] = getStripLengths( majorSamples, minorSamples );
//		int stripLengths[] = new int[] { 2 };

		LineStripArray l = new LineStripArray(majorSamples*(minorSamples+1)+minorSamples*(majorSamples+1),
												GeometryArray.COORDINATES, stripLengths);

//		set(l, 0, new Point3f(-1, -1, -1 ) );
//		set(l, 0, new Point3f( 1, 1, 1 ) );

		Point3f[] vertices = getVertices( majorRadius, minorRadius, majorSamples, minorSamples );

		if( ( majorSamples > 0 ) && ( minorSamples > 0 ) )
		{
			int startOfSecondBlock = (minorSamples+1)*majorSamples;
			for (int ii = 0; ii <= majorSamples; ii++)
			{
				int iiMod = ii%majorSamples;
				for (int jj = 0; jj <= minorSamples; jj++)
				{
					int jjMod = jj%minorSamples;

					// little circles
					set( l, jj + iiMod*(minorSamples+1),
						vertices[jjMod + minorSamples*iiMod]);

					// large circles
					set( l, startOfSecondBlock + ii + jjMod*(majorSamples+1),
						vertices[iiMod * minorSamples + jjMod]);

				}
			}
		}

		setAppearance( app );
		setGeometry( l );
//		return new Shape3D(l, app);
	}

	public Appearance createLineAppearance (Color color, double lineWidth) {
		Appearance app = new Appearance();
		ColoringAttributes colorAttr = new ColoringAttributes();
		colorAttr.setColor(new Color3f(color));
		app.setColoringAttributes(colorAttr);
		PolygonAttributes polyAttr = new PolygonAttributes();
		polyAttr.setCullFace(PolygonAttributes.CULL_NONE);
		app.setPolygonAttributes(polyAttr);
		LineAttributes lineAttr = new LineAttributes();
		lineAttr.setLineWidth((float)lineWidth);
		lineAttr.setLineAntialiasingEnable(true);
		app.setLineAttributes(lineAttr);
		return app;
	}

	public void set (LineStripArray l, int i, Point3d a) {
		l.setCoordinate(i, a);
	}

	public void set (LineStripArray l, int i, Point3f a) {
		l.setCoordinate(i, a);
	}
}
