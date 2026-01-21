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
import java.util.List;

/**
 * Model representing an utterance (speaker segment) in the transcript.
 */
public class Utterance {

    private String text;
    private Integer start;
    private Integer end;
    private Double confidence;
    private String speaker;
    private List<Word> words;

    public Utterance() {
        this.words = new ArrayList<>();
    }

    public Utterance(String text, Integer start, Integer end, Double confidence, String speaker, List<Word> words) {
        this.text = text;
        this.start = start;
        this.end = end;
        this.confidence = confidence;
        this.speaker = speaker;
        this.words = words != null ? words : new ArrayList<>();
    }

    /**
     * Creates Utterance from JSON.
     *
     * @param json JSON object
     * @return Utterance instance
     * @throws JSONException if parsing fails
     */
    public static Utterance fromJson(JSONObject json) throws JSONException {
        Utterance utterance = new Utterance();
        if (json.has(AssemblyAIConstants.UTTERANCE_TEXT)) {
            utterance.setText(json.getString(AssemblyAIConstants.UTTERANCE_TEXT));
        }
        if (json.has(AssemblyAIConstants.UTTERANCE_START)) {
            utterance.setStart(json.getInt(AssemblyAIConstants.UTTERANCE_START));
        }
        if (json.has(AssemblyAIConstants.UTTERANCE_END)) {
            utterance.setEnd(json.getInt(AssemblyAIConstants.UTTERANCE_END));
        }
        if (json.has(AssemblyAIConstants.UTTERANCE_CONFIDENCE)) {
            utterance.setConfidence(json.getDouble(AssemblyAIConstants.UTTERANCE_CONFIDENCE));
        }
        if (json.has(AssemblyAIConstants.UTTERANCE_SPEAKER)) {
            utterance.setSpeaker(json.getString(AssemblyAIConstants.UTTERANCE_SPEAKER));
        }
        if (json.has(AssemblyAIConstants.WORDS)) {
            JSONArray wordsArray = json.getJSONArray(AssemblyAIConstants.WORDS);
            List<Word> wordsList = new ArrayList<>();
            for (int i = 0; i < wordsArray.length(); i++) {
                wordsList.add(Word.fromJson(wordsArray.getJSONObject(i)));
            }
            utterance.setWords(wordsList);
        }
        return utterance;
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

    public List<Word> getWords() {
        return words;
    }

    public void setWords(List<Word> words) {
        this.words = words;
    }

    @Override
    public String toString() {
        return "Utterance{" +
                "text='" + text + '\'' +
                ", start=" + start +
                ", end=" + end +
                ", confidence=" + confidence +
                ", speaker='" + speaker + '\'' +
                ", words=" + words +
                '}';
    }
}

