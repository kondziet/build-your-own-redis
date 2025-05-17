package pl.kondziet;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Scanner;

public class Client {

    public void start(int port, Scanner scanner) {
        String resp = "*2\r\n$3\r\nset\r\n$3\r\nget\r\n";

        try (var server = SocketChannel.open()) {
            server.connect(new InetSocketAddress(port));
            System.out.println("Connection established");
            var buffer = ByteBuffer.allocate(1024);

            buffer.clear().put(resp.getBytes()).flip();
            while (buffer.hasRemaining()) {
                server.write(buffer);
            }
            buffer.clear();
            var bytesRead = server.read(buffer);
            buffer.flip();
            var data = new String(buffer.array(), buffer.position(), bytesRead);
            System.out.println(data);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
