package org.apache.flume.source.appmon;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.flume.Event;
import org.apache.flume.event.SimpleEvent;

import com.dynatrace.diagnostics.core.realtime.export.BtExport.BtOccurrence;
import com.dynatrace.diagnostics.core.realtime.export.BtExport.BusinessTransaction;
import com.dynatrace.diagnostics.core.realtime.export.BtExport.BusinessTransaction.Type;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

public class BtExportHelper {

	// private static final Logger logger = LoggerFactory.getLogger(BtExportHelper.class);

	public static final String HEADER_KEY_TYPE = "x-dynatrace-type";
	public static final String HEADER_KEY_BT_TYPE = "btType";
	public static final String HEADER_KEY_BT_NAME = "btName";
	public static final String HEADER_KEY_SYSTEM_PROFILE = "systemProfile";
	public static final String HEADER_KEY_SERVER = "server";

	private static final DateFormat dateFormat;
	private static final Gson gson;

	static {
		dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SZ");
		gson = new GsonBuilder().create();
	}

	public static void initEvents(BusinessTransaction bt, List<Event> events)
			throws IOException {

		for (BtOccurrence occurrence : bt.getOccurrencesList()) {
			Event event = new SimpleEvent();
			Map<String, String> headers = event.getHeaders();
			Type btType = bt.getType();
			if (btType.equals(Type.PUREPATH)) {
				headers.put(HEADER_KEY_TYPE, "dynatrace-appmon-purepath");
			} else if (btType.equals(Type.USER_ACTION)) {
				headers.put(HEADER_KEY_TYPE, "dynatrace-appmon-useraction");
			} else if (btType.equals(Type.VISIT)) {
				headers.put(HEADER_KEY_TYPE, "dynatrace-appmon-visit");
			} else {
				headers.put(HEADER_KEY_TYPE, "dynatrace-appmon-purepath");
			}

			JsonObject jsonElement = new JsonObject();
			if (bt.hasName()) {
				jsonElement.addProperty("name", bt.getName());
			}
			if (bt.hasApplication()) {
				jsonElement.addProperty("application", bt.getApplication());
			}
			if (bt.hasSystemProfile()) {
				jsonElement.addProperty("systemProfile", bt.getSystemProfile());
			}
			if (bt.hasType()) {
				jsonElement.addProperty("type", btType.name());
			}
			if (occurrence.hasPurePathId()) {
				jsonElement.addProperty("purePathId", occurrence.getPurePathId());
			}
			if (occurrence.hasStartTime()) {
				jsonElement.addProperty("startTime", dateFormat.format(new Date(occurrence.getStartTime())));
			}
			if (occurrence.hasEndTime()) {
				jsonElement.addProperty("endTime", dateFormat.format(new Date(occurrence.getEndTime())));
			}
			final int nrOfSplittings = bt.getDimensionNamesCount();
			if (nrOfSplittings > 0) {
				final JsonObject dimensions = new JsonObject();
				// safety net, in case the number of dimensions changed in
				// between
				final int realSize = occurrence.getDimensionsCount();
				for (int i = 0; i < nrOfSplittings && i < realSize; i++) {
					dimensions.addProperty(bt.getDimensionNames(i), occurrence.getDimensions(i));
				}
				jsonElement.add("dimensions", dimensions);
			}

			final int nrOfMeasures = bt.getMeasureNamesCount();
			if (nrOfMeasures > 0) {
				final JsonObject measures = new JsonObject();
				// safety net, in case the number of measures changed in between
				final int realSize = occurrence.getValuesCount();
				for (int i = 0; i < nrOfMeasures && i < realSize; i++) {
					measures.addProperty(bt.getMeasureNames(i), occurrence.getValues(i));
				}

				jsonElement.add("measures", measures);
			}

			if (occurrence.hasFailed()) {
				jsonElement.addProperty("failed", occurrence.getFailed());
			}

			if (occurrence.hasVisitId()) {
				jsonElement.addProperty("visitId", occurrence.getVisitId());
			}

			if (occurrence.hasActionName()) {
				jsonElement.addProperty("actionName", occurrence.getActionName());
			}

			if (occurrence.hasApdex()) {
				jsonElement.addProperty("apdex", occurrence.getApdex());
			}

			if (occurrence.hasConverted()) {
				jsonElement.addProperty("converted", occurrence.getConverted());
			}

			if (occurrence.hasQuery()) {
				jsonElement.addProperty("query", occurrence.getQuery());
			}

			if (occurrence.hasUrl()) {
				jsonElement.addProperty("url", occurrence.getUrl());
			}

			if (occurrence.hasUser()) {
				jsonElement.addProperty("user", occurrence.getUser());
			}

			if (occurrence.hasResponseTime()) {
				jsonElement.addProperty("responseTime", occurrence.getResponseTime());
			}
			if (occurrence.hasDuration()) {
				jsonElement.addProperty("duration", occurrence.getDuration());
			}

			if (occurrence.hasCpuTime()) {
				jsonElement.addProperty("cpuTime", occurrence.getCpuTime());
			}

			if (occurrence.hasExecTime()) {
				jsonElement.addProperty("execTime", occurrence.getExecTime());
			}

			if (occurrence.hasSuspensionTime()) {
				jsonElement.addProperty("suspensionTime", occurrence.getSuspensionTime());
			}

			if (occurrence.hasSyncTime()) {
				jsonElement.addProperty("syncTime", occurrence.getSyncTime());
			}

			if (occurrence.hasWaitTime()) {
				jsonElement.addProperty("waitTime", occurrence.getWaitTime());
			}

			if (occurrence.hasNrOfActions()) {
				jsonElement.addProperty("nrOfActions", occurrence.getNrOfActions());
			}

			if (occurrence.hasClientFamily()) {
				jsonElement.addProperty("clientFamily", occurrence.getClientFamily());
			}

			if (occurrence.hasClientIP()) {
				jsonElement.addProperty("clientIP", occurrence.getClientIP());
			}

			if (occurrence.hasContinent()) {
				jsonElement.addProperty("continent", occurrence.getContinent());
			}

			if (occurrence.hasCountry()) {
				jsonElement.addProperty("country", occurrence.getCountry());
			}

			if (occurrence.hasCity()) {
				jsonElement.addProperty("city", occurrence.getCity());
			}

			if (occurrence.hasFailedActions()) {
				jsonElement.addProperty("failedActions", occurrence.getFailedActions());
			}

			if (occurrence.hasClientErrors()) {
				jsonElement.addProperty("clientErrors", occurrence.getClientErrors());
			}

			if (occurrence.hasExitActionFailed()) {
				jsonElement.addProperty("exitActionFailed", occurrence.getExitActionFailed());
			}

			if (occurrence.hasBounce()) {
				jsonElement.addProperty("bounce", occurrence.getBounce());
			}

			if (occurrence.hasOsFamily()) {
				jsonElement.addProperty("osFamily", occurrence.getOsFamily());
			}

			if (occurrence.hasOsName()) {
				jsonElement.addProperty("osName", occurrence.getOsName());
			}

			if (occurrence.hasConnectionType()) {
				jsonElement.addProperty("connectionType", occurrence.getConnectionType());
			}
			event.setBody(gson.toJson(jsonElement).getBytes("UTF-8"));
			events.add(event);
		}
	}
}
