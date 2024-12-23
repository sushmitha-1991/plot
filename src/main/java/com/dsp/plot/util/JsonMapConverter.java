package com.dsp.plot.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Converter
public class JsonMapConverter implements AttributeConverter<List<Map<String, Object>>, String> {

	private static final ObjectMapper objectMapper = new ObjectMapper();
	private static final Logger logger = LoggerFactory.getLogger(MapJsonConverter.class);

	static {
		// Register the JavaTimeModule to handle Java 8 time types
		objectMapper.registerModule(new JavaTimeModule());
	}

	@Override
	public String convertToDatabaseColumn(List<Map<String, Object>> attribute) {
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
	public List<Map<String, Object>> convertToEntityAttribute(String dbData) {
		if (dbData == null) {
			return null;
		}
		try {
			// Try to parse as a list of maps
			return objectMapper.readValue(dbData, new TypeReference<List<Map<String, Object>>>() {});
		} catch (IOException e) {
			logger.warn("Failed to parse JSON as List<Map<String, Object>>. Attempting Map<String, Map<String, Object>> format.");

			try {
				// If parsing as a list fails, try parsing as a map and convert to list
				Map<String, Map<String, Object>> mapData = objectMapper.readValue(dbData, new TypeReference<Map<String, Map<String, Object>>>() {});
				return new ArrayList<>(mapData.values());
			} catch (IOException innerException) {
				logger.error("Error converting JSON to List<Map<String, Object>>: {}", dbData, innerException);
				throw new IllegalArgumentException("Error converting JSON to List<Map<String, Object>>", innerException);
			}
		}
	}
}
