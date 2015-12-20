/*
 * Copyright © YOLANDA. All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.yolanda.nohttp.cookie;

/**
 * </br>
 * Created in Dec 19, 2015 4:16:24 PM
 * 
 * @author YOLANDA;
 */
public class Where {

	private StringBuilder builder;

	public Where() {
		builder = new StringBuilder();
	}

	public Where(CharSequence columnName, CharSequence op, Object value) {
		builder = new StringBuilder();
		add(columnName, op, value);
	}

	public final Where set(String row) {
		clear().add(row);
		return this;
	}

	public final Where add(CharSequence row) {
		builder.append(row);
		return this;
	}

	public final Where add(CharSequence columnName, CharSequence op, Object value) {
		builder.append("\"").append(columnName).append("\" ").append(op);
		if (value instanceof Long || value instanceof Integer)
			builder.append(value);
		else
			builder.append(" '").append(value).append("'");
		return this;
	}

	public final Where isNull(CharSequence columnName) {
		builder.append("\"").append(columnName).append("\" ").append("IS ").append("NULL");
		return this;
	}

	public final Where insert(int offset, CharSequence s) {
		builder.insert(offset, s);
		return this;
	}

	public final Where and(CharSequence columnName, CharSequence op, Object value) {
		and();
		add(columnName, op, value);
		return this;
	}

	public final Where and(CharSequence row) {
		and();
		builder.append(row);
		return this;
	}

	private final Where and() {
		if (builder.length() > 0) {
			builder.append(" AND ");
		}
		return this;
	}

	public final Where andNull(CharSequence columnName) {
		and();
		isNull(columnName);
		return this;
	}

	public final Where or(CharSequence columnName, CharSequence op, CharSequence value) {
		or();
		add(columnName, op, value);
		return this;
	}

	public final Where or(CharSequence row) {
		or();
		builder.append(row);
		return this;
	}

	public final Where orNull(CharSequence columnName) {
		or();
		isNull(columnName);
		return this;
	}

	private final Where or() {
		if (builder.length() > 0) {
			builder.append(" OR ");
		}
		return this;
	}

	public final Where clear() {
		builder.delete(0, builder.length());
		return this;
	}

	public final String get() {
		return builder.toString();
	}

	@Override
	public String toString() {
		return builder.toString();
	}
}
