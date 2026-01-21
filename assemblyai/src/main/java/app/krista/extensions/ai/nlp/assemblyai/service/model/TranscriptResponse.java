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

package app.krista.extensions.ai.nlp.assemblyai.service.model;

import app.krista.extensions.ai.nlp.assemblyai.util.AssemblyAIConstants;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Response model for transcript operations.
 * Contains the full transcript data including text, words, utterances, and analysis results.
 */
public class TranscriptResponse {

    private String id;
    private String status;
    private String text;
    private String audioUrl;
    private Double audioDuration;
    private Double confidence;
    private String languageCode;
    private Boolean speakerLabels;
    private Boolean sentimentAnalysis;
    private Boolean iabCategories;
    private Boolean redactPii;
    private String error;
    private List<Word> words;
    private List<Utterance> utterances;
    private List<SentimentAnalysisResult> sentimentAnalysisResults;
    private Map<String, Object> iabCategoriesResult;

    public TranscriptResponse() {
        this.words = new ArrayList<>();
        this.utterances = new ArrayList<>();
        this.sentimentAnalysisResults = new ArrayList<>();
        this.iabCategoriesResult = new HashMap<>();
    }

    /**
     * Creates TranscriptResponse from JSON.
     *
     * @param json JSON object
     * @return TranscriptResponse instance
     * @throws JSONException if parsing fails
     */
    public static TranscriptResponse fromJson(JSONObject json) throws JSONException {
        TranscriptResponse response = new TranscriptResponse();

        if (json.has(AssemblyAIConstants.ID)) {
            response.setId(json.getString(AssemblyAIConstants.ID));
        }
        if (json.has(AssemblyAIConstants.STATUS)) {
            response.setStatus(json.getString(AssemblyAIConstants.STATUS));
        }
        if (json.has(AssemblyAIConstants.TEXT) && !json.isNull(AssemblyAIConstants.TEXT)) {
            response.setText(json.getString(AssemblyAIConstants.TEXT));
        }
        if (json.has(AssemblyAIConstants.AUDIO_URL)) {
            response.setAudioUrl(json.getString(AssemblyAIConstants.AUDIO_URL));
        }
        if (json.has(AssemblyAIConstants.AUDIO_DURATION) && !json.isNull(AssemblyAIConstants.AUDIO_DURATION)) {
            response.setAudioDuration(json.getDouble(AssemblyAIConstants.AUDIO_DURATION));
        }
        if (json.has(AssemblyAIConstants.CONFIDENCE) && !json.isNull(AssemblyAIConstants.CONFIDENCE)) {
            response.setConfidence(json.getDouble(AssemblyAIConstants.CONFIDENCE));
        }
        if (json.has(AssemblyAIConstants.LANGUAGE_CODE)) {
            response.setLanguageCode(json.getString(AssemblyAIConstants.LANGUAGE_CODE));
        }
        if (json.has(AssemblyAIConstants.SPEAKER_LABELS)) {
            response.setSpeakerLabels(json.getBoolean(AssemblyAIConstants.SPEAKER_LABELS));
        }
        if (json.has(AssemblyAIConstants.SENTIMENT_ANALYSIS)) {
            response.setSentimentAnalysis(json.getBoolean(AssemblyAIConstants.SENTIMENT_ANALYSIS));
        }
        if (json.has(AssemblyAIConstants.IAB_CATEGORIES)) {
            response.setIabCategories(json.getBoolean(AssemblyAIConstants.IAB_CATEGORIES));
        }
        if (json.has(AssemblyAIConstants.REDACT_PII)) {
            response.setRedactPii(json.getBoolean(AssemblyAIConstants.REDACT_PII));
        }
        if (json.has(AssemblyAIConstants.ERROR) && !json.isNull(AssemblyAIConstants.ERROR)) {
            response.setError(json.getString(AssemblyAIConstants.ERROR));
        }

        // Parse words array
        if (json.has(AssemblyAIConstants.WORDS) && !json.isNull(AssemblyAIConstants.WORDS)) {
            JSONArray wordsArray = json.getJSONArray(AssemblyAIConstants.WORDS);
            List<Word> wordsList = new ArrayList<>();
            for (int i = 0; i < wordsArray.length(); i++) {
                wordsList.add(Word.fromJson(wordsArray.getJSONObject(i)));
            }
            response.setWords(wordsList);
        }

        // Parse utterances array
        if (json.has(AssemblyAIConstants.UTTERANCES) && !json.isNull(AssemblyAIConstants.UTTERANCES)) {
            JSONArray utterancesArray = json.getJSONArray(AssemblyAIConstants.UTTERANCES);
            List<Utterance> utterancesList = new ArrayList<>();
            for (int i = 0; i < utterancesArray.length(); i++) {
                utterancesList.add(Utterance.fromJson(utterancesArray.getJSONObject(i)));
            }
            response.setUtterances(utterancesList);
        }

        // Parse sentiment analysis results
        if (json.has(AssemblyAIConstants.SENTIMENT_ANALYSIS_RESULTS) && !json.isNull(AssemblyAIConstants.SENTIMENT_ANALYSIS_RESULTS)) {
            JSONArray sentimentArray = json.getJSONArray(AssemblyAIConstants.SENTIMENT_ANALYSIS_RESULTS);
            List<SentimentAnalysisResult> sentimentList = new ArrayList<>();
            for (int i = 0; i < sentimentArray.length(); i++) {
                sentimentList.add(SentimentAnalysisResult.fromJson(sentimentArray.getJSONObject(i)));
            }
            response.setSentimentAnalysisResults(sentimentList);
        }

        // Parse IAB categories result
        if (json.has(AssemblyAIConstants.IAB_CATEGORIES_RESULT) && !json.isNull(AssemblyAIConstants.IAB_CATEGORIES_RESULT)) {
            JSONObject iabJson = json.getJSONObject(AssemblyAIConstants.IAB_CATEGORIES_RESULT);
            Map<String, Object> iabMap = new HashMap<>();
            // Convert JSONObject to Map (simplified - can be enhanced)
            response.setIabCategoriesResult(iabMap);
        }

        return response;
    }

