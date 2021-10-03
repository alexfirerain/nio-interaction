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
            socketChannel.connect(new InetSocketAddress(WSE_Config.HOSTNAME, WSE_Config.PORT));
            System.out.println("Соединение с " + socketChannel.getRemoteAddress());

            //  создание входного буфера
            final ByteBuffer inputBuffer = ByteBuffer.allocate(2 << 10);
            // вывод на экран того, что получили во входной буфер
            System.out.println(new String(inputBuffer.array(), 0,
                    socketChannel.read(inputBuffer), StandardCharsets.UTF_8).trim());
            inputBuffer.clear();

            // создание строки ввода
            String input;
            do {
                input = scanner.nextLine();

                // отправляем в канал то, что набрали в консоли
                socketChannel.write(ByteBuffer.wrap(input.getBytes(StandardCharsets.UTF_8)));


                if ("=".equals(input) || "terminate".equals(input) || "end".equals(input)) {
                    // определяем, сколько байт читается с буфера
                    int bytesCount = socketChannel.read(inputBuffer);
                    inputBuffer.clear();
                    System.out.println("Ответ сервера:");
                    // выводим в консоль прочитанные байты как строку
                    String response = new String(inputBuffer.array(),
                            0,
                            bytesCount,
                            StandardCharsets.UTF_8);

                    if (!response.isBlank()){
                        System.out.println(response);
                    }
                }

            } while (!"end".equals(input) && !"terminate".equals(input));
            socketChannel.finishConnect();
        }
        System.out.println("Завершение сеанса");
    }
}
