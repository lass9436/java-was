package codesquad.socket;

import java.io.IOException;
import java.net.ServerSocket;

public class MySocket extends ServerSocket {

	public MySocket(int port, int backlog) throws IOException {
		super(port, backlog);
	}
}
