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

package de.huxhorn.sulky.groovy;

import groovy.lang.GroovyClassLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.Serializable;

/**
 * This class helps creating an instance from a given Groovy file.
 * It supports automatic refresh if the file is changed.
 */
public class GroovyInstance
	implements Serializable
{
	private static final long serialVersionUID = -1275072482996745126L;

	private final Logger logger = LoggerFactory.getLogger(GroovyInstance.class);

	private static final int DEFAULT_REFRESH_INTERVAL = 2000;

	private String groovyFileName;
	private int refreshInterval = DEFAULT_REFRESH_INTERVAL;
	private transient Class instanceClass;
	private transient Object instance;
	private transient long lastRefresh;
	private transient long previousFileTimestamp;
	private transient String errorMessage;
	private transient Throwable errorCause;

	public String getGroovyFileName()
	{
		return groovyFileName;
	}

	public int getRefreshInterval()
	{
		return refreshInterval;
	}

	public void setRefreshInterval(int refreshInterval)
	{
		if(refreshInterval < 0)
		{
			throw new IllegalArgumentException("refreshInterval must not be negative!");
		}
		this.refreshInterval = refreshInterval;
	}

	public void setGroovyFileName(String groovyFileName)
	{
		if(groovyFileName == null || !groovyFileName.equals(this.groovyFileName))
		{
			this.groovyFileName = groovyFileName;
			// force reinitialization
			instanceClass = null;
			instance = null;
		}
	}

	public String getErrorMessage()
	{
		return errorMessage;
	}

	public Throwable getErrorCause()
	{
		return errorCause;
	}

	private void initInstanceClass()
	{
		long current = System.currentTimeMillis();
		if(instanceClass != null && current - lastRefresh < refreshInterval)
		{
			return;
		}

		if(groovyFileName == null)
		{
			handleError("groovyFileName must not be null!", null);
			return;
		}

		lastRefresh = current;
		instance = null;
		instanceClass = null;

		File groovyFile = new File(groovyFileName);
		if(!groovyFile.isFile())
		{
			handleError("'"+groovyFile.getAbsolutePath()+"' is not a file!", null);
			return;
		}
		if(!groovyFile.canRead())
		{
			handleError("'"+groovyFile.getAbsolutePath()+"' can not be read!", null);
			return;
		}

		long fileTimestamp = groovyFile.lastModified();
		if(instanceClass == null || previousFileTimestamp != fileTimestamp)
		{
			GroovyClassLoader gcl = new GroovyClassLoader();
			gcl.setShouldRecompile(true);
			try
			{
				instanceClass = gcl.parseClass(groovyFile);
				instance = null;
				previousFileTimestamp = fileTimestamp;
				if(logger.isInfoEnabled()) logger.info("Parsed class {} from '{}'.", instanceClass.getName(), groovyFile.getAbsolutePath());
			}
			catch(Throwable e)
			{
				handleError("Exception while parsing class from '" + groovyFile.getAbsolutePath() + "'!", e);
			}
		}
	}

	private void initInstance()
	{
		long current = System.currentTimeMillis();
		if(instance != null && current - lastRefresh < refreshInterval)
		{
			return;
		}

		initInstanceClass();
		instance = null;
		if(instanceClass != null)
		{
			try
			{
				instance = instanceClass.newInstance();
			}
			catch(Throwable e)
			{
				handleError("Exception while creating instance of '"+instanceClass.getName()+"'!", e);
			}
		}
	}

	public Class getInstanceClass()
	{
		initInstanceClass();
		return instanceClass;
	}

	public Object getInstance()
	{
		initInstance();
		return instance;
	}

	private void handleError(String message, Throwable throwable)
	{
		errorMessage = message;
		errorCause = throwable;
		instanceClass = null;
		instance = null;
		if(logger.isWarnEnabled())
		{
			if(throwable != null)
			{
				logger.warn(message, throwable);
			}
			else
			{
				logger.warn(message);
			}
		}
	}
}
