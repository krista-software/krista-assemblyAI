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

package app.krista.extensions.ai.nlp.assemblyai.catalog.area;

import app.krista.extension.executor.*;
import app.krista.extension.impl.anno.CatalogRequest;
import app.krista.extension.impl.anno.*;
import app.krista.extensions.ai.nlp.assemblyai.catalog.query.TranscribeCallQuery;
import app.krista.model.base.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;

/**
 * Transcription Area - Main catalog area for AssemblyAI transcription operations.
 *
 * This class follows the Command/Query Separation pattern:
 * - Contains ONLY Krista-specific catalog request definitions
 * - Delegates to Query/Command handlers for business logic
 * - No direct service layer calls
 * - No AssemblyAI-specific logic
 *
 * Architecture:
 * TranscriptionArea (Krista) -> Query/Command (Orchestration) -> Service (AssemblyAI) -> Bridge (Transformation)
 */
@Domain(id = "catEntryDomain_8a8d7d63-5223-450f-95c2-649d2ff36cf5",
        name = "NLP",
        ecosystemId = "catEntryEcosystem_2141418b-7dd8-4c04-865e-8763ba9aadcf",
        ecosystemName = "AI",
        ecosystemVersion = "584d496c-af39-43a5-9aec-8122708f4c9b")
public class TranscriptionArea {

	private static final Logger LOG = LogManager.getLogger(TranscriptionArea.class.getName());

	private final TranscribeCallQuery transcribeCallQuery;

	/**
	 * Constructor for dependency injection.
	 *
	 * @param transcribeCallQuery Query handler for transcribe call operations
	 */
	@Inject
	public TranscriptionArea(TranscribeCallQuery transcribeCallQuery) {
		this.transcribeCallQuery = transcribeCallQuery;
		LOG.info("TranscriptionArea initialized with Command/Query pattern");
	}

	/**
	 * Transcribe Call - QUERY_SYSTEM catalog request.
	 *
	 * This catalog request follows the Command/Query pattern:
	 * - Defines input/output fields (Krista-specific)
	 * - Delegates to TranscribeCallQuery for orchestration
	 * - Contains NO business logic
	 * - Contains NO AssemblyAI-specific code
	 *
	 * @param audioFile The audio file to transcribe
	 * @param redactPII Whether to redact personally identifiable information
	 * @return ExtensionResponse with transcription results
	 */
	@CatalogRequest(
			id = "localDomainRequest_1f463996-4a6d-4c99-a60f-ead71b28f66e",
			name = "Transcribe Call",
			description = "Transcribes a call recording with speaker diarization, sentiment analysis, and topic detection",
			area = "Transcription",
			type = CatalogRequest.Type.QUERY_SYSTEM)
	@Field.Text(name = "Topic Detected", required = false, attributes = {@Attribute(name = "visualWidth", value = "S")}, options = {})
	@Field.Text(name = "Sentiment", required = false, attributes = {@Attribute(name = "visualWidth", value = "S")}, options = {})
	@Field(name = "Transcript Text", type = "Paragraph", required = false, attributes = {@Attribute(name = "visualWidth", value = "L")}, options = {})
	@Field.File(name = "Transcript File", multipleFileUpload = false, required = false, attributes = {@Attribute(name = "visualWidth", value = "S")}, options = {})
	@Field.Boolean(name = "Transcription Successful", required = false, attributes = {@Attribute(name = "visualWidth", value = "S")}, options = {})
	public ExtensionResponse transcribeCall(
			@Field.File(name = "Audio File", multipleFileUpload = false, required = true, attributes = {@Attribute(name = "visualWidth", value = "S")}, options = {}) File audioFile,
			@Field.Boolean(name = "Redact PII", required = false, attributes = {@Attribute(name = "visualWidth", value = "S")}, options = {}) Boolean redactPII) {

		LOG.info("Transcribe Call catalog request invoked - File: {}, Redact PII: {}",
				audioFile != null ? audioFile.getFileName() : "null", redactPII);

		// Delegate to query handler (Command/Query pattern)
		return transcribeCallQuery.execute(audioFile, redactPII);
	}
}