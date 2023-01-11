package com.asset.contactstrategy.common.logger;

import java.time.ZoneId;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.nio.charset.Charset;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.Node;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginConfiguration;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.layout.AbstractStringLayout;
import java.util.Map;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.util.KeyValuePair;

/**
 *
 * @author mohamed.sdky
 */
@Plugin(name = "AdvancedJSONLayout", category = Node.CATEGORY, elementType = Layout.ELEMENT_TYPE)
public class AdvancedJSONLayout extends AbstractStringLayout {

    ObjectMapper objectMapper = new ObjectMapper();
    private final String lineSeparator = System.lineSeparator();
    private final boolean newLine;
    private final boolean exposeMessageOnRoot;
    private final boolean exposeContextMapOnRoot;
    private final DateTimeFormatter dateTimeFormatter;
    private final boolean formatDateTime;
    private final boolean locationInfo;
    private final boolean exposeLocationOnRoot;
    private final KeyValuePair[] additionalFields;

    @PluginFactory
    public static AdvancedJSONLayout createLayout(@PluginConfiguration final Configuration config,
            @PluginAttribute(value = "charset", defaultString = "UTF-8") final Charset charset,
            @PluginAttribute(value = "newLine", defaultBoolean = true) final Boolean newLine,
            @PluginAttribute(value = "exposeMessageOnRoot", defaultBoolean = false) final Boolean exposeMessageOnRoot,
            @PluginAttribute(value = "exposeContextMapOnRoot", defaultBoolean = false) final Boolean exposeContextMapOnRoot,
            @PluginAttribute(value = "dateTimeFormat") final String dateTimeFormat,
            @PluginAttribute(value = "locationInfo", defaultBoolean = false) final Boolean locationInfo,
            @PluginAttribute(value = "exposeLocationOnRoot", defaultBoolean = true) final Boolean exposeLocationOnRoot,
            @PluginElement("AdditionalField") KeyValuePair[] additionalFields) {
        return new AdvancedJSONLayout(config, charset, newLine, exposeMessageOnRoot, exposeContextMapOnRoot,
                dateTimeFormat, locationInfo, exposeLocationOnRoot, additionalFields, null, null);
    }

    public AdvancedJSONLayout(Configuration config, Charset aCharset,
            Boolean newLine, Boolean exposeMessageOnRoot, Boolean exposeContextMapOnRoot,
            String dateTimeFormat, Boolean locationInfo, Boolean exposeLocationOnRoot, KeyValuePair[] additionalFields,
            Serializer headerSerializer, Serializer footerSerializer) {
        super(config, aCharset, headerSerializer, footerSerializer);
        this.newLine = newLine;
        this.exposeMessageOnRoot = exposeMessageOnRoot;
        this.exposeContextMapOnRoot = exposeContextMapOnRoot;
        if (dateTimeFormat == null || dateTimeFormat.isEmpty()) {
            formatDateTime = false;
            this.dateTimeFormatter = null;
        } else {
            formatDateTime = true;
            this.dateTimeFormatter = DateTimeFormatter.ofPattern(dateTimeFormat);
        }
        this.locationInfo = locationInfo;
        this.exposeLocationOnRoot = exposeLocationOnRoot;
        this.additionalFields = additionalFields;
    }

    @Override
    public String toSerializable(LogEvent logEvent) {

        long logProcessingTime = System.currentTimeMillis();
        ObjectNode jsonData = objectMapper.createObjectNode();
        try {

            handleLogMessage(jsonData, logEvent);
            handleContextMap(jsonData, logEvent);
            handleAdditionalFields(jsonData);
            handleLocationInfo(jsonData, logEvent);
            handleDateTime(jsonData, logEvent);
            handleThrowable(jsonData, logEvent);

            jsonData.put("threadName", logEvent.getThreadName());
            jsonData.put("level", logEvent.getLevel().name());

            String jsonOut = objectMapper.writeValueAsString(jsonData);
            return jsonOut.substring(0, jsonOut.lastIndexOf('}')) + (",\"logProcessingTime\":\""
                    + (System.currentTimeMillis() - logProcessingTime) + "\"}") + (newLine ? lineSeparator : "");
        } catch (JsonProcessingException ex) {
            return jsonData.toString();
        }
    }