    /**
     * Checks if the transcript is completed.
     *
     * @return true if status is completed
     */
    public boolean isCompleted() {
        return AssemblyAIConstants.STATUS_COMPLETED.equals(status);
    }

    /**
     * Checks if the transcript has an error.
     *
     * @return true if status is error
     */
    public boolean hasError() {
        return AssemblyAIConstants.STATUS_ERROR.equals(status);
    }

    /**
     * Checks if the transcript is still processing.
     *
     * @return true if status is queued or processing
     */
    public boolean isProcessing() {
        return AssemblyAIConstants.STATUS_QUEUED.equals(status) ||
                AssemblyAIConstants.STATUS_PROCESSING.equals(status);
    }

    // Getters and Setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getAudioUrl() {
        return audioUrl;
    }

    public void setAudioUrl(String audioUrl) {
        this.audioUrl = audioUrl;
    }

    public Double getAudioDuration() {
        return audioDuration;
    }

    public void setAudioDuration(Double audioDuration) {
        this.audioDuration = audioDuration;
    }

    public Double getConfidence() {
        return confidence;
    }

    public void setConfidence(Double confidence) {
        this.confidence = confidence;
    }

    public String getLanguageCode() {
        return languageCode;
    }

    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }

    public Boolean getSpeakerLabels() {
        return speakerLabels;
    }

    public void setSpeakerLabels(Boolean speakerLabels) {
        this.speakerLabels = speakerLabels;
    }

    public Boolean getSentimentAnalysis() {
        return sentimentAnalysis;
    }

    public void setSentimentAnalysis(Boolean sentimentAnalysis) {
        this.sentimentAnalysis = sentimentAnalysis;
    }

    public Boolean getIabCategories() {
        return iabCategories;
    }

    public void setIabCategories(Boolean iabCategories) {
        this.iabCategories = iabCategories;
    }

    public Boolean getRedactPii() {
        return redactPii;
    }

    public void setRedactPii(Boolean redactPii) {
        this.redactPii = redactPii;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public List<Word> getWords() {
        return words;
    }

    public void setWords(List<Word> words) {
        this.words = words;
    }

    public List<Utterance> getUtterances() {
        return utterances;
    }

    public void setUtterances(List<Utterance> utterances) {
        this.utterances = utterances;
    }

    public List<SentimentAnalysisResult> getSentimentAnalysisResults() {
        return sentimentAnalysisResults;
    }

    public void setSentimentAnalysisResults(List<SentimentAnalysisResult> sentimentAnalysisResults) {
        this.sentimentAnalysisResults = sentimentAnalysisResults;
    }

    public Map<String, Object> getIabCategoriesResult() {
        return iabCategoriesResult;
    }

    public void setIabCategoriesResult(Map<String, Object> iabCategoriesResult) {
        this.iabCategoriesResult = iabCategoriesResult;
    }

    @Override
    public String toString() {
        return "TranscriptResponse{" +
                "id='" + id + '\'' +
                ", status='" + status + '\'' +
                ", text='" + text + '\'' +
                ", audioUrl='" + audioUrl + '\'' +
                ", audioDuration=" + audioDuration +
                ", confidence=" + confidence +
                ", error='" + error + '\'' +
                '}';
    }
}

