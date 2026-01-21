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

import app.krista.extensions.ai.nlp.assemblyai.catalog.AssemblyAIAttributes;
import app.krista.extensions.ai.nlp.assemblyai.catalog.KristaMediaClient;
import app.krista.extensions.ai.nlp.assemblyai.util.AssemblyAIConstants;
import app.krista.extensions.ai.nlp.assemblyai.service.model.SentimentAnalysisResult;
import app.krista.extensions.ai.nlp.assemblyai.service.model.TranscriptRequest;
import app.krista.extensions.ai.nlp.assemblyai.service.model.TranscriptResponse;
import app.krista.extensions.ai.nlp.assemblyai.service.model.Utterance;
import app.krista.extensions.ai.nlp.assemblyai.service.model.UploadResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.jvnet.hk2.annotations.Service;

import javax.inject.Inject;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service class for handling transcription operations.
 * Provides methods for uploading audio files, creating transcripts, and retrieving results.
 */
@Service
public class TranscriptionService {

    private static final Logger LOG = LogManager.getLogger(TranscriptionService.class.getName());

    private final AssemblyAIAttributes attributes;
    private final KristaMediaClient kristaMediaClient;

    /**
     * Constructor for dependency injection.
     *
     * @param attributes AssemblyAI platform attributes
     * @param kristaMediaClient Krista media client for file conversion
     * @throws IllegalArgumentException if required parameters are null
     */
    @Inject
    public TranscriptionService(AssemblyAIAttributes attributes, KristaMediaClient kristaMediaClient) {
        if (attributes == null) {
            throw new IllegalArgumentException("AssemblyAIAttributes cannot be null");
        }
        if (kristaMediaClient == null) {
            throw new IllegalArgumentException("KristaMediaClient cannot be null");
        }
        if (attributes.getBaseUrl() == null || attributes.getBaseUrl().trim().isEmpty()) {
            throw new IllegalArgumentException("Base URL cannot be null or empty");
        }
        if (attributes.getApiKey() == null || attributes.getApiKey().trim().isEmpty()) {
            throw new IllegalArgumentException("API key cannot be null or empty");
        }

        this.attributes = attributes;
        this.kristaMediaClient = kristaMediaClient;
        LOG.info("TranscriptionService initialized with base URL: {}", attributes.getBaseUrl());
        LOG.debug("API key configured: {}", attributes.getApiKey() != null ? "Yes" : "No");
    }

    /**
     * Creates a REST client instance.
     *
     * @return AssemblyAIRestClient instance
     */
    private AssemblyAIRestClient getRestClient() {
        LOG.trace("Creating AssemblyAI REST client for base URL: {}", attributes.getBaseUrl());
        return new AssemblyAIRestClient(attributes.getBaseUrl(), attributes.getApiKey());
    }

    /**
     * Uploads an audio file to AssemblyAI.
     *
     * @param audioFile The audio file to upload
     * @return UploadResponse containing the upload URL
     * @throws IOException   if file operations fail
     * @throws JSONException if JSON parsing fails
     */
    public UploadResponse uploadAudioFile(File audioFile) throws IOException, JSONException {
        validateAudioFile(audioFile);

        LOG.info("Uploading audio file: {}", audioFile.getName());

        AssemblyAIRestClient restClient = getRestClient();
        JSONObject response = restClient.uploadFile(audioFile);

        UploadResponse uploadResponse = UploadResponse.fromJson(response);
        LOG.info("File uploaded successfully. Upload URL: {}", uploadResponse.getUploadUrl());

        return uploadResponse;
    }

