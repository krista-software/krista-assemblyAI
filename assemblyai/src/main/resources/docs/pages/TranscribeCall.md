# Transcribe Call

## Overview

Transcribes a call recording with speaker diarization, sentiment analysis, and topic detection in a single end-to-end operation. This is the **recommended** approach for call transcription as it handles the entire workflow automatically - from upload to final results.

## Request Details

- **Area**: Transcription
- **Type**: QUERY_SYSTEM
- **Retry Support**: ❌ No - This is a synchronous operation that completes the entire workflow

## Input Parameters

| Parameter Name | Type | Required | Description | Example |
|----------------|------|----------|-------------|---------|
| Audio File | File | Yes | The call recording to transcribe | customer_call.mp3 |
| Redact PII | Boolean | No | Whether to redact personally identifiable information | false |

### Parameter Details

#### Audio File
- **Supported Formats**: WAV, MP3, FLAC
- **Maximum Size**: 5GB per file
- **Quality**: Higher quality audio produces better transcription accuracy
- **Channels**: Mono or stereo (stereo recommended for speaker diarization)

#### Redact PII
- **Default**: false (PII not redacted)
- **When true**: Redacts sensitive information including:
  - Social Security Numbers (SSN)
  - Credit card numbers
  - Phone numbers
  - Email addresses
  - Dates of birth
  - Physical addresses
  - Account numbers
- **Use Case**: Enable for compliance with privacy regulations (GDPR, HIPAA, etc.)

## Output Parameters

| Parameter Name | Type | Description | Example |
|----------------|------|-------------|---------|
| Transcription Successful | Boolean | Indicates if transcription completed successfully | true |
| Transcript File | File | Formatted text file containing complete transcript | transcript_customer_call.txt |
| Transcript Text | Text | Raw transcript text | "Agent: Hello, how can I help you today?..." |
| Sentiment | Text | Sentiment analysis with percentages | "POSITIVE: 45.2%, NEGATIVE: 12.3%, NEUTRAL: 42.5%" |
| Topic Detected | Text | Detected topics from IAB taxonomy | "Business>CustomerService, Technology>Software" |

### Output Details

#### Transcription Successful
- **true**: Transcription completed without errors
- **false**: Transcription failed (check error message)

#### Transcript File
- **Format**: Plain text (.txt)
- **Naming**: `transcript_<original_filename>.txt`
- **Content**: Includes:
  - Original filename
  - Transcription date/time
  - Full transcript text
  - Sentiment analysis summary
  - Detected topics
- **Encoding**: UTF-8
- **Line Breaks**: Platform-appropriate (CRLF on Windows, LF on Unix)

#### Transcript Text
- **Format**: Plain text
- **Speaker Labels**: Included if multiple speakers detected (e.g., "Speaker A:", "Speaker B:")
- **Timestamps**: Not included in text (available in detailed results)
- **Formatting**: Sentences with proper punctuation

#### Sentiment
- **Format**: "SENTIMENT: percentage%, ..."
- **Sentiments**: POSITIVE, NEGATIVE, NEUTRAL
- **Calculation**: Percentage of conversation segments with each sentiment
- **Example**: "POSITIVE: 60.0%, NEGATIVE: 10.0%, NEUTRAL: 30.0%"
- **Use Case**: Understand overall emotional tone of the conversation

#### Topic Detected
- **Format**: Comma-separated list of topics
- **Taxonomy**: IAB Content Taxonomy (standardized categories)
- **Hierarchy**: Category>Subcategory format
- **Examples**:
  - "Business>Marketing"
  - "Technology>Software"
  - "Finance>PersonalFinance"
- **Empty**: "No topics detected" if no clear topics identified

## Validation Rules

| Validation | Error Message | Resolution |
|------------|---------------|------------|
| Audio File is required | "Audio File is required" | Provide a valid audio file |
| Audio File must be valid | "Invalid audio file format" | Use supported format (WAV, MP3, FLAC) |
| Audio File size limit | "File size exceeds 5GB limit" | Reduce file size or split into smaller files |
| Transcription must complete | "Transcription failed: {error}" | Check audio quality, format, and try again |

## Error Handling

### Input Errors (INPUT_ERROR)

**Cause**: Invalid or missing Audio File parameter  
**Common Scenarios**:
- Audio File parameter is null or empty
- File format is not supported
- File is corrupted or unreadable

**Error Message**: `"Audio File is required"` or `"Invalid audio file format"`
**Resolution**:
1. Provide a valid audio file
2. Verify file format is supported (WAV, MP3, FLAC)
3. Check file is not corrupted
4. Ensure file size is under 5GB

### Logic Errors (LOGIC_ERROR)

