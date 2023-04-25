package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.CharBuffer;
import java.nio.channels.Channels;
import jnr.unixsocket.UnixSocketAddress;
import jnr.unixsocket.UnixSocketChannel;

public class Main {
    public static void main(String[] args) throws IOException {
        java.io.File path = new java.io.File("/var/run/docker.sock");

        String data = "GET /info HTTP/1.1\r\nHost: docker\r\n\r\n";
        UnixSocketAddress address = new UnixSocketAddress(path);
        UnixSocketChannel channel = UnixSocketChannel.open(address);

        System.out.println("connected to " + channel.getRemoteSocketAddress());
        PrintWriter w = new PrintWriter(Channels.newOutputStream(channel));
        w.print(data);
        w.flush();

        InputStreamReader r = new InputStreamReader(Channels.newInputStream(channel));
        BufferedReader bufferedReader = new BufferedReader(r);
        StringBuilder sb = new StringBuilder();
        String line;
        while (!(line = bufferedReader.readLine()).equals("")) {
            sb.append(line).append("\n");
        }
        System.out.println(sb);
        int chunkLength = Integer.parseInt(bufferedReader.readLine(), 16);
        CharBuffer cb = CharBuffer.allocate(chunkLength);
        bufferedReader.read(cb);
        cb.flip();
        System.out.println(cb);
    }
}
