package org.peerbox.presenter.validation;

import java.nio.file.Path;
import java.nio.file.Paths;

import javafx.beans.property.StringProperty;
import javafx.scene.control.TextField;

import org.peerbox.presenter.validation.ValidationUtils.ValidationResult;

public class RootPathValidator extends TextFieldValidator {

	public RootPathValidator(TextField txtField, StringProperty errorProperty) {
		super(txtField, errorProperty, false);
	}
	
	@Override
	public ValidationResult validate(final String value) {
		
		if(value == null) {
			return ValidationResult.ERROR;
		}
		
		Path path = Paths.get(value);
		ValidationResult res = SelectRootPathUtils.validateRootPath(path);
		if (res.isError()) {
			decorateError();
			setErrorMessage(res.getMessage());
		} else {
			undecorateError();
			clearErrorMessage();
		}
		return res;
	}

	public void reset() {
		undecorateError();
		clearErrorMessage();
	}

}