    /**
     * Creates a transcript from an audio URL.
     *
     * @param request Transcript request parameters
     * @return TranscriptResponse with the transcript ID and initial status
     * @throws JSONException if JSON operations fail
     */
    public TranscriptResponse createTranscript(TranscriptRequest request) throws JSONException {
        LOG.info("Creating transcript for audio URL: {}", request.getAudioUrl());

        if (request.getAudioUrl() == null || request.getAudioUrl().trim().isEmpty()) {
            throw new IllegalArgumentException("Audio URL is required");
        }

        AssemblyAIRestClient restClient = getRestClient();
        JSONObject requestJson = request.toJson();
        JSONObject response = restClient.createTranscript(requestJson);

        TranscriptResponse transcriptResponse = TranscriptResponse.fromJson(response);
        LOG.info("Transcript created with ID: {}, Status: {}", transcriptResponse.getId(), transcriptResponse.getStatus());

        return transcriptResponse;
    }

    /**
     * Gets the status and details of a transcript.
     *
     * @param transcriptId The transcript ID
     * @return TranscriptResponse with current status and details
     * @throws JSONException if JSON parsing fails
     */
    public TranscriptResponse getTranscript(String transcriptId) throws JSONException {
        LOG.info("Getting transcript: {}", transcriptId);

        if (transcriptId == null || transcriptId.trim().isEmpty()) {
            throw new IllegalArgumentException("Transcript ID is required");
        }

        AssemblyAIRestClient restClient = getRestClient();
        JSONObject response = restClient.getTranscript(transcriptId);

        TranscriptResponse transcriptResponse = TranscriptResponse.fromJson(response);
        LOG.info("Transcript {} status: {}", transcriptId, transcriptResponse.getStatus());

        return transcriptResponse;
    }

    /**
     * Polls for transcript completion.
     *
     * @param transcriptId      The transcript ID
     * @param pollingIntervalMs Polling interval in milliseconds
     * @param maxAttempts       Maximum number of polling attempts
     * @return Completed TranscriptResponse
     * @throws JSONException        if JSON parsing fails
     * @throws InterruptedException if polling is interrupted
     * @throws IllegalArgumentException if parameters are invalid
     */
    public TranscriptResponse pollForCompletion(String transcriptId, int pollingIntervalMs, int maxAttempts)
            throws JSONException, InterruptedException {
        if (transcriptId == null || transcriptId.trim().isEmpty()) {
            throw new IllegalArgumentException("Transcript ID cannot be null or empty");
        }
        if (pollingIntervalMs <= 0) {
            throw new IllegalArgumentException("Polling interval must be positive");
        }
        if (maxAttempts <= 0) {
            throw new IllegalArgumentException("Max attempts must be positive");
        }

        LOG.info("Polling for transcript completion: {}", transcriptId);

        int attempts = 0;
        TranscriptResponse response;

        while (attempts < maxAttempts) {
            response = getTranscript(transcriptId);

            if (response.isCompleted()) {
                LOG.info("Transcript completed successfully: {}", transcriptId);
                return response;
            }

            if (response.hasError()) {
                String errorMsg = response.getError() != null ? response.getError() : "Unknown error";
                LOG.error("Transcript failed with error: {}", errorMsg);
                throw new RuntimeException("Transcript failed: " + errorMsg);
            }

            if (response.isProcessing()) {
                LOG.debug("Transcript still processing. Attempt {}/{}", attempts + 1, maxAttempts);
                Thread.sleep(pollingIntervalMs);
                attempts++;
            } else {
                LOG.warn("Unexpected transcript status: {}", response.getStatus());
                Thread.sleep(pollingIntervalMs);
                attempts++;
            }
        }

        throw new RuntimeException("Transcript polling timed out after " + maxAttempts + " attempts");
    }

