# API Reference - AssemblyAI Extension for Krista

Complete API reference for the AssemblyAI Extension.

---

## Table of Contents

- [Catalog Layer API](#catalog-layer-api)
- [Service Layer API](#service-layer-api)
- [Model Classes](#model-classes)
- [Error Codes](#error-codes)
- [Constants](#constants)

---

## Catalog Layer API

### TranscribeCallQuery

Main catalog request for transcribing audio files.

#### Constructor
```java
public TranscribeCallQuery(AssemblyAIAttributes attributes)
```

#### Method: execute

Transcribes an audio file with optional PII redaction.

```java
public ExtensionResponse execute(File audioFile, Boolean redactPii)
```

**Parameters:**
- `audioFile` (File, required): Audio file to transcribe
  - Supported formats: MP3, WAV, FLAC, M4A, AAC, OGG, WebM
  - Maximum size: 5 GB
  - Must exist and be readable
- `redactPii` (Boolean, required): Enable PII redaction
  - `true`: Redact sensitive information (SSN, credit cards, etc.)
  - `false`: No redaction

**Returns:** `ExtensionResponse`

**Success Response:**
```json
{
  "successful": true,
  "transcriptText": "Full transcript text...",
  "transcriptFile": {
    "id": "file-123",
    "name": "transcript.txt",
    "size": 1024
  },
  "sentiment": "POSITIVE",
  "topics": ["Technology>AI", "Business>Customer Service"],
  "duration": 45.2,
  "timestamp": 1699564800000
}
```

**Error Response:**
```json
{
  "successful": false,
  "errorMessage": "Audio file is required",
  "errorCode": "INPUT_ERROR",
  "timestamp": 1699564800000
}
```

**Throws:**
- `IllegalArgumentException`: If audioFile is null or invalid
- `IOException`: If file cannot be read
- `RuntimeException`: If transcription fails

**Example:**
```java
TranscribeCallQuery query = new TranscribeCallQuery(attributes);
File audioFile = new File("path/to/audio.mp3");
ExtensionResponse response = query.execute(audioFile, false);
```

---

## Service Layer API

### TranscriptionService

Core service for AssemblyAI operations.

#### Constructor
```java
public TranscriptionService(AssemblyAIAttributes attributes, 
                           KristaMediaClient kristaMediaClient)
```

**Parameters:**
- `attributes`: Extension configuration
- `kristaMediaClient`: Krista media client (can be null for standalone usage)

---

### Method: uploadAudioFile

Uploads an audio file to AssemblyAI.

```java
public UploadResponse uploadAudioFile(File audioFile) throws IOException
```

**Parameters:**
- `audioFile` (File, required): Audio file to upload

**Returns:** `UploadResponse` containing upload URL

**Throws:**
- `IllegalArgumentException`: If file is null or invalid
- `IOException`: If upload fails

**Example:**
```java
TranscriptionService service = new TranscriptionService(attributes, null);
UploadResponse response = service.uploadAudioFile(audioFile);
String uploadUrl = response.getUploadUrl();
```

---

### Method: createTranscript

Creates a transcript from an audio URL.

```java
public TranscriptResponse createTranscript(TranscriptRequest request) 
    throws IOException
```

**Parameters:**
- `request` (TranscriptRequest, required): Transcript configuration

**Returns:** `TranscriptResponse` with transcript ID and status

**Throws:**
- `IllegalArgumentException`: If request is null or invalid
- `IOException`: If API call fails

**Example:**
```java
TranscriptRequest request = new TranscriptRequest(uploadUrl);
request.setSpeakerLabels(true);
request.setSentimentAnalysis(true);
request.setIabCategories(true);
request.setRedactPii(false);

TranscriptResponse response = service.createTranscript(request);
```

---

### Method: getTranscript

Retrieves transcript status and results.

```java
public TranscriptResponse getTranscript(String transcriptId) 
    throws IOException
```

**Parameters:**
- `transcriptId` (String, required): Transcript ID from createTranscript

**Returns:** `TranscriptResponse` with current status and results

**Throws:**
- `IllegalArgumentException`: If transcriptId is null or empty
- `IOException`: If API call fails

**Example:**
```java
TranscriptResponse response = service.getTranscript("transcript-123");
System.out.println("Status: " + response.getStatus());
```

---

### Method: pollForCompletion

Polls for transcript completion.

```java
public TranscriptResponse pollForCompletion(String transcriptId,
                                           int pollIntervalMs,
                                           int maxAttempts) 
    throws IOException, InterruptedException
```

**Parameters:**
- `transcriptId` (String, required): Transcript ID
- `pollIntervalMs` (int, required): Polling interval in milliseconds
- `maxAttempts` (int, required): Maximum polling attempts

**Returns:** `TranscriptResponse` when completed or failed

**Throws:**
- `IllegalArgumentException`: If parameters are invalid
- `IOException`: If API call fails
- `InterruptedException`: If polling is interrupted
- `RuntimeException`: If max attempts exceeded

**Example:**
```java
// Poll every 3 seconds, max 200 attempts (10 minutes)
TranscriptResponse completed = service.pollForCompletion(
    transcriptId, 3000, 200
);
```

---

### Method: uploadAndTranscribe

Uploads and transcribes in one call.

```java
public TranscriptResponse uploadAndTranscribe(File audioFile,
                                              boolean speakerLabels,
                                              boolean sentimentAnalysis,
                                              boolean iabCategories,
                                              boolean redactPii) 
    throws IOException
```

**Parameters:**
- `audioFile` (File, required): Audio file
- `speakerLabels` (boolean): Enable speaker diarization
- `sentimentAnalysis` (boolean): Enable sentiment analysis
- `iabCategories` (boolean): Enable topic detection
- `redactPii` (boolean): Enable PII redaction

**Returns:** `TranscriptResponse` with transcript ID

**Example:**
```java
TranscriptResponse response = service.uploadAndTranscribe(
    audioFile, true, true, true, false
);
```

---

### Method: transcribeCallComplete

Complete end-to-end transcription workflow.

```java
public CallTranscriptionResult transcribeCallComplete(File audioFile,
                                                     boolean speakerLabels,
                                                     boolean sentimentAnalysis,
                                                     boolean iabCategories,
                                                     boolean redactPii) 
    throws IOException, InterruptedException
```

**Parameters:**
- Same as uploadAndTranscribe

**Returns:** `CallTranscriptionResult` with all results

**Example:**
```java
CallTranscriptionResult result = service.transcribeCallComplete(
    audioFile, true, true, true, false
);

System.out.println("Transcript: " + result.getTranscriptText());
System.out.println("Sentiment: " + result.getSentiment());
System.out.println("Topics: " + result.getTopics());
```

---

### Method: validateAudioFile

Validates an audio file.

```java
public void validateAudioFile(File audioFile)
```

**Parameters:**
- `audioFile` (File, required): File to validate

**Throws:**
- `IllegalArgumentException`: If file is invalid

**Validation Rules:**
- File must not be null
- File must exist
- File must be readable
- File size must be > 0

**Example:**
```java
try {
    service.validateAudioFile(audioFile);
    System.out.println("File is valid");
} catch (IllegalArgumentException e) {
    System.err.println("Invalid file: " + e.getMessage());
}
```

---

### Method: extractSentiment

Extracts overall sentiment from results.

```java
public String extractSentiment(List<SentimentAnalysisResult> sentimentResults)
```

**Parameters:**
- `sentimentResults` (List, nullable): Sentiment analysis results

**Returns:** String sentiment ("POSITIVE", "NEUTRAL", "NEGATIVE", or "UNKNOWN")

**Example:**
```java
String sentiment = service.extractSentiment(
    transcript.getSentimentAnalysisResults()
);
```

---

### Method: extractTopics

Extracts topics from IAB categories.

```java
public List<String> extractTopics(Map<String, Object> iabCategoriesResult)
```

**Parameters:**
- `iabCategoriesResult` (Map, nullable): IAB categories result

**Returns:** List of topic strings

**Example:**
```java
List<String> topics = service.extractTopics(
    transcript.getIabCategoriesResult()
);
```

---

### Method: createTranscriptFile

Creates a Krista File from transcript text.

```java
public app.krista.model.base.File createTranscriptFile(String transcriptText)
```

**Parameters:**
- `transcriptText` (String, required): Transcript text

**Returns:** Krista File object

**Example:**
```java
app.krista.model.base.File file = service.createTranscriptFile(
    transcript.getText()
);
```

---

## Model Classes

### TranscriptRequest

Configuration for transcript creation.

**Fields:**
- `audioUrl` (String, required): URL of uploaded audio
- `speakerLabels` (Boolean): Enable speaker diarization
- `sentimentAnalysis` (Boolean): Enable sentiment analysis
- `iabCategories` (Boolean): Enable topic detection
- `redactPii` (Boolean): Enable PII redaction
- `languageCode` (String): Language code (default: "en")

**Methods:**
```java
public TranscriptRequest(String audioUrl)
public void setSpeakerLabels(Boolean speakerLabels)
public void setSentimentAnalysis(Boolean sentimentAnalysis)
public void setIabCategories(Boolean iabCategories)
public void setRedactPii(Boolean redactPii)
public void setLanguageCode(String languageCode)
public JSONObject toJSON()
```

---

### TranscriptResponse

Response from transcript operations.

**Fields:**
- `id` (String): Transcript ID
- `status` (String): Status (queued, processing, completed, error)
- `text` (String): Transcript text (when completed)
- `words` (List<Word>): Word-level details
- `utterances` (List<Utterance>): Speaker utterances
- `sentimentAnalysisResults` (List<SentimentAnalysisResult>): Sentiment data
- `iabCategoriesResult` (Map): Topic categories
- `error` (String): Error message (if failed)

**Methods:**
```java
public static TranscriptResponse fromJSON(JSONObject json)
public boolean isCompleted()
public boolean isFailed()
public String getId()
public String getStatus()
public String getText()
// ... getters for all fields
```

---

### UploadResponse

Response from file upload.

**Fields:**
- `uploadUrl` (String): URL of uploaded file

**Methods:**
```java
public static UploadResponse fromJSON(JSONObject json)
public String getUploadUrl()
```

---

### CallTranscriptionResult

Complete transcription result.

**Fields:**
- `successful` (boolean): Success flag
- `transcriptFile` (File): Transcript file
- `transcriptText` (String): Full transcript
- `sentiment` (String): Overall sentiment
- `topics` (List<String>): Detected topics

**Methods:**
```java
public boolean isSuccessful()
public File getTranscriptFile()
public String getTranscriptText()
public String getSentiment()
public List<String> getTopics()
```

---

## Error Codes

### INPUT_ERROR
Invalid input parameters.

**Common Causes:**
- Null or missing required parameters
- Invalid file format
- File doesn't exist or isn't readable
- Empty or invalid URLs

**Resolution:** Validate inputs before calling API

---

### LOGIC_ERROR
Business logic failure.

**Common Causes:**
- Transcription failed
- Polling timeout
- Invalid transcript status

**Resolution:** Check logs for details, retry if transient

---

### SYSTEM_ERROR
System-level failure.

**Common Causes:**
- Network errors
- API unavailable
- Authentication failure

**Resolution:** Check network, API key, and service status

---

## Constants

### AssemblyAIConstants

```java
public class AssemblyAIConstants {
    // API Endpoints
    public static final String UPLOAD_ENDPOINT = "/upload";
    public static final String TRANSCRIPT_ENDPOINT = "/transcript";
    
    // Status Values
    public static final String STATUS_QUEUED = "queued";
    public static final String STATUS_PROCESSING = "processing";
    public static final String STATUS_COMPLETED = "completed";
    public static final String STATUS_ERROR = "error";
    
    // Default Values
    public static final int DEFAULT_POLL_INTERVAL_MS = 3000;
    public static final int DEFAULT_MAX_ATTEMPTS = 200;
    public static final String DEFAULT_LANGUAGE = "en";
}
```

---

## Usage Patterns

### Pattern 1: Simple Transcription
```java
TranscribeCallQuery query = new TranscribeCallQuery(attributes);
ExtensionResponse response = query.execute(audioFile, false);
```

### Pattern 2: Advanced Features
```java
TranscriptionService service = new TranscriptionService(attributes, null);
TranscriptResponse response = service.uploadAndTranscribe(
    audioFile, true, true, true, false
);
TranscriptResponse completed = service.pollForCompletion(
    response.getId(), 3000, 200
);
```

### Pattern 3: Custom Workflow
```java
// Upload
UploadResponse upload = service.uploadAudioFile(audioFile);

// Configure
TranscriptRequest request = new TranscriptRequest(upload.getUploadUrl());
request.setSpeakerLabels(true);

// Create
TranscriptResponse transcript = service.createTranscript(request);

// Poll
TranscriptResponse completed = service.pollForCompletion(
    transcript.getId(), 5000, 360
);
```

---

**See Also:**
- [Quick Start Guide](../QUICKSTART.md)
- [Examples](EXAMPLES.md)
- [Architecture](../ARCHITECTURE.md)

