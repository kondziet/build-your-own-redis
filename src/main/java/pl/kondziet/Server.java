package pl.kondziet;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashSet;
import java.util.Set;

public class Server {

    private final Set<SocketChannel> clients = new HashSet<>();
    private final DataTypeParser dataTypeParser = new DataTypeParser();

    public void start(int port) {
        try (var server = ServerSocketChannel.open();
             var selector = Selector.open()) {
            server.configureBlocking(false);
            server.bind(new InetSocketAddress(port));
            server.register(selector, SelectionKey.OP_ACCEPT);

            var buffer = ByteBuffer.allocate(1024);
            while (true) {
                selector.select();
                for (var key : selector.selectedKeys()) {
                    if (key.isAcceptable()) {
                        if (key.channel() instanceof ServerSocketChannel channel) {
                            var client = channel.accept();
                            var socket = client.socket();
                            System.out.println("CONNECTED: " + socket.getInetAddress().getHostAddress() + " : " + socket.getPort());
                            client.configureBlocking(false);
                            client.register(selector, SelectionKey.OP_READ);
                            clients.add(client);
                        }
                    } else if (key.isReadable()) {
                        if (key.channel() instanceof SocketChannel channel) {
                            // handle rapidly closed connection, java.net.SocketException: Connection reset
                            var bytesRead = channel.read(buffer);
                            if (bytesRead == -1) {
                                var socket = channel.socket();
                                var clientInfo = socket.getInetAddress().getHostAddress() + ":" + socket.getPort();
                                System.out.println("DISCONNECTED: " + clientInfo);
                                channel.close();
                                clients.remove(channel);
                                continue;
                            }
                            buffer.flip();
                            var data = new String(buffer.array(), buffer.position(), bytesRead);
                            System.out.println(data);
                            Outcome<DataType> outcome = dataTypeParser.parse(data);
                            switch (outcome) {
                                case Outcome.Success<DataType> result -> System.out.println(result);
                                case Outcome.Failure<DataType> ignored ->
                                        System.out.println("Command incomplete... listening for subsequent write event");
                                case Outcome.Incomplete<DataType> ignored -> {
                                }
                            }
                            // check whether requested command can be handled right away, or need to wait for remaining part to come
                            // if so, create some sort of session, attach it during register of upcoming OP_READ event.
                            // if write couldn't been completed at once perform accordingly like above. Current solution
                            // may block event loop in edge cases.
                            while (buffer.hasRemaining()) {
                                channel.write(buffer);
                            }
                            buffer.clear();
                        }
                    }
                }
                selector.selectedKeys().clear();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            for (var client : clients) {
                try {
                    client.close();
                } catch (IOException e) {
                    System.err.println("Failed to close client: " + e.getMessage());
                } finally {
                    clients.remove(client);
                }
            }
        }
    }
}
