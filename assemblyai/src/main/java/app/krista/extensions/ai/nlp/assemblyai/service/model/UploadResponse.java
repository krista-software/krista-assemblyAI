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
 * Response model for audio file upload.
 */
public class UploadResponse {

    private String uploadUrl;

    public UploadResponse() {
    }

    public UploadResponse(String uploadUrl) {
        this.uploadUrl = uploadUrl;
    }

    /**
     * Creates UploadResponse from JSON.
     *
     * @param json JSON object
     * @return UploadResponse instance
     * @throws JSONException if parsing fails
     */
    public static UploadResponse fromJson(JSONObject json) throws JSONException {
        UploadResponse response = new UploadResponse();
        if (json.has(AssemblyAIConstants.UPLOAD_URL)) {
            response.setUploadUrl(json.getString(AssemblyAIConstants.UPLOAD_URL));
        }
        return response;
    }

    public String getUploadUrl() {
        return uploadUrl;
    }

    public void setUploadUrl(String uploadUrl) {
        this.uploadUrl = uploadUrl;
    }

    @Override
    public String toString() {
        return "UploadResponse{" +
                "uploadUrl='" + uploadUrl + '\'' +
                '}';
    }
}

