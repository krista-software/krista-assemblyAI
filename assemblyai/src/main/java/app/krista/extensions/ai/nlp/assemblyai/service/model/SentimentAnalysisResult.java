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
 * Model representing sentiment analysis results for a segment of text.
 */
public class SentimentAnalysisResult {

    private String text;
    private Integer start;
    private Integer end;
    private String sentiment;
    private Double confidence;
    private String speaker;

    public SentimentAnalysisResult() {
    }

    public SentimentAnalysisResult(String text, Integer start, Integer end, String sentiment, Double confidence, String speaker) {
        this.text = text;
        this.start = start;
        this.end = end;
        this.sentiment = sentiment;
        this.confidence = confidence;
        this.speaker = speaker;
    }

    /**
     * Creates SentimentAnalysisResult from JSON.
     *
     * @param json JSON object
     * @return SentimentAnalysisResult instance
     * @throws JSONException if parsing fails
     */
    public static SentimentAnalysisResult fromJson(JSONObject json) throws JSONException {
        SentimentAnalysisResult result = new SentimentAnalysisResult();
        if (json.has(AssemblyAIConstants.WORD_TEXT)) {
            result.setText(json.getString(AssemblyAIConstants.WORD_TEXT));
        }
        if (json.has(AssemblyAIConstants.START)) {
            result.setStart(json.getInt(AssemblyAIConstants.START));
        }
        if (json.has(AssemblyAIConstants.END)) {
            result.setEnd(json.getInt(AssemblyAIConstants.END));
        }
        if (json.has(AssemblyAIConstants.SENTIMENT)) {
            result.setSentiment(json.getString(AssemblyAIConstants.SENTIMENT));
        }
        if (json.has(AssemblyAIConstants.SENTIMENT_CONFIDENCE)) {
            result.setConfidence(json.getDouble(AssemblyAIConstants.SENTIMENT_CONFIDENCE));
        }
        if (json.has(AssemblyAIConstants.SPEAKER)) {
            result.setSpeaker(json.getString(AssemblyAIConstants.SPEAKER));
        }
        return result;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Integer getStart() {
        return start;
    }

    public void setStart(Integer start) {
        this.start = start;
    }

    public Integer getEnd() {
        return end;
    }

    public void setEnd(Integer end) {
        this.end = end;
    }

    public String getSentiment() {
        return sentiment;
    }

    public void setSentiment(String sentiment) {
        this.sentiment = sentiment;
    }

    public Double getConfidence() {
        return confidence;
    }

    public void setConfidence(Double confidence) {
        this.confidence = confidence;
    }

    public String getSpeaker() {
        return speaker;
    }

    public void setSpeaker(String speaker) {
        this.speaker = speaker;
    }

    @Override
    public String toString() {
        return "SentimentAnalysisResult{" +
                "text='" + text + '\'' +
                ", start=" + start +
                ", end=" + end +
                ", sentiment='" + sentiment + '\'' +
                ", confidence=" + confidence +
                ", speaker='" + speaker + '\'' +
                '}';
    }
}