    /**
     * Uploads an audio file and creates a transcript in one operation.
     *
     * @param audioFile         The audio file to upload and transcribe
     * @param speakerLabels     Enable speaker diarization
     * @param sentimentAnalysis Enable sentiment analysis
     * @param iabCategories     Enable topic detection
     * @param redactPii         Enable PII redaction
     * @return TranscriptResponse with the transcript ID and initial status
     * @throws IOException   if file operations fail
     * @throws JSONException if JSON operations fail
     */
    public TranscriptResponse uploadAndTranscribe(File audioFile, Boolean speakerLabels,
                                                   Boolean sentimentAnalysis, Boolean iabCategories,
                                                   Boolean redactPii) throws IOException, JSONException {
        // Validate input parameters
        if (audioFile == null) {
            throw new IllegalArgumentException("Audio file cannot be null");
        }

        // Upload the file (validation happens inside uploadAudioFile)
        UploadResponse uploadResponse = uploadAudioFile(audioFile);

        LOG.info("Upload and transcribe: {}", audioFile.getName());

        // Create transcript request
        TranscriptRequest request = new TranscriptRequest(uploadResponse.getUploadUrl());
        request.setSpeakerLabels(speakerLabels != null ? speakerLabels : false);
        request.setSentimentAnalysis(sentimentAnalysis != null ? sentimentAnalysis : false);
        request.setIabCategories(iabCategories != null ? iabCategories : false);
        request.setRedactPii(redactPii != null ? redactPii : false);

        // Create transcript
        return createTranscript(request);
    }

    /**
     * Validates an audio file before upload.
     *
     * @param audioFile The file to validate
     * @throws IllegalArgumentException if validation fails
     */
    private void validateAudioFile(File audioFile) {
        LOG.debug("Validating audio file: {}", audioFile != null ? audioFile.getName() : "null");

        if (audioFile == null) {
            LOG.error("Validation failed: Audio file is null");
            throw new IllegalArgumentException("Audio file is required");
        }

        if (!audioFile.exists()) {
            LOG.error("Validation failed: Audio file does not exist: {}", audioFile.getPath());
            throw new IllegalArgumentException("Audio file does not exist: " + audioFile.getPath());
        }

        if (!audioFile.isFile()) {
            LOG.error("Validation failed: Path is not a file: {}", audioFile.getPath());
            throw new IllegalArgumentException("Path is not a file: " + audioFile.getPath());
        }

        if (audioFile.length() == 0) {
            LOG.error("Validation failed: Audio file is empty: {}", audioFile.getName());
            throw new IllegalArgumentException("Audio file is empty");
        }

        long fileSizeMB = audioFile.length() / (1024 * 1024);
        LOG.debug("Audio file size: {} MB", fileSizeMB);

        if (audioFile.length() > AssemblyAIConstants.MAX_FILE_SIZE_BYTES) {
            LOG.error("Validation failed: Audio file exceeds maximum size. Size: {} MB, Max: 500 MB", fileSizeMB);
            throw new IllegalArgumentException("Audio file exceeds maximum size of 500 MB");
        }

        // Validate file extension
        String fileName = audioFile.getName().toLowerCase();
        boolean validExtension = false;
        for (String ext : AssemblyAIConstants.SUPPORTED_AUDIO_FORMATS) {
            if (fileName.endsWith(ext)) {
                validExtension = true;
                break;
            }
        }

        if (!validExtension) {
            LOG.error("Validation failed: Unsupported audio format: {}. Supported formats: {}",
                    fileName, String.join(", ", AssemblyAIConstants.SUPPORTED_AUDIO_FORMATS));
            throw new IllegalArgumentException("Unsupported audio format. Supported formats: " +
                    String.join(", ", AssemblyAIConstants.SUPPORTED_AUDIO_FORMATS));
        }

        LOG.debug("Audio file validation passed: {} ({} MB)", audioFile.getName(), fileSizeMB);
    }

