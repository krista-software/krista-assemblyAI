# Security Policy

## Supported Versions

We release patches for security vulnerabilities. Which versions are eligible for receiving such patches depends on the CVSS v3.0 Rating:

| Version | Supported          | CVSS v3.0 Rating |
| ------- | ------------------ | ---------------- |
| 1.0.x   | :white_check_mark: | All levels       |
| 0.9.x   | :x:                | N/A              |
| < 0.9   | :x:                | N/A              |

## Reporting a Vulnerability

**Please do not report security vulnerabilities through public GitHub issues.**

Instead, please report them via email to: **security@your-org.com**

You should receive a response within 48 hours. If for some reason you do not, please follow up via email to ensure we received your original message.

### What to Include

Please include the following information in your report:

- **Type of issue** (e.g., buffer overflow, SQL injection, cross-site scripting, etc.)
- **Full paths of source file(s)** related to the manifestation of the issue
- **Location of the affected source code** (tag/branch/commit or direct URL)
- **Step-by-step instructions** to reproduce the issue
- **Proof-of-concept or exploit code** (if possible)
- **Impact of the issue**, including how an attacker might exploit it

This information will help us triage your report more quickly.

## Security Update Process

1. **Report Received**: Security team acknowledges receipt within 48 hours
2. **Initial Assessment**: Team assesses severity and impact (1-3 business days)
3. **Fix Development**: Team develops and tests fix
4. **Security Advisory**: Draft security advisory (if needed)
5. **Coordinated Disclosure**: Notify affected users
6. **Public Release**: Release patch and publish advisory

## Security Best Practices

### For Users

#### API Key Security
```properties
# ❌ BAD: Hardcoded API key
assemblyai.api.key=abc123def456

# ✅ GOOD: Environment variable
assemblyai.api.key=${ASSEMBLYAI_API_KEY}
```

#### Configuration Security
```properties
# ✅ Use HTTPS only
assemblyai.base.url=https://api.assemblyai.com/v2

# ✅ Enable PII redaction for sensitive data
pii.redaction.enabled=true

# ✅ Set appropriate log level for production
assemblyai.log.level=WARN  # Don't log sensitive data
```

#### File Validation
```java
// ✅ Always validate files before processing
File audioFile = new File(userInput);
if (!audioFile.exists() || !audioFile.canRead()) {
    throw new SecurityException("Invalid file access");
}

// ✅ Check file size to prevent DoS
long maxSize = 100 * 1024 * 1024; // 100 MB
if (audioFile.length() > maxSize) {
    throw new SecurityException("File too large");
}
```

#### Input Sanitization
```java
// ✅ Sanitize user inputs
String fileName = sanitizeFileName(userInput);

// ✅ Validate file extensions
if (!isAllowedExtension(fileName)) {
    throw new SecurityException("Invalid file type");
}
```

### For Developers

#### Secure Coding Practices

1. **Input Validation**
```java
// ✅ Validate all inputs
public void processFile(File file) {
    if (file == null) {
        throw new IllegalArgumentException("File cannot be null");
    }
    if (!file.exists()) {
        throw new IllegalArgumentException("File does not exist");
    }
    // Process file
}
```

2. **Error Handling**
```java
// ✅ Don't expose sensitive information in errors
try {
    processFile(file);
} catch (Exception e) {
    LOG.error("File processing failed", e);
    throw new RuntimeException("Processing failed"); // Generic message
}

// ❌ Don't expose internal details
catch (Exception e) {
    throw new RuntimeException("Failed: " + e.getMessage()); // Exposes internals
}
```

3. **Logging Security**
```java
// ✅ Don't log sensitive data
LOG.info("Processing file: {}", file.getName());

// ❌ Don't log API keys or tokens
LOG.info("Using API key: {}", apiKey); // NEVER DO THIS
```

4. **Resource Management**
```java
// ✅ Always close resources
try (FileInputStream fis = new FileInputStream(file)) {
    // Process file
} // Automatically closed

// ✅ Use try-with-resources for HTTP clients
try (Response response = client.target(url).request().get()) {
    // Process response
}
```

## Known Security Considerations

### API Key Exposure
- **Risk**: API keys in logs or error messages
- **Mitigation**: Never log API keys; use environment variables
- **Status**: Implemented in v1.0.0

