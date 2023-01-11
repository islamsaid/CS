package com.asset.contactstrategy.common.logger;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.logging.log4j.message.ObjectMessage;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 *
 * @author esmail.anbar
 */
public class StructuredLogFactory {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static StructuredLog put(String key, String value) {
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        return new StructuredLog(key, value);
    }

//    Limiting it to always have a message as a begining for the log
//    public static StructuredLog put(String key, int value) {
//        return new StructuredLog(key, value);
//    }
//
//    public static StructuredLog put(String key, double value) {
//        return new StructuredLog(key, value);
//    }
//
//    public static StructuredLog put(String key, float value) {
//        return new StructuredLog(key, value);
//    }
//
//    public static StructuredLog put(String key, char value) {
//        return new StructuredLog(key, value);
//    }
//
//    public static StructuredLog put(String key, Long value) {
//        return new StructuredLog(key, value);
//    }
//
//    public static StructuredLog put(String key, boolean value) {
//        return new StructuredLog(key, value);
//    }
    public static class StructuredLog {

        private final ObjectNode jsonData = objectMapper.createObjectNode();
        private final long time = System.currentTimeMillis();

        public StructuredLog(String key, String value) {
            jsonData.put(key, value);
        }

        public StructuredLog put(String key, String value) {
            jsonData.put(key, value);
            return this;
        }

        //Mohamed Alaa Sedky
        public StructuredLog put(String key, int value) {
            jsonData.put(key, value);
            return this;
        }

        public StructuredLog put(String key, double value) {
            jsonData.put(key, value);
            return this;
        }

        public StructuredLog put(String key, float value) {
            jsonData.put(key, value);
            return this;
        }

        public StructuredLog put(String key, char value) {
            jsonData.put(key, value);
            return this;
        }

        public StructuredLog put(String key, Long value) {
            jsonData.put(key, value);
            return this;
        }

        public StructuredLog put(String key, boolean value) {
            jsonData.put(key, value);
            return this;
        }

        public StructuredLog put(String key, Date value) {
            jsonData.putPOJO(key, value);
            return this;
        }

        public StructuredLog put(String key, AtomicLong value) {
            jsonData.put(key, value.get());
            return this;
        }

        public StructuredLog put(String key, ArrayList value) {
            if (value != null) {
                ArrayNode arrayValue = objectMapper.createArrayNode();
                for (Object val : value) {
                    arrayValue.add(objectMapper.convertValue(val, JsonNode.class));
                }
                jsonData.set(key, arrayValue);
            } else {
                jsonData.set(key, NullNode.instance);
            }
            return this;
        }

        public StructuredLog put(String key, Object value) {
            if (value != null) {
                jsonData.set(key, objectMapper.convertValue(value, JsonNode.class));
            } else {
                jsonData.set(key, NullNode.instance);
            }
            return this;
        }

        public StructuredLog putCSV(String key, String value) {
            if (value != null) {
                ArrayNode csvValue = objectMapper.createArrayNode();
                for (String string : value.split(",")) {
                    csvValue.add(string);
                    jsonData.set(key, csvValue);
                }
            } else {
                jsonData.set(key, NullNode.instance);
            }
            return this;
        }

        public ObjectMessage build() {
            jsonData.put("logBuildTime", System.currentTimeMillis() - time);
            return new ObjectMessage(jsonData);
        }
    }
}