    /**
     * Complete end-to-end call transcription with diarization, sentiment analysis, and topic detection.
     * This method handles the entire workflow:
     * 1. Converts Krista file to Java file
     * 2. Uploads to AssemblyAI
     * 3. Creates transcript with all features
     * 4. Polls until completion
     * 5. Extracts and formats results
     * 6. Creates transcript file
     * 7. Converts back to Krista file
     *
     * @param audioFile The Krista audio file to transcribe
     * @param redactPii Whether to redact PII (defaults to false if null)
     * @return CallTranscriptionResult containing all transcription data
     * @throws IOException if file operations fail
     * @throws JSONException if JSON parsing fails
     * @throws InterruptedException if polling is interrupted
     * @throws IllegalArgumentException if audioFile is null
     */
    public CallTranscriptionResult transcribeCallComplete(app.krista.model.base.File audioFile, Boolean redactPii)
            throws IOException, JSONException, InterruptedException {

        // Validate input
        if (audioFile == null) {
            LOG.error("Audio file cannot be null");
            throw new IllegalArgumentException("Audio file cannot be null");
        }

        File tempAudioFile = null;
        File tempTranscriptFile = null;

        try {
            // Default redactPii to false if not provided
            if (redactPii == null) {
                redactPii = false;
            }

            // Step 1: Convert Krista file to Java file
            LOG.info("Converting Krista file to Java file");
            tempAudioFile = kristaMediaClient.toJavaFile(audioFile);

            // Step 2: Upload and create transcript
            LOG.info("Uploading and creating transcript");
            TranscriptResponse transcriptResponse = uploadAndTranscribe(
                    tempAudioFile,
                    true,  // speaker_labels - always enabled for call transcription
                    true,  // sentiment_analysis - always enabled
                    true,  // iab_categories - always enabled for topic detection
                    redactPii
            );

            String transcriptId = transcriptResponse.getId();
            LOG.info("Transcript created with ID: {}", transcriptId);

            // Step 3: Poll for completion
            LOG.info("Polling for transcript completion");
            TranscriptResponse completedResponse = pollForCompletion(
                    transcriptId,
                    AssemblyAIConstants.DEFAULT_POLLING_INTERVAL_MS,
                    AssemblyAIConstants.MAX_POLLING_ATTEMPTS
            );

            // Step 4: Check if transcription was successful
            boolean isSuccessful = completedResponse.isCompleted();
            LOG.info("Transcription completed: {}", isSuccessful);

            if (!isSuccessful) {
                String errorMsg = completedResponse.getError() != null ?
                        completedResponse.getError() : "Transcription failed with unknown error";
                LOG.error("Transcription failed: {}", errorMsg);

                return new CallTranscriptionResult(
                        false,
                        null,
                        "",
                        "Error: " + errorMsg,
                        ""
                );
            }

            // Step 5: Extract diarized transcript text (fallback to plain text if utterances are unavailable)
            String transcriptText = buildDiarizedTranscript(completedResponse);
            LOG.info("Transcript text length: {} characters", transcriptText.length());

            // Step 6: Extract sentiment analysis
            String sentiment = extractSentiment(completedResponse);
            LOG.info("Sentiment extracted: {}", sentiment);

            // Step 7: Extract topics
            String topics = extractTopics(completedResponse);
            LOG.info("Topics extracted: {}", topics);

            // Step 8: Create transcript file
            LOG.info("Creating transcript file");
            tempTranscriptFile = createTranscriptFile(audioFile.getFileName(), transcriptText, sentiment, topics);

            // Step 9: Convert to Krista file
            app.krista.model.base.File kristaTranscriptFile = kristaMediaClient.toKristaFile(tempTranscriptFile);
            LOG.info("Transcript file created: {}", kristaTranscriptFile.getFileName());

            // Step 10: Return result
            LOG.info("Call transcription completed successfully");
            return new CallTranscriptionResult(
                    true,
                    kristaTranscriptFile,
                    transcriptText,
                    sentiment,
                    topics
            );

        } finally {
            // Clean up temporary files
            if (tempAudioFile != null && tempAudioFile.exists()) {
                boolean deleted = tempAudioFile.delete();
                LOG.debug("Temporary audio file deleted: {}", deleted);
            }
            if (tempTranscriptFile != null && tempTranscriptFile.exists()) {
                boolean deleted = tempTranscriptFile.delete();
                LOG.debug("Temporary transcript file deleted: {}", deleted);
            }
        }
    }

