/*
 * 
 */
package com.accolite.test.morgan.stanley.file;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.io.StringWriter;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * The Class Util.
 */
public class Util {

	/**
	 * Generate random words.
	 *
	 * @param numberOfWords
	 *            the number of words
	 * @return the string[]
	 */
	public static String[] generateRandomWords(int numberOfWords) {
		Random random = new Random();
		String[] randomStrings = new String[numberOfWords];
		for (int i = 0; i < numberOfWords; i++) {
			char[] word = new char[random.nextInt(8) + 3];
			for (int j = 0; j < word.length; j++) {
				word[j] = (char) ('a' + random.nextInt(26));
			}
			randomStrings[i] = new String(word);
		}
		return randomStrings;
	}

	/**
	 * Generate random line.
	 *
	 * @return the string
	 */
	public static String generateRandomLine() {
		StringBuilder builder = new StringBuilder();
		String[] words = generateRandomWords(new Random().nextInt(500));
		for (String word : words) {
			builder.append(word);
			builder.append(" ");
		}
		return builder.toString();
	}

	/**
	 * Generate content file.
	 *
	 * @param filePath
	 *            the file path
	 * @param minSize
	 *            the min size
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static void generateContentFile(String filePath, long minSize) throws IOException {
		long curSize = 0;
		File contentFile = new File(filePath);
		if (contentFile.exists()) {
			contentFile.delete();
		}
		int lineCount = 0;
		FileWriter fileWriter = null;
		BufferedWriter bufferWriter = null;
		String lineSep = System.getProperty("line.separator");
		int lineSepCnt = lineSep.getBytes(getFileEncoding(null)).length;
		try {
			fileWriter = new FileWriter(contentFile);
			String encoding = fileWriter.getEncoding();
			bufferWriter = new BufferedWriter(fileWriter);
			String line = null;
			int percentComplete = 0;
			Set<Integer> percentCompleted = new HashSet<>();

			while (curSize < minSize) {
				percentComplete = getPercentage(curSize, minSize);
				if ((percentComplete % 5 == 0) && !percentCompleted.contains(percentComplete)) {
					percentCompleted.add(percentComplete);
					logInfo("Percentage Complete: " + percentComplete + "%");
				}
				line = generateRandomLine();
				lineCount++;
				curSize += (line.getBytes(encoding).length + lineSepCnt);
				bufferWriter.write(line);
				bufferWriter.write(lineSep);
			}
			percentComplete = getPercentage(curSize, minSize);
			if ((percentComplete % 5 == 0) && !percentCompleted.contains(percentComplete)) {
				percentCompleted.add(percentComplete);
				logInfo("Percentage Complete: " + percentComplete + "%");
			}
		} finally {
			try {
				if (bufferWriter != null) {
					bufferWriter.flush();
					bufferWriter.close();
				}
			} catch (Exception e) {
				logError(e);
			}
			logInfo("Lines generated: " + lineCount);
		}
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
	public static int getPercentage(long current, long total) {
		return (int) (((double) current / total) * (100));
	}

	/**
	 * Gets the size in giga bytes.
	 *
	 * @param size
	 *            the size
	 * @return the size in giga bytes
	 */
	public static int getSizeInGigaBytes(long size) {
		return (int) (size / (1024 * 1024 * 1024));
	}

	/**
	 * Gets the size in meg bytes.
	 *
	 * @param size
	 *            the size
	 * @return the size in meg bytes
	 */
	public static int getSizeInMegBytes(long size) {
		return (int) (size / (1024 * 1024));
	}

	/**
	 * Gets the size in kilo bytes.
	 *
	 * @param size
	 *            the size
	 * @return the size in kilo bytes
	 */
	public static int getSizeInKiloBytes(long size) {
		return (int) (size / (1024));
	}

	/**
	 * Log info.
	 *
	 * @param message
	 *            the message
	 */
	public static void logInfo(String message) {
		System.out.print("INFO: ");
		System.out.println(message);
	}

	/**
	 * Log warning.
	 *
	 * @param message
	 *            the message
	 */
	public static void logWarning(String message) {
		System.out.print("WARN: ");
		System.out.println(message);
	}

	/**
	 * Log error.
	 *
	 * @param message
	 *            the message
	 */
	public static void logError(String message) {
		System.err.println("ERROR: ");
		System.err.println(message);
	}

	/**
	 * Log error.
	 *
	 * @param throwable
	 *            the throwable
	 */
	public static void logError(Throwable throwable) {
		logError(exceptionToString(throwable));
	}

	/**
	 * Exception to string.
	 *
	 * @param throwable
	 *            the throwable
	 * @return the string
	 */
	private static String exceptionToString(Throwable throwable) {
		StringWriter stringWriter = new StringWriter();
		PrintWriter printWriter = new PrintWriter(stringWriter);
		printWriter.println(throwable.getClass().getName());
		printWriter.println("StackTrace :");
		throwable.printStackTrace(printWriter);
		Throwable cause = getRootCause(throwable);
		if (!cause.equals(throwable)) {
			printWriter.println();
			printWriter.println("Root Cause: ");
			cause.printStackTrace(printWriter);
		}
		return stringWriter.toString();
	}

	/**
	 * Gets the root cause.
	 *
	 * @param throwable
	 *            the throwable
	 * @return the root cause
	 */
	private static Throwable getRootCause(Throwable throwable) {
		Throwable cause = throwable;
		while (cause.getCause() != null) {
			cause = cause.getCause();
			if (cause == throwable) {
				break;
			}
		}
		return cause;
	}

