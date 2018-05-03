package com.test.morgan;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@SuppressWarnings("unused")
public class MultipleThreadMultipleSearch {

	private String filePath;
	private String fileEncoding;

	private int partCount;
	private long[][] partPointersAndSizes;

	private int[] percentComplete;
	private int[] lineCounts;
	private Set<Integer> percentCompleted;

	private FileChannel fileChannel;
	private FileInputStream fileInputStream;

	public MultipleThreadMultipleSearch(String filePath, int parallelism) throws IOException {
		this.filePath = filePath;
		this.partCount = parallelism;
	}

	public String execute() throws IOException, InterruptedException {
		logInfo("Starting Processs: " + getClass().getSimpleName());
		FileReader fileReader = new FileReader("D:/TestContent.txt");
		fileEncoding = fileReader.getEncoding();
		fileReader.close();

		this.partPointersAndSizes = getPartPointersAndSizes(filePath, partCount);
		this.percentComplete = new int[partPointersAndSizes.length];
		this.lineCounts = new int[partPointersAndSizes.length];

		percentCompleted = new HashSet<Integer>();

		fileInputStream = new FileInputStream(filePath);
		fileChannel = fileInputStream.getChannel();

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

		List<LineInfo> maxLineInfos = null;
		int maxWordCount = -1;
		int absLineIndex = -1;
		for (int i = 0; i < filePartSearches.length; i++) {
			if (maxWordCount < filePartSearches[i].getLineInfos().get(0).getWordCount()) {
				maxLineInfos = filePartSearches[i].getLineInfos();
				maxWordCount = filePartSearches[i].getLineInfos().get(0).getWordCount();
			}
			absLineIndex += lineCounts[i];
		}
		fileInputStream.close();
		return resultToString(maxLineInfos, absLineIndex);
	}

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

	private String resultToString(LineInfo info) {
		return resultToString(info.getContent(), info.getLineIndex(), info.getWordCount());
	}

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

	private int getPercentage(long current, long total) {
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

	private long[][] getPartPointersAndSizes(String filePath, int parts) throws IOException {
		// byteChannelRead();
		if (parts == 1) {
			return new long[][] { { 0l, new File(filePath).length(), } };
		}
		RandomAccessFile file = new RandomAccessFile(filePath, "r");
		long[][] partPointersAndSizes = new long[parts][2];
		int partSize = (int) (file.length() / parts);
		int remaining = (int) (file.length() % partSize);
		for (int i = 0; i < partPointersAndSizes.length; i++) {
			partPointersAndSizes[i][0] = partSize * i;
			partPointersAndSizes[i][1] = partSize;
		}

		for (int i = 1; i < partPointersAndSizes.length; i++) {
			file.seek(partPointersAndSizes[i][0]);
			int adjustment = file.readLine().getBytes(fileEncoding).length;
			partPointersAndSizes[i - 1][1] += adjustment;
			partPointersAndSizes[i][1] -= adjustment;
			partPointersAndSizes[i][0] = partPointersAndSizes[i - 1][0] + partPointersAndSizes[i - 1][1];
		}
		partPointersAndSizes[partPointersAndSizes.length - 1][1] += remaining;
		return partPointersAndSizes;
	}

	public class FilePartSearch implements Runnable {

		private int sequence;
		private List<LineInfo> lineInfos;

		public FilePartSearch(int sequence) {
			this.sequence = sequence;
			this.lineInfos = new ArrayList<LineInfo>();
		}

		@Override
		public void run() {
			try {
				execute();
			} catch (Exception e) {
				logError(e);
			}
		}

		public List<LineInfo> getLineInfos() {
			return lineInfos;
		}

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
							logInfo("Percentage Complete: " + totalCompleted + " part: " + sequence);
						}
					}
					lineIndex++;
					wordCount = line.split(wordSep).length;
					if (wordCount > maxWordCount) {
						lineInfos.clear();
						maxWordCount = wordCount;
						maxLine = line;
						maxLineIndex = lineIndex;
						lineInfos.add(new LineInfo(maxLine, maxWordCount, maxLineIndex));
					} else if (wordCount == maxWordCount) {
						lineInfos.add(new LineInfo(line, wordCount, lineIndex));
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
						logInfo("Percentage Complete: " + totalCompleted + " part: " + sequence);
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
				logInfo("Finishing Processs: " + getClass().getSimpleName() + " part: " + sequence);
			}
		}

		public BufferedReader getReader() throws Exception {
			logInfo("Reading the channel: " + partPointersAndSizes[sequence][0] + ":"
					+ partPointersAndSizes[sequence][1]);
			ByteBuffer buff = ByteBuffer.allocate((int) partPointersAndSizes[sequence][1]);
			fileChannel.read(buff, partPointersAndSizes[sequence][0]);
			BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(buff.array())));
			logInfo("Done Reading the channel: " + partPointersAndSizes[sequence][0] + ":"
					+ partPointersAndSizes[sequence][1]);
			return reader;
		}

	}

	public static void main(String args[]) throws Exception {
		System.out.println(new MultipleThreadMultipleSearch("D:/TestContent.txt", 4).execute());
	}
}