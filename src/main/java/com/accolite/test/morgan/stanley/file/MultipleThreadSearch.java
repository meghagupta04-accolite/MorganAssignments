/*
 * 
 */
package com.accolite.test.morgan.stanley.file;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.HashSet;
import java.util.Set;

/**
 * The Class MultipleThreadSearch.
 */
@SuppressWarnings("unused")
public class MultipleThreadSearch {

	/** The file path. */
	private String filePath;

	/** The file encoding. */
	private String fileEncoding;

	/** The part count. */
	private int partCount;

	/** The part pointers and sizes. */
	private long[][] partPointersAndSizes;

	/** The percent complete. */
	private int[] percentComplete;

	/** The line counts. */
	private int[] lineCounts;

	/** The percent completed. */
	private Set<Integer> percentCompleted;

	/** The file channel. */
	private FileChannel fileChannel;

	/** The file input stream. */
	private FileInputStream fileInputStream;

	/**
	 * Instantiates a new multiple thread search.
	 *
	 * @param filePath
	 *            the file path
	 * @param parallelism
	 *            the parallelism
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public MultipleThreadSearch(String filePath, int parallelism) throws IOException {
		this.filePath = filePath;
		this.partCount = parallelism;
	}

	/**
	 * Execute.
	 *
	 * @return the string
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws InterruptedException
	 *             the interrupted exception
	 */
	public String execute() throws IOException, InterruptedException {
		logInfo("Starting Processs: " + getClass().getSimpleName());

		this.fileEncoding = Util.getFileEncoding(filePath);
		this.partPointersAndSizes = Util.getPartPointersAndSizes(filePath, partCount);

		this.percentComplete = new int[partPointersAndSizes.length];
		this.lineCounts = new int[partPointersAndSizes.length];

		this.percentCompleted = new HashSet<Integer>();

		this.fileInputStream = new FileInputStream(filePath);
		this.fileChannel = fileInputStream.getChannel();

		FilePartSearch[] filePartSearches = new FilePartSearch[partPointersAndSizes.length];
		
		Thread[] filePartSearchThread = new Thread[partPointersAndSizes.length];
		
		for (int i = 0; i < partPointersAndSizes.length; i++) {
			filePartSearches[i] = new FilePartSearch(i);
			filePartSearchThread[i] = new Thread(filePartSearches[i]);
			filePartSearchThread[i].start();
		}

		for (int i = 0; i < filePartSearchThread.length; i++) {
			filePartSearchThread[i].join();
		}

		String maxLine = null;
		int maxWordCount = -1;
		int maxLineIndex = -1;
		int absLineIndex = 0;
		for (int i = 0; i < filePartSearches.length; i++) {
			if (maxWordCount < filePartSearches[i].getLineInfo().getWordCount()) {
				maxLine = filePartSearches[i].getLineInfo().getContent();
				maxWordCount = filePartSearches[i].getLineInfo().getWordCount();
				maxLineIndex = absLineIndex + filePartSearches[i].getLineInfo().getLineIndex();
			}
			absLineIndex += lineCounts[i];
		}
		fileInputStream.close();
		return resultToString(maxLine, maxLineIndex, maxWordCount, absLineIndex);
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
	 * @param totalLineCount
	 *            the total line count
	 * @return the string
	 */
	private String resultToString(String maxLine, int maxLineIndex, int maxWordCount, int totalLineCount) {
		StringBuilder builder = new StringBuilder();

		builder.append("Maximum number of words found:");
		builder.append("\r\n"/* "\n" */);

		builder.append("Line: ");
		builder.append(maxLine);
		builder.append("\r\n"/* "\n" */);

		builder.append("@Line Index: ");
		builder.append(maxLineIndex);
		builder.append("\r\n"/* "\n" */);

		builder.append("With a word count of: ");
		builder.append(maxWordCount);
		builder.append("\r\n"/* "\n" */);

		builder.append("Out of a total line count of: ");
		builder.append(totalLineCount);
		builder.append("\r\n"/* "\n" */);

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
	 * The Class FilePartSearch.
	 */
	public class FilePartSearch implements Runnable {

		/** The sequence. */
		private int sequence;

		/** The line info. */
		private LineInfo lineInfo;

		/**
		 * Instantiates a new file part search.
		 *
		 * @param sequence
		 *            the sequence
		 */
		public FilePartSearch(int sequence) {
			this.sequence = sequence;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run() {
			try {
				execute();
			} catch (Exception e) {
				logError(e);
			}
		}

		/**
		 * Gets the line info.
		 *
		 * @return the line info
		 */
		public LineInfo getLineInfo() {
			return lineInfo;
		}

		/**
		 * Sets the line info.
		 *
		 * @param lineInfo
		 *            the new line info
		 */
		private void setLineInfo(LineInfo lineInfo) {
			this.lineInfo = lineInfo;
		}

		/**
		 * Execute.
		 *
		 * @throws Exception
		 *             the exception
		 */
		private void execute() throws Exception {
			logInfo("Starting Processs: " + getClass().getSimpleName() + " part: " + sequence);

			String maxLine = null;
			Integer maxWordCount = -1;
			Integer maxLineIndex = -1;

			String wordSep = "\\s";

			String line;
			int lineIndex = 0;
			int wordCount = 0;
			BufferedReader fileReader = getReader();
			try {
				long curSize = 0;
				while ((line = fileReader.readLine()) != null && curSize < partPointersAndSizes[sequence][1]) {
					curSize += (line.getBytes(fileEncoding).length);
					synchronized (percentCompleted) {
						percentComplete[sequence] = getPercentage(curSize, partPointersAndSizes[sequence][1]);
						int totalCompleted = 0;
						for (int i = 0; i < percentComplete.length; i++) {
							totalCompleted += percentComplete[i];
						}
						totalCompleted = totalCompleted / percentComplete.length;
						if ((totalCompleted % 5 == 0) && !percentCompleted.contains(totalCompleted)) {
							percentCompleted.add(totalCompleted);
							logInfo("Percentage Complete: " + totalCompleted + "% part: " + sequence + " @ "
									+ percentComplete[sequence] + "%");
						}
					}
					lineIndex++;
					wordCount = line.split(wordSep).length;
					if (wordCount > maxWordCount) {
						maxWordCount = wordCount;
						maxLine = line;
						maxLineIndex = lineIndex;
					}
				}
				synchronized (percentCompleted) {
					percentComplete[sequence] = getPercentage(curSize, partPointersAndSizes[sequence][1]);
					int totalCompleted = 0;
					for (int i = 0; i < percentComplete.length; i++) {
						totalCompleted += percentComplete[i];
					}
					totalCompleted = totalCompleted / percentComplete.length;
					if ((totalCompleted % 5 == 0) && !percentCompleted.contains(totalCompleted)) {
						percentCompleted.add(totalCompleted);
						logInfo("Percentage Complete: " + totalCompleted + "% part: " + sequence + " @ "
								+ percentComplete[sequence] + "%");
					}
				}
			} finally {
				try {
					if (fileReader != null) {
						fileReader.close();
					}
				} catch (Exception e) {
					logError(e);
				}
				lineCounts[sequence] = lineIndex;
				setLineInfo(new LineInfo(maxLine, maxWordCount, maxLineIndex));
				logInfo("Finishing Processs: " + getClass().getSimpleName() + " part: " + sequence);
			}
		}

		/**
		 * Gets the reader.
		 *
		 * @return the reader
		 * @throws Exception
		 *             the exception
		 */
		public BufferedReader getReader() throws Exception {
			logInfo("Reading the channel: " + partPointersAndSizes[sequence][0] + ":"
					+ (partPointersAndSizes[sequence][1] - 1));
			ByteBuffer buff = ByteBuffer.allocate((int) partPointersAndSizes[sequence][1]);
			fileChannel.read(buff, partPointersAndSizes[sequence][0]);
			BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(buff.array())));
			logInfo("Done Reading the channel: " + partPointersAndSizes[sequence][0] + ":"
					+ (partPointersAndSizes[sequence][1] - 1));
			return reader;
		}

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
		System.out.println(new MultipleThreadSearch(
				new File(new File(System.getProperty("user.home")), "TestContent.txt").getAbsolutePath(), 4).execute());
	}
}