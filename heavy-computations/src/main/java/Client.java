import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Client {

    public static void main(String[] args) throws IOException {
        // Определяем сокет сервера
        final SocketChannel socketChannel = SocketChannel.open();
        InetSocketAddress socketAddress = new InetSocketAddress("127.0.0.1", 23334);
// подключаемся к серверу
        socketChannel.connect(socketAddress);

        try (Scanner scanner = new Scanner(System.in)) {
// Определяем буфер для получения данных
            final ByteBuffer inputBuffer = ByteBuffer.allocate(2 << 10);
            System.out.println(new String(inputBuffer.array(), 0, socketChannel.read(inputBuffer),
                    StandardCharsets.UTF_8).trim());
            String msg;
            while (true) {
                System.out.println("Enter message for server...");
                msg = scanner.nextLine();
                if ("end".equals(msg)) break;
                socketChannel.write(
                        ByteBuffer.wrap(
                                msg.getBytes(StandardCharsets.UTF_8)));
                Thread.sleep(2000);
                int bytesCount = socketChannel.read(inputBuffer);
                System.out.println(new String(inputBuffer.array(), 0, bytesCount,
                        StandardCharsets.UTF_8).trim());
                inputBuffer.clear();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            socketChannel.close();
        }


    }

}