**Cause**: Transcription processing failed  
**Common Scenarios**:
- Audio quality too poor for transcription
- Audio file contains no speech
- Language not supported
- File format issues

**Error Message**: `"Transcription failed: {specific error}"`  
**Resolution**:
1. Verify audio contains clear speech
2. Check audio quality (reduce background noise)
3. Ensure language is supported (primarily English)
4. Try re-encoding audio file
5. Verify file is not corrupted

### System Errors (SYSTEM_ERROR)

**Cause**: System-level failures during transcription  
**Common Scenarios**:
- Network connectivity issues
- AssemblyAI API unavailable
- Timeout during processing
- File upload failures

**Error Message**: `"Transcription Failed: {error details}"`  
**Resolution**:
1. Check network connectivity
2. Verify AssemblyAI API status
3. Retry the operation
4. Check API key is valid
5. Contact support if issue persists

## Usage Examples

### Example 1: Customer Service Call Transcription

**Scenario**: Transcribe a customer service call to analyze sentiment and topics

**Input**:
```
Audio File: customer_service_call_20231028.mp3
Redact PII: false
```

**Output**:
```
Transcription Successful: true
Transcript Text: "Agent: Thank you for calling customer support. How can I help you today? Customer: Hi, I'm having trouble with my account login..."
Sentiment: "POSITIVE: 55.0%, NEGATIVE: 15.0%, NEUTRAL: 30.0%"
Topic Detected: "Business>CustomerService, Technology>Software"
Transcript File: transcript_customer_service_call_20231028.txt
```

**Result**: Successfully transcribed call with positive sentiment and identified customer service topic

### Example 2: Compliance Call with PII Redaction

**Scenario**: Transcribe a financial services call with PII redaction for compliance

**Input**:
```
Audio File: financial_consultation.wav
Redact PII: true
```

**Output**:
```
Transcription Successful: true
Transcript Text: "Advisor: Can you confirm your account number? Client: Yes, it's [REDACTED]. Advisor: And your date of birth? Client: [REDACTED]..."
Sentiment: "NEUTRAL: 70.0%, POSITIVE: 20.0%, NEGATIVE: 10.0%"
Topic Detected: "Finance>PersonalFinance, Business>Consulting"
Transcript File: transcript_financial_consultation.txt
```

**Result**: Successfully transcribed with sensitive information redacted

### Example 3: Sales Call Analysis

**Scenario**: Transcribe a sales call to analyze customer sentiment and topics discussed

**Input**:
```
Audio File: sales_demo_call.mp3
Redact PII: false
```

**Output**:
```
Transcription Successful: true
Transcript Text: "Sales Rep: Let me show you our new features. Customer: This looks great! I'm really impressed with the analytics dashboard..."
Sentiment: "POSITIVE: 75.0%, NEUTRAL: 20.0%, NEGATIVE: 5.0%"
Topic Detected: "Business>Sales, Technology>Software, Business>Marketing"
Transcript File: transcript_sales_demo_call.txt
```

**Result**: High positive sentiment indicates successful sales call

## Business Rules

1. **Automatic Feature Enablement**: Speaker diarization, sentiment analysis, and topic detection are always enabled
2. **Speaker Diarization**: Automatically identifies and labels different speakers (Speaker A, Speaker B, etc.)
3. **Sentiment Granularity**: Sentiment is analyzed per conversation segment, then aggregated
4. **Topic Detection**: Uses IAB Content Taxonomy for standardized topic classification
5. **PII Redaction**: When enabled, replaces sensitive information with [REDACTED] placeholder
6. **File Cleanup**: Temporary files are automatically cleaned up after processing
7. **Polling Mechanism**: Automatically polls for completion every 3 seconds (configurable)
8. **Maximum Wait Time**: Default 10 minutes (200 attempts × 3 seconds)
9. **Transcript File Format**: Always UTF-8 encoded plain text
10. **Error Propagation**: All errors are captured and returned with detailed messages

## Limitations

1. **Language Support**: Primarily optimized for English (other languages supported but may have lower accuracy)
2. **File Size**: Maximum 5GB per file (AssemblyAI limitation)
3. **Processing Time**: Typically 15-30% of audio duration (e.g., 10-minute audio takes ~2-3 minutes)
4. **Concurrent Requests**: Subject to AssemblyAI API rate limits based on your plan
5. **Speaker Identification**: Diarization labels speakers but doesn't identify who they are (Speaker A, B, C, etc.)
6. **Topic Taxonomy**: Limited to IAB Content Taxonomy categories
7. **Sentiment Accuracy**: Depends on audio quality and clarity of speech
8. **PII Detection**: May not catch all PII variations or context-specific sensitive information
9. **Audio Quality**: Poor quality audio may result in lower transcription accuracy
10. **Network Dependency**: Requires stable internet connection throughout the process

