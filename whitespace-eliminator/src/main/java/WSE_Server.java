import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Сервер-Пробелоуничтожитель.
 * При запуске слушает подключения с адреса "GUSCI_Config.HOSTNAME, GUSCI_Config.PORT",
 * принимает строки от клиента и, по строке '=', отдаёт их обратно, но без пробелов.
 * Завершение работы с клиентом осуществляется строкой 'end' от клиента.
 * Остановка работы осуществляется строкой 'terminate' от клиента.
 */
public class WSE_Server {

    public static void main(String[] args) throws IOException {
        final ServerSocketChannel serverChannel = ServerSocketChannel.open();
        serverChannel.bind(new InetSocketAddress(GUSCI_Config.HOSTNAME, GUSCI_Config.WSE_PORT));
        StringBuilder processedData = new StringBuilder();
        consoler("Сервер запускается");

        server:
        while (true) {
            try (SocketChannel socketChannel = serverChannel.accept()) {
                consoler("Связались с " + socketChannel.getRemoteAddress());
                final ByteBuffer inputBuffer = ByteBuffer.allocate(2 << 10);
                socketChannel.write(transmit("""
                                
                                Здравствуйте, вас приветствует сервис WhiteSpace Eliminator!
                                Вводите и отправляйте свой текст.
                                Когда нужно получить обработанный результат, отправьте '=',
                                \tчтобы закончить работу, отправьте 'end'."""
                        ));
                consoler("Отправлено приветствие");
                String nextData = "";
                while (socketChannel.isConnected()) {
                    // читаем, что отправил клиент
                    int bytesCount = socketChannel.read(inputBuffer);
                    if (bytesCount == -1) break;
                    if (bytesCount > 0) {
                        nextData = new String(inputBuffer.array(), 0, bytesCount, StandardCharsets.UTF_8);
                    }
                    inputBuffer.clear();
                    consoler("Получено от клиента: " + nextData);
                    boolean sessionClosing = "end".equals(nextData) || "terminate".equals(nextData);

                    // если пользователь запросил вывод или вообще выходит, отправляем результат
                    if ("=".equals(nextData) || sessionClosing) {
                        // получаем обработанный результат
                        String result = processedData.toString();
                        processedData = new StringBuilder();

                        // если результат пустой, говорим об этом
                        if (result.isBlank()) {
                            result = sessionClosing ?
                                    "<завершение сеанса>" :
                                    "<нет данных для вывода>";
                        } else if (sessionClosing) {
                                result += "\n<завершение сеанса>";
                        }
                        // отправляем обработанный результат
                        socketChannel.write(transmit(result));
                        consoler("Отправлено клиенту: " + result);
                        if ("end".equals(nextData)) {
                            consoler("Завершение сеанса с клиентом");
                        }
                        if ("terminate".equals(nextData)) {
                            consoler("От клиента получена команда на остановку");
                            break server;
                        }
                    } else {
                        // иначе отправляем результат на обработку
                        processedData.append(processString(nextData));
                    }
                }
            } catch (IOException err) {
                consoler(err.getMessage());
            }
        }
        consoler("Сервер останавливается");
    }

    /**
     * Возвращает байт-буфер для передачи через канал.
     * Содержание вычисляется на основе переданной строки в указанной кодировке.
     * @param msg передаваемое сообщение.
     * @return соответствующий переданному сообщению буфер.
     */
    private static ByteBuffer transmit(String msg) {
        return ByteBuffer.wrap(msg.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Возвращает переданную строку, удалив из неё все пробелы.
     * @param data переданная строка.
     * @return обработанную строку.
     */
    private static String processString(String data) {
        String[] words = data.split(" ");
        StringBuilder builder = new StringBuilder();
        for (String s : words) {
            if (s.isBlank()) continue;
            builder.append(s);
        }
        return builder.toString();
    }

    /**
     * Консоль-логер: выводит в консоль текущее время (минуты:секунды) и событие.
     * @param event отображаемое событие.
     */
    private static void consoler(String event) {
        System.out.println(new SimpleDateFormat("mm:ss - ").format(new Date()) + event);
    }

}
