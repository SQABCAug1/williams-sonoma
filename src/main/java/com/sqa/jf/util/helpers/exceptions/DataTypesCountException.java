package com.sqa.jf.util.helpers.exceptions;

public class DataTypesCountException extends DataTypesMismatchException {
	@Override
	public String getMessage() {
		return super.getMessage()
				+ "\nNumber of arguments on passed DataTypes does not match with Database column count";
	}
}
