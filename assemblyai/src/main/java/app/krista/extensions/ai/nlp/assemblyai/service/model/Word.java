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
 * Model representing a word in the transcript with timing and speaker information.
 */
public class Word {

    private String text;
    private Integer start;
    private Integer end;
    private Double confidence;
    private String speaker;

    public Word() {
    }

    public Word(String text, Integer start, Integer end, Double confidence, String speaker) {
        this.text = text;
        this.start = start;
        this.end = end;
        this.confidence = confidence;
        this.speaker = speaker;
    }

    /**
     * Creates Word from JSON.
     *
     * @param json JSON object
     * @return Word instance
     * @throws JSONException if parsing fails
     */
    public static Word fromJson(JSONObject json) throws JSONException {
        Word word = new Word();
        if (json.has(AssemblyAIConstants.WORD_TEXT)) {
            word.setText(json.getString(AssemblyAIConstants.WORD_TEXT));
        }
        if (json.has(AssemblyAIConstants.START)) {
            word.setStart(json.getInt(AssemblyAIConstants.START));
        }
        if (json.has(AssemblyAIConstants.END)) {
            word.setEnd(json.getInt(AssemblyAIConstants.END));
        }
        if (json.has(AssemblyAIConstants.CONFIDENCE)) {
            word.setConfidence(json.getDouble(AssemblyAIConstants.CONFIDENCE));
        }
        if (json.has(AssemblyAIConstants.SPEAKER)) {
            word.setSpeaker(json.getString(AssemblyAIConstants.SPEAKER));
        }
        return word;
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
        return "Word{" +
                "text='" + text + '\'' +
                ", start=" + start +
                ", end=" + end +
                ", confidence=" + confidence +
                ", speaker='" + speaker + '\'' +
                '}';
    }
}

