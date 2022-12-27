import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class ChatClient {
    private static final String HOST = "127.0.0.1";// "150.230.140.227";
    private static final int PORT = 5555;

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
        // Create the client socket
        AsynchronousSocketChannel client = AsynchronousSocketChannel.open();

        // Connect to the server
        Future<Void> result = client.connect(new InetSocketAddress(HOST, PORT));
        result.get();

        System.out.println("Connected to " + HOST + ":" + PORT);
        // Read messages from the server
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        client.read(buffer, null, new CompletionHandler<Integer, Void>() {
            @Override
            public void completed(Integer result, Void attachment) {
                if (result == -1) {
                    return;
                }
                buffer.flip();
                System.out.println(new String(buffer.array(), 0, buffer.limit()));
                buffer.clear();
                client.read(buffer, null, this);
            }

            @Override
            public void failed(Throwable exc, Void attachment) {
                exc.printStackTrace();
            }
        });

        // Read messages from the user and send them to the server
        BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
        String line;

        while ((line = userInput.readLine()) != null) {
            client.write(ByteBuffer.wrap(line.getBytes()));
        }
    }
}
