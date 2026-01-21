/*
 * AssemblyAI Extension for Krista
 * Copyright (C) 2024 Krista Software
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

package app.krista.extensions.ai.nlp.assemblyai.integration;

import app.krista.extensions.ai.nlp.assemblyai.catalog.AssemblyAIAttributes;
import app.krista.extensions.ai.nlp.assemblyai.bridge.ResponseMapper;
import app.krista.extensions.ai.nlp.assemblyai.catalog.query.TranscribeCallQuery;
import app.krista.extensions.ai.nlp.assemblyai.util.AssemblyAIConstants;
import app.krista.extensions.ai.nlp.assemblyai.catalog.KristaMediaClient;
import app.krista.extensions.ai.nlp.assemblyai.service.TranscriptionService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Base class for integration tests.
 * 
 * This class provides:
 * - Real credentials loading from config.properties
 * - Real service instances (NO MOCKS)
 * - Common test setup and utilities
 * - Integration test configuration
 * 
 * Integration Test Philosophy:
 * - Use REAL AssemblyAI API credentials
 * - Use REAL audio files
 * - Test REAL end-to-end workflows
 * - NO MOCKS - test actual behavior
 * - Verify actual API responses
 * 
 * Configuration:
 * - Credentials loaded from src/test/resources/config.properties
 * - Feature flags control which tests run
 * - Environment variables override config.properties
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class IntegrationTestBase {
    
    private static final Logger LOG = LogManager.getLogger(IntegrationTestBase.class.getName());
    
    protected Properties testConfig;
    protected AssemblyAIAttributes attributes;
    protected TranscriptionService transcriptionService;
    protected KristaMediaClient kristaMediaClient;
    protected ResponseMapper responseMapper;
    protected TranscribeCallQuery transcribeCallQuery;
    
    // Test configuration flags
    protected boolean enableRealApiCalls;
    protected boolean enableUploadTests;
    protected boolean enableTranscriptionTests;
    protected boolean enableSentimentTests;
    protected boolean enableTopicTests;
    
    // Test configuration values
    protected String testAudioFilePath;
    protected int testTimeoutSeconds;
    protected int testPollingIntervalSeconds;
    protected int testMaxPollingAttempts;
    
    @BeforeAll
    public void setupIntegrationTest() throws IOException {
        LOG.info("Setting up integration test base");
        
        // Load test configuration
        loadTestConfiguration();
        
        // Initialize real services (NO MOCKS)
        initializeRealServices();
        
        LOG.info("Integration test base setup complete - Real API calls enabled: {}", 
                enableRealApiCalls);
    }
    
    @BeforeEach
    public void beforeEachTest() {
        LOG.info("Starting integration test: {}", getClass().getSimpleName());
    }
    
    /**
     * Loads test configuration from config.properties.
     * Environment variables override config.properties values.
     */
    private void loadTestConfiguration() throws IOException {
        LOG.info("Loading test configuration from config.properties");
        
        testConfig = new Properties();
        
        // Load from config.properties
        try (InputStream input = new FileInputStream("src/test/resources/config.properties")) {
            testConfig.load(input);
            LOG.info("Loaded test configuration from config.properties");
        } catch (IOException e) {
            LOG.warn("Could not load config.properties, using defaults: {}", e.getMessage());
        }
        
        // Override with environment variables if present
        String apiKeyFromEnv = System.getenv("ASSEMBLYAI_API_KEY");
        if (apiKeyFromEnv != null && !apiKeyFromEnv.isEmpty()) {
            testConfig.setProperty("assemblyai.api.key", apiKeyFromEnv);
            LOG.info("Using API key from environment variable");
        }
        
        // Load configuration values
        String apiKey = testConfig.getProperty("assemblyai.api.key", "test-api-key");
        String baseUrl = testConfig.getProperty("assemblyai.base.url", 
                AssemblyAIConstants.DEFAULT_BASE_URL);
        
        // Load feature flags
        enableRealApiCalls = Boolean.parseBoolean(
                testConfig.getProperty("test.enable.real.api.calls", "false"));
        enableUploadTests = Boolean.parseBoolean(
                testConfig.getProperty("test.enable.upload.tests", "false"));
        enableTranscriptionTests = Boolean.parseBoolean(
                testConfig.getProperty("test.enable.transcription.tests", "false"));
        enableSentimentTests = Boolean.parseBoolean(
                testConfig.getProperty("test.enable.sentiment.tests", "false"));
        enableTopicTests = Boolean.parseBoolean(
                testConfig.getProperty("test.enable.topic.tests", "false"));
        
        // Load test configuration
        testAudioFilePath = testConfig.getProperty("test.audio.file.path", 
                "src/test/resources/test-audio-sample.mp3");
        testTimeoutSeconds = Integer.parseInt(
                testConfig.getProperty("test.timeout.seconds", "600"));
        testPollingIntervalSeconds = Integer.parseInt(
                testConfig.getProperty("test.polling.interval.seconds", "3"));
        testMaxPollingAttempts = Integer.parseInt(
                testConfig.getProperty("test.max.polling.attempts", "200"));
        
        // Create attributes
        attributes = new AssemblyAIAttributes(apiKey, baseUrl, "DEBUG");
        
        LOG.info("Test configuration loaded - API Key present: {}, Base URL: {}, Real API: {}",
                !apiKey.equals("test-api-key"), baseUrl, enableRealApiCalls);
    }
    
    /**
     * Initializes real service instances.
     * NO MOCKS - all services are real implementations.
     */
    private void initializeRealServices() {
        LOG.info("Initializing real service instances (NO MOCKS)");
        
        // Initialize real Krista media client
        kristaMediaClient = new KristaMediaClient();
        LOG.debug("KristaMediaClient initialized");
        
        // Initialize real transcription service
        transcriptionService = new TranscriptionService(attributes, kristaMediaClient);
        LOG.debug("TranscriptionService initialized");

        // Initialize real mapper
        responseMapper = new ResponseMapper();
        LOG.debug("ResponseMapper initialized");

        // Initialize real query handler
        transcribeCallQuery = new TranscribeCallQuery(
                transcriptionService, responseMapper);
        LOG.debug("TranscribeCallQuery initialized");

        LOG.info("All real services initialized successfully");
    }
    
    /**
     * Checks if real API calls are enabled.
     * Tests should skip if real API calls are disabled.
     *
     * @return true if real API calls are enabled
     */
    protected boolean isRealApiEnabled() {
        return enableRealApiCalls;
    }
    
    /**
     * Gets a descriptive skip message for tests.
     *
     * @return Skip message
     */
    protected String getSkipMessage() {
        return "Skipping test - Real API calls disabled. " +
                "Set test.enable.real.api.calls=true in config.properties to enable.";
    }
    
    /**
     * Validates that API key is configured.
     *
     * @return true if API key is configured
     */
    protected boolean isApiKeyConfigured() {
        String apiKey = attributes.getApiKey();
        return apiKey != null && 
               !apiKey.isEmpty() && 
               !apiKey.equals("test-api-key") &&
               !apiKey.equals("YOUR_ASSEMBLYAI_API_KEY_HERE");
    }
}

