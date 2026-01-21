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
import app.krista.extension.impl.anno.*;
import app.krista.extensions.ai.nlp.assemblyai.service.AssemblyAIRestClient;
import app.krista.extensions.ai.nlp.assemblyai.util.AssemblyAIConstants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codehaus.jettison.json.JSONObject;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Map;

/**
 * Main extension class for AssemblyAI integration.
 * Provides audio transcription services with speaker diarization, sentiment analysis, and topic detection.
 */
@Field.Text(value = AssemblyAIConstants.API_KEY, isSecured = true)
@Java(version = Java.Version.JAVA_21)
@Extension(name = AssemblyAIConstants.EXTENSION_NAME, jaxrsId = AssemblyAIConstants.JAXRS_ID, version = "1.0.2")
@StaticResource(path = "docs", file = "docs")
public class AssemblyAIExtension {

    private static final Logger LOG = LogManager.getLogger(AssemblyAIExtension.class.getName());

    private final AssemblyAIAttributes assemblyAIAttributes;

    /**
     * Constructor for dependency injection.
     *
     * @param assemblyAIAttributes Platform attributes
     */
    @Inject
    public AssemblyAIExtension(AssemblyAIAttributes assemblyAIAttributes) {
        LOG.info("AssemblyAIExtension initialized");
        this.assemblyAIAttributes = assemblyAIAttributes;
    }

    /**
     * Provides custom tabs for the extension UI.
     *
     * @return Map of tab names to paths
     */
    @InvokerRequest(InvokerRequest.Type.CUSTOM_TABS)
    public Map<String, String> customTabs() {
        return Map.of("Documentation", "static/docs");
    }

    /**
     * Tests the connection to AssemblyAI API.
     *
     * @throws IllegalArgumentException if connection fails
     */
    @InvokerRequest(InvokerRequest.Type.TEST_CONNECTION)
    public void testConnection() {
        LOG.info("Testing connection to AssemblyAI API");
        validateAndTestConnection(assemblyAIAttributes);
    }

    /**
     * Validates connection attributes before saving.
     *
     * @param connectionAttributes Connection attributes to validate
     * @throws IllegalArgumentException if validation fails
     */
    @InvokerRequest(InvokerRequest.Type.VALIDATE_ATTRIBUTES)
    public void validateAttributes(Map<String, Object> connectionAttributes) {
        LOG.info("Validating connection attributes");
        AssemblyAIAttributes tempAttributes = AssemblyAIAttributes.create(connectionAttributes);
        tempAttributes.validate();
        validateAndTestConnection(tempAttributes);
    }

    /**
     * Handles attribute updates.
     *
     * @param oldAttributes Old attributes
     * @param newAttributes New attributes
     */
    @InvokerRequest(InvokerRequest.Type.INVOKER_UPDATED)
    public void attributesUpdated(Map<String, Object> oldAttributes, Map<String, Object> newAttributes) {
        LOG.info("Attributes updated " + newAttributes);
        assemblyAIAttributes.update(newAttributes);
    }

    /**
     * Validates and tests the connection to AssemblyAI API.
     * Uses the List Transcripts API endpoint which doesn't require a transcript ID.
     *
     * @param attributes Attributes to test
     * @throws IllegalArgumentException if connection test fails
     */
    private void validateAndTestConnection(AssemblyAIAttributes attributes) {
        try {
            attributes.validate();

            AssemblyAIRestClient restClient = new AssemblyAIRestClient(
                    attributes.getBaseUrl(),
                    attributes.getApiKey()
            );

            // Test the connection by listing transcripts with limit=1
            // This is a lightweight API call that validates the API key without requiring a transcript ID
            try {
                JSONObject response = restClient.listTranscripts(1);

                // If we get here, the API key is valid and the connection is successful
                LOG.info("Connection test successful - API key is valid");

                // Optionally log the response structure for debugging
                if (response.has("transcripts")) {
                    LOG.debug("Successfully retrieved transcript list");
                }

            } catch (RuntimeException e) {
                // Check if it's an authentication error
                String errorMessage = e.getMessage().toLowerCase();
                if (errorMessage.contains("unauthorized") ||
                        errorMessage.contains("invalid") ||
                        errorMessage.contains("authentication") ||
                        errorMessage.contains("api key") ||
                        errorMessage.contains("missing") ||
                        errorMessage.contains("token")) {
                    throw new IllegalArgumentException(AssemblyAIConstants.ERROR_INVALID_API_KEY);
                }
                // Other errors indicate connection issues
                throw new IllegalArgumentException(AssemblyAIConstants.ERROR_CONNECTION_FAILED + ": " + errorMessage);
            }

        } catch (IllegalArgumentException e) {
            LOG.error("Connection validation failed: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            LOG.error("Connection test failed", e);
            throw new IllegalArgumentException(AssemblyAIConstants.ERROR_CONNECTION_FAILED + ": " + e.getMessage());
        }
    }
}

