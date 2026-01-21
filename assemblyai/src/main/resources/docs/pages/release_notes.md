# Release Notes - Krista AssemblyAI Extension

## Version 1.0.2

**Developer Name**: Krista Development Team

**Krista Service APIs Java**: 1.0.117

**Global Catalog Version**: GC-2025.11.3

**Release Date**: 2025-11-18

### Changes

- Transcript Text for **Transcribe Call** is now constructed from speaker diarization utterances (with speaker labels and time ranges in `mm:ss - mm:ss` format), with safe fallback to plain transcript text when diarization/utterances are not available.

---

## Version 1.0.1

**Developer Name**: Krista Development Team

**Krista Service APIs Java**: 1.0.117

**Global Catalog Version**: GC-2025.11.2

**Release Date**: 2025-10-31

## Features

### Transcribe Call
Complete end-to-end transcription with speaker diarization, sentiment analysis, and topic detection in a single request
- Automatic upload and transcription workflow
- Built-in polling mechanism with configurable intervals
- Support for multiple audio formats (WAV, MP3,  FLAC)
- Speaker diarization to identify and label different speakers
- Sentiment analysis with percentage breakdowns (POSITIVE, NEGATIVE, NEUTRAL)
- Topic detection using IAB Content Taxonomy
- Optional PII redaction for compliance (SSN, credit cards, phone numbers, emails, addresses)
- Returns formatted transcript file with comprehensive metadata
- Maximum file size: 5GB per file
- Processing time: Typically 15-30% of audio duration
