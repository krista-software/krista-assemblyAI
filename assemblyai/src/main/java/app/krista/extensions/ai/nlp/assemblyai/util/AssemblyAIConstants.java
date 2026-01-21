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

package app.krista.extensions.ai.nlp.assemblyai.util;

/**
 * Constants for AssemblyAI API integration.
 * Contains API endpoints, field names, and configuration values.
 */
public final class AssemblyAIConstants {

    private AssemblyAIConstants() {
        // Private constructor to prevent instantiation
    }

    // Extension Configuration
    public static final String EXTENSION_NAME = "AssemblyAI";
    public static final String JAXRS_ID = "assemblyai";

    // Connection Attributes
    public static final String API_KEY = "apiKey";
    public static final String LOG_LEVEL = "logLevel";

    // Default API Configuration
    public static final String DEFAULT_BASE_URL = "https://api.assemblyai.com/v2";

    // API Endpoints
    public static final String UPLOAD_ENDPOINT = "/upload";
    public static final String TRANSCRIPT_ENDPOINT = "/transcript";

    // HTTP Headers
    public static final String AUTHORIZATION_HEADER = "authorization";
    public static final String CONTENT_TYPE_HEADER = "Content-Type";

    // JSON Field Names - Upload Response
    public static final String UPLOAD_URL = "upload_url";

    // JSON Field Names - Transcript Request
    public static final String AUDIO_URL = "audio_url";
    public static final String SPEAKER_LABELS = "speaker_labels";
    public static final String SENTIMENT_ANALYSIS = "sentiment_analysis";
    public static final String IAB_CATEGORIES = "iab_categories";
    public static final String REDACT_PII = "redact_pii";
    public static final String LANGUAGE_CODE = "language_code";
    public static final String PUNCTUATE = "punctuate";
    public static final String FORMAT_TEXT = "format_text";

    // JSON Field Names - Transcript Response
    public static final String ID = "id";
    public static final String STATUS = "status";
    public static final String TEXT = "text";
    public static final String WORDS = "words";
    public static final String UTTERANCES = "utterances";
    public static final String CONFIDENCE = "confidence";
    public static final String AUDIO_DURATION = "audio_duration";
    public static final String ERROR = "error";
    public static final String SENTIMENT_ANALYSIS_RESULTS = "sentiment_analysis_results";
    public static final String IAB_CATEGORIES_RESULT = "iab_categories_result";

    // JSON Field Names - Word
    public static final String WORD_TEXT = "text";
    public static final String START = "start";
    public static final String END = "end";
    public static final String SPEAKER = "speaker";

    // JSON Field Names - Utterance
    public static final String UTTERANCE_TEXT = "text";
    public static final String UTTERANCE_START = "start";
    public static final String UTTERANCE_END = "end";
    public static final String UTTERANCE_CONFIDENCE = "confidence";
    public static final String UTTERANCE_SPEAKER = "speaker";

    // JSON Field Names - Sentiment Analysis
    public static final String SENTIMENT = "sentiment";
    public static final String SENTIMENT_CONFIDENCE = "confidence";

    // Transcript Status Values
    public static final String STATUS_QUEUED = "queued";
    public static final String STATUS_PROCESSING = "processing";
    public static final String STATUS_COMPLETED = "completed";
    public static final String STATUS_ERROR = "error";

    // Sentiment Values
    public static final String SENTIMENT_POSITIVE = "POSITIVE";
    public static final String SENTIMENT_NEGATIVE = "NEGATIVE";
    public static final String SENTIMENT_NEUTRAL = "NEUTRAL";

    // Error Messages
    public static final String ERROR_INVALID_API_KEY = "Invalid API key provided";
    public static final String ERROR_UPLOAD_FAILED = "Failed to upload audio file";
    public static final String ERROR_TRANSCRIPT_CREATION_FAILED = "Failed to create transcript";
    public static final String ERROR_TRANSCRIPT_NOT_FOUND = "Transcript not found";
    public static final String ERROR_INVALID_FILE = "Invalid audio file";
    public static final String ERROR_CONNECTION_FAILED = "Failed to connect to AssemblyAI API";

    // Polling Configuration
    public static final int DEFAULT_POLLING_INTERVAL_MS = 3000; // 3 seconds
    public static final int MAX_POLLING_ATTEMPTS = 200; // 10 minutes max (200 * 3 seconds)

    // File Upload Configuration
    public static final long MAX_FILE_SIZE_BYTES = 500L * 1024 * 1024; // 500 MB
    public static final String[] SUPPORTED_AUDIO_FORMATS = {".wav", ".mp3", ".mp4", ".m4a", ".flac", ".ogg"};
}