    /**
     * Extracts sentiment analysis summary from the transcript response.
     *
     * @param response The completed transcript response
     * @return Sentiment summary string
     */
    private String extractSentiment(TranscriptResponse response) {
        LOG.debug("Extracting sentiment analysis from transcript response");

        if (response.getSentimentAnalysisResults() == null || response.getSentimentAnalysisResults().isEmpty()) {
            LOG.warn("No sentiment analysis results available in transcript response");
            return "No sentiment data available";
        }

        // Count sentiment occurrences
        Map<String, Long> sentimentCounts = response.getSentimentAnalysisResults().stream()
                .collect(Collectors.groupingBy(
                        SentimentAnalysisResult::getSentiment,
                        Collectors.counting()
                ));

        LOG.debug("Sentiment analysis results: {} total segments, {} unique sentiments",
                response.getSentimentAnalysisResults().size(), sentimentCounts.size());

        // Calculate percentages
        long total = response.getSentimentAnalysisResults().size();
        StringBuilder sentimentSummary = new StringBuilder();

        sentimentCounts.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .forEach(entry -> {
                    String sentiment = entry.getKey();
                    long count = entry.getValue();
                    double percentage = (count * 100.0) / total;
                    sentimentSummary.append(String.format("%s: %.1f%% (%d), ",
                            sentiment, percentage, count));
                });

        // Remove trailing comma and space
        if (sentimentSummary.length() > 2) {
            sentimentSummary.setLength(sentimentSummary.length() - 2);
        }

        String result = sentimentSummary.toString();
        LOG.debug("Sentiment summary generated: {}", result);
        return result;
    }

    /**
     * Extracts top topics from IAB categories.
     *
     * @param response The completed transcript response
     * @return Topics summary string
     */
    private String extractTopics(TranscriptResponse response) {
        LOG.debug("Extracting topics from IAB categories");

        if (response.getIabCategoriesResult() == null || response.getIabCategoriesResult().isEmpty()) {
            LOG.warn("No IAB categories results available in transcript response");
            return "No topics detected";
        }

        try {
            // The IAB categories result contains a "summary" field with top categories
            Object summaryObj = response.getIabCategoriesResult().get("summary");
            if (summaryObj instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> summary = (Map<String, Object>) summaryObj;

                LOG.debug("Found {} IAB categories in summary", summary.size());

                // Extract top categories with their relevance scores
                List<String> topCategories = new ArrayList<>();
                summary.forEach((category, relevance) -> {
                    if (relevance instanceof Number) {
                        double score = ((Number) relevance).doubleValue();
                        if (score > 0.3) { // Only include categories with >30% relevance
                            topCategories.add(String.format("%s (%.1f%%)", category, score * 100));
                            LOG.trace("Topic included: {} with score {}", category, score);
                        } else {
                            LOG.trace("Topic excluded (low score): {} with score {}", category, score);
                        }
                    }
                });

                if (!topCategories.isEmpty()) {
                    String result = String.join(", ", topCategories);
                    LOG.debug("Extracted {} relevant topics (>30% relevance): {}", topCategories.size(), result);
                    return result;
                }
            }

            // Fallback: just list the keys
            String fallbackResult = String.join(", ", response.getIabCategoriesResult().keySet());
            LOG.debug("Using fallback topic extraction: {}", fallbackResult);
            return fallbackResult;

        } catch (Exception e) {
            LOG.warn("Error extracting topics: {}", e.getMessage(), e);
            return "Topics available but could not be parsed";
        }
    }
    /**
     * Builds a diarized transcript text from utterances.
     * Falls back to the plain transcript text when utterances are not available.
     *
     * @param response The completed transcript response
     * @return Formatted transcript text with speaker labels and time ranges
     */
    private String buildDiarizedTranscript(TranscriptResponse response) {
        if (response == null) {
            return "";
        }

        if (response.getUtterances() == null || response.getUtterances().isEmpty()) {
            String text = response.getText();
            return text != null ? text : "";
        }

        StringBuilder builder = new StringBuilder();
        for (Utterance utterance : response.getUtterances()) {
            if (utterance == null) {
                continue;
            }

            String speaker = utterance.getSpeaker() != null ? utterance.getSpeaker() : "?";
            String start = formatTime(utterance.getStart());
            String end = formatTime(utterance.getEnd());
            String text = utterance.getText() != null ? utterance.getText() : "";

            builder.append("Speaker ")
                    .append(speaker)
                    .append(" (")
                    .append(start)
                    .append(" - ")
                    .append(end)
                    .append("): ")
                    .append(text)
                    .append(System.lineSeparator());
        }

        return builder.toString().trim();
    }

