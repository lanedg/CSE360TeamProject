package inputValidation;

public class ValidateInputLength {
	
	public static String checkInputLength(String input) {
		if (input.length() > 32) {
			return "One or more inputs are too large. Each input must be less than 32 characters.";
		} else {
			return "";
		}
	}
}
