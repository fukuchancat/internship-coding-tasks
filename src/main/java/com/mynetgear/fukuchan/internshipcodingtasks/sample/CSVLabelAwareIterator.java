package com.mynetgear.fukuchan.internshipcodingtasks.sample;

import au.com.bytecode.opencsv.CSVReader;
import lombok.NonNull;
import org.deeplearning4j.text.sentenceiterator.SentencePreProcessor;
import org.deeplearning4j.text.sentenceiterator.labelaware.LabelAwareSentenceIterator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class CSVLabelAwareIterator implements LabelAwareSentenceIterator {
	private String cachedLabelLine;
	private String currentLabel;
	private boolean finished;
	private CSVReader csvReader;

	public CSVLabelAwareIterator(@NonNull Path file) throws IOException {
		this.csvReader = new CSVReader(Files.newBufferedReader(file), ',', '"', '\\', 1, false);
		this.finished = false;
	}

	@Override
	public String currentLabel() {
		return currentLabel;
	}

	@Override
	public List<String> currentLabels() {
		List<String> a = new ArrayList<>();
		a.add(currentLabel);
		return a;
	}

	@Override
	public String nextSentence() {
		if (!this.hasNext()) {
			throw new NoSuchElementException("No more lines");
		} else {
			String currentLine = this.cachedLabelLine;
			this.cachedLabelLine = null;
			return currentLine;
		}
	}

	@Override
	public boolean hasNext() {
		if (this.cachedLabelLine != null) {
			return true;
		} else if (this.finished) {
			return false;
		} else {
			try {
				String[] ioe;
				do {
					ioe = csvReader.readNext();
					if (ioe == null) {
						this.finished = true;
						return false;
					}
				} while (!this.isValidLine(ioe));

				// doc_id
				this.currentLabel = ioe[0];

				// doc_text
				this.cachedLabelLine = ioe[1];

				return true;
			} catch (IOException e) {
				this.finish();
				throw new IllegalStateException(e);
			}
		}
	}

	private boolean isValidLine(String[] line) {
		return true;
	}

	@Override
	public void reset() {
	}

	public void finish() {
		try {
			if (this.csvReader != null) {
				this.csvReader.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Override
	public SentencePreProcessor getPreProcessor() {
		return null;
	}

	@Override
	public void setPreProcessor(SentencePreProcessor sentencePreProcessor) {
	}
}
