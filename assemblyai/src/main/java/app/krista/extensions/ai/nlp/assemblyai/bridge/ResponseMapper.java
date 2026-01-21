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

package app.krista.extensions.ai.nlp.assemblyai.bridge;

import app.krista.extension.executor.ExtensionResponse;
import app.krista.extensions.ai.nlp.assemblyai.service.TranscriptionService.CallTranscriptionResult;
import org.jvnet.hk2.annotations.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Simple mapper that directly converts AssemblyAI service results to Krista ExtensionResponse.
 * No intermediate bridge entities - direct translation.
 *
 * Purpose: Transform service layer results into Krista's ExtensionResponse format.
 */
@Service
public class ResponseMapper {

    /**
     * Maps CallTranscriptionResult to ExtensionResponse.
     * Field names match the catalog request @Field annotations exactly.
     *
     * @param result The transcription result from service layer
     * @return ExtensionResponse for Krista platform
     * @throws IllegalArgumentException if result is null
     */
    public ExtensionResponse toExtensionResponse(CallTranscriptionResult result) {
        if (result == null) {
            throw new IllegalArgumentException("CallTranscriptionResult cannot be null");
        }

        if (!result.isSuccessful()) {
            return createFailureResponse("Transcription failed");
        }

        Map<String, Object> responseData = new HashMap<>();
        responseData.put("Transcription Successful", result.isSuccessful());
        responseData.put("Transcript File", result.getTranscriptFile());
        responseData.put("Transcript Text", result.getTranscriptText() != null ? result.getTranscriptText() : "");
        responseData.put("Sentiment", result.getSentiment() != null ? result.getSentiment() : "No sentiment data");
        responseData.put("Topic Detected", result.getTopics() != null ? result.getTopics() : "No topics detected");

        return new ExtensionResponse(
                ExtensionResponse.Result.SUCCESS,
                responseData,
                null, null, null
        );
    }

    /**
     * Creates a validation error response.
     *
     * @param errorMessage The validation error message
     * @return ExtensionResponse with validation error
     */
    public ExtensionResponse createValidationErrorResponse(String errorMessage) {
        if (errorMessage == null || errorMessage.trim().isEmpty()) {
            errorMessage = "Validation error occurred";
        }

        Map<String, Object> errorData = new HashMap<>();
        errorData.put("success", false);
        errorData.put("error", errorMessage);
        errorData.put("errorCode", "VALIDATION_ERROR");

        return new ExtensionResponse(
                ExtensionResponse.Result.FAILURE,
                errorData,
                null, null, null
        );
    }

    /**
     * Creates a failure response.
     *
     * @param errorMessage The error message
     * @return ExtensionResponse with failure
     */
    public ExtensionResponse createFailureResponse(String errorMessage) {
        if (errorMessage == null || errorMessage.trim().isEmpty()) {
            errorMessage = "Transcription failed with unknown error";
        }

        Map<String, Object> errorData = new HashMap<>();
        errorData.put("success", false);
        errorData.put("error", errorMessage);
        errorData.put("errorCode", "TRANSCRIPTION_ERROR");

        return new ExtensionResponse(
                ExtensionResponse.Result.FAILURE,
                errorData,
                null, null, null
        );
    }

    /**
     * Creates an interrupted response.
     *
     * @param message The interruption message
     * @return ExtensionResponse with interrupted status
     */
    public ExtensionResponse createInterruptedResponse(String message) {
        if (message == null || message.trim().isEmpty()) {
            message = "Operation was interrupted";
        }

        Map<String, Object> errorData = new HashMap<>();
        errorData.put("success", false);
        errorData.put("error", message);
        errorData.put("errorCode", "INTERRUPTED");

        return new ExtensionResponse(
                ExtensionResponse.Result.FAILURE,
                errorData,
                null, null, null
        );
    }
}

