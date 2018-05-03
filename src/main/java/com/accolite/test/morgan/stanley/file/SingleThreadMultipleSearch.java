/*
 * 
 */
package com.accolite.test.morgan.stanley.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * The Class SingleThreadMultipleSearch.
 * There can be several lines with maximum number of words, it searches all those lines.
 */
@SuppressWarnings("unused")
public class SingleThreadMultipleSearch {
private static final String  WORD_SEPARATOR = "\\s";
	/** The file path. */
	private String filePath;

	/**
	 * Instantiates a new single thread multiple search.
	 *
	 * @param filePath
	 *            the file path
	 */
	public SingleThreadMultipleSearch(String filePath) {
		this.filePath = filePath;
	}

	/**
	 * Execute.
	 *
	 * @return the string
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public String execute() throws IOException {
		logInfo("Starting Processs: " + getClass().getSimpleName());

		String maxLine = null;
		Integer maxWordCount = -1;
		Integer maxLineIndex = -1;

		String wordSep = WORD_SEPARATOR;

		String line;
		int lineIndex = 0;
		int wordCount = 0;

		FileReader fileReader = null;
		BufferedReader bufferReader = null;
		
		/*
		 * ArrayList<LineInfo> stores the line information of all lines with max words
		 * */
		List<LineInfo> infos = new ArrayList<LineInfo>();
		
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
					logInfo("Percentage Complete: " + percentComplete + "%");
				}

				lineIndex++;
				wordCount = line.split(wordSep).length;
				if (wordCount > maxWordCount) {
					infos.clear();
					maxWordCount = wordCount;
					maxLine = line;
					maxLineIndex = lineIndex;
					infos.add(new LineInfo(maxLine, maxWordCount, maxLineIndex));
				} else if (wordCount == maxWordCount) {
					infos.add(new LineInfo(line, wordCount, lineIndex));
				}
			}
			percentComplete = getPercentage(curSize, minSize);
			if ((percentComplete % 5 == 0) && !percentCompleted.contains(percentComplete)) {
				percentCompleted.add(percentComplete);
				logInfo("Percentage Complete: " + percentComplete + "%");
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
		return resultToString(infos, lineIndex);
	}

	/**
	 * Result to string.
	 *
	 * @param infos
	 *            the infos
	 * @param totalLineCount
	 *            the total line count
	 * @return the string
	 */
	private String resultToString(List<LineInfo> infos, int totalLineCount) {

		StringBuilder builder = new StringBuilder();

		builder.append("Maximum number of words found:");
		builder.append("\n");

		builder.append("Occurrence: ");
		builder.append(infos.size());
		builder.append("\n");

		for (LineInfo info : infos) {
			builder.append(resultToString(info.getContent(), info.getLineIndex(), info.getWordCount()));
		}

		builder.append("Out of a total line count of: ");
		builder.append(totalLineCount);
		builder.append("\n");

		return builder.toString();
	}

	/**
	 * Result to string.
	 *
	 * @param info
	 *            the info
	 * @return the string
	 */
	private String resultToString(LineInfo info) {
		return resultToString(info.getContent(), info.getLineIndex(), info.getWordCount());
	}

	/**
	 * Result to string.
	 *
	 * @param maxLine
	 *            the max line
	 * @param maxLineIndex
	 *            the max line index
	 * @param maxWordCount
	 *            the max word count
	 * @return the string
	 */
	private String resultToString(String maxLine, int maxLineIndex, int maxWordCount) {
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

		return builder.toString();
	}

	/**
	 * Gets the percentage.
	 *
	 * @param current
	 *            the current
	 * @param total
	 *            the total
	 * @return the percentage
	 */
	private int getPercentage(long current, long total) {
		return Util.getPercentage(current, total);
	}

	/**
	 * Log info.
	 *
	 * @param message
	 *            the message
	 */
	private void logInfo(String message) {
		Util.logInfo(message);
	}

	/**
	 * Log warning.
	 *
	 * @param message
	 *            the message
	 */
	private void logWarning(String message) {
		Util.logWarning(message);
	}

	/**
	 * Log error.
	 *
	 * @param message
	 *            the message
	 */
	private void logError(String message) {
		Util.logError(message);
	}

	/**
	 * Log error.
	 *
	 * @param throwable
	 *            the throwable
	 */
	private void logError(Throwable throwable) {
		Util.logError(throwable);
	}

	/**
	 * The main method.
	 *
	 * @param args
	 *            the arguments
	 * @throws Exception
	 *             the exception
	 */
	public static void main(String args[]) throws Exception {
		System.out.println(new SingleThreadMultipleSearch(
				new File(new File(System.getProperty("user.home")), "TestContent.txt").getAbsolutePath()).execute());
	}
}