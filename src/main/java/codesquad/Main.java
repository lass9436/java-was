package codesquad;

import java.io.IOException;

import codesquad.server.WebServer;

public class Main {

	public static void main(String[] args) throws IOException {
		WebServer webServer = new WebServer(8080, 5, 10);
		webServer.start();
	}
}
