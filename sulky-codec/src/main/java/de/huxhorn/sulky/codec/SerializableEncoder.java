/*
 * sulky-modules - several general-purpose modules.
 * Copyright (C) 2007-2009 Joern Huxhorn
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
package de.huxhorn.sulky.codec;

import org.apache.commons.io.IOUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.zip.GZIPOutputStream;

public class SerializableEncoder<E extends Serializable>
	implements Encoder<E>
{
	private boolean compressing;

	public SerializableEncoder()
	{
		this(false);
	}

	public SerializableEncoder(boolean compressing)
	{
		this.compressing = compressing;
	}

	public boolean isCompressing()
	{
		return compressing;
	}

	public void setCompressing(boolean compressing)
	{
		this.compressing = compressing;
	}

	public byte[] encode(E object)
	{
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream oos = null;
		try
		{
			if(compressing)
			{
				GZIPOutputStream gos = new GZIPOutputStream(bos);
				oos = new ObjectOutputStream(gos);
			}
			else
			{
				oos = new ObjectOutputStream(bos);
			}
			oos.writeObject(object);
			oos.flush();
			oos.close();
			return bos.toByteArray();
		}
		catch(IOException e)
		{
			e.printStackTrace();
			return null;
		}
		finally
		{
			IOUtils.closeQuietly(oos);
			IOUtils.closeQuietly(bos);
		}
	}

	public String toString()
	{
		return "SerializableEncoder[compressing=" + compressing + "]";
	}
}