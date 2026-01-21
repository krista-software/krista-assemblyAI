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
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

/**
 * Request model for creating a transcript.
 */
public class TranscriptRequest {

    private String audioUrl;
    private Boolean speakerLabels;
    private Boolean sentimentAnalysis;
    private Boolean iabCategories;
    private Boolean redactPii;
    private String languageCode;
    private Boolean punctuate;
    private Boolean formatText;

    public TranscriptRequest() {
        // Default values
        this.speakerLabels = true;
        this.sentimentAnalysis = true;
        this.iabCategories = true;
        this.redactPii = false;
        this.punctuate = true;
        this.formatText = true;
    }

    public TranscriptRequest(String audioUrl) {
        this();
        this.audioUrl = audioUrl;
    }

    /**
     * Converts this request to JSON.
     *
     * @return JSONObject representation
     * @throws JSONException if conversion fails
     */
    public JSONObject toJson() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(AssemblyAIConstants.AUDIO_URL, audioUrl);
        if (speakerLabels != null) {
            json.put(AssemblyAIConstants.SPEAKER_LABELS, speakerLabels);
        }
        if (sentimentAnalysis != null) {
            json.put(AssemblyAIConstants.SENTIMENT_ANALYSIS, sentimentAnalysis);
        }
        if (iabCategories != null) {
            json.put(AssemblyAIConstants.IAB_CATEGORIES, iabCategories);
        }
        if (redactPii != null) {
            json.put(AssemblyAIConstants.REDACT_PII, redactPii);
        }
        if (languageCode != null) {
            json.put(AssemblyAIConstants.LANGUAGE_CODE, languageCode);
        }
        if (punctuate != null) {
            json.put(AssemblyAIConstants.PUNCTUATE, punctuate);
        }
        if (formatText != null) {
            json.put(AssemblyAIConstants.FORMAT_TEXT, formatText);
        }
        return json;
    }

    public String getAudioUrl() {
        return audioUrl;
    }

    public void setAudioUrl(String audioUrl) {
        this.audioUrl = audioUrl;
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

    public String getLanguageCode() {
        return languageCode;
    }

    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }

    public Boolean getPunctuate() {
        return punctuate;
    }

    public void setPunctuate(Boolean punctuate) {
        this.punctuate = punctuate;
    }

    public Boolean getFormatText() {
        return formatText;
    }

    public void setFormatText(Boolean formatText) {
        this.formatText = formatText;
    }

    @Override
    public String toString() {
        return "TranscriptRequest{" +
                "audioUrl='" + audioUrl + '\'' +
                ", speakerLabels=" + speakerLabels +
                ", sentimentAnalysis=" + sentimentAnalysis +
                ", iabCategories=" + iabCategories +
                ", redactPii=" + redactPii +
                ", languageCode='" + languageCode + '\'' +
                ", punctuate=" + punctuate +
                ", formatText=" + formatText +
                '}';
    }
}

