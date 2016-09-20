package com.sqa.jf.util.helpers.exceptions;

public class DataTypesTypeException extends DataTypesMismatchException {
	@Override
	public String getMessage() {
		return super.getMessage() + "\nType of arguments on passed DataTypes does not match with types in Database";
	}
}
