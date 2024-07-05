package codesquad;

import codesquad.server.WebServer;

public class Main {

	public static void main(String[] args) {
		WebServer webServer = new WebServer(8080, 5, 10);
		webServer.start();
	}
}
