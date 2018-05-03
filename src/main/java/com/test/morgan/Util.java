package com.test.morgan;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class Util {

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

	public static String generateRandomLine() {
		StringBuilder builder = new StringBuilder();
		String[] words = generateRandomWords(new Random().nextInt(2000));
		for (String word : words) {
			builder.append(word);
			builder.append(" ");
		}
		return builder.toString();
	}

	public static void generateContentFile(String filePath, long minSize) throws IOException {
		long curSize = 0;
		File contentFile = new File(filePath);
		if (contentFile.exists()) {
			contentFile.delete();
		}

		FileWriter fileWriter = null;
		BufferedWriter bufferWriter = null;
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
					logInfo("Percentage Complete: " + percentComplete);
				}
				line = generateRandomLine();
				curSize += (line.getBytes(encoding).length);
				bufferWriter.write(line);
				bufferWriter.write("\n");
			}
			percentComplete = getPercentage(curSize, minSize);
			if ((percentComplete % 5 == 0) && !percentCompleted.contains(percentComplete)) {
				percentCompleted.add(percentComplete);
				logInfo("Percentage Complete: " + percentComplete);
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
		}
	}

	public static int getPercentage(long current, long total) {
		return (int) (((double) current / total) * (100));
	}

	public static int getSizeInGigaBytes(long size) {
		return (int) (size / (1024 * 1024 * 1024));
	}

	public static int getSizeInMegBytes(long size) {
		return (int) (size / (1024 * 1024));
	}

	public static int getSizeInKiloBytes(long size) {
		return (int) (size / (1024));
	}

	public static void logInfo(String message) {
		System.out.print("INFO: ");
		System.out.println(message);
	}

	public static void logWarning(String message) {
		System.out.print("WARN: ");
		System.out.println(message);
	}

	public static void logError(String message) {
		System.err.println("ERROR: ");
		System.err.println(message);
	}

	public static void logError(Throwable throwable) {
		logError(exceptionToString(throwable));
	}

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

	public static void main(String[] args) throws Exception {
		try {
			generateContentFile("D:/TestContent.txt",
					((1024 * 1L) * 1024 * 1024) + ((1024 * 1L) * 1024) + ((1024 * 1L)));
		} catch (Exception e) {
			logError(e);
			throw e;
		} finally {
		}
	}
}
