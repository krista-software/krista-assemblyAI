# Supported Requests

## Catalog Requests

Krista AssemblyAI extension supports the following Catalog Request:

> #### Important Usage Notes:
> - The **Success** indicator will be marked as Yes if the response is successfully retrieved.
> - All audio files are uploaded to AssemblyAI's secure CDN before transcription.
> - Transcription is an asynchronous process handled automatically.
> - Audio files support multiple formats: WAV, MP3, FLAC.

## Transcription Area

### Transcribe Call

- **Description**: Complete end-to-end transcription with speaker diarization, sentiment analysis, and topic detection in a single request.
- **Input Parameters**:

| **Parameter Name** | **Parameter Type** | **Mandatory** | **Example** |
|--------------------|--------------------| --------------|-------------|
| Audio File         | File               | Yes           | recording.mp3 |
| Redact PII         | Boolean            | No            | false |

- **Output**:

| **Parameter Name** | **Parameter Type** | **Example** |
|--------------------|--------------------|-------------|
| Transcription Successful | Boolean | true |
| Transcript File | File | transcript_recording.txt |
| Transcript Text | Text | "Hello, this is a test..." |
| Sentiment | Text | "POSITIVE: 45.2%, NEGATIVE: 12.3%, NEUTRAL: 42.5%" |
| Topic Detected | Text | "Business>Marketing, Technology>Software" |

> #### Notes:
> - Automatically handles upload, transcription, and polling
> - Includes speaker diarization (identifies different speakers)
> - Provides sentiment analysis with percentages
> - Detects topics using IAB taxonomy
> - Optionally redacts PII (personally identifiable information)
> - Processing time: typically 15-30% of audio duration

## Usage Example

**Use Case**: Transcribe a customer service call with all features

**Input**:
```
Audio File: customer_call.mp3
Redact PII: true
```

**Output**:
```
Transcription Successful: true
Transcript Text: "Agent: Hello, how can I help you today?..."
Sentiment: "POSITIVE: 60%, NEGATIVE: 10%, NEUTRAL: 30%"
Topic Detected: "CustomerService>Support, Business>Sales"
Transcript File: transcript_customer_call.txt
```

## Error Handling

The AssemblyAI extension includes comprehensive error handling for:
- Invalid audio file formats
- File upload failures
- Transcription errors
- API rate limiting
- Network connectivity issues
- Authentication failures

## Audio Format Support

The extension supports the following audio formats:
- WAV (Waveform Audio File Format)
- MP3 (MPEG Audio Layer 3)
- FLAC (Free Lossless Audio Codec)

## Performance Considerations

- **Processing Time**: Typically 15-30% of audio duration (e.g., 10-minute audio takes ~2-3 minutes)
- **Maximum File Size**: 500 MB per file
- **Concurrent Requests**: Subject to AssemblyAI API rate limits based on your plan

## Best Practices

1. **Automatic workflow**: The "Transcribe Call" request handles upload, transcription, and polling automatically
2. **Speaker diarization**: Automatically enabled to identify different speakers
3. **Sentiment analysis**: Automatically enabled to understand emotional tone
4. **Enable PII redaction for compliance**: Set "Redact PII" to true to protect sensitive information
5. **Monitor API usage**: Track usage in AssemblyAI dashboard
6. **Handle errors gracefully**: Implement retry logic for transient failures

