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
	private boolean isAnd;
	private boolean isOr;

	private StringBuilder builder;

	public Where() {
		builder = new StringBuilder();
	}

	/**
	 * eg: name op value
	 */
	public Where(String columnName, String op, Object value) {
		this();
		builder.append(columnName);
		builder.append(op);
		builder.append(value);
	}

	/**
	 * eg: name op value
	 */
	public Where set(String columnName, String op, Object value) {
		builder.delete(0, builder.length());
		builder.append(columnName);
		builder.append(op);
		builder.append(value);
		return this;
	}

	/**
	 * eg: xxx and name op value
	 */
	public Where and(String columnName, String op, Object value) {
		if (builder.length() > 0) {
			if (!isAnd && isOr) {
				builder.insert(0, '(');
				builder.append(')');
			}
			builder.append(" and ");
		}
		builder.append(columnName);
		builder.append(op);
		builder.append(value);
		isAnd = true;
		isOr = false;
		return this;
	}

	/**
	 * eg: xxx and (row)
	 */
	public Where and(String row) {
		if (builder.length() > 0) {
			if (!isAnd && isOr) {
				builder.insert(0, '(');
				builder.append(')');
			}
			builder.append(" and ");
		}
		builder.append("(");
		builder.append(row);
		builder.append(")");
		isAnd = true;
		isOr = false;
		return this;
	}

	/**
	 * eg: xxx or name op value
	 */
	public Where or(String columnName, String op, Object value) {
		if (builder.length() > 0) {
			if (isAnd && !isOr) {
				builder.insert(0, '(');
				builder.append(')');
			}
			builder.append(" or ");
		}
		builder.append(columnName);
		builder.append(op);
		builder.append(value);
		isAnd = false;
		isOr = true;
		return this;
	}

	/**
	 * eg: xxx or (row)
	 */
	public Where or(String row) {
		if (builder.length() > 0) {
			if (!isAnd && isOr) {
				builder.insert(0, '(');
				builder.append(')');
			}
			builder.append(" or ");
		}
		builder.append("(");
		builder.append(row);
		builder.append(")");
		isAnd = false;
		isOr = true;
		return this;
	}

	/**
	 * eg: xxx and (firstName firstOp firstValue or secondName secondOp secondValue)
	 */
	public Where andOr(String firstColumnName, String firstOp, Object firstValue, String secondColumnName, String secondOp, Object secondValue) {
		if (builder.length() > 0) {
			if (!isAnd && isOr) {
				builder.insert(0, '(');
				builder.append(")");
			}
			builder.append(" and ");
		}
		builder.append("(");
		builder.append(firstColumnName);
		builder.append(firstOp);
		builder.append(firstValue);
		builder.append(" or ");
		builder.append(firstColumnName);
		builder.append(firstOp);
		builder.append(firstValue);
		builder.append(")");
		isAnd = true;
		isOr = false;
		return this;
	}

	/**
	 * eg: xxx or (firstName firstOp firstValue and secondName secondOp secondValue)
	 */
	public Where orAnd(String firstColumnName, String firstOp, Object firstValue, String secondColumnName, String secondOp, Object secondValue) {
		if (builder.length() > 0) {
			if (isAnd && isOr) {
				builder.insert(0, '(');
				builder.append(")");
			}
			builder.append(" or ");
		}
		builder.append("(");
		builder.append(firstColumnName);
		builder.append(firstOp);
		builder.append(firstValue);
		builder.append(" and ");
		builder.append(firstColumnName);
		builder.append(firstOp);
		builder.append(firstValue);
		builder.append(")");
		isAnd = false;
		isOr = true;
		return this;
	}

	public String get() {
		return builder.toString();
	}

	@Override
	public String toString() {
		return builder.toString();
	}
}
