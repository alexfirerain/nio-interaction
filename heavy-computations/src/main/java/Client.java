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

            //  создание входного буфера
            final ByteBuffer inputBuffer = ByteBuffer.allocate(2 << 10);
            // вывод на экран того, что получили во входной буфер
            System.out.println(new String(inputBuffer.array(), 0,
                    socketChannel.read(inputBuffer), StandardCharsets.UTF_8).trim());
            inputBuffer.clear();

            // создание строки ввода
            String input;
            while (true) {
                input = scanner.nextLine();
                if ("end".equals(input)) break;

                // отправляем в канал то, что набрали в консоли
                socketChannel.write(ByteBuffer.wrap(input.getBytes(StandardCharsets.UTF_8)));

                if ("terminate".equals(input)) break;

                // определяем, сколько байт читается с буфера
                int bytesCount = socketChannel.read(inputBuffer);
                inputBuffer.clear();
                // выводим в консоль прочитанные байты как строку
                System.out.println(new String(inputBuffer.array(),
                        0,
                        bytesCount,
                        StandardCharsets.UTF_8));

            }
        }
//        catch (InterruptedException e) {
//            e.printStackTrace();
//        }


    }

}
