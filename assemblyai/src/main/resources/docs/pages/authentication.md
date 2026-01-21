# Authentication

The Krista AssemblyAI extension uses API Key authentication for secure access to AssemblyAI services.

## API Key Authentication

### How It Works

1. **API Key Storage**: Your AssemblyAI API key is securely stored in Krista's credential store
2. **Request Authentication**: Every API request includes the API key in the Authorization header
3. **Secure Transmission**: All API requests use HTTPS for encrypted communication
4. **No Token Refresh**: Unlike OAuth, API keys don't expire and don't require refresh

### Authentication Flow

```
1. User configures API key in Krista extension settings
   ↓
2. Krista stores API key securely in credential store
   ↓
3. Extension retrieves API key when making requests
   ↓
4. API key is included in Authorization header
   ↓
5. AssemblyAI validates the API key
   ↓
6. Request is processed if key is valid
```

## Security Considerations

### API Key Storage
- API keys are stored securely in Krista's encrypted credential store
- Keys are never logged or exposed in error messages
- Keys are encrypted at rest and in transit

### API Security
- All API requests use HTTPS (TLS 1.2+)
- API keys are sent in Authorization header, not in URL
- Input validation prevents injection attacks
- Rate limiting protects against abuse

### Best Practices
- Use dedicated API keys for each Krista environment (dev, test, prod)
- Regularly monitor API usage in AssemblyAI dashboard
- Implement proper error handling for authentication failures
- Rotate API keys periodically (every 90 days recommended)
- Use IP whitelisting if available in your AssemblyAI plan

## Required Permissions

The AssemblyAI API key provides access to:

| Permission              | Description                        | Usage                                    |
|-------------------------|------------------------------------|-----------------------------------------|
| Upload Audio            | Upload audio files to AssemblyAI   | Required for all transcription requests |
| Create Transcripts      | Create transcription jobs          | Core functionality                      |
| Retrieve Transcripts    | Get transcription results          | Retrieve completed transcriptions       |
| Speaker Diarization     | Identify different speakers        | Call transcription feature              |
| Sentiment Analysis      | Analyze emotional tone             | Call transcription feature              |
| Topic Detection         | Detect topics using IAB taxonomy   | Call transcription feature              |
| PII Redaction           | Redact sensitive information       | Optional privacy feature                |

## Troubleshooting Authentication

### Common Issues

**Invalid API Key**
- Verify the API key is copied correctly without extra spaces
- Check if the API key has been regenerated in AssemblyAI dashboard
- Ensure the API key belongs to an active account

**Authentication Failed**
- Verify API key is configured correctly in Krista
- Check if your AssemblyAI account is active
- Ensure you haven't exceeded your plan's limits
- Verify network connectivity to AssemblyAI API

**Rate Limit Exceeded**
- Check your current usage in AssemblyAI dashboard
- Upgrade your plan if you need higher limits
- Implement request throttling in your workflows
- Contact AssemblyAI support for temporary limit increases

**Permission Denied**
- Verify your plan includes the requested features
- Check if specific features require plan upgrade
- Ensure account is in good standing

### Debug Steps

1. **Verify API Key Configuration**
   - Check extension settings in Krista
   - Ensure API key is saved correctly
   - Test with a simple request

2. **Check AssemblyAI Account Status**
   - Log in to AssemblyAI dashboard
   - Verify account is active
   - Check usage limits and quotas

3. **Test API Connectivity**
   - Verify network connectivity
   - Check firewall rules
   - Test API endpoint accessibility

4. **Review Extension Logs**
   - Check Krista extension logs for errors
   - Look for authentication-related messages
   - Identify specific error codes

5. **Contact Support**
   - Krista support for extension issues
   - AssemblyAI support for API issues
   - Provide error logs and request IDs

## API Key Rotation

### When to Rotate

- Every 90 days as a security best practice
- Immediately if key is compromised
- When team members with key access leave
- After security incidents

### How to Rotate

1. **Generate New Key**
   - Log in to AssemblyAI dashboard
   - Generate a new API key
   - Keep both old and new keys active temporarily

2. **Update Krista Configuration**
   - Update extension settings with new API key
   - Test with a simple transcription request
   - Verify all functionality works

3. **Deactivate Old Key**
   - Once new key is verified working
   - Regenerate the old key in AssemblyAI dashboard
   - This invalidates the old key

4. **Update Documentation**
   - Document the rotation date
   - Update any runbooks or procedures
   - Notify relevant team members

## Security Checklist

- [ ] API key stored in Krista credential store (not hardcoded)
- [ ] Different API keys for dev/test/prod environments
- [ ] API key rotation schedule established
- [ ] Usage monitoring enabled in AssemblyAI dashboard
- [ ] Error handling configured to not expose API keys
- [ ] Network security (HTTPS, firewall rules) verified
- [ ] Access to API key limited to authorized personnel
- [ ] Incident response plan for compromised keys
- [ ] Regular security audits scheduled

