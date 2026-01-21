# Usage Examples - AssemblyAI Extension for Krista

Comprehensive examples for common use cases.

---

## Table of Contents

- [Basic Examples](#basic-examples)
- [Advanced Features](#advanced-features)
- [Error Handling](#error-handling)
- [Integration Patterns](#integration-patterns)
- [Real-World Scenarios](#real-world-scenarios)

---

## Basic Examples

### Example 1: Simple Transcription

Transcribe an audio file without any advanced features.

```java
import app.krista.extensions.ai.nlp.assemblyai.catalog.query.TranscribeCallQuery;
import app.krista.extensions.ai.nlp.assemblyai.catalog.AssemblyAIAttributes;
import app.krista.model.base.ExtensionResponse;
import java.io.File;
import java.util.Map;

public class SimpleTranscriptionExample {
    public static void main(String[] args) {
        // Initialize attributes
        AssemblyAIAttributes attributes = new AssemblyAIAttributes(
            "your-api-key",
            "https://api.assemblyai.com/v2",
            "INFO"
        );
        
        // Create catalog request
        TranscribeCallQuery query = new TranscribeCallQuery(attributes);
        
        // Prepare audio file
        File audioFile = new File("path/to/audio.mp3");
        
        // Transcribe without PII redaction
        ExtensionResponse response = query.execute(audioFile, false);
        
        // Extract results
        Map<String, Object> result = (Map<String, Object>) response.getResponseValue();
        
        if ((Boolean) result.get("successful")) {
            String transcript = (String) result.get("transcriptText");
            System.out.println("Transcript: " + transcript);
        } else {
            String error = (String) result.get("errorMessage");
            System.err.println("Error: " + error);
        }
    }
}
```

---

### Example 2: Transcription with PII Redaction

Automatically redact sensitive information.

```java
public class PiiRedactionExample {
    public static void main(String[] args) {
        AssemblyAIAttributes attributes = new AssemblyAIAttributes(
            "your-api-key",
            "https://api.assemblyai.com/v2",
            "INFO"
        );
        
        TranscribeCallQuery query = new TranscribeCallQuery(attributes);
        File audioFile = new File("path/to/sensitive-audio.mp3");
        
        // Enable PII redaction
        ExtensionResponse response = query.execute(audioFile, true);
        
        Map<String, Object> result = (Map<String, Object>) response.getResponseValue();
        String transcript = (String) result.get("transcriptText");
        
        // Sensitive information will be redacted:
        // "My SSN is 123-45-6789" → "My SSN is [SSN]"
        // "My credit card is 4111-1111-1111-1111" → "My credit card is [CREDIT_CARD]"
        System.out.println("Redacted Transcript: " + transcript);
    }
}
```

---

## Advanced Features

### Example 3: Speaker Diarization

Identify different speakers in a conversation.

```java
import app.krista.extensions.ai.nlp.assemblyai.service.TranscriptionService;
import app.krista.extensions.ai.nlp.assemblyai.service.model.*;

public class SpeakerDiarizationExample {
    public static void main(String[] args) throws Exception {
        AssemblyAIAttributes attributes = new AssemblyAIAttributes(
            "your-api-key",
            "https://api.assemblyai.com/v2",
            "INFO"
        );
        
        TranscriptionService service = new TranscriptionService(attributes, null);
        
        // Upload file
        File audioFile = new File("path/to/conversation.mp3");
        UploadResponse upload = service.uploadAudioFile(audioFile);
        
        // Create transcript with speaker labels
        TranscriptRequest request = new TranscriptRequest(upload.getUploadUrl());
        request.setSpeakerLabels(true);
        
        TranscriptResponse transcript = service.createTranscript(request);
        
        // Poll for completion
        TranscriptResponse completed = service.pollForCompletion(
            transcript.getId(), 3000, 200
        );
        
        // Process utterances (speaker-separated segments)
        for (Utterance utterance : completed.getUtterances()) {
            System.out.println("Speaker " + utterance.getSpeaker() + ": " + 
                             utterance.getText());
        }
        
        /* Output:
         * Speaker A: Hello, how can I help you today?
         * Speaker B: I'd like to inquire about my account.
         * Speaker A: Sure, I can help with that.
         */
    }
}
```

---

### Example 4: Sentiment Analysis

Analyze emotional tone of the conversation.

```java
public class SentimentAnalysisExample {
    public static void main(String[] args) throws Exception {
        AssemblyAIAttributes attributes = new AssemblyAIAttributes(
            "your-api-key",
            "https://api.assemblyai.com/v2",
            "INFO"
        );
        
        TranscriptionService service = new TranscriptionService(attributes, null);
        
        // Upload and create transcript with sentiment analysis
        File audioFile = new File("path/to/audio.mp3");
        UploadResponse upload = service.uploadAudioFile(audioFile);
        
        TranscriptRequest request = new TranscriptRequest(upload.getUploadUrl());
        request.setSentimentAnalysis(true);
        
        TranscriptResponse transcript = service.createTranscript(request);
        TranscriptResponse completed = service.pollForCompletion(
            transcript.getId(), 3000, 200
        );
        
        // Extract overall sentiment
        String overallSentiment = service.extractSentiment(
            completed.getSentimentAnalysisResults()
        );
        System.out.println("Overall Sentiment: " + overallSentiment);
        
        // Analyze sentiment per sentence
        for (SentimentAnalysisResult sentiment : completed.getSentimentAnalysisResults()) {
            System.out.println("Text: " + sentiment.getText());
            System.out.println("Sentiment: " + sentiment.getSentiment());
            System.out.println("Confidence: " + sentiment.getConfidence());
            System.out.println("---");
        }
        
        /* Output:
         * Overall Sentiment: POSITIVE
         * Text: Thank you for your help.
         * Sentiment: POSITIVE
         * Confidence: 0.95
         * ---
         * Text: I'm frustrated with this issue.
         * Sentiment: NEGATIVE
         * Confidence: 0.87
         */
    }
}
```

---

### Example 5: Topic Detection

Automatically categorize content using IAB taxonomy.

```java
public class TopicDetectionExample {
    public static void main(String[] args) throws Exception {
        AssemblyAIAttributes attributes = new AssemblyAIAttributes(
            "your-api-key",
            "https://api.assemblyai.com/v2",
            "INFO"
        );
        
        TranscriptionService service = new TranscriptionService(attributes, null);
        
        File audioFile = new File("path/to/audio.mp3");
        UploadResponse upload = service.uploadAudioFile(audioFile);
        
        TranscriptRequest request = new TranscriptRequest(upload.getUploadUrl());
        request.setIabCategories(true);
        
        TranscriptResponse transcript = service.createTranscript(request);
        TranscriptResponse completed = service.pollForCompletion(
            transcript.getId(), 3000, 200
        );
        
        // Extract topics
        List<String> topics = service.extractTopics(
            completed.getIabCategoriesResult()
        );
        
        System.out.println("Detected Topics:");
        for (String topic : topics) {
            System.out.println("- " + topic);
        }
        
        /* Output:
         * Detected Topics:
         * - Technology>Artificial Intelligence
         * - Business>Customer Service
         * - Technology>Cloud Computing
         */
    }
}
```

---

### Example 6: Complete Workflow with All Features

End-to-end transcription with all features enabled.

```java
public class CompleteWorkflowExample {
    public static void main(String[] args) throws Exception {
        AssemblyAIAttributes attributes = new AssemblyAIAttributes(
            "your-api-key",
            "https://api.assemblyai.com/v2",
            "DEBUG"  // Enable debug logging
        );
        
        TranscriptionService service = new TranscriptionService(attributes, null);
        
        File audioFile = new File("path/to/audio.mp3");
        
        // Complete workflow with all features
        CallTranscriptionResult result = service.transcribeCallComplete(
            audioFile,
            true,   // Speaker labels
            true,   // Sentiment analysis
            true,   // Topic detection
            false   // PII redaction (disabled for this example)
        );
        
        if (result.isSuccessful()) {
            System.out.println("=== Transcription Results ===");
            System.out.println("\nFull Transcript:");
            System.out.println(result.getTranscriptText());
            
            System.out.println("\nOverall Sentiment: " + result.getSentiment());
            
            System.out.println("\nDetected Topics:");
            for (String topic : result.getTopics()) {
                System.out.println("- " + topic);
            }
            
            System.out.println("\nTranscript File:");
            System.out.println("- ID: " + result.getTranscriptFile().getId());
            System.out.println("- Name: " + result.getTranscriptFile().getName());
            System.out.println("- Size: " + result.getTranscriptFile().getSize() + " bytes");
        } else {
            System.err.println("Transcription failed");
        }
    }
}
```

---

## Error Handling

### Example 7: Comprehensive Error Handling

Handle all possible error scenarios.

```java
public class ErrorHandlingExample {
    public static void main(String[] args) {
        AssemblyAIAttributes attributes = new AssemblyAIAttributes(
            "your-api-key",
            "https://api.assemblyai.com/v2",
            "INFO"
        );
        
        TranscriptionService service = new TranscriptionService(attributes, null);
        File audioFile = new File("path/to/audio.mp3");
        
        try {
            // Validate file first
            service.validateAudioFile(audioFile);
            
            // Upload file
            UploadResponse upload = service.uploadAudioFile(audioFile);
            
            // Create transcript
            TranscriptRequest request = new TranscriptRequest(upload.getUploadUrl());
            TranscriptResponse transcript = service.createTranscript(request);
            
            // Poll with timeout handling
            TranscriptResponse completed = service.pollForCompletion(
                transcript.getId(), 3000, 200
            );
            
            if (completed.isFailed()) {
                System.err.println("Transcription failed: " + completed.getError());
                return;
            }
            
            System.out.println("Success: " + completed.getText());
            
        } catch (IllegalArgumentException e) {
            // Input validation errors
            System.err.println("Invalid input: " + e.getMessage());
            
        } catch (IOException e) {
            // Network or API errors
            System.err.println("API error: " + e.getMessage());
            
        } catch (InterruptedException e) {
            // Polling interrupted
            System.err.println("Polling interrupted: " + e.getMessage());
            Thread.currentThread().interrupt();
            
        } catch (RuntimeException e) {
            // Timeout or other runtime errors
            System.err.println("Runtime error: " + e.getMessage());
        }
    }
}
```

---

### Example 8: Retry Logic

Implement retry logic for transient failures.

```java
public class RetryLogicExample {
    private static final int MAX_RETRIES = 3;
    private static final int RETRY_DELAY_MS = 5000;
    
    public static void main(String[] args) {
        AssemblyAIAttributes attributes = new AssemblyAIAttributes(
            "your-api-key",
            "https://api.assemblyai.com/v2",
            "INFO"
        );
        
        TranscriptionService service = new TranscriptionService(attributes, null);
        File audioFile = new File("path/to/audio.mp3");
        
        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            try {
                System.out.println("Attempt " + attempt + " of " + MAX_RETRIES);
                
                UploadResponse upload = service.uploadAudioFile(audioFile);
                TranscriptRequest request = new TranscriptRequest(upload.getUploadUrl());
                TranscriptResponse transcript = service.createTranscript(request);
                TranscriptResponse completed = service.pollForCompletion(
                    transcript.getId(), 3000, 200
                );
                
                System.out.println("Success: " + completed.getText());
                return; // Success, exit
                
            } catch (IOException e) {
                System.err.println("Attempt " + attempt + " failed: " + e.getMessage());
                
                if (attempt < MAX_RETRIES) {
                    try {
                        System.out.println("Retrying in " + RETRY_DELAY_MS + "ms...");
                        Thread.sleep(RETRY_DELAY_MS);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                } else {
                    System.err.println("All retry attempts failed");
                }
            } catch (Exception e) {
                System.err.println("Non-retryable error: " + e.getMessage());
                return;
            }
        }
    }
}
```

---

## Integration Patterns

### Example 9: Batch Processing

Process multiple audio files in batch.

```java
public class BatchProcessingExample {
    public static void main(String[] args) throws Exception {
        AssemblyAIAttributes attributes = new AssemblyAIAttributes(
            "your-api-key",
            "https://api.assemblyai.com/v2",
            "INFO"
        );
        
        TranscriptionService service = new TranscriptionService(attributes, null);
        
        // List of audio files to process
        List<File> audioFiles = Arrays.asList(
            new File("audio1.mp3"),
            new File("audio2.mp3"),
            new File("audio3.mp3")
        );
        
        List<String> transcriptIds = new ArrayList<>();
        
        // Upload and create transcripts for all files
        for (File audioFile : audioFiles) {
            try {
                UploadResponse upload = service.uploadAudioFile(audioFile);
                TranscriptRequest request = new TranscriptRequest(upload.getUploadUrl());
                TranscriptResponse transcript = service.createTranscript(request);
                transcriptIds.add(transcript.getId());
                
                System.out.println("Started transcription for: " + audioFile.getName());
            } catch (Exception e) {
                System.err.println("Failed to start transcription for: " + 
                                 audioFile.getName() + " - " + e.getMessage());
            }
        }
        
        // Poll for completion of all transcripts
        for (String transcriptId : transcriptIds) {
            try {
                TranscriptResponse completed = service.pollForCompletion(
                    transcriptId, 3000, 200
                );
                System.out.println("Completed: " + transcriptId);
                System.out.println("Text: " + completed.getText().substring(0, 100) + "...");
            } catch (Exception e) {
                System.err.println("Failed to complete: " + transcriptId + 
                                 " - " + e.getMessage());
            }
        }
    }
}
```

---

### Example 10: Async Processing with Callbacks

Process transcriptions asynchronously.

```java
import java.util.concurrent.*;

public class AsyncProcessingExample {
    public static void main(String[] args) throws Exception {
        AssemblyAIAttributes attributes = new AssemblyAIAttributes(
            "your-api-key",
            "https://api.assemblyai.com/v2",
            "INFO"
        );
        
        TranscriptionService service = new TranscriptionService(attributes, null);
        ExecutorService executor = Executors.newFixedThreadPool(5);
        
        File audioFile = new File("path/to/audio.mp3");
        
        // Submit async task
        Future<CallTranscriptionResult> future = executor.submit(() -> {
            return service.transcribeCallComplete(
                audioFile, true, true, true, false
            );
        });
        
        // Do other work while transcription is processing
        System.out.println("Transcription started, doing other work...");
        
        // Get result when ready
        try {
            CallTranscriptionResult result = future.get(30, TimeUnit.MINUTES);
            System.out.println("Transcription completed: " + result.getTranscriptText());
        } catch (TimeoutException e) {
            System.err.println("Transcription timed out");
            future.cancel(true);
        } finally {
            executor.shutdown();
        }
    }
}
```

---

## Real-World Scenarios

### Example 11: Customer Service Call Analysis

Analyze customer service calls for quality assurance.

```java
public class CustomerServiceAnalysisExample {
    public static void main(String[] args) throws Exception {
        // ... initialization code ...
        
        CallTranscriptionResult result = service.transcribeCallComplete(
            callRecording, true, true, true, true  // All features + PII redaction
        );
        
        // Analyze call quality
        String sentiment = result.getSentiment();
        List<String> topics = result.getTopics();
        
        // Generate report
        System.out.println("=== Call Analysis Report ===");
        System.out.println("Call Sentiment: " + sentiment);
        System.out.println("Topics Discussed: " + String.join(", ", topics));
        
        // Flag for review if negative sentiment
        if ("NEGATIVE".equals(sentiment)) {
            System.out.println("⚠️ FLAGGED FOR REVIEW: Negative sentiment detected");
        }
        
        // Check for specific topics
        if (topics.stream().anyMatch(t -> t.contains("Complaint"))) {
            System.out.println("⚠️ FLAGGED FOR REVIEW: Complaint detected");
        }
    }
}
```

---

**See Also:**
- [API Reference](API_REFERENCE.md)
- [Troubleshooting](TROUBLESHOOTING.md)
- [Quick Start](../QUICKSTART.md)

