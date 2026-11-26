package guiTools;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import com.google.gson.JsonObject;

/** Tests the three AI help actions. */
public class ContributorAITest {

	/** Checks the draft and editing instructions */
	@Test
	void write() {
		JsonObject body = ContributorAI.makeRequest("Write", "Test login", "We test the login yesterday.");
		assertEquals("Title: Test login\nExperience: We test the login yesterday.", body.get("prompt").getAsString());
		assertTrue(body.get("system").getAsString().contains("Lightly edit"));
	}

	/** Checks the questions requested for a title */
	@Test
	void brainstorm() {
		JsonObject body = ContributorAI.makeRequest("Brainstorm", "Broken build", "");
		assertTrue(body.get("prompt").getAsString().contains("Broken build"));
		assertTrue(body.get("system").getAsString().contains("two short questions"));
	}

	/** Checks the search title and both examples */
	@Test
	void find() {
		JsonObject body = ContributorAI.makeRequest("Find an example", "Merge conflict", "Draft in progress");
		// Search only needs the title.
		assertEquals("Merge conflict", body.get("prompt").getAsString());
		assertTrue(ContributorAI.example("merge").startsWith("Resolve a merge conflict"));
		assertTrue(ContributorAI.example("title").startsWith("Catch an empty title"));
		assertEquals("No matching example.", ContributorAI.example("none"));
	}
}
