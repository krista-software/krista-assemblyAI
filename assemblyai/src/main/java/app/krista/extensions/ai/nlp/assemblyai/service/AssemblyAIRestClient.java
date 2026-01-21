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

package app.krista.extensions.ai.nlp.assemblyai.service;

import app.krista.extensions.ai.nlp.assemblyai.util.AssemblyAIConstants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;

/**
 * REST client for AssemblyAI API communication.
 * Handles HTTP requests to AssemblyAI endpoints with proper authentication.
 */
public class AssemblyAIRestClient {

    private static final Logger LOG = LogManager.getLogger(AssemblyAIRestClient.class.getName());

    private final String baseUrl;
    private final String apiKey;
    private final Client client;

    /**
     * Constructor for AssemblyAIRestClient.
     *
     * @param baseUrl Base URL for AssemblyAI API
     * @param apiKey  API key for authentication
     * @throws IllegalArgumentException if baseUrl or apiKey is null or empty
     */
    public AssemblyAIRestClient(String baseUrl, String apiKey) {
        if (baseUrl == null || baseUrl.trim().isEmpty()) {
            throw new IllegalArgumentException("Base URL cannot be null or empty");
        }
        if (apiKey == null || apiKey.trim().isEmpty()) {
            throw new IllegalArgumentException("API key cannot be null or empty");
        }

        this.baseUrl = baseUrl;
        this.apiKey = apiKey;
        this.client = ClientBuilder.newClient();
        LOG.debug("AssemblyAIRestClient initialized with base URL: {}", baseUrl);
    }

    /**
     * Uploads an audio file to AssemblyAI.
     *
     * @param audioFile The audio file to upload
     * @return JSONObject containing the upload_url
     * @throws IOException If file reading fails
     * @throws IllegalArgumentException if audioFile is null
     */
    public JSONObject uploadFile(File audioFile) throws IOException {
        if (audioFile == null) {
            throw new IllegalArgumentException("Audio file cannot be null");
        }
        if (!audioFile.exists()) {
            throw new IllegalArgumentException("Audio file does not exist: " + audioFile.getPath());
        }
        if (!audioFile.isFile()) {
            throw new IllegalArgumentException("Path is not a file: " + audioFile.getPath());
        }

        LOG.info("Uploading audio file: {}", audioFile.getName());

        byte[] fileData = Files.readAllBytes(audioFile.toPath());

        Builder request = client.target(baseUrl + AssemblyAIConstants.UPLOAD_ENDPOINT)
                .request(MediaType.APPLICATION_JSON);
        addAuthHeader(request);

        Response response = request.post(Entity.entity(fileData, MediaType.APPLICATION_OCTET_STREAM));
        return handleResponse(response);
    }

    /**
     * Creates a transcript request.
     *
     * @param requestBody JSON body containing transcript parameters
     * @return JSONObject containing the transcript response
     * @throws IllegalArgumentException if requestBody is null
     */
    public JSONObject createTranscript(JSONObject requestBody) {
        if (requestBody == null) {
            throw new IllegalArgumentException("Request body cannot be null");
        }

        LOG.info("Creating transcript with parameters: {}", requestBody);

        Builder request = client.target(baseUrl + AssemblyAIConstants.TRANSCRIPT_ENDPOINT)
                .request(MediaType.APPLICATION_JSON);
        addAuthHeader(request);

        Response response = request.post(Entity.entity(requestBody.toString(), MediaType.APPLICATION_JSON));
        return handleResponse(response);
    }

    /**
     * Gets transcript status and details.
     *
     * @param transcriptId The transcript ID
     * @return JSONObject containing the transcript details
     * @throws IllegalArgumentException if transcriptId is null or empty
     */
    public JSONObject getTranscript(String transcriptId) {
        if (transcriptId == null || transcriptId.trim().isEmpty()) {
            throw new IllegalArgumentException("Transcript ID cannot be null or empty");
        }

        LOG.info("Getting transcript: {}", transcriptId);

        Builder request = client.target(baseUrl + AssemblyAIConstants.TRANSCRIPT_ENDPOINT + "/" + transcriptId)
                .request(MediaType.APPLICATION_JSON);
        addAuthHeader(request);

        Response response = request.get();
        return handleResponse(response);
    }

