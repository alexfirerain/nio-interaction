import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Client {
    public static final String HOSTNAME = "127.0.0.1";
    public static final int PORT = 4160;


    public static void main(String[] args) throws IOException {
        final SocketChannel socketChannel = SocketChannel.open();

        try (socketChannel; Scanner scanner = new Scanner(System.in))
        {
            socketChannel.connect(new InetSocketAddress(HOSTNAME, PORT));
            final ByteBuffer inputBuffer = ByteBuffer.allocate(2 << 10);
            System.out.println(new String(inputBuffer.array(), 0,
                    socketChannel.read(inputBuffer), StandardCharsets.UTF_8).trim());
            inputBuffer.clear();
            String input;

            while (true) {
                input = scanner.nextLine();
                if ("end".equals(input)) break;

                socketChannel.write(ByteBuffer.wrap(input.getBytes(StandardCharsets.UTF_8)));

//                Thread.sleep( 2000 );

                int bytesCount = socketChannel.read(inputBuffer);

                System.out.println(new String(inputBuffer.array(),
                        0,
                        bytesCount,
                        StandardCharsets.UTF_8).trim());

                inputBuffer.clear();
            }
        }
//        catch (InterruptedException e) {
//            e.printStackTrace();
//        }


    }

}
