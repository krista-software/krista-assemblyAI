# Troubleshooting Guide - AssemblyAI Extension for Krista

Common issues and their solutions.

---

## Table of Contents

- [Authentication Issues](#authentication-issues)
- [File Upload Issues](#file-upload-issues)
- [Transcription Issues](#transcription-issues)
- [Performance Issues](#performance-issues)
- [Integration Issues](#integration-issues)
- [Testing Issues](#testing-issues)
- [Debugging Tips](#debugging-tips)

---

## Authentication Issues

### Issue: "Invalid API Key" Error

**Symptoms:**
```
ERROR: Authentication failed: Invalid API key
```

**Causes:**
1. API key is incorrect or expired
2. API key not properly configured
3. Environment variable not set

**Solutions:**

1. **Verify API Key:**
```bash
curl -H "authorization: YOUR_API_KEY" \
  https://api.assemblyai.com/v2/transcript
```

2. **Check Configuration:**
```properties
# Ensure API key is set correctly
assemblyai.api.key=your-actual-api-key-here
```

3. **Use Environment Variable:**
```bash
export ASSEMBLYAI_API_KEY="your-api-key"
```

```properties
assemblyai.api.key=${ASSEMBLYAI_API_KEY}
```

4. **Regenerate API Key:**
- Log in to AssemblyAI dashboard
- Navigate to API Keys section
- Generate new key
- Update configuration

---

### Issue: "API Key Not Found" Error

**Symptoms:**
```
ERROR: API key is required but not configured
```

**Solution:**
```java
// Ensure attributes are properly initialized
AssemblyAIAttributes attributes = new AssemblyAIAttributes(
    "your-api-key",
    "https://api.assemblyai.com/v2",
    "INFO"
);
```

---

## File Upload Issues

### Issue: "File Not Found" Error

**Symptoms:**
```
ERROR: Audio file not found: /path/to/audio.mp3
```

**Solutions:**

1. **Verify File Path:**
```java
File audioFile = new File("path/to/audio.mp3");
System.out.println("File exists: " + audioFile.exists());
System.out.println("Absolute path: " + audioFile.getAbsolutePath());
```

2. **Check File Permissions:**
```bash
ls -l /path/to/audio.mp3
# Should show read permissions
```

3. **Use Absolute Path:**
```java
File audioFile = new File("/absolute/path/to/audio.mp3");
```

---

### Issue: "File Too Large" Error

**Symptoms:**
```
ERROR: File size exceeds maximum limit
```

**Solutions:**

1. **Check File Size:**
```java
long fileSizeInMB = audioFile.length() / (1024 * 1024);
System.out.println("File size: " + fileSizeInMB + " MB");
```

2. **Compress Audio:**
```bash
# Using ffmpeg to compress
ffmpeg -i input.mp3 -b:a 128k output.mp3
```

3. **Split Large Files:**
```bash
# Split into 50MB chunks
ffmpeg -i input.mp3 -f segment -segment_time 600 -c copy output%03d.mp3
```

**Limits:**
- Maximum file size: 5 GB
- Recommended: Under 100 MB for faster processing

---

### Issue: "Unsupported File Format" Error

**Symptoms:**
```
ERROR: Unsupported audio format: .xyz
```

**Supported Formats:**
- ✅ MP3
- ✅ WAV
- ✅ FLAC
- ✅ M4A
- ✅ AAC
- ✅ OGG
- ✅ WebM

**Solution:**
```bash
# Convert to MP3 using ffmpeg
ffmpeg -i input.xyz -acodec libmp3lame output.mp3
```

---

### Issue: "Upload Timeout" Error

**Symptoms:**
```
ERROR: Upload timed out after 60 seconds
```

**Solutions:**

1. **Check Network Connection:**
```bash
ping api.assemblyai.com
```

2. **Increase Timeout (if configurable):**
```java
// Future enhancement - not yet implemented
// service.setUploadTimeout(300000); // 5 minutes
```

3. **Use Smaller Files:**
- Compress audio
- Reduce bitrate
- Split into chunks

---

## Transcription Issues

### Issue: "Transcription Failed" Error

**Symptoms:**
```
ERROR: Transcription failed with status: error
```

**Solutions:**

1. **Check Transcript Error:**
```java
TranscriptResponse response = service.getTranscript(transcriptId);
if (response.isFailed()) {
    System.err.println("Error: " + response.getError());
}
```

2. **Common Causes:**
- Audio quality too poor
- File corrupted
- Unsupported language
- Audio too short (< 0.5 seconds)

3. **Verify Audio Quality:**
```bash
# Check audio properties
ffprobe input.mp3
```

4. **Test with Sample File:**
```bash
# Download AssemblyAI sample
curl -L -o test.mp3 \
  "https://github.com/AssemblyAI-Examples/audio-examples/raw/main/20230607_me_canadian_wildfires.mp3"
```

---

### Issue: "Polling Timeout" Error

**Symptoms:**
```
ERROR: Transcription did not complete within timeout period
```

**Solutions:**

1. **Increase Max Attempts:**
```java
// For longer files, increase timeout
TranscriptResponse completed = service.pollForCompletion(
    transcriptId,
    5000,  // Poll every 5 seconds
    720    // Max 720 attempts (1 hour)
);
```

2. **Estimate Processing Time:**
```
Processing time ≈ Audio duration × 0.3
Example: 10 minute audio ≈ 3 minutes processing
```

3. **Check Transcript Status Manually:**
```bash
curl -H "authorization: YOUR_API_KEY" \
  https://api.assemblyai.com/v2/transcript/TRANSCRIPT_ID
```

---

### Issue: "Empty Transcript" Error

**Symptoms:**
```
Transcript completed but text is empty or null
```

**Solutions:**

1. **Check Audio Content:**
- Ensure audio contains speech
- Verify audio isn't silent
- Check audio isn't corrupted

2. **Verify Language Setting:**
```java
TranscriptRequest request = new TranscriptRequest(uploadUrl);
request.setLanguageCode("en"); // Set correct language
```

3. **Test Audio Playback:**
```bash
# Play audio to verify content
ffplay input.mp3
```

---

## Performance Issues

### Issue: Slow Transcription

**Symptoms:**
- Transcription takes longer than expected
- Polling times out frequently

**Solutions:**

1. **Optimize Audio Format:**
```bash
# Convert to optimal format
ffmpeg -i input.wav -acodec libmp3lame -b:a 128k output.mp3
```

2. **Reduce Audio Length:**
- Split long files into segments
- Process in parallel

3. **Adjust Polling Interval:**
```java
// Longer interval for long files
service.pollForCompletion(transcriptId, 10000, 360);
```

---

### Issue: High Memory Usage

**Symptoms:**
- OutOfMemoryError
- Application slowdown

**Solutions:**

1. **Increase JVM Memory:**
```bash
java -Xmx2G -jar your-app.jar
```

2. **Process Files in Batches:**
```java
// Don't load all files at once
for (File file : audioFiles) {
    processFile(file);
    // Allow garbage collection
    System.gc();
}
```

3. **Stream Large Files:**
```java
// Use streaming for large files
try (FileInputStream fis = new FileInputStream(audioFile)) {
    // Process in chunks
}
```

---

## Integration Issues

### Issue: "Jersey Client Initialization Error"

**Symptoms:**
```
NullPointerException: Cannot invoke 
"org.glassfish.hk2.api.DynamicConfigurationService.createDynamicConfiguration()"
```

**Cause:**
Jersey client requires HK2 dependency injection from Krista platform.

**Solutions:**

1. **Run in Krista Platform:**
- Tests marked with `@Disabled` require Krista runtime
- Deploy extension to Krista platform
- Run tests within platform environment

2. **For Standalone Testing:**
- Use validation tests (don't require platform)
- Mock external dependencies
- Use embedded test servers

---

### Issue: "FileRepository is Null" Error

**Symptoms:**
```
NullPointerException: FileRepository is null
```

**Cause:**
FileRepository is a Krista platform service.

**Solution:**
```java
// Use direct File objects in standalone mode
File audioFile = new File("path/to/audio.mp3");

// In Krista platform, FileRepository is injected
TranscriptionService service = new TranscriptionService(
    attributes, 
    kristaMediaClient  // Provided by platform
);
```

---

## Testing Issues

### Issue: Tests Fail with "Real API" Errors

**Symptoms:**
```
Tests marked with @EnabledIf("isRealApiEnabled") fail
```

**Solutions:**

1. **Configure Test Properties:**
```properties
# src/test/resources/config.properties
assemblyai.api.key=your-test-api-key
test.enable.real.api.calls=true
```

2. **Skip Real API Tests:**
```bash
# Tests with @Disabled are automatically skipped
./gradlew test
```

3. **Run Only Validation Tests:**
```bash
./gradlew test --tests "*Test" -x "*IntegrationTest"
```

---

### Issue: "Test Audio File Not Found"

**Symptoms:**
```
ERROR: Test audio file not found: src/test/resources/audio/test.mp3
```

**Solution:**
```bash
# Download test audio file
cd src/test/resources/audio
curl -L -o test.mp3 \
  "https://github.com/AssemblyAI-Examples/audio-examples/raw/main/20230607_me_canadian_wildfires.mp3"
```

---

## Debugging Tips

### Enable Debug Logging

```properties
# Set log level to DEBUG
assemblyai.log.level=DEBUG
```

**Debug Output Includes:**
- API request/response details
- File upload progress
- Polling status
- Error stack traces

---

### Check API Status

```bash
# Verify AssemblyAI API is operational
curl -I https://api.assemblyai.com/v2/transcript
```

---

### Validate Configuration

```java
// Print configuration for debugging
System.out.println("API Key: " + 
    (apiKey != null ? "***" + apiKey.substring(apiKey.length()-4) : "null"));
System.out.println("Base URL: " + baseUrl);
System.out.println("Log Level: " + logLevel);
```

---

### Test Network Connectivity

```bash
# Test connection to AssemblyAI
curl -v https://api.assemblyai.com/v2/transcript

# Check DNS resolution
nslookup api.assemblyai.com

# Test with proxy (if applicable)
curl -x http://proxy:port https://api.assemblyai.com/v2/transcript
```

---

### Capture HTTP Traffic

```bash
# Use tcpdump to capture traffic
sudo tcpdump -i any -w capture.pcap host api.assemblyai.com

# Analyze with Wireshark
wireshark capture.pcap
```

---

### Generate Thread Dump

```bash
# If application hangs
jstack <pid> > thread-dump.txt
```

---

### Check Disk Space

```bash
# Ensure sufficient disk space for temp files
df -h
```

---

## Getting Help

### Before Asking for Help

1. **Enable DEBUG logging**
2. **Collect error messages**
3. **Note the exact steps to reproduce**
4. **Check existing issues**

### Where to Get Help

- **GitHub Issues**: [Report bugs](https://github.com/your-org/krista-assemblyai-extension/issues)
- **Discussions**: [Ask questions](https://github.com/your-org/krista-assemblyai-extension/discussions)
- **Email**: support@your-org.com

### What to Include

```markdown
**Environment:**
- Java version: 21.0.1
- Krista version: 1.0.117
- Extension version: 1.0.0
- OS: Ubuntu 22.04

**Issue:**
[Clear description]

**Steps to Reproduce:**
1. Step 1
2. Step 2
3. Step 3

**Expected Behavior:**
[What should happen]

**Actual Behavior:**
[What actually happens]

**Logs:**
[Include relevant logs with DEBUG level]

**Code Sample:**
[Minimal reproducible example]
```

---

## Common Error Messages

| Error Message | Cause | Solution |
|--------------|-------|----------|
| "Invalid API key" | Wrong or expired key | Verify API key in dashboard |
| "File not found" | Invalid file path | Check file path and permissions |
| "Upload failed" | Network issue | Check connectivity, retry |
| "Transcription timeout" | File too long | Increase max attempts |
| "Unsupported format" | Wrong file type | Convert to supported format |
| "Empty transcript" | No speech in audio | Verify audio content |
| "Jersey client error" | Missing platform | Run in Krista platform |
| "Out of memory" | File too large | Increase JVM memory |

---

**Still having issues? [Open an issue](https://github.com/your-org/krista-assemblyai-extension/issues) and we'll help!**

