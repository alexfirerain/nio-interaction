import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class WSE_Client {

    public static void main(String[] args) throws IOException {
        final SocketChannel socketChannel = SocketChannel.open();

        try (socketChannel; Scanner scanner = new Scanner(System.in)) {
            socketChannel.connect(new InetSocketAddress(GUSCI_Config.HOSTNAME, GUSCI_Config.WSE_PORT));
            System.out.println("Соединение с " + socketChannel.getRemoteAddress());
            final ByteBuffer inputBuffer = ByteBuffer.allocate(2 << 10);
            System.out.println(new String(inputBuffer.array(), 0,
                    socketChannel.read(inputBuffer), StandardCharsets.UTF_8).trim());
            inputBuffer.clear();

            String input;
            boolean exit;
            do {
                input = scanner.nextLine();
                socketChannel.write(ByteBuffer.wrap(input.getBytes(StandardCharsets.UTF_8)));
                exit = "end".equals(input) || "terminate".equals(input);
                if ("=".equals(input) || exit) {
                    int bytesCount = socketChannel.read(inputBuffer);
                    inputBuffer.clear();
                    String response = new String(inputBuffer.array(),0, bytesCount, StandardCharsets.UTF_8);
                    System.out.println("Ответ сервера:\n" + response);
                }
            } while (!exit);
            socketChannel.finishConnect();
        }
        System.out.println("\nЗавершение работы");
    }
}
