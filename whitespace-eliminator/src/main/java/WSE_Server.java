import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

public class WSE_Server {

    public static void main(String[] args) throws IOException {
        final ServerSocketChannel serverChannel = ServerSocketChannel.open();
        serverChannel.bind(new InetSocketAddress(WSE_Config.HOSTNAME, WSE_Config.PORT));
//        StringBuilder inputData = new StringBuilder();
        StringBuilder processedData = new StringBuilder();
        System.out.println("Сервер запускается");

        server:
        while (true) {
            try (SocketChannel socketChannel = serverChannel.accept()) {
                System.out.println("Связались с " + socketChannel.getRemoteAddress());

                final ByteBuffer inputBuffer = ByteBuffer.allocate(2 << 10);

                socketChannel.write(transmit("""
                                Здравствуйте, вас приветствует сервис WhiteSpace Eliminator!
                                Вводите и отправляйте свой текст.
                                Когда нужно получить обработанный результат, отправьте '=',
                                \tчтобы закончить работу, отправьте 'end'."""
                        ));
                System.out.println("Отправлено приветствие");

                String nextData = "";
                while (socketChannel.isConnected()) {
                    // читаем, что отправил клиент
                    int bytesCount = socketChannel.read(inputBuffer);
                    if (bytesCount == -1) break;
                    if (bytesCount > 0) {
                        nextData = new String(inputBuffer.array(), 0, bytesCount, StandardCharsets.UTF_8);
                    }
                    inputBuffer.clear();
                    System.out.println("Получено от клиента: " + nextData);


                    // если пользователь запросил вывод или вообще выходит, отправляем результат
                    if ("=".equals(nextData) || "end".equals(nextData) || "terminate".equals(nextData)) {
                        // получаем обработанный результат
                        String result = processedData.toString();
                        processedData = new StringBuilder();
                        // если результат пустой, говорим об этом
                        if (result.isBlank() && "=".equals(nextData)) {
                            result = "<нет данных для вывода>";
                        }
                        // отправляем обработанный результат
                        socketChannel.write(transmit(result));
                        System.out.println("Отправлено клиенту: " + result);

                        if ("terminate".equals(nextData)) {
                            System.out.println("От клиента получена команда на остановку");
                            break server;
                        }
                    } else {
                        // иначе отправляем результат на обработку
                        processedData.append(processString(nextData));
                    }

                }

            } catch (IOException err) {
                System.out.println(err.getMessage());
            }
        }
        System.out.println("Сервер останавливается");
    }

    private static ByteBuffer transmit(String msg) {
        return ByteBuffer.wrap(msg.getBytes(StandardCharsets.UTF_8));
    }

    private static String processString(String data) {
        String[] words = data.split(" ");
        StringBuilder builder = new StringBuilder();
        for (String s : words) {
            if (s.isBlank()) continue;
            builder.append(s);
        }
        return builder.toString();
    }

}
