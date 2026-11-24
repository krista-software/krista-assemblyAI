# Extension Configuration

This guide provides step-by-step instructions for configuring the AssemblyAI extension in Krista.

## Overview

The AssemblyAI extension requires minimal configuration - only an API key is needed to connect to AssemblyAI's transcription services. The extension automatically handles all other settings including the API endpoint, polling intervals, and feature enablement.

## Prerequisites

Before configuring the extension, ensure you have:

- [ ] An active AssemblyAI account
- [ ] Your AssemblyAI API key ([How to obtain](pages/ObtainingApiKey.md))
- [ ] Access to Krista administration console
- [ ] Appropriate permissions to configure extensions in Krista

## Configuration Parameters

The extension requires the following configuration parameter:

| Parameter | Type | Required | Description | Example |
|-----------|------|----------|-------------|---------|
| **API Key** | Text (Secured) | Yes | Your AssemblyAI API key for authentication | `f4a4eb8c18db4fb280606244e0d68421` |

### API Key

- **Purpose**: Authenticates all requests to the AssemblyAI API
- **Format**: 32-character hexadecimal string
- **Security**: Stored encrypted in Krista's credential store
- **Scope**: Provides access to all AssemblyAI transcription features
- **Obtain**: See [Obtaining API Key](pages/ObtainingApiKey.md)

**Note**: The extension uses the default AssemblyAI API endpoint (`https://api.assemblyai.com/v2`) automatically. No additional endpoint configuration is required.

## Configuration Steps

### Step 1: Access Extension Configuration

1. Log in to your Krista administration console
2. Navigate to **Extensions** or **Integrations** section
3. Locate **AssemblyAI** in the extension list
4. Click **Configure** or **Settings** to open the configuration panel

### Step 2: Enter API Key

1. In the **API Key** field, paste your AssemblyAI API key
2. Ensure there are no extra spaces before or after the key
3. The key should be exactly 32 characters long
4. Click **Save** or **Apply** to store the configuration

**Security Note**: The API key is automatically encrypted when saved and is never exposed in logs or error messages.

### Step 3: Test Connection

After saving the configuration:

1. Click the **Test Connection** button
2. The extension will validate the API key by making a test request to AssemblyAI
3. Wait for the connection test to complete (typically 2-5 seconds)
4. Verify you see a success message

**What the test does**:
- Validates API key format
- Verifies network connectivity to AssemblyAI API
- Confirms API key is active and has proper permissions
- Checks that the AssemblyAI service is accessible

### Step 4: Verify Extension Status

1. Confirm the extension status shows as **Active** or **Enabled**
2. Verify the configuration was saved successfully
3. Review any informational messages or warnings
4. Document the configuration for your team

## Environment-Specific Configuration

### Development Environment

For development and testing:

```
API Key: [Development API Key]
```

**Recommendations**:
- Use a separate API key for development
- Monitor usage to stay within free tier limits
- Test with small audio files to conserve quota
- Document the development API key location

### Production Environment

For production deployments:

```
API Key: [Production API Key]
```

**Recommendations**:
- Use a dedicated production API key
- Implement API key rotation schedule
- Monitor usage and set up alerts
- Ensure appropriate plan limits for production volume
- Document key rotation procedures

**Best Practice**: Always use separate API keys for each environment to:
- Track usage independently
- Isolate development from production
- Enable environment-specific key rotation
- Prevent accidental production usage in development
- Simplify troubleshooting and auditing

## Configuration Validation

### Validation Checklist

After configuration, verify the following:

- [ ] API key is configured and saved
- [ ] Test connection succeeds
- [ ] Extension status is Active/Enabled
- [ ] Configuration is documented for your team
- [ ] Team members know how to access the configuration
- [ ] API key rotation schedule is established

### Test with Sample Request

Validate the configuration with a simple test:

1. Create a test workflow in Krista
2. Add the **"Transcribe Call"** catalog request
3. Upload a small test audio file (< 1 minute, WAV or MP3 format)
4. Set **Redact PII** to `false`
5. Execute the workflow
6. Verify you receive a successful transcription result

**Expected Results**:
- Transcription Successful: `true`
- Transcript Text: Contains the transcribed audio
- Sentiment: Shows sentiment percentages
- Topic Detected: May show detected topics (or "No topics detected")
- Transcript File: Contains formatted transcript