### File Upload Vulnerabilities
- **Risk**: Malicious file uploads
- **Mitigation**: File type validation, size limits, virus scanning
- **Status**: File validation implemented in v1.0.0

### Denial of Service (DoS)
- **Risk**: Large file uploads causing resource exhaustion
- **Mitigation**: File size limits, rate limiting
- **Status**: Size limits implemented in v1.0.0

### Man-in-the-Middle (MITM)
- **Risk**: Intercepted API communications
- **Mitigation**: HTTPS only, certificate validation
- **Status**: HTTPS enforced in v1.0.0

### PII Exposure
- **Risk**: Sensitive personal information in transcripts
- **Mitigation**: PII redaction feature
- **Status**: Implemented in v1.0.0

## Security Features

### Current Implementation

#### 1. Input Validation
- File existence and readability checks
- File size validation (max 5 GB)
- File format validation
- Parameter null checks
- URL validation

#### 2. Secure Communication
- HTTPS-only API communication
- TLS 1.2+ required
- Certificate validation
- Secure headers

#### 3. Error Handling
- Generic error messages to users
- Detailed logging for administrators
- No sensitive data in error responses
- Proper exception handling

#### 4. PII Protection
- Automatic PII redaction option
- Configurable redaction policies
- Secure transcript storage
- Data retention policies

#### 5. Authentication
- API key authentication
- Secure key storage
- Key rotation support
- Environment variable configuration

### Planned Security Enhancements

#### Version 1.1.0
- [ ] Rate limiting for API calls
- [ ] Request signing for additional security
- [ ] Audit logging for all operations
- [ ] Enhanced file type validation

#### Version 1.2.0
- [ ] End-to-end encryption for file uploads
- [ ] Multi-factor authentication support
- [ ] Advanced threat detection
- [ ] Security scanning integration

## Compliance

### Data Protection
- **GDPR**: PII redaction support
- **CCPA**: Data deletion capabilities
- **HIPAA**: Encryption and audit logging (planned)

### Standards
- **OWASP Top 10**: Addressed in design
- **CWE/SANS Top 25**: Regular security reviews
- **NIST Cybersecurity Framework**: Aligned practices

## Security Audits

### Internal Audits
- **Frequency**: Quarterly
- **Scope**: Code review, dependency scanning, penetration testing
- **Last Audit**: 2024-11-01
- **Next Audit**: 2025-02-01

### External Audits
- **Frequency**: Annually
- **Last Audit**: Not yet conducted
- **Next Audit**: Planned for Q2 2025

## Dependency Security

### Automated Scanning
We use automated tools to scan dependencies for known vulnerabilities:
- **Gradle Dependency Check**: Weekly scans
- **GitHub Dependabot**: Automatic PR for updates
- **Snyk**: Continuous monitoring

### Current Dependencies
All dependencies are regularly updated to address security vulnerabilities:
- Jersey 2.35 (latest stable)
- Jackson 2.15.2 (latest stable)
- Netty 4.1.118.Final (security patches applied)
- Commons Codec 1.17.1 (CVE-2024-48924 fixed)

## Incident Response

### Response Team
- **Security Lead**: security@your-org.com
- **Response Time**: 48 hours for initial response
- **Escalation**: Critical issues escalated immediately

### Response Process
1. **Detection**: Vulnerability reported or detected
2. **Assessment**: Severity and impact analysis
3. **Containment**: Immediate mitigation if needed
4. **Remediation**: Develop and test fix
5. **Communication**: Notify affected users
6. **Post-Mortem**: Document lessons learned

## Security Contacts

- **General Security**: security@your-org.com
- **Urgent Issues**: security-urgent@your-org.com (24/7)
- **PGP Key**: Available at https://your-org.com/security/pgp-key.asc

## Acknowledgments

We thank the following security researchers for responsibly disclosing vulnerabilities:

- (No vulnerabilities reported yet)

## Additional Resources

- [OWASP Top 10](https://owasp.org/www-project-top-ten/)
- [CWE Top 25](https://cwe.mitre.org/top25/)
- [NIST Cybersecurity Framework](https://www.nist.gov/cyberframework)
- [AssemblyAI Security](https://www.assemblyai.com/security)

---

**Last Updated**: 2024-11-04  
**Version**: 1.0.0

