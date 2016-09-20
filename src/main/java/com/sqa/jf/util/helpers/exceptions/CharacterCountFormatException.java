package com.sqa.jf.util.helpers.exceptions;

public class CharacterCountFormatException extends Exception {
	@Override
	public Throwable getCause() {
		return new Throwable("Too many letters present in passed parameter String");
	}

	@Override
	public String getMessage() {
		return "Character count exception";
	}
}
