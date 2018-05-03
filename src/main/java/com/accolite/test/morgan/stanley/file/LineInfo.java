/*
 * 
 */
package com.accolite.test.morgan.stanley.file;

/**
 * The Class LineInfo.
 */
public class LineInfo {

	/** The content. */
	private String content = null;

	/** The word count. */
	private int wordCount = -1;

	/** The line index. */
	private int lineIndex = -1;

	/**
	 * Instantiates a new line info.
	 *
	 * @param content
	 *            the content
	 * @param wordCount
	 *            the word count
	 * @param lineIndex
	 *            the line index
	 */
	public LineInfo(String content, int wordCount, int lineIndex) {
		this.content = content;
		this.wordCount = wordCount;
		this.lineIndex = lineIndex;
	}

	/**
	 * Gets the content.
	 *
	 * @return the content
	 */
	public String getContent() {
		return content;
	}

	/**
	 * Sets the content.
	 *
	 * @param content
	 *            the new content
	 */
	public void setContent(String content) {
		this.content = content;
	}

	/**
	 * Gets the word count.
	 *
	 * @return the word count
	 */
	public int getWordCount() {
		return wordCount;
	}

	/**
	 * Sets the word count.
	 *
	 * @param wordCount
	 *            the new word count
	 */
	public void setWordCount(int wordCount) {
		this.wordCount = wordCount;
	}

	/**
	 * Gets the line index.
	 *
	 * @return the line index
	 */
	public int getLineIndex() {
		return lineIndex;
	}

	/**
	 * Sets the line index.
	 *
	 * @param lineIndex
	 *            the new line index
	 */
	public void setLineIndex(int lineIndex) {
		this.lineIndex = lineIndex;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((content == null) ? 0 : content.hashCode());
		result = prime * result + lineIndex;
		result = prime * result + wordCount;
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof LineInfo)) {
			return false;
		}
		LineInfo other = (LineInfo) obj;
		if (content == null) {
			if (other.content != null) {
				return false;
			}
		} else if (!content.equals(other.content)) {
			return false;
		}
		if (lineIndex != other.lineIndex) {
			return false;
		}
		if (wordCount != other.wordCount) {
			return false;
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "LineInfo [content=" + content + ", wordCount=" + wordCount + ", lineIndex=" + lineIndex + "]";
	}

}