    /**
     * Lists transcripts with optional pagination.
     * This is useful for testing API connectivity without requiring a transcript ID.
     *
     * @param limit Maximum number of transcripts to return (default: 1)
     * @return JSONObject containing the list of transcripts and pagination details
     */
    public JSONObject listTranscripts(int limit) {
        LOG.info("Listing transcripts with limit: {}", limit);

        Builder request = client.target(baseUrl + AssemblyAIConstants.TRANSCRIPT_ENDPOINT)
                .queryParam("limit", limit)
                .request(MediaType.APPLICATION_JSON);
        addAuthHeader(request);

        Response response = request.get();
        return handleResponse(response);
    }

    /**
     * Makes a GET request to a specific endpoint.
     *
     * @param endpoint    The API endpoint
     * @param queryParams Query parameters
     * @return JSONObject response
     */
    public JSONObject get(String endpoint, Map<String, Object> queryParams) {
        LOG.info("GET request to: {}", endpoint);

        Builder request = buildRequest(endpoint, queryParams);
        addAuthHeader(request);

        Response response = request.get();
        return handleResponse(response);
    }

    /**
     * Makes a POST request to a specific endpoint.
     *
     * @param endpoint The API endpoint
     * @param body     Request body
     * @return JSONObject response
     */
    public JSONObject post(String endpoint, JSONObject body) {
        LOG.info("POST request to: {}", endpoint);

        Builder request = client.target(baseUrl + endpoint)
                .request(MediaType.APPLICATION_JSON);
        addAuthHeader(request);

        Response response = request.post(Entity.entity(body.toString(), MediaType.APPLICATION_JSON));
        return handleResponse(response);
    }

    /**
     * Builds a request with query parameters.
     *
     * @param endpoint    The API endpoint
     * @param queryParams Query parameters
     * @return Request builder
     */
    private Builder buildRequest(String endpoint, Map<String, Object> queryParams) {
        var target = client.target(baseUrl + endpoint);

        if (queryParams != null && !queryParams.isEmpty()) {
            for (Map.Entry<String, Object> entry : queryParams.entrySet()) {
                target = target.queryParam(entry.getKey(), entry.getValue());
            }
        }

        return target.request(MediaType.APPLICATION_JSON);
    }

    /**
     * Adds authorization header to the request.
     *
     * @param request Request builder
     */
    private void addAuthHeader(Builder request) {
        request.header(AssemblyAIConstants.AUTHORIZATION_HEADER, apiKey);
    }

    /**
     * Handles HTTP response and converts to JSONObject.
     *
     * @param response HTTP response
     * @return JSONObject parsed from response
     * @throws RuntimeException if response indicates an error
     */
    private JSONObject handleResponse(Response response) {
        int status = response.getStatus();
        String responseBody = response.readEntity(String.class);

        LOG.debug("Response status: {}, body: {}", status, responseBody);

        if (status >= 200 && status < 300) {
            try {
                return new JSONObject(responseBody);
            } catch (JSONException e) {
                LOG.error("Failed to parse response JSON: {}", responseBody, e);
                throw new RuntimeException("Failed to parse API response", e);
            }
        } else {
            LOG.error("API request failed with status {}: {}", status, responseBody);
            String errorMessage = extractErrorMessage(responseBody);
            throw new RuntimeException("API request failed: " + errorMessage);
        }
    }

    /**
     * Extracts error message from response body.
     *
     * @param responseBody Response body string
     * @return Error message
     */
    private String extractErrorMessage(String responseBody) {
        try {
            JSONObject errorJson = new JSONObject(responseBody);
            if (errorJson.has(AssemblyAIConstants.ERROR)) {
                return errorJson.getString(AssemblyAIConstants.ERROR);
            }
            return responseBody;
        } catch (JSONException e) {
            return responseBody;
        }
    }

    /**
     * Closes the HTTP client.
     */
    public void close() {
        if (client != null) {
            client.close();
        }
    }
}

