package inputValidation;

public class ValidateInputLength {
	public String checkInputLength(String input) {
		if (input.length() > 32) {
			return "Input too large. Input must be less than 32 characters.";
		} else {
			return "";
		}
	}
}