If all results are as expected, your configuration is correct!

## Troubleshooting

### API Key Not Accepted

**Symptoms**:
- "Invalid API key" error message
- "Authentication failed" error
- Test connection fails with 401 Unauthorized

**Possible Causes**:
- API key copied incorrectly (extra spaces, missing characters)
- API key was regenerated in AssemblyAI dashboard
- AssemblyAI account is inactive or suspended
- API key belongs to a different account

**Solutions**:
1. Verify the API key is copied correctly without extra spaces
2. Check if the key was regenerated in AssemblyAI dashboard
3. Ensure the AssemblyAI account is active and in good standing
4. Try generating a new API key from AssemblyAI dashboard
5. Verify you're using the correct AssemblyAI account

### Connection Test Fails

**Symptoms**:
- "Connection failed" error message
- "Unable to reach API" error
- Timeout during connection test

**Possible Causes**:
- Network connectivity issues
- Firewall blocking HTTPS traffic to AssemblyAI
- Proxy configuration issues
- AssemblyAI service outage

**Solutions**:
1. Verify network connectivity from Krista server to internet
2. Check firewall rules allow HTTPS (port 443) to `api.assemblyai.com`
3. Verify proxy settings if your network uses a proxy
4. Check AssemblyAI service status: https://status.assemblyai.com/
5. Test connectivity using curl: `curl -H "authorization: YOUR_API_KEY" https://api.assemblyai.com/v2/transcript`

### Configuration Not Saving

**Symptoms**:
- Settings revert to previous values after saving
- "Save failed" error message
- Changes don't persist after page refresh

**Possible Causes**:
- Insufficient permissions in Krista
- Validation errors in the configuration form
- Database connection issues
- Browser cache issues

**Solutions**:
1. Verify you have administrator permissions in Krista
2. Check for validation error messages in the form
3. Review Krista server logs for save errors
4. Try clearing browser cache and cookies
5. Try using a different browser
6. Contact Krista support if issue persists

## Security Best Practices

### Credential Management

1. **Never hardcode API keys** in workflows, scripts, or code
2. **Use Krista's credential store** for all sensitive configuration
3. **Rotate API keys regularly** (recommended: every 90 days)
4. **Limit access** to extension configuration to authorized personnel only
5. **Audit configuration changes** regularly and maintain change log
6. **Document key rotation procedures** for your team

### Network Security

1. **Use HTTPS only** for all API communication (enforced by extension)
2. **Configure firewall rules** to allow only necessary outbound traffic
3. **Enable IP whitelisting** if available in your AssemblyAI plan
4. **Monitor API usage** for unusual patterns or anomalies
5. **Implement rate limiting** in your workflows if needed

### Access Control

1. **Implement role-based access control** for extension configuration
2. **Log all configuration changes** with user attribution and timestamps
3. **Review access logs** regularly for unauthorized access attempts
4. **Revoke access immediately** for users who no longer need it
5. **Use separate accounts** for different teams or departments

## Advanced Configuration

### Polling Configuration

The extension uses built-in polling to check transcription status:

- **Polling Interval**: 3 seconds (hardcoded, optimal for most use cases)
- **Maximum Attempts**: 200 attempts (10 minutes total)
- **Timeout Calculation**: 200 attempts × 3 seconds = 600 seconds (10 minutes)

**Note**: These settings are optimized for typical audio files and cannot be changed through configuration. For very long audio files (> 1 hour), the extension will continue polling up to the maximum timeout.

### API Endpoint

The extension uses the default AssemblyAI API endpoint:

- **Base URL**: `https://api.assemblyai.com/v2`
- **Upload Endpoint**: `/upload`
- **Transcript Endpoint**: `/transcript`

**Note**: The API endpoint is hardcoded and cannot be changed. This ensures compatibility with AssemblyAI's production service.

## Related Pages

- [Obtaining API Key](pages/ObtainingApiKey.md) - How to get your AssemblyAI API key
- [Authentication](pages/Authentication.md) - Details about API key authentication
- [Transcribe Call](pages/TranscribeCall.md) - Using the transcription feature
- [Home](../README.md) - Extension overview and documentation

