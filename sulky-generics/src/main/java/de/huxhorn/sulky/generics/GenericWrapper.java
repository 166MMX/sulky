/*
 * sulky-modules - several general-purpose modules.
 * Copyright (C) 2007-2010 Joern Huxhorn
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/*
 * Copyright 2007-2010 Joern Huxhorn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.huxhorn.sulky.generics;

public class GenericWrapper<T>
	implements Wrapper
{
	private T wrapped;

	public GenericWrapper(T wrapped)
	{
		this.wrapped = wrapped;
	}

	protected T getWrapped()
	{
		return wrapped;
	}

	public <T> T unwrap(Class<T> iface)
		throws IllegalArgumentException
	{
		if(iface.isInstance(wrapped))
		{
			return iface.cast(wrapped);
		}
		if(Wrapper.class.isInstance(wrapped))
		{
			if(Wrapper.class.cast(wrapped).isWrapperFor(iface))
			{
				return Wrapper.class.cast(wrapped).unwrap(iface);
			}
		}
		throw new IllegalArgumentException("This Wrapper does not wrap " + iface.getClass() + "!");
	}

	public boolean isWrapperFor(Class<?> iface)
	{
		if(iface.isInstance(wrapped))
		{
			return true;
		}
		if(Wrapper.class.isInstance(wrapped))
		{
			return Wrapper.class.cast(wrapped).isWrapperFor(iface);
		}
		return false;
	}

	public boolean equals(Object o)
	{
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;

		final GenericWrapper that = (GenericWrapper) o;

		return !(wrapped != null ? !wrapped.equals(that.wrapped) : that.wrapped != null);

	}

	public int hashCode()
	{
		return (wrapped != null ? wrapped.hashCode() : 0);
	}

	public String toString()
	{
		return "wrapper-" + wrapped.getClass().getSimpleName() + "[wrapped=" + wrapped + "]";
	}
}
