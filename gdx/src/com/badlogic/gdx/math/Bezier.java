/*******************************************************************************
 * Copyright 2011 See AUTHORS file.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.badlogic.gdx.math;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;

/** Implementation of the Bezier curve.
 * @author Xoppa */
public class Bezier<T extends Vector<T>> implements Path<T> {
	// TODO implement Serializable
	
	/** Simple linear interpolation 
	 * @param out The {@link Vector} to set to the result.
	 * @param t The location (ranging 0..1) on the line.
	 * @param p0 The start point.
	 * @param p1 The end point.
	 * @return The value specified by out for chaining */
	public static <T extends Vector<T>> T linear(final T out, final float t, final T p0, final T p1) {
		return out.set(p0).mul(1f - t).add(p1.tmp().mul(t)); // Could just use lerp...
	}
	
	/** Quadratic Bezier curve 
	 * @param out The {@link Vector} to set to the result.
	 * @param t The location (ranging 0..1) on the curve.
	 * @param p0 The first bezier point.
	 * @param p1 The second bezier point.
	 * @param p2 The third bezier point.
	 * @return The value specified by out for chaining */
	public static <T extends Vector<T>> T quadratic(final T out, final float t, final T p0, final T p1, final T p2) {
		final float dt = 1f - t;
		return out.set(p0).mul(dt*dt).add(p1.tmp().mul(2*dt*t)).add(p2.tmp().mul(t*t));
	}
	
	/** Cubic Bezier curve
	 * @param out The {@link Vector} to set to the result.
	 * @param t The location (ranging 0..1) on the curve.
	 * @param p0 The first bezier point.
	 * @param p1 The second bezier point.
	 * @param p2 The third bezier point.
	 * @param p3 The fourth bezier point.
	 * @return The value specified by out for chaining */
	public static <T extends Vector<T>> T cubic(final T out, final float t, final T p0, final T p1, final T p2, final T p3) {
		final float dt = 1f - t;
		final float dt2 = dt * dt;
		final float t2 = t * t;
		return out.set(p0).mul(dt2*dt).add(p1.tmp().mul(3*dt2*t)).add(p2.tmp().mul(3*dt*t2)).add(p3.tmp().mul(t2*t));
	}
	
	public Array<T> points = new Array<T>();
	
	public Bezier() {	}
	public Bezier(final T... points) {
		set(points);
	}
	public Bezier(final T[] points, final int offset, final int length) {
		set(points, offset, length);
	}
	public Bezier(final Array<T> points, final int offset, final int length) {
		set(points, offset, length);
	}
	
	public Bezier set(final T... points) {
		return set(points, 0, points.length);
	}
	public Bezier set(final T[] points, final int offset, final int length) {
		if (length < 2 || length > 4)
			throw new GdxRuntimeException("Only first, second and third degree Bezier curves are supported.");
		this.points.clear();
		this.points.addAll(points, offset, length);
		return this;
	}
	public Bezier set(final Array<T> points, final int offset, final int length) {
		if (length < 2 || length > 4)
			throw new GdxRuntimeException("Only first, second and third degree Bezier curves are supported.");
		this.points.clear();
		this.points.addAll(points, offset, length);
		return this;
	}
	
	public T valueAt(final T out, final float t) {
		final int n = points.size; 
		if (n == 2)
			linear(out, t, points.get(0), points.get(1));
		else if (n == 3)
			quadratic(out, t, points.get(0), points.get(1), points.get(2));
		else if (n == 4)
			cubic(out, t, points.get(0), points.get(1), points.get(2), points.get(3));
		return out;
	}
	
	public float approximate(final T v) {
		// TODO: make a real approximate method
		T p1 = points.get(0);
		T p2 = points.get(points.size-1);
		T p3 = v;
		float l1 = p1.dst(p2);
		float l2 = p3.dst(p2);
		float l3 = p3.dst(p1);
		float s = (l2*l2 + l1*l1 - l3*l3) / (2*l1);
		return MathUtils.clamp((l1-s)/l1, 0f, 1f);
	}
}
