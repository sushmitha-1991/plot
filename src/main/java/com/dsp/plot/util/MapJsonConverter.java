/**
 * 
 * @author 
 * @version 
 */
package com.dsp.plot.util;

import java.io.IOException;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Converter
public class MapJsonConverter implements AttributeConverter<Map<String, Object>, String> {

	private static final ObjectMapper objectMapper = new ObjectMapper();
	private static final Logger logger = LoggerFactory.getLogger(MapJsonConverter.class);

	static {
		// Register the JavaTimeModule to handle Java 8 time types
		objectMapper.registerModule(new JavaTimeModule());
	}

	@Override
	public String convertToDatabaseColumn(Map<String, Object> attribute) {
		if (attribute == null) {
			return null;
		}
		try {
			return objectMapper.writeValueAsString(attribute);
		} catch (JsonProcessingException e) {
			logger.error("Error converting map to JSON: {}", attribute, e);
			throw new IllegalArgumentException("Error converting map to JSON", e);
		}
	}

	@Override
	public Map<String, Object> convertToEntityAttribute(String dbData) {
		if (dbData == null) {
			return null;
		}
		try {
			return objectMapper.readValue(dbData, Map.class);
		} catch (IOException e) {
			logger.error("Error converting JSON to map: {}", dbData, e);
			throw new IllegalArgumentException("Error converting JSON to map", e);
		}
	}
}