	/**
	 * Gets the file encoding.
	 *
	 * @param filePath
	 *            the file path
	 * @return the file encoding
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static String getFileEncoding(String filePath) throws IOException {
		String fileEncoding = "UTF-8";
		if (filePath == null || !new File(filePath).exists()) {
			return fileEncoding;
		}
		FileReader fileReader = null;
		try {
			fileReader = new FileReader(filePath);
			fileEncoding = fileReader.getEncoding();
		} finally {
			if (fileReader != null) {
				fileReader.close();
			}
		}
		return fileEncoding;
	}

	/**
	 * Gets the part pointers and sizes.
	 *
	 * @param filePath
	 *            the file path
	 * @param parts
	 *            the parts
	 * @return the part pointers and sizes
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static long[][] getPartPointersAndSizes(String filePath, int parts) throws IOException {
		// byteChannelRead();
		long[][] partPointersAndSizes = new long[parts][2];
		if (parts == 1) {
			return new long[][] { { 0l, new File(filePath).length(), } };
		}
		RandomAccessFile file = null;
		try {
			file = new RandomAccessFile(filePath, "r");
			int partSize = (int) ((file.length() - 1) / parts);
			for (int i = 0; i < partPointersAndSizes.length; i++) {
				partPointersAndSizes[i][0] = partSize * i;
				partPointersAndSizes[i][1] = partSize;
			}
			for (int i = 1; i < partPointersAndSizes.length; i++) {
				file.seek(partPointersAndSizes[i][0]);
				file.readLine();
				partPointersAndSizes[i - 1][1] = file.getFilePointer() - partPointersAndSizes[i - 1][0] /*- 1*/;
				partPointersAndSizes[i][0] = file.getFilePointer();
			}
			partPointersAndSizes[partPointersAndSizes.length - 1][1] = file.length()
					- partPointersAndSizes[partPointersAndSizes.length - 1][0];

			for (int i = 0; i < partPointersAndSizes.length; i++) {
				file.seek(partPointersAndSizes[i][0] + partPointersAndSizes[i][1] - 1);
				int charz = file.read();
				if (charz == '\n') {
					logInfo("NewLineTermination@partPointersAndSizes[i][0]+ partPointersAndSizes[i][1]: ("
							+ (partPointersAndSizes[i][0] + partPointersAndSizes[i][1]) + ") " + charz);
				} else {
					logWarning("@partPointersAndSizes[i][0]+ partPointersAndSizes[i][1]: ("
							+ (partPointersAndSizes[i][0] + partPointersAndSizes[i][1]) + ") " + (char) charz);
				}
			}
			int charz = file.read();
			if (charz == -1) {
				logInfo("EOF@ReadCorrectly ( " + -1 + " ) ");
			} else {
				logWarning("@ReadInCorrectly ( " + (char) charz + " ) ");
			}

			StringBuilder builder = new StringBuilder();
			builder.append("PartPointersAndSizes: ");
			for (int i = 0; i < partPointersAndSizes.length; i++) {
				if (i == 0) {
					builder.append("[ ");
				}
				builder.append("Part");
				builder.append(i);
				builder.append(": {");
				builder.append("Pointer: ");
				builder.append(partPointersAndSizes[i][0]);
				builder.append(" , Size: ");
				builder.append(partPointersAndSizes[i][1]);
				builder.append(" , Pointer+Size: ");
				builder.append(partPointersAndSizes[i][0] + partPointersAndSizes[i][1]);
				builder.append("} ");
				if (i == partPointersAndSizes.length - 1) {
					builder.append(" ]");
				} else {
					builder.append(" , ");
				}
			}
			logInfo(builder.toString());
		} finally {
			if (file != null) {
				file.close();
			}
		}
		return partPointersAndSizes;
	}

	/**
	 * Chunk file into parts.
	 *
	 * @param filePath
	 *            the file path
	 * @param parts
	 *            the parts
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static void chunkFileIntoParts(String filePath, int parts) throws IOException {
		InputStream inputStream = new BufferedInputStream(new FileInputStream(filePath));
		long[][] partPointersAndSizes = getPartPointersAndSizes(filePath, parts);
		byte[] buffer = new byte[1024 * 10];
		long read = 0;
		long total = new File(filePath).length();
		int totalCompleted = 0;
		Set<Integer> percentCompleted = new HashSet<Integer>();
		try {
			for (int i = 0; i < partPointersAndSizes.length; i++) {
				OutputStream outputStream = null;
				int lread = 0;
				try {
					outputStream = new BufferedOutputStream(new FileOutputStream(filePath + i));
					while (read < partPointersAndSizes[i][0] + partPointersAndSizes[i][1]) {
						lread = inputStream.read(buffer);
						outputStream.write(buffer, 0, lread);
						read += lread;
						totalCompleted = getPercentage(read, total);
						if ((totalCompleted % 5 == 0) && !percentCompleted.contains(totalCompleted)) {
							percentCompleted.add(totalCompleted);
							logInfo("Percentage Complete: " + totalCompleted + "%");
						}
					}
				} finally {
					if (outputStream != null) {
						outputStream.flush();
						outputStream.close();
					}
				}
			}
		} finally {
			if (inputStream != null) {
				inputStream.close();
			}
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
	public static void main(String[] args) throws Exception {
		try {
			generateContentFile(
					new File(new File(System.getProperty("user.home")), "TestContent.txt").getAbsolutePath(),
					((1024 * 1L) * 1024 * 1024) + ((1024 * 0L) * 1024) + ((1024 * 0L)));
			chunkFileIntoParts(new File(new File(System.getProperty("user.home")), "TestContent.txt").getAbsolutePath(),
					4);
		} catch (Exception e) {
			logError(e);
			throw e;
		} finally {
		}
	}
}
