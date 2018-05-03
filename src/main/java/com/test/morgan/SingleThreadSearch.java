package com.test.morgan;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("unused")
public class SingleThreadSearch {

	private String filePath;

	public SingleThreadSearch(String filePath) {
		this.filePath = filePath;
	}

	public String execute() throws IOException {
		logInfo("Starting Processs: " + getClass().getSimpleName());

		String maxLine = null;
		Integer maxWordCount = -1;
		Integer maxLineIndex = -1;

		String wordSep = "\\s";

		String line;
		int lineIndex = 0;
		int wordCount = 0;

		FileReader fileReader = null;
		BufferedReader bufferReader = null;
		try {
			fileReader = new FileReader(filePath);
			String encoding = fileReader.getEncoding();
			bufferReader = new BufferedReader(fileReader);
			int percentComplete = 0;
			Set<Integer> percentCompleted = new HashSet<>();
			long curSize = 0;
			long minSize = new File(filePath).length();
			while ((line = bufferReader.readLine()) != null) {
				curSize += (line.getBytes(encoding).length);
				percentComplete = getPercentage(curSize, minSize);
				if ((percentComplete % 5 == 0) && !percentCompleted.contains(percentComplete)) {
					percentCompleted.add(percentComplete);
					logInfo("Percentage Complete: " + percentComplete);
				}

				lineIndex++;
				wordCount = line.split(wordSep).length;
				if (wordCount > maxWordCount) {
					maxWordCount = wordCount;
					maxLine = line;
					maxLineIndex = lineIndex;
				}
			}
			percentComplete = getPercentage(curSize, minSize);
			if ((percentComplete % 5 == 0) && !percentCompleted.contains(percentComplete)) {
				percentCompleted.add(percentComplete);
				logInfo("Percentage Complete: " + percentComplete);
			}
		} finally {
			try {
				if (bufferReader != null) {
					bufferReader.close();
				}
				if (bufferReader != null) {
					bufferReader.close();
				}
			} catch (Exception e) {
				logError(e);
			}
			logInfo("Finishing Processs: " + getClass().getSimpleName());
		}
		return resultToString(maxLine, maxLineIndex, maxWordCount, lineIndex);
	}

	private String resultToString(String maxLine, int maxLineIndex, int maxWordCount, int totalLineCount) {
		StringBuilder builder = new StringBuilder();

		builder.append("Maximum number of words found:");
		builder.append("\n");

		builder.append("Line: ");
		builder.append(maxLine);
		builder.append("\n");

		builder.append("@Line Index: ");
		builder.append(maxLineIndex);
		builder.append("\n");

		builder.append("With a word count of: ");
		builder.append(maxWordCount);
		builder.append("\n");

		builder.append("Out of a total line count of: ");
		builder.append(totalLineCount);
		builder.append("\n");

		return builder.toString();
	}

	public static int getPercentage(long current, long total) {
		return Util.getPercentage(current, total);
	}

	private void logInfo(String message) {
		Util.logInfo(message);
	}

	private void logWarning(String message) {
		Util.logWarning(message);
	}

	private void logError(String message) {
		Util.logError(message);
	}

	private void logError(Throwable throwable) {
		Util.logError(throwable);
	}

	public static void main(String args[]) throws Exception {
		System.out.println(new SingleThreadSearch("D:/TestContent.txt").execute());
	}
}