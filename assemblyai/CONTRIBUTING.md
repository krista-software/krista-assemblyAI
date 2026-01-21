# Contributing to AssemblyAI Extension for Krista

First off, thank you for considering contributing to the AssemblyAI Extension! It's people like you that make this extension the world's most robust AssemblyAI integration for Krista.

## 📋 Table of Contents

- [Code of Conduct](#code-of-conduct)
- [How Can I Contribute?](#how-can-i-contribute)
- [Development Setup](#development-setup)
- [Coding Standards](#coding-standards)
- [Testing Guidelines](#testing-guidelines)
- [Pull Request Process](#pull-request-process)
- [License](#license)

---

## 📜 Code of Conduct

This project and everyone participating in it is governed by our [Code of Conduct](CODE_OF_CONDUCT.md). By participating, you are expected to uphold this code.

---

## 🤝 How Can I Contribute?

### Reporting Bugs

Before creating bug reports, please check the [existing issues](https://github.com/your-org/krista-assemblyai-extension/issues) to avoid duplicates.

**When filing a bug report, include:**
- **Clear title and description**
- **Steps to reproduce** the issue
- **Expected behavior** vs **actual behavior**
- **Code samples** or test cases
- **Environment details** (Java version, Krista version, OS)
- **Log output** (with DEBUG level if possible)

**Example Bug Report:**
```markdown
### Bug: Transcription fails for WAV files over 50MB

**Environment:**
- Java: 21.0.1
- Krista: 1.0.117
- OS: Ubuntu 22.04

**Steps to Reproduce:**
1. Upload WAV file > 50MB
2. Call transcribeCall.execute(file, false)
3. Observe error

**Expected:** Successful transcription
**Actual:** IOException: Connection timeout

**Logs:**
[Include relevant log output]
```

### Suggesting Enhancements

Enhancement suggestions are tracked as GitHub issues. When creating an enhancement suggestion, include:

- **Clear use case**: Why is this enhancement needed?
- **Proposed solution**: How should it work?
- **Alternatives considered**: What other approaches did you consider?
- **Additional context**: Screenshots, mockups, or examples

### Your First Code Contribution

Unsure where to begin? Look for issues labeled:
- `good first issue` - Simple issues perfect for newcomers
- `help wanted` - Issues where we need community help
- `documentation` - Documentation improvements

### Pull Requests

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Make your changes
4. Add tests for your changes
5. Ensure all tests pass
6. Commit with clear messages
7. Push to your fork
8. Open a Pull Request

---

## 🛠️ Development Setup

### Prerequisites
- Java 21 or higher
- Gradle 8.x
- Git
- IDE (IntelliJ IDEA recommended)

### Setup Steps

1. **Fork and Clone**
```bash
git clone https://github.com/YOUR_USERNAME/krista-assemblyai-extension.git
cd krista-assemblyai-extension/assemblyai
```

2. **Build the Project**
```bash
./gradlew clean build
```

3. **Run Tests**
```bash
./gradlew test
```

4. **Set Up IDE**
```bash
# For IntelliJ IDEA
./gradlew idea

# For Eclipse
./gradlew eclipse
```

5. **Configure Test Environment**
Create `src/test/resources/config.properties`:
```properties
assemblyai.api.key=your-test-api-key
assemblyai.base.url=https://api.assemblyai.com/v2
test.enable.real.api.calls=false  # Set to true for integration tests
```

---

## 📝 Coding Standards

### Java Style Guide

We follow standard Java conventions with some specific guidelines:

#### Package Structure
```
app.krista.extensions.ai.nlp.assemblyai
├── catalog/          # Krista orchestration layer
├── service/          # Business logic layer
├── bridge/           # Transformation layer
├── util/             # Utility classes
└── model/            # Data models
```

#### Naming Conventions
- **Classes**: PascalCase (`TranscriptionService`)
- **Methods**: camelCase (`uploadAudioFile`)
- **Constants**: UPPER_SNAKE_CASE (`MAX_FILE_SIZE`)
- **Variables**: camelCase (`audioFile`)

#### Code Style
```java
// Good: Clear, descriptive names
public TranscriptResponse createTranscript(TranscriptRequest request) {
    validateRequest(request);
    JSONObject response = restClient.createTranscript(request.toJSON());
    return TranscriptResponse.fromJSON(response);
}

// Bad: Unclear names, no validation
public TranscriptResponse create(TranscriptRequest r) {
    return TranscriptResponse.fromJSON(restClient.create(r.toJSON()));
}
```

#### Error Handling
```java
// Good: Specific exceptions with context
if (audioFile == null) {
    throw new IllegalArgumentException(
        "Audio file is required for transcription"
    );
}

// Bad: Generic exceptions
if (audioFile == null) {
    throw new Exception("Error");
}
```

#### Logging
```java
// Good: Structured logging with context
LOG.info("Starting transcription for file: {}, size: {} bytes", 
    audioFile.getName(), audioFile.length());

// Bad: Unstructured logging
LOG.info("Starting transcription");
```

### Documentation Standards

#### JavaDoc Requirements
All public methods must have JavaDoc:

```java
/**
 * Uploads an audio file to AssemblyAI for transcription.
 *
 * @param audioFile The audio file to upload (MP3, WAV, FLAC, etc.)
 * @return UploadResponse containing the upload URL
 * @throws IOException If file reading or upload fails
 * @throws IllegalArgumentException If audioFile is null or invalid
 */
public UploadResponse uploadAudioFile(File audioFile) throws IOException {
    // Implementation
}
```

#### Code Comments
```java
// Good: Explain WHY, not WHAT
// Poll with exponential backoff to avoid rate limiting
int pollInterval = 3000;

// Bad: Obvious comments
// Set poll interval to 3000
int pollInterval = 3000;
```

---

## 🧪 Testing Guidelines

### Test Coverage Requirements
- **Minimum**: 90% code coverage
- **Target**: 95%+ code coverage
- **Critical paths**: 100% coverage

### Test Structure

#### Unit Tests
```java
@Test
@DisplayName("Should throw exception for null audio file")
public void testUploadAudioFile_NullFile() {
    IllegalArgumentException exception = assertThrows(
        IllegalArgumentException.class,
        () -> transcriptionService.uploadAudioFile(null)
    );
    
    assertTrue(exception.getMessage().contains("required"));
}
```

#### Integration Tests
```java
@Test
@EnabledIf("isRealApiEnabled")
@DisplayName("Complete workflow: Upload + Create + Poll - Real API")
public void testCompleteWorkflow_RealAPI() throws Exception {
    // Given
    File audioFile = new File(testAudioFilePath);
    
    // When
    UploadResponse upload = service.uploadAudioFile(audioFile);
    TranscriptRequest request = new TranscriptRequest(upload.getUploadUrl());
    TranscriptResponse transcript = service.createTranscript(request);
    TranscriptResponse completed = service.pollForCompletion(transcript.getId());
    
    // Then
    assertTrue(completed.isCompleted());
    assertNotNull(completed.getText());
}
```

### Test Naming Convention
```
test[MethodName]_[Scenario]_[ExpectedResult]

Examples:
- testUploadAudioFile_ValidFile_ReturnsUploadUrl
- testCreateTranscript_NullUrl_ThrowsException
- testPollForCompletion_Timeout_ThrowsException
```

### Running Tests
```bash
# All tests
./gradlew test

# Specific test class
./gradlew test --tests "TranscriptionServiceTest"

# Integration tests only
./gradlew test --tests "*IntegrationTest"

# With coverage report
./gradlew test jacocoTestReport
```

---

## 🔄 Pull Request Process

### Before Submitting

1. **Update Documentation**
   - Update README.md if adding features
   - Add JavaDoc for new public methods
   - Update CHANGELOG.md

2. **Add Tests**
   - Unit tests for new functionality
   - Integration tests for API interactions
   - Ensure 90%+ coverage

3. **Run Quality Checks**
```bash
./gradlew clean build test
./gradlew checkstyleMain checkstyleTest
```

4. **Update Version**
   - Follow [Semantic Versioning](https://semver.org/)
   - Update `version.properties`

### PR Template

```markdown
## Description
Brief description of changes

## Type of Change
- [ ] Bug fix
- [ ] New feature
- [ ] Breaking change
- [ ] Documentation update

## Testing
- [ ] Unit tests added/updated
- [ ] Integration tests added/updated
- [ ] All tests passing
- [ ] Coverage maintained/improved

## Checklist
- [ ] Code follows style guidelines
- [ ] Self-review completed
- [ ] Comments added for complex logic
- [ ] Documentation updated
- [ ] No new warnings
- [ ] GPLv3 license header added to new files

## Related Issues
Fixes #(issue number)
```

### Review Process

1. **Automated Checks**: CI/CD runs tests and quality checks
2. **Code Review**: Maintainer reviews code
3. **Feedback**: Address review comments
4. **Approval**: Maintainer approves PR
5. **Merge**: PR is merged to main branch

### After Merge

- Your contribution will be included in the next release
- You'll be added to CONTRIBUTORS.md
- Release notes will credit your contribution

---

## 📄 License

By contributing, you agree that your contributions will be licensed under the GNU General Public License v3.0.

All new files must include the GPLv3 license header:

```java
/*
 * AssemblyAI Extension for Krista
 * Copyright (C) 2024 Your Organization
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
```

---

## 🎯 Development Workflow

### Branch Naming
- `feature/description` - New features
- `bugfix/description` - Bug fixes
- `docs/description` - Documentation
- `refactor/description` - Code refactoring

### Commit Messages
Follow [Conventional Commits](https://www.conventionalcommits.org/):

```
feat: add PII redaction support
fix: resolve timeout issue for large files
docs: update API reference
test: add integration tests for sentiment analysis
refactor: simplify error handling logic
```

### Release Process
1. Update version in `version.properties`
2. Update CHANGELOG.md
3. Create release branch
4. Tag release
5. Build and publish artifacts

---

## 🙏 Recognition

Contributors will be recognized in:
- CONTRIBUTORS.md file
- Release notes
- Project README

Thank you for making this project better! 🎉

---

## 📞 Questions?

- **General Questions**: [GitHub Discussions](https://github.com/your-org/krista-assemblyai-extension/discussions)
- **Bug Reports**: [GitHub Issues](https://github.com/your-org/krista-assemblyai-extension/issues)
- **Security Issues**: security@your-org.com

---

**Happy Contributing! 🚀**

