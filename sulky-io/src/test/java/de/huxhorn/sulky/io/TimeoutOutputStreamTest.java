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

package de.huxhorn.sulky.io;

import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;
import org.easymock.IAnswer;
import static org.easymock.EasyMock.*;
import org.junit.Test;

import java.io.IOException;
import java.io.OutputStream;

public class TimeoutOutputStreamTest
{

	@Test
	public void normalUse()
		throws IOException
	{
		byte[] bytes = new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9};
		OutputStream mockStream = createStrictMock(OutputStream.class);
		mockStream.write(eq(bytes));
		mockStream.flush();
		mockStream.write(eq(17));
		mockStream.write(eq(bytes), eq(0), eq(5));
		mockStream.close();

		replay(mockStream);

		TimeoutOutputStream instance = new TimeoutOutputStream(mockStream, 1000);
		assertTrue("Stream is already closed!", !instance.isClosed());
		assertTrue("Watchdog Thread is not running!", instance.isWatchdogThreadRunning());
		instance.write(bytes);
		instance.flush();
		instance.write(17);
		instance.write(bytes, 0, 5);

		assertTrue("Stream is already closed!", !instance.isClosed());
		assertTrue("Watchdog Thread is not running!", instance.isWatchdogThreadRunning());

		instance.close();
		waitForSomeTime();
		verify(mockStream);
		assertTrue("Stream is not closed!", instance.isClosed());
		assertTrue("Watchdog Thread is still running!", !instance.isWatchdogThreadRunning());
	}

	@Test
	public void exceptionInWriteByte()
		throws IOException
	{
		OutputStream mockStream = createStrictMock(OutputStream.class);
		mockStream.write(17);
		//noinspection ThrowableInstanceNeverThrown
		expectLastCall().andThrow(new IOException("Some exception"));

		mockStream.close();

		replay(mockStream);

		TimeoutOutputStream instance = new TimeoutOutputStream(mockStream, 1000);
		assertTrue("Stream is already closed!", !instance.isClosed());
		assertTrue("Watchdog Thread is not running!", instance.isWatchdogThreadRunning());
		try
		{
			instance.write(17);
			fail("Exception should have been thrown!");
		}
		catch(IOException ex)
		{
			// expected
		}
		waitForSomeTime();
		verify(mockStream);
		assertTrue("Stream is not closed!", instance.isClosed());
		assertTrue("Watchdog Thread is still running!", !instance.isWatchdogThreadRunning());
	}


	@Test
	public void exceptionInWriteByteArray()
		throws IOException
	{
		byte[] bytes = new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9};
		OutputStream mockStream = createStrictMock(OutputStream.class);
		mockStream.write(eq(bytes));
		//noinspection ThrowableInstanceNeverThrown
		expectLastCall().andThrow(new IOException("Some exception"));

		mockStream.close();

		replay(mockStream);

		TimeoutOutputStream instance = new TimeoutOutputStream(mockStream, 1000);
		assertTrue("Stream is already closed!", !instance.isClosed());
		assertTrue("Watchdog Thread is not running!", instance.isWatchdogThreadRunning());
		try
		{
			instance.write(bytes);
			fail("Exception should have been thrown!");
		}
		catch(IOException ex)
		{
			// expected
		}
		waitForSomeTime();
		verify(mockStream);
		assertTrue("Stream is not closed!", instance.isClosed());
		assertTrue("Watchdog Thread is still running!", !instance.isWatchdogThreadRunning());
	}

	@Test
	public void exceptionInWriteByteArrayOffset()
		throws IOException
	{
		byte[] bytes = new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9};
		OutputStream mockStream = createStrictMock(OutputStream.class);
		mockStream.write(eq(bytes), eq(0), eq(5));
		//noinspection ThrowableInstanceNeverThrown
		expectLastCall().andThrow(new IOException("Some exception"));

		mockStream.close();

		replay(mockStream);

		TimeoutOutputStream instance = new TimeoutOutputStream(mockStream, 1000);
		assertTrue("Stream is already closed!", !instance.isClosed());
		assertTrue("Watchdog Thread is not running!", instance.isWatchdogThreadRunning());
		try
		{
			instance.write(bytes, 0, 5);
			fail("Exception should have been thrown!");
		}
		catch(IOException ex)
		{
			// expected
		}
		waitForSomeTime();
		verify(mockStream);
		assertTrue("Stream is not closed!", instance.isClosed());
		assertTrue("Watchdog Thread is still running!", !instance.isWatchdogThreadRunning());
	}

	@Test
	public void exceptionInFlush()
		throws IOException
	{
		OutputStream mockStream = createStrictMock(OutputStream.class);
		mockStream.flush();
		//noinspection ThrowableInstanceNeverThrown
		expectLastCall().andThrow(new IOException("Some exception"));

		mockStream.close();

		replay(mockStream);

		TimeoutOutputStream instance = new TimeoutOutputStream(mockStream, 1000);
		assertTrue("Stream is already closed!", !instance.isClosed());
		assertTrue("Watchdog Thread is not running!", instance.isWatchdogThreadRunning());
		try
		{
			instance.flush();
			fail("Exception should have been thrown!");
		}
		catch(IOException ex)
		{
			// expected
		}
		waitForSomeTime();
		verify(mockStream);
		assertTrue("Stream is not closed!", instance.isClosed());
		assertTrue("Watchdog Thread is still running!", !instance.isWatchdogThreadRunning());
	}

	@Test
	public void exceptionInClose()
		throws IOException
	{
		OutputStream mockStream = createStrictMock(OutputStream.class);
		mockStream.close();
		//noinspection ThrowableInstanceNeverThrown
		expectLastCall().andThrow(new IOException("Some exception"));

		replay(mockStream);

		TimeoutOutputStream instance = new TimeoutOutputStream(mockStream, 1000);
		assertTrue("Stream is already closed!", !instance.isClosed());
		assertTrue("Watchdog Thread is not running!", instance.isWatchdogThreadRunning());
		try
		{
			instance.close();
			fail("Exception should have been thrown!");
		}
		catch(IOException ex)
		{
			// expected
		}
		waitForSomeTime();
		verify(mockStream);
		assertTrue("Stream is not closed!", instance.isClosed());
		assertTrue("Watchdog Thread is still running!", !instance.isWatchdogThreadRunning());
	}


	@Test
	public void runtimeExceptionInWriteByte()
		throws IOException
	{
		OutputStream mockStream = createStrictMock(OutputStream.class);
		mockStream.write(17);
		//noinspection ThrowableInstanceNeverThrown
		expectLastCall().andThrow(new RuntimeException("Some exception"));

		mockStream.close();

		replay(mockStream);

		TimeoutOutputStream instance = new TimeoutOutputStream(mockStream, 1000);
		assertTrue("Stream is already closed!", !instance.isClosed());
		assertTrue("Watchdog Thread is not running!", instance.isWatchdogThreadRunning());
		try
		{
			instance.write(17);
			fail("Exception should have been thrown!");
		}
		catch(RuntimeException ex)
		{
			// expected
		}
		waitForSomeTime();
		verify(mockStream);
		assertTrue("Stream is not closed!", instance.isClosed());
		assertTrue("Watchdog Thread is still running!", !instance.isWatchdogThreadRunning());
	}

	@Test
	public void runtimeExceptionInWriteByteArray()
		throws IOException
	{
		byte[] bytes = new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9};
		OutputStream mockStream = createStrictMock(OutputStream.class);
		mockStream.write(eq(bytes));
		//noinspection ThrowableInstanceNeverThrown
		expectLastCall().andThrow(new RuntimeException("Some exception"));

		mockStream.close();

		replay(mockStream);

		TimeoutOutputStream instance = new TimeoutOutputStream(mockStream, 1000);
		assertTrue("Stream is already closed!", !instance.isClosed());
		assertTrue("Watchdog Thread is not running!", instance.isWatchdogThreadRunning());
		try
		{
			instance.write(bytes);
			fail("Exception should have been thrown!");
		}
		catch(RuntimeException ex)
		{
			// expected
		}
		waitForSomeTime();
		verify(mockStream);
		assertTrue("Stream is not closed!", instance.isClosed());
		assertTrue("Watchdog Thread is still running!", !instance.isWatchdogThreadRunning());
	}

	@Test
	public void runtimeExceptionInWriteByteArrayOffset()
		throws IOException
	{
		byte[] bytes = new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9};
		OutputStream mockStream = createStrictMock(OutputStream.class);
		mockStream.write(eq(bytes), eq(0), eq(5));
		//noinspection ThrowableInstanceNeverThrown
		expectLastCall().andThrow(new RuntimeException("Some exception"));

		mockStream.close();

		replay(mockStream);

		TimeoutOutputStream instance = new TimeoutOutputStream(mockStream, 1000);
		assertTrue("Stream is already closed!", !instance.isClosed());
		assertTrue("Watchdog Thread is not running!", instance.isWatchdogThreadRunning());
		try
		{
			instance.write(bytes, 0, 5);
			fail("Exception should have been thrown!");
		}
		catch(RuntimeException ex)
		{
			// expected
		}
		waitForSomeTime();
		verify(mockStream);
		assertTrue("Stream is not closed!", instance.isClosed());
		assertTrue("Watchdog Thread is still running!", !instance.isWatchdogThreadRunning());
	}

	@Test
	public void runtimeExceptionInFlush()
		throws IOException
	{
		OutputStream mockStream = createStrictMock(OutputStream.class);
		mockStream.flush();
		//noinspection ThrowableInstanceNeverThrown
		expectLastCall().andThrow(new RuntimeException("Some exception"));

		mockStream.close();

		replay(mockStream);

		TimeoutOutputStream instance = new TimeoutOutputStream(mockStream, 1000);
		assertTrue("Stream is already closed!", !instance.isClosed());
		assertTrue("Watchdog Thread is not running!", instance.isWatchdogThreadRunning());
		try
		{
			instance.flush();
			fail("Exception should have been thrown!");
		}
		catch(RuntimeException ex)
		{
			// expected
		}
		waitForSomeTime();
		verify(mockStream);
		assertTrue("Stream is not closed!", instance.isClosed());
		assertTrue("Watchdog Thread is still running!", !instance.isWatchdogThreadRunning());
	}

	@Test
	public void runtimeExceptionInClose()
		throws IOException
	{
		OutputStream mockStream = createStrictMock(OutputStream.class);
		mockStream.close();
		//noinspection ThrowableInstanceNeverThrown
		expectLastCall().andThrow(new RuntimeException("Some exception"));

		replay(mockStream);

		TimeoutOutputStream instance = new TimeoutOutputStream(mockStream, 1000);
		assertTrue("Stream is already closed!", !instance.isClosed());
		assertTrue("Watchdog Thread is not running!", instance.isWatchdogThreadRunning());
		try
		{
			instance.close();
			fail("Exception should have been thrown!");
		}
		catch(RuntimeException ex)
		{
			// expected
		}
		waitForSomeTime();
		verify(mockStream);
		assertTrue("Stream is not closed!", instance.isClosed());
		assertTrue("Watchdog Thread is still running!", !instance.isWatchdogThreadRunning());
	}


	@Test
	public void timeoutInWriteByte()
		throws IOException
	{
		OutputStream mockStream = createStrictMock(OutputStream.class);
		makeThreadSafe(mockStream, true);
		mockStream.write(eq(17));
		expectLastCall().andAnswer(new IAnswer<Object>()
		{
			public Object answer()
				throws Throwable
			{
				Thread.sleep(300);
				return null;
			}
		});
		mockStream.close();

		replay(mockStream);

		TimeoutOutputStream instance = new TimeoutOutputStream(mockStream, 100);
		assertTrue("Stream is already closed!", !instance.isClosed());
		assertTrue("Watchdog Thread is not running!", instance.isWatchdogThreadRunning());

		instance.write(17); // would throw exception in case of a real output stream.
		waitForSomeTime();
		verify(mockStream);
		assertTrue("Stream is not closed!", instance.isClosed());
		assertTrue("Watchdog Thread is still running!", !instance.isWatchdogThreadRunning());
	}

	@Test
	public void timeoutInWriteByteArray()
		throws IOException
	{
		byte[] bytes = new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9};
		OutputStream mockStream = createStrictMock(OutputStream.class);
		makeThreadSafe(mockStream, true);
		mockStream.write(eq(bytes));
		expectLastCall().andAnswer(new IAnswer<Object>()
		{
			public Object answer()
				throws Throwable
			{
				Thread.sleep(300);
				return null;
			}
		});
		mockStream.close();

		replay(mockStream);

		TimeoutOutputStream instance = new TimeoutOutputStream(mockStream, 100);
		assertTrue("Stream is already closed!", !instance.isClosed());
		assertTrue("Watchdog Thread is not running!", instance.isWatchdogThreadRunning());

		instance.write(bytes); // would throw exception in case of a real output stream.
		waitForSomeTime();
		verify(mockStream);
		assertTrue("Stream is not closed!", instance.isClosed());
		assertTrue("Watchdog Thread is still running!", !instance.isWatchdogThreadRunning());
	}

	@Test
	public void timeoutInWriteByteArrayOffset()
		throws IOException
	{
		byte[] bytes = new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9};
		OutputStream mockStream = createStrictMock(OutputStream.class);
		makeThreadSafe(mockStream, true);
		mockStream.write(eq(bytes), eq(0), eq(5));
		expectLastCall().andAnswer(new IAnswer<Object>()
		{
			public Object answer()
				throws Throwable
			{
				Thread.sleep(300);
				return null;
			}
		});
		mockStream.close();

		replay(mockStream);

		TimeoutOutputStream instance = new TimeoutOutputStream(mockStream, 100);
		assertTrue("Stream is already closed!", !instance.isClosed());
		assertTrue("Watchdog Thread is not running!", instance.isWatchdogThreadRunning());

		instance.write(bytes, 0, 5); // would throw exception in case of a real output stream.
		waitForSomeTime();
		verify(mockStream);
		assertTrue("Stream is not closed!", instance.isClosed());
		assertTrue("Watchdog Thread is still running!", !instance.isWatchdogThreadRunning());
	}

	@Test
	public void timeoutInFlush()
		throws IOException
	{
		OutputStream mockStream = createStrictMock(OutputStream.class);
		makeThreadSafe(mockStream, true);
		mockStream.flush();
		expectLastCall().andAnswer(new IAnswer<Object>()
		{
			public Object answer()
				throws Throwable
			{
				Thread.sleep(300);
				return null;
			}
		});
		mockStream.close();

		replay(mockStream);

		TimeoutOutputStream instance = new TimeoutOutputStream(mockStream, 100);
		assertTrue("Stream is already closed!", !instance.isClosed());
		assertTrue("Watchdog Thread is not running!", instance.isWatchdogThreadRunning());

		instance.flush(); // would throw exception in case of a real output stream.
		waitForSomeTime();
		verify(mockStream);
		assertTrue("Stream is not closed!", instance.isClosed());
		assertTrue("Watchdog Thread is still running!", !instance.isWatchdogThreadRunning());
	}

	@Test
	public void timeoutInClose()
		throws IOException
	{
		OutputStream mockStream = createStrictMock(OutputStream.class);
		makeThreadSafe(mockStream, true);
		mockStream.close();
		expectLastCall().andAnswer(new IAnswer<Object>()
		{
			public Object answer()
				throws Throwable
			{
				Thread.sleep(300);
				return null;
			}
		});

		replay(mockStream);

		TimeoutOutputStream instance = new TimeoutOutputStream(mockStream, 100);
		assertTrue("Stream is already closed!", !instance.isClosed());
		assertTrue("Watchdog Thread is not running!", instance.isWatchdogThreadRunning());

		instance.close(); // would throw exception in case of a real output stream.
		waitForSomeTime();
		verify(mockStream);
		assertTrue("Stream is not closed!", instance.isClosed());
		assertTrue("Watchdog Thread is still running!", !instance.isWatchdogThreadRunning());
	}

	private void waitForSomeTime()
	{
		try
		{
			Thread.sleep(100);
		}
		catch(InterruptedException e)
		{
			// ignore
		}
	}

}
