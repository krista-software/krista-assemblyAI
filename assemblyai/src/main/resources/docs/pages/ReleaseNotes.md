# Release Notes

## Version 1.0.2

**Release Date**: 2026-02-05

### Changes

- Transcript Text for **Transcribe Call** is now constructed from speaker diarization utterances (with speaker labels and time ranges in `mm:ss - mm:ss` format), with safe fallback to plain transcript text when diarization/utterances are not available.

---

## Version 1.0.1

**Release Date**: 2025-10-31

### Features

#### Transcribe Call
Complete end-to-end transcription with speaker diarization, sentiment analysis, and topic detection in a single request:
- Automatic upload and transcription workflow
- Built-in polling mechanism with configurable intervals
- Support for multiple audio formats (WAV, MP3, MP4, M4A, FLAC, OGG)
- Speaker diarization to identify and label different speakers
- Sentiment analysis with percentage breakdowns (POSITIVE, NEGATIVE, NEUTRAL)
- Topic detection using IAB Content Taxonomy
- Optional PII redaction for compliance (SSN, credit cards, phone numbers, emails, addresses)
- Returns formatted transcript file with comprehensive metadata
- Maximum file size: 500MB per file
- Processing time: Typically 15-30% of audio duration

---

## Related Pages

- [Extension Configuration](pages/ExtensionConfiguration.md)
- [Authentication](pages/Authentication.md)
- [Transcribe Call](pages/TranscribeCall.md)
