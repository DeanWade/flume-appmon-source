/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.flume.source.appmon;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.event.SimpleEvent;
import org.apache.flume.source.http.HTTPSourceHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.ByteStreams;

public class IncidentHTTPSourceHandler implements HTTPSourceHandler {

	private static final Logger logger = LoggerFactory.getLogger(IncidentHTTPSourceHandler.class);
	
	public static final String HEADER_KEY_TYPE = "x-dynatrace-type";
	
	@Override
	public List<Event> getEvents(HttpServletRequest request) throws Exception {
		String contentType = request.getContentType();
		if("application/json".equals(contentType)){
			return handlAlert(request);
		}
		logger.warn("Unrecognized Content Type: " + contentType);
		return new ArrayList<Event>(0);
	}
	
	private List<Event> handlAlert(HttpServletRequest request) throws Exception{
		List<Event> events = new ArrayList<Event>(1);
		try {
			byte[] data = new byte[request.getContentLength()];
			ByteStreams.readFully(request.getInputStream(), data);
			Event event = new SimpleEvent();
			Map<String, String> headers = event.getHeaders();
			headers.put(HEADER_KEY_TYPE, "dynatrace-appmon-alert");
			event.setBody(data);
			events.add(event);
			return events;
		} catch (Exception ex) {
			logger.error(ex.getMessage());
			throw ex;
		}
	}

	@Override
	public void configure(Context context) {
		//do nothing
	}


}
