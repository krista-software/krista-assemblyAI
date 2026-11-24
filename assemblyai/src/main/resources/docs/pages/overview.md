# Overview

The Krista AssemblyAI extension connects to AssemblyAI's powerful speech-to-text API and provides advanced audio transcription capabilities with speaker diarization, sentiment analysis, and topic detection.

## Key Features

* **Automatic Speech Recognition (ASR)**: Convert audio files to text with high accuracy
* **Speaker Diarization**: Identify and label different speakers in the audio
* **Sentiment Analysis**: Analyze the emotional tone of the conversation
* **Topic Detection**: Automatically detect topics and categories using IAB taxonomy
* **PII Redaction**: Optionally redact personally identifiable information from transcripts
* **Multiple Audio Formats**: Support for WAV, MP3, MP4, M4A, FLAC, OGG

## Use Cases

* **Call Center Analytics**: Transcribe and analyze customer service calls
* **Meeting Transcription**: Convert meeting recordings to searchable text
* **Content Creation**: Generate transcripts for podcasts, videos, and interviews
* **Compliance & Quality Assurance**: Monitor and analyze conversations for compliance
* **Voice of Customer Analysis**: Extract insights from customer feedback and calls

## Prerequisites

* You must have an AssemblyAI account with an active API key
* You must have appropriate permissions to access the AssemblyAI API
* Audio files must be in a supported format

## Supported Audio Formats

The extension supports the following audio formats:
* WAV (Waveform Audio File Format)
* MP3 (MPEG Audio Layer 3)
* MP4 (MPEG-4 Audio)
* M4A (MPEG-4 Audio)
* FLAC (Free Lossless Audio Codec)
* OGG (Ogg Vorbis)

## Technical Specifications

| Property | Value |
|----------|-------|
| **Maximum File Size** | 500MB per file |
| **Supported Languages** | English (primary), with support for other languages |
| **Processing Time** | Typically 15-30% of audio duration |
| **API Rate Limits** | Subject to AssemblyAI's rate limits based on your plan |

## Related Pages

- [Obtaining API Key](pages/ObtainingApiKey.md)
- [Authentication](pages/Authentication.md)
- [Extension Configuration](pages/ExtensionConfiguration.md)
- [Transcribe Call](pages/TranscribeCall.md)
- [Home](../README.md)
