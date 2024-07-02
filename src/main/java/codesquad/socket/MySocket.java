package codesquad.socket;

import java.io.IOException;
import java.net.ServerSocket;

public class MySocket extends ServerSocket {

	public MySocket(int port) throws IOException {
		super(port);
	}
}