    /**
     * Formats milliseconds to a mm:ss time string.
     *
     * @param millis Time in milliseconds (may be null)
     * @return Formatted time string
     */
    private String formatTime(Integer millis) {
        if (millis == null || millis < 0) {
            return "0:00";
        }
        int totalSeconds = millis / 1000;
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        return String.format("%d:%02d", minutes, seconds);
    }



    /**
     * Creates a text file with the transcript and analysis results.
     *
     * @param originalFileName Original audio file name
     * @param transcriptText The transcript text
     * @param sentiment Sentiment summary
     * @param topics Topics summary
     * @return File object containing the transcript
     * @throws IOException if file creation fails
     * @throws IllegalArgumentException if required parameters are null or empty
     */
    private File createTranscriptFile(String originalFileName, String transcriptText,
                                      String sentiment, String topics) throws IOException {
        // Validate inputs
        if (originalFileName == null || originalFileName.trim().isEmpty()) {
            throw new IllegalArgumentException("Original file name cannot be null or empty");
        }
        if (transcriptText == null) {
            transcriptText = ""; // Allow empty transcript but not null
        }
        if (sentiment == null) {
            sentiment = "No sentiment data available";
        }
        if (topics == null) {
            topics = "No topics detected";
        }

        LOG.debug("Creating transcript file for: {}", originalFileName);

        // Generate transcript file name
        String baseName = originalFileName.contains(".") ?
                originalFileName.substring(0, originalFileName.lastIndexOf(".")) :
                originalFileName;

        LOG.debug("Base name for transcript: {}", baseName);

        // Create temporary file
        File transcriptFile = File.createTempFile("transcript_", ".txt");
        LOG.debug("Temporary transcript file created at: {}", transcriptFile.getAbsolutePath());

        // Write content - only the transcript text (no metadata headers)
        try (FileWriter writer = new FileWriter(transcriptFile)) {
            writer.write(transcriptText);
        }

        long fileSizeKB = transcriptFile.length() / 1024;
        LOG.info("Transcript file created successfully: {} ({} KB)", transcriptFile.getName(), fileSizeKB);

        return transcriptFile;
    }

    /**
     * Result object for complete call transcription.
     */
    public static class CallTranscriptionResult {
        private final boolean successful;
        private final app.krista.model.base.File transcriptFile;
        private final String transcriptText;
        private final String sentiment;
        private final String topics;

        public CallTranscriptionResult(boolean successful, app.krista.model.base.File transcriptFile,
                                       String transcriptText, String sentiment, String topics) {
            this.successful = successful;
            this.transcriptFile = transcriptFile;
            this.transcriptText = transcriptText;
            this.sentiment = sentiment;
            this.topics = topics;
        }

        public boolean isSuccessful() {
            return successful;
        }

        public app.krista.model.base.File getTranscriptFile() {
            return transcriptFile;
        }

        public String getTranscriptText() {
            return transcriptText;
        }

        public String getSentiment() {
            return sentiment;
        }

        public String getTopics() {
            return topics;
        }
    }
}
