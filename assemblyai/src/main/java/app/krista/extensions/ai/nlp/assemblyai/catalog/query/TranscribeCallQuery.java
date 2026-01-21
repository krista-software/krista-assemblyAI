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

package app.krista.extensions.ai.nlp.assemblyai.catalog.query;

import app.krista.extension.executor.ExtensionResponse;
import app.krista.extensions.ai.nlp.assemblyai.bridge.ResponseMapper;
import app.krista.extensions.ai.nlp.assemblyai.service.TranscriptionService;
import app.krista.extensions.ai.nlp.assemblyai.service.TranscriptionService.CallTranscriptionResult;
import app.krista.model.base.File;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jvnet.hk2.annotations.Service;

import javax.inject.Inject;

/**
 * Query handler for transcribing call recordings.
 * Follows the Command/Query Separation pattern - this is a QUERY operation.
 *
 * Responsibilities:
 * - Validate input parameters (Krista-specific)
 * - Coordinate service layer calls
 * - Handle Krista-specific concerns (logging, error handling)
 * - Delegate AssemblyAI operations to service layer
 * - Transform results to ExtensionResponse
 *
 * This class contains ONLY Krista-specific logic.
 * All AssemblyAI-specific logic is in the service layer.
 */
@Service
public class TranscribeCallQuery {

    private static final Logger LOG = LogManager.getLogger(TranscribeCallQuery.class.getName());

    private final TranscriptionService transcriptionService;
    private final ResponseMapper responseMapper;

    /**
     * Constructor for dependency injection.
     *
     * @param transcriptionService AssemblyAI transcription service
     * @param responseMapper Mapper to convert service results to ExtensionResponse
     */
    @Inject
    public TranscribeCallQuery(
            TranscriptionService transcriptionService,
            ResponseMapper responseMapper) {
        this.transcriptionService = transcriptionService;
        this.responseMapper = responseMapper;
        LOG.info("TranscribeCallQuery initialized");
    }
    
    /**
     * Executes the transcribe call query.
     *
     * Workflow:
     * 1. Validate Krista input (audio file)
     * 2. Delegate to AssemblyAI service layer
     * 3. Map service result directly to ExtensionResponse
     *
     * @param audioFile The Krista audio file to transcribe
     * @param redactPii Whether to redact PII (defaults to false if null)
     * @return ExtensionResponse with transcription results
     */
    public ExtensionResponse execute(File audioFile, Boolean redactPii) {
        LOG.info("Transcribe Call query received - File: {}, Redact PII: {}",
                audioFile != null ? audioFile.getFileName() : "null", redactPii);

        try {
            // Step 1: Validate Krista input
            String validationError = validateAudioFile(audioFile);
            if (validationError != null) {
                LOG.warn("Audio file validation failed: {}", validationError);
                return responseMapper.createValidationErrorResponse(validationError);
            }

            LOG.info("Audio file validated - Name: {}", audioFile.getFileName());

            // Step 2: Delegate to service layer (AssemblyAI-specific logic)
            LOG.debug("Delegating to TranscriptionService for processing");
            CallTranscriptionResult serviceResult =
                    transcriptionService.transcribeCallComplete(audioFile, redactPii);

            // Step 3: Map service result directly to ExtensionResponse
            LOG.debug("Mapping service result to ExtensionResponse");
            ExtensionResponse response = responseMapper.toExtensionResponse(serviceResult);

            LOG.info("Transcription query completed successfully");
            return response;

        } catch (InterruptedException e) {
            LOG.error("Transcription interrupted for file: {}",
                    audioFile != null ? audioFile.getFileName() : "null", e);
            Thread.currentThread().interrupt();
            return responseMapper.createInterruptedResponse("Transcription was interrupted");

        } catch (Exception e) {
            LOG.error("Transcription failed for file: {}",
                    audioFile != null ? audioFile.getFileName() : "null", e);
            return responseMapper.createFailureResponse(
                    "Transcription failed: " + e.getMessage());
        }
    }

    /**
     * Validates audio file.
     * This is Krista-specific validation logic.
     *
     * @param audioFile The Krista audio file
     * @return Error message if validation fails, null if valid
     */
    private String validateAudioFile(File audioFile) {
        // Validate audio file is not null
        if (audioFile == null) {
            return "Audio file is required";
        }

        // Validate file name
        if (audioFile.getFileName() == null || audioFile.getFileName().trim().isEmpty()) {
            return "Audio file name is required";
        }

        LOG.debug("Audio file validation passed - File: {}", audioFile.getFileName());
        return null;
    }
}