    private void handleDateTime(ObjectNode jsonData, LogEvent logEvent) {
        if (formatDateTime) {
            jsonData.put("dateTime", dateTimeFormatter.format(LocalDateTime.ofInstant(Instant.ofEpochMilli(
                    logEvent.getInstant().getEpochMillisecond()), ZoneId.systemDefault())));
        } else {
            jsonData.put("dateTime", logEvent.getTimeMillis());
        }
    }

    private void handleLogMessage(ObjectNode jsonData, LogEvent logEvent) {
        Object[] msgParameters = logEvent.getMessage().getParameters();
        if (msgParameters != null && msgParameters.length > 0) {
            ObjectNode msgNode;
            if (exposeMessageOnRoot) {
                msgNode = jsonData;
            } else {
                msgNode = objectMapper.createObjectNode();
            }
            int index = 0;
            for (Object msgParameter : msgParameters) {
                if (msgParameter == null) {
                    continue;
                } else if (msgParameter instanceof ObjectNode) {
                    msgNode.setAll((ObjectNode) msgParameter);
                } else if (msgParameter instanceof Map) {
                    for (Map.Entry<Object, Object> entry : ((Map<Object, Object>) msgParameter).entrySet()) {
                        msgNode.putPOJO(entry.getKey().toString(), entry.getValue());
                    }
                } //                else if (msgParameter instanceof Date) {
                //                    if (formatDateTime) {
                //                        jsonData.put("dateTime", dateTimeFormatter.format(LocalDateTime.ofInstant(Instant.ofEpochMilli(
                //                                logEvent.getInstant().getEpochMillisecond()), ZoneId.systemDefault())));
                //                    } else {
                //                        jsonData.put("dateTime", logEvent.getTimeMillis());
                //                    }
                //                } 
                else {
                    msgNode.putPOJO(msgParameter.getClass().getSimpleName() + "-" + index, msgParameter.toString());
                }
                index++;
            }
            if (!exposeMessageOnRoot) {
                jsonData.set("message", msgNode);
            }
        } else {
            jsonData.put("message", logEvent.getMessage().getFormattedMessage());
        }
    }

    private void handleContextMap(ObjectNode jsonData, LogEvent logEvent) {
        Map<String, String> contextMap = logEvent.getContextData().toMap();
        if (contextMap != null && !contextMap.isEmpty()) {
            ObjectNode msgNode;
            if (exposeContextMapOnRoot) {
                msgNode = jsonData;
            } else {
                msgNode = objectMapper.createObjectNode();
            }
            for (Map.Entry<String, String> entry : contextMap.entrySet()) {
                msgNode.putPOJO(entry.getKey(), entry.getValue());
            }
            if (!exposeContextMapOnRoot) {
                jsonData.set("contextMap", msgNode);
            }
        }
    }

    private void handleAdditionalFields(ObjectNode jsonData) {
        if (additionalFields != null && additionalFields.length != 0) {
            for (KeyValuePair additionalField : additionalFields) {
                jsonData.put(additionalField.getKey(), additionalField.getValue());
            }
        }
    }

    private void handleLocationInfo(ObjectNode jsonData, LogEvent logEvent) {
        if (locationInfo) {
            StackTraceElement ste = logEvent.getSource();
            ObjectNode msgNode;
            if (exposeLocationOnRoot) {
                msgNode = jsonData;
            } else {
                msgNode = objectMapper.createObjectNode();
            }
            msgNode.put("className", ste.getClassName());
            msgNode.put("fileName", ste.getFileName());
            msgNode.put("lineNumber", ste.getLineNumber());
            msgNode.put("methodName", ste.getMethodName());
            if (!exposeLocationOnRoot) {
                jsonData.set("source", msgNode);
            }
        }
    }

    private void handleThrowable(ObjectNode jsonData, LogEvent logEvent) {
        Throwable throwable = logEvent.getThrown();
        if (throwable != null) {
            ObjectNode msgNode = objectMapper.createObjectNode();
            msgNode.put("message", throwable.getMessage());
            StackTraceElement[] ste = throwable.getStackTrace();
            if (ste != null) {
                ArrayNode stackArray = objectMapper.createArrayNode();
                for (StackTraceElement stackTraceElement : ste) {
                    stackArray.add(stackTraceElement.getClassName()
                            + "." + stackTraceElement.getMethodName()
                            + "(" + stackTraceElement.getFileName() + ":" + stackTraceElement.getLineNumber() + ")");
                }
                msgNode.set("stackTrace", stackArray);
            }
            jsonData.set("thrown", msgNode);
        }
    }
}
