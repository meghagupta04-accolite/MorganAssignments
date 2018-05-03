package com.test.morgan;

public class LineInfo {

	private String content = null;
	private int wordCount = -1;
	private int lineIndex = -1;

	public LineInfo(String content, int wordCount, int lineIndex) {
		this.content = content;
		this.wordCount = wordCount;
		this.lineIndex = lineIndex;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public int getWordCount() {
		return wordCount;
	}

	public void setWordCount(int wordCount) {
		this.wordCount = wordCount;
	}

	public int getLineIndex() {
		return lineIndex;
	}

	public void setLineIndex(int lineIndex) {
		this.lineIndex = lineIndex;
	}

	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((content == null) ? 0 : content.hashCode());
		result = prime * result + lineIndex;
		result = prime * result + wordCount;
		return result;
	}

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

	public String toString() {
		return "LineInfo [content=" + content + ", wordCount=" + wordCount + ", lineIndex=" + lineIndex + "]";
	}

}
