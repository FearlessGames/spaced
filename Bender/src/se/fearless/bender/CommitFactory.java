package se.fearless.bender;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.simpleframework.http.Request;

import java.io.IOException;

public class CommitFactory {
	private CommitFactory() {
	}

	public static Commit create(Request request) throws IOException {
		if (request.contains("payload")) {
			ObjectMapper mapper = new ObjectMapper();
			JsonNode payload = mapper.readTree(request.getParameter("payload"));

			String revision = payload.get("after").getTextValue();

			String author = "";
			String commitMessage = "";

			for (JsonNode commit : payload.get("commits")) {
				author = commit.get("author").get("name").getTextValue();
				commitMessage += commit.get("message").getTextValue() + " ";
			}

			return new Commit("", author, revision, commitMessage);

		} else {
			String revision = request.getParameter("revision");
			String author = request.getParameter("author");
			String commitMessage = request.getParameter("message");
			String project = request.getParameter("project");

			return new Commit(project, author, revision, commitMessage);
		}
	}
}
