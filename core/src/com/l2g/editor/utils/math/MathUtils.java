/** Copyright 2014 Robin Stumm (serverkorken@gmail.com, http://dermetfan.net)
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License. */

package com.l2g.editor.utils.math;

import com.badlogic.gdx.utils.FloatArray;

public class MathUtils extends com.l2g.editor.utils.utils.math.MathUtils {

	public static FloatArray clamp(FloatArray values, float min, float max) {
		clamp(values.items, 0, values.size, min, max);
		return values;
	}

	public static FloatArray abs(FloatArray values) {
		abs(values.items, 0, values.size);
		return values;
	}

	public static FloatArray add(FloatArray values, float value) {
		add(values.items, 0, values.size, value);
		return values;
	}

	public static FloatArray sub(FloatArray values, float value) {
		sub(values.items, 0, values.size, value);
		return values;
	}

	public static FloatArray mul(FloatArray values, float factor) {
		mul(values.items, 0, values.size, factor);
		return values;
	}

	public static FloatArray div(FloatArray values, float divisor) {
		div(values.items, 0, values.size, divisor);
		return values;
	}

	public static float sum(FloatArray values) {
		return sum(values.items, 0, values.size);
	}

	public static float amplitude2(FloatArray f) {
		return amplitude2(f.items, 0, f.size);
	}

	public static float max(FloatArray floats) {
		return max(floats.items, 0, floats.size);
	}

	public static float min(FloatArray floats) {
		return min(floats.items, 0, floats.size);
	}

	public static float nearest(float value, float range, FloatArray values) {
		return nearest(value, range, values.items, 0, values.size);
	}

	public static float nearest(float value, FloatArray values) {
		return nearest(value, values.items, 0, values.size);
	}

	public static FloatArray scale(FloatArray values, float min, float max) {
		scale(values.items, 0, values.size, min, max);
		return values;
	}

}
