# Quick Start Guide - AssemblyAI Extension for Krista

This guide will help you get up and running with the AssemblyAI extension in under 10 minutes.

---

## 📋 Prerequisites

Before you begin, ensure you have:

- ✅ **Java 21 or higher** installed
- ✅ **Gradle 8.x** installed (or use the included wrapper)
- ✅ **Krista AI platform** (version 1.0.117 or higher)
- ✅ **AssemblyAI API key** ([Sign up here](https://www.assemblyai.com/))

### Verify Java Installation
```bash
java -version
# Should show: openjdk version "21" or higher
```

### Verify Gradle Installation
```bash
./gradlew --version
# Should show: Gradle 8.x
```

---

## 🚀 Step 1: Get Your AssemblyAI API Key

1. Go to [https://www.assemblyai.com/](https://www.assemblyai.com/)
2. Sign up for a free account
3. Navigate to your dashboard
4. Copy your API key from the "API Keys" section

**Note**: The free tier includes 5 hours of transcription per month.

---

## 📥 Step 2: Clone and Build

### Clone the Repository
```bash
git clone https://github.com/your-org/krista-assemblyai-extension.git
cd krista-assemblyai-extension/assemblyai
```

### Build the Extension
```bash
./gradlew clean build
```

**Expected Output:**
```
BUILD SUCCESSFUL in 10s
```

The compiled JAR will be in: `build/libs/AssemblyAI-1.0.0.jar`

---

## ⚙️ Step 3: Configure the Extension

### Option A: Using Krista UI

1. Log in to Krista platform
2. Navigate to **Extensions** → **Add Extension**
3. Upload `AssemblyAI-1.0.0.jar`
4. Configure the extension:
   - **API Key**: Your AssemblyAI API key
   - **Base URL**: `https://api.assemblyai.com/v2` (default)
   - **Log Level**: `INFO` (or `DEBUG` for troubleshooting)

### Option B: Using Configuration File

Create `config/assemblyai.properties`:
```properties
# AssemblyAI Configuration
assemblyai.api.key=your-api-key-here
assemblyai.base.url=https://api.assemblyai.com/v2
assemblyai.log.level=INFO
```

---

## 🎯 Step 4: Your First Transcription

### Example 1: Basic Transcription

```java
import app.krista.extensions.ai.nlp.assemblyai.catalog.TranscribeCallQuery;
import app.krista.model.base.ExtensionResponse;
import java.io.File;

// Initialize the catalog request
TranscribeCallQuery transcribeCall = new TranscribeCallQuery(attributes);

// Prepare your audio file
File audioFile = new File("path/to/your/audio.mp3");

// Transcribe (without PII redaction)
ExtensionResponse response = transcribeCall.execute(audioFile, false);

// Get the results
Map<String, Object> result = (Map<String, Object>) response.getResponseValue();
String transcriptText = (String) result.get("transcriptText");
String sentiment = (String) result.get("sentiment");
List<String> topics = (List<String>) result.get("topics");

System.out.println("Transcript: " + transcriptText);
System.out.println("Sentiment: " + sentiment);
System.out.println("Topics: " + topics);
```

### Example 2: With PII Redaction

```java
// Transcribe with PII redaction enabled
ExtensionResponse response = transcribeCall.execute(audioFile, true);

// Sensitive information (SSN, credit cards, etc.) will be automatically redacted
```

### Example 3: Using Service Layer Directly

```java
import app.krista.extensions.ai.nlp.assemblyai.service.TranscriptionService;
import app.krista.extensions.ai.nlp.assemblyai.service.model.*;

// Initialize service
TranscriptionService service = new TranscriptionService(attributes, null);

// Upload file
UploadResponse uploadResponse = service.uploadAudioFile(audioFile);

// Create transcript with features
TranscriptRequest request = new TranscriptRequest(uploadResponse.getUploadUrl());
request.setSpeakerLabels(true);        // Enable speaker diarization
request.setSentimentAnalysis(true);    // Enable sentiment analysis
request.setIabCategories(true);        // Enable topic detection
request.setRedactPii(false);           // Disable PII redaction

// Create and poll for completion
TranscriptResponse transcript = service.createTranscript(request);
TranscriptResponse completed = service.pollForCompletion(
    transcript.getId(),
    3000,  // Poll every 3 seconds
    200    // Max 200 attempts (10 minutes)
);

// Get results
System.out.println("Transcript: " + completed.getText());
System.out.println("Status: " + completed.getStatus());
```

---

## 🎨 Step 5: Supported Audio Formats

The extension supports all formats that AssemblyAI supports:

- ✅ **MP3** (recommended)
- ✅ **WAV**
- ✅ **FLAC**
- ✅ **M4A**
- ✅ **AAC**
- ✅ **OGG**
- ✅ **WebM**

**File Size Limits:**
- Maximum: 5 GB per file
- Recommended: Under 100 MB for faster processing

---

## 🔍 Step 6: Understanding the Response

### Success Response Structure
```json
{
  "successful": true,
  "transcriptText": "Hello, this is a test transcription...",
  "transcriptFile": {
    "id": "file-123",
    "name": "transcript.txt",
    "size": 1024
  },
  "sentiment": "POSITIVE",
  "topics": [
    "Technology>Artificial Intelligence",
    "Business>Customer Service"
  ],
  "duration": 45.2,
  "timestamp": 1699564800000
}
```

### Error Response Structure
```json
{
  "successful": false,
  "errorMessage": "Invalid audio file format",
  "errorCode": "INPUT_ERROR",
  "timestamp": 1699564800000
}
```

---

## 🐛 Step 7: Troubleshooting

### Common Issues

#### Issue 1: "Invalid API Key"
**Solution**: Verify your API key is correct and active
```bash
curl -H "authorization: YOUR_API_KEY" https://api.assemblyai.com/v2/transcript
```

#### Issue 2: "File Not Found"
**Solution**: Check file path and permissions
```java
File audioFile = new File("path/to/audio.mp3");
System.out.println("File exists: " + audioFile.exists());
System.out.println("Can read: " + audioFile.canRead());
```

#### Issue 3: "Transcription Timeout"
**Solution**: Increase polling timeout for longer audio files
```java
// For longer files, increase max attempts
TranscriptResponse completed = service.pollForCompletion(
    transcript.getId(),
    5000,  // Poll every 5 seconds
    360    // Max 360 attempts (30 minutes)
);
```

#### Issue 4: "Jersey Client Initialization Error"
**Solution**: This occurs in standalone tests. Real API tests require Krista platform runtime.
```java
// Tests marked with @Disabled will be skipped in standalone mode
// Remove @Disabled annotation when running in Krista platform
```

---

## 📊 Step 8: Monitor Your Usage

### Check API Usage
```bash
curl -H "authorization: YOUR_API_KEY" \
  https://api.assemblyai.com/v2/account
```

### Enable Debug Logging
```properties
assemblyai.log.level=DEBUG
```

This will log:
- API requests and responses
- File upload progress
- Polling status
- Error details

---

## 🎓 Step 9: Next Steps

Now that you have the basics working, explore advanced features:

1. **Speaker Diarization**: Identify different speakers
   - See [examples/speaker-diarization.md](docs/examples/speaker-diarization.md)

2. **Sentiment Analysis**: Analyze emotional tone
   - See [examples/sentiment-analysis.md](docs/examples/sentiment-analysis.md)

3. **Topic Detection**: Categorize content automatically
   - See [examples/topic-detection.md](docs/examples/topic-detection.md)

4. **PII Redaction**: Protect sensitive information
   - See [examples/pii-redaction.md](docs/examples/pii-redaction.md)

5. **Custom Workflows**: Build complex automation
   - See [DEVELOPER_GUIDE.md](DEVELOPER_GUIDE.md)

---

## 📚 Additional Resources

### Documentation
- [Architecture Overview](ARCHITECTURE.md)
- [API Reference](docs/API_REFERENCE.md)
- [Testing Guide](docs/TESTING.md)
- [Troubleshooting](docs/TROUBLESHOOTING.md)

### Examples
- [Complete Examples](docs/EXAMPLES.md)
- [Integration Patterns](docs/INTEGRATION_PATTERNS.md)
- [Best Practices](docs/BEST_PRACTICES.md)

### Community
- [GitHub Issues](https://github.com/your-org/krista-assemblyai-extension/issues)
- [Discussions](https://github.com/your-org/krista-assemblyai-extension/discussions)
- [Contributing Guide](CONTRIBUTING.md)

---

## ✅ Quick Reference

### Build Commands
```bash
./gradlew clean          # Clean build artifacts
./gradlew build          # Build the extension
./gradlew test           # Run tests
./gradlew jar            # Create JAR file
```

### Test Commands
```bash
./gradlew test --tests "*IntegrationTest"           # Integration tests
./gradlew test --tests "TranscriptionServiceTest"   # Specific test
./gradlew jacocoTestReport                          # Coverage report
```

### Configuration Properties
```properties
assemblyai.api.key=<your-key>                       # Required
assemblyai.base.url=https://api.assemblyai.com/v2   # Optional
assemblyai.log.level=INFO                           # Optional (DEBUG, INFO, WARN, ERROR)
```

---

## 🎉 Success!

You're now ready to use the AssemblyAI extension in your Krista workflows!

**Need Help?**
- 📖 Read the [full documentation](README.md)
- 💬 Ask in [GitHub Discussions](https://github.com/your-org/krista-assemblyai-extension/discussions)
- 🐛 Report issues on [GitHub](https://github.com/your-org/krista-assemblyai-extension/issues)

---

**Happy Transcribing! 🎙️**

