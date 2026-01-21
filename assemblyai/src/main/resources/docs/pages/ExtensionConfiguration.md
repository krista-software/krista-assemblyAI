# Configuring AssemblyAI Extension in Krista

This guide walks you through configuring the AssemblyAI extension in your Krista environment.

## Prerequisites

Before configuring the extension, ensure you have:
- [ ] An active AssemblyAI account
- [ ] Your AssemblyAI API key (see [Obtaining API Key](pages/obtainingApiKey.md))
- [ ] Access to Krista administration console
- [ ] Appropriate permissions to configure extensions in Krista

## Configuration Steps

### Step 1: Access Extension Configuration

1. Log in to your Krista administration console
2. Navigate to **Extensions** or **Integrations**
3. Locate **AssemblyAI Extension** in the list
4. Click **Configure** or **Settings**

### Step 2: Enter API Credentials

Configure the following required settings:

| Setting Name | Description | Example Value | Required |
|--------------|-------------|---------------|----------|
| **API Key** | Your AssemblyAI API key | `f4a4eb8c18db4fb280606244e0d68421` | Yes |

#### API Key Configuration

1. In the **API Key** field, paste your AssemblyAI API key
2. The key should be 32 characters long (hexadecimal format)
3. Click **Save** or **Apply**

**Important**: The API key is stored securely in Krista's credential store and is encrypted at rest.

**Note**: The extension uses the default AssemblyAI API endpoint (`https://api.assemblyai.com/v2`) automatically.

### Step 3: Test the Connection

After configuring the credentials:

1. Click **Test Connection** button (if available)
2. The extension will attempt to authenticate with AssemblyAI
3. Verify you see a success message
4. If the test fails, review the error message and verify your API key



### Step 4: Verify Extension Status

1. Check that the extension status shows as **Active** or **Enabled**
2. Verify the configuration is saved successfully
3. Review any warning or informational messages

## Environment-Specific Configuration

### Development Environment

```
API Key: [Development API Key]

```

### Production Environment

```
API Key: [Production API Key]

```

**Best Practice**: Use separate API keys for each environment to:
- Track usage separately
- Isolate development from production
- Enable independent key rotation
- Prevent accidental production usage in development

## Configuration Validation

After configuration, verify the following:

### Checklist

- [ ] API key is configured and saved
- [ ] Test connection succeeds
- [ ] Extension status is Active/Enabled
- [ ] Polling settings are appropriate for your use case
- [ ] Configuration is documented for your team

### Test with Sample Request

Create a simple test workflow:

1. Use the **"Upload Audio File"** catalog request
2. Upload a small test audio file (< 1 minute)
3. Use the **"Create Transcript"** request with the upload URL
4. Use the **"Get Transcript Status"** request to check progress
5. Use the **"Get Transcript Result"** to retrieve the transcript

If all steps succeed, your configuration is correct!

## Troubleshooting Configuration Issues

### API Key Not Accepted

**Symptoms**: "Invalid API key" or "Authentication failed" errors

**Solutions**:
1. Verify the API key is copied correctly (no extra spaces)
2. Check if the key was regenerated in AssemblyAI dashboard
3. Ensure the AssemblyAI account is active
4. Try generating a new API key

### Connection Test Fails

**Symptoms**: "Connection failed" or "Unable to reach API" errors

**Solutions**:
1. Verify network connectivity from Krista server
2. Check firewall rules allow HTTPS to `api.assemblyai.com`
3. Verify proxy settings if applicable
4. Check AssemblyAI service status: https://status.assemblyai.com/

### Configuration Not Saving

**Symptoms**: Settings revert after saving

**Solutions**:
1. Verify you have admin permissions in Krista
2. Check for validation errors in the form
3. Review Krista logs for save errors
4. Try clearing browser cache and retry

### Polling Timeouts

**Symptoms**: "Transcription timed out" errors for long audio files

**Solutions**:
1. Increase **Max Polling Attempts** (e.g., from 200 to 300)
2. Verify the audio file isn't corrupted
3. Check AssemblyAI dashboard for processing status
4. Consider splitting very long audio files

## Security Best Practices

### Credential Management

1. **Never hardcode API keys** in workflows or scripts
2. **Use Krista's credential store** for all sensitive data
3. **Rotate API keys** every 90 days
4. **Limit access** to extension configuration to authorized personnel
5. **Audit configuration changes** regularly

### Network Security

1. **Use HTTPS only** for all API communication
2. **Configure firewall rules** to allow only necessary traffic
3. **Enable IP whitelisting** if available in your AssemblyAI plan
4. **Monitor API usage** for unusual patterns

### Access Control

1. **Implement role-based access** for extension configuration
2. **Log all configuration changes** with user attribution
3. **Review access logs** regularly
4. **Revoke access** for users who no longer need it

## Getting Help

### Documentation Resources

- [AssemblyAI API Documentation](https://www.assemblyai.com/docs)
- [Krista Extension Documentation](/)
- [Transcribe Call](pages/TranscribeCall.md)

### Support Channels

- **Krista Support**: For extension configuration issues
- **AssemblyAI Support**: For API key and account issues
- **Community Forums**: For general questions and best practices

### Providing Information for Support

When contacting support, include:
- Extension version
- Krista version
- Error messages (with sensitive data redacted)
- Steps to reproduce the issue
- Configuration settings (without API key)
- Relevant log excerpts