## Best Practices

### 1. Audio Quality Optimization
- Use high-quality audio recordings (minimum 16kHz sample rate)
- Reduce background noise before transcription
- Ensure clear speech without excessive overlapping
- Use stereo recordings for better speaker separation

### 2. PII Redaction Strategy
- Enable PII redaction for all customer-facing calls in regulated industries
- Review redacted transcripts to ensure compliance
- Store original audio separately with appropriate access controls
- Document PII redaction policy for audit purposes

### 3. Sentiment Analysis Interpretation
- Consider sentiment in context of conversation flow
- Look for sentiment shifts during the conversation
- Combine with topic detection for deeper insights
- Use sentiment trends over multiple calls for pattern analysis

### 4. Topic Detection Usage
- Use topics to categorize and route calls automatically
- Identify trending topics across multiple calls
- Combine topics with sentiment for comprehensive analysis
- Create topic-based reports for business intelligence

### 5. Error Handling
- Implement retry logic for transient failures
- Log all transcription attempts for audit trail
- Monitor transcription success rates
- Alert on repeated failures for investigation

### 6. Performance Optimization
- Process calls in batches during off-peak hours
- Monitor API usage to stay within rate limits
- Consider file size when planning batch operations
- Use appropriate polling intervals based on audio length

## Common Use Cases

### 1. Call Center Quality Assurance
```
Scenario: Analyze customer service calls for quality and compliance
Action: Transcribe calls with PII redaction enabled
Result: Review transcripts for agent performance, customer satisfaction, and compliance
Benefits: Identify training opportunities, ensure regulatory compliance
```

### 2. Sales Performance Analysis
```
Scenario: Evaluate sales call effectiveness
Action: Transcribe sales calls and analyze sentiment
Result: Identify successful sales techniques and areas for improvement
Benefits: Improve sales training, increase conversion rates
```

### 3. Meeting Documentation
```
Scenario: Create searchable records of team meetings
Action: Transcribe meeting recordings with speaker diarization
Result: Generate meeting minutes with speaker attribution
Benefits: Improve meeting follow-up, create searchable knowledge base
```

### 4. Voice of Customer Analysis
```
Scenario: Extract customer feedback from support calls
Action: Transcribe calls and detect topics
Result: Identify common customer issues and sentiment trends
Benefits: Improve product development, enhance customer experience
```

## Technical Implementation

### Service Class
- **Class**: TranscriptionService
- **Package**: app.krista.extensions.ai.nlp.assemblyai.service
- **Method**: transcribeCallComplete(File audioFile, Boolean redactPii)

### Workflow Steps
1. Convert Krista file to Java file
2. Upload audio file to AssemblyAI
3. Create transcript with all features enabled
4. Poll for completion (3-second intervals, max 200 attempts)
5. Extract sentiment analysis results
6. Extract topic detection results
7. Create formatted transcript file
8. Convert file back to Krista format
9. Return comprehensive results

### Helper Methods
- `extractSentiment()`: Aggregates sentiment analysis with percentages
- `extractTopics()`: Extracts top IAB categories
- `createTranscriptFile()`: Generates formatted transcript file

## Troubleshooting

### Issue: Transcription Takes Too Long

**Cause**: Long audio file or high API load  
**Solution**:
1. Check audio file length (longer files take more time)
2. Verify AssemblyAI API status
3. Consider increasing max polling attempts for very long files
4. Split very long audio files into smaller segments

### Issue: Poor Transcription Accuracy

**Cause**: Low audio quality or unsupported language  
**Solution**:
1. Improve audio quality (reduce noise, increase clarity)
2. Verify language is English (primary supported language)
3. Check speaker overlap (reduce simultaneous speech)
4. Use higher quality audio format (WAV instead of MP3)

### Issue: No Topics Detected

**Cause**: Content doesn't match IAB taxonomy or unclear topics  
**Solution**:
1. This is normal for some conversations
2. Topics require clear, identifiable subject matter
3. Short conversations may not have enough context
4. Review transcript to verify content clarity

### Issue: PII Not Fully Redacted

**Cause**: PII detection has limitations  
**Solution**:
1. Review redacted transcripts manually
2. Implement additional post-processing if needed
3. Use context-aware redaction for specific use cases
4. Document any additional PII handling requirements

## See Also

- [Extension Configuration](pages/ExtensionConfiguration.md) - Configure polling settings
- [Authentication](pages/authentication.md) - API key setup
- [Obtaining API Key](pages/obtainingApiKey.md) - Get AssemblyAI API key

