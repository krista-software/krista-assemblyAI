/*
 * AssemblyAI Extension for Krista
 * Copyright (C) 2024 Krista Software
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package app.krista.extensions.ai.nlp.assemblyai.catalog;

import app.krista.extension.executor.Invoker;
import app.krista.extensions.ai.nlp.assemblyai.util.AssemblyAIConstants;
import app.krista.extensions.ai.nlp.assemblyai.util.LogLevelManager;
import org.jvnet.hk2.annotations.Service;

import javax.inject.Inject;
import java.util.Map;

/**
 * Platform attributes for AssemblyAI integration.
 * Manages API key, base URL, and log level configuration.
 */
@Service
public class AssemblyAIAttributes {

    private String apiKey;
    private String baseUrl;
    private String logLevel;

    /**
     * Creates AssemblyAIAttributes from a map of connection attributes.
     *
     * @param attributes Connection attributes map
     * @return AssemblyAIAttributes instance
     */
    public static AssemblyAIAttributes create(Map<String, Object> attributes) {
        return new AssemblyAIAttributes(attributes);
    }

    /**
     * Creates AssemblyAIAttributes from Invoker and connection attributes.
     *
     * @param invoker    Krista invoker
     * @param attributes Connection attributes map
     * @return AssemblyAIAttributes instance
     */
    public static AssemblyAIAttributes create(Invoker invoker, Map<String, Object> attributes) {
        return new AssemblyAIAttributes(attributes);
    }

    /**
     * Constructor for dependency injection.
     *
     * @param invoker Krista invoker
     */
    @Inject
    public AssemblyAIAttributes(Invoker invoker) {
        this(invoker.getAttributes());
    }

    /**
     * Constructor from attributes map.
     *
     * @param attributes Connection attributes map
     */
    public AssemblyAIAttributes(Map<String, Object> attributes) {
        this(
                (String) attributes.get(AssemblyAIConstants.API_KEY),
                AssemblyAIConstants.DEFAULT_BASE_URL,
                (String) attributes.getOrDefault(AssemblyAIConstants.LOG_LEVEL, "INFO")
        );
    }

    /**
     * Constructor with explicit parameters.
     *
     * @param apiKey  AssemblyAI API key
     * @param baseUrl Base URL for AssemblyAI API
     */
    public AssemblyAIAttributes(String apiKey, String baseUrl) {
        this(apiKey, baseUrl, "INFO");
    }

    /**
     * Constructor with explicit parameters including log level.
     *
     * @param apiKey   AssemblyAI API key
     * @param baseUrl  Base URL for AssemblyAI API
     * @param logLevel Log level for the extension
     */
    public AssemblyAIAttributes(String apiKey, String baseUrl, String logLevel) {
        this.apiKey = apiKey;
        this.baseUrl = baseUrl != null && !baseUrl.trim().isEmpty() ? baseUrl : AssemblyAIConstants.DEFAULT_BASE_URL;
        this.logLevel = logLevel != null && !logLevel.trim().isEmpty() ? logLevel : "INFO";

        // Apply log level configuration
        LogLevelManager.setLogLevel(this.logLevel);
    }

    /**
     * Gets the API key.
     *
     * @return API key
     */
    public String getApiKey() {
        return apiKey;
    }

    /**
     * Gets the base URL.
     *
     * @return Base URL
     */
    public String getBaseUrl() {
        return baseUrl;
    }

    /**
     * Gets the log level.
     *
     * @return Log level
     */
    public String getLogLevel() {
        return logLevel;
    }

    /**
     * Sets the log level and applies it.
     *
     * @param logLevel New log level
     */
    public void setLogLevel(String logLevel) {
        this.logLevel = logLevel != null && !logLevel.trim().isEmpty() ? logLevel : "INFO";
        LogLevelManager.setLogLevel(this.logLevel);
    }

    /**
     * Updates attributes from a new map.
     *
     * @param newAttributes New attributes map
     */
    public void update(Map<String, Object> newAttributes) {
        if (newAttributes != null) {
            String newApiKey = (String) newAttributes.get(AssemblyAIConstants.API_KEY);

            if (newApiKey != null) {
                this.apiKey = newApiKey;
            }

            // Update log level if provided
            String newLogLevel = (String) newAttributes.get(AssemblyAIConstants.LOG_LEVEL);
            if (newLogLevel != null) {
                setLogLevel(newLogLevel);
            }

            // baseUrl is always set to DEFAULT_BASE_URL and cannot be updated
        }
    }

    /**
     * Validates that required attributes are present and valid.
     *
     * @throws IllegalArgumentException if validation fails
     */
    public void validate() {
        if (apiKey == null || apiKey.trim().isEmpty()) {
            throw new IllegalArgumentException("API Key is required");
        }

    }
}

