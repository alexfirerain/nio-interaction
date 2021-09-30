import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Date;

public class Server {
    public static final int CIT_LIMIT = 15;

    public static void main(String[] args) throws IOException {

        // Занимаем порт, определяя серверный сокет
        final ServerSocketChannel serverChannel = ServerSocketChannel.open();
        serverChannel.bind(new InetSocketAddress("localhost", 4160));
        while (true) {
            // Ждем подключения клиента и получаем потоки для дальнейшей работы
            try (SocketChannel socketChannel = serverChannel.accept()) {
                // Определяем буфер для получения данных
                final ByteBuffer inputBuffer = ByteBuffer.allocate(2 << 10);

                socketChannel.write(
                        transmit("""
                                Здравствуйте, вас приветствует %s!
                                Сегодня вам доступно вычисление N-го члена Фибоначчи!
                                Чтобы воспользоваться услугой, отправьте команду 'fib x', где 'x' – интересующий вас член."""
                ));
                System.out.println("Отправлено приветствие");

                while (socketChannel.isConnected()) {
                    int bytesCount = socketChannel.read(inputBuffer);
                    if (bytesCount == -1) break;

                    String clientsRequest = new String(inputBuffer.array(), 0, bytesCount, StandardCharsets.UTF_8);
                    inputBuffer.clear();
                    System.out.println("Получено от клиента: " + clientsRequest);

                    if (!clientsRequest.startsWith("fib")) {
                        socketChannel.write(echo(clientsRequest,
                                "это конечно хорошо, но как насчёт Фибоначчи?" )
                        );
                        continue;
                    }

                    String[] request = clientsRequest.split(" ");
                    if (request.length < 2) {
                        socketChannel.write(echo(clientsRequest,
                                "это прикольно, но нужно соблюсти формат." )
                        );
                        continue;
                    }

                    int argument;
                    try {
                        argument = Integer.parseInt(request[1]);
                    } catch (NumberFormatException e) {
                        socketChannel.write(echo(clientsRequest,
                                "это прикольно, но нужно соблюсти формат." )
                        );
                        continue;
                    }

                    socketChannel.write(transmit(computingResponse(argument)));
                    socketChannel.write(transmit("Команда получена!"));
//                    long result = Computer.fibonacci(argument);
//                    System.out.println("Вычислен результат: " + result);
//                    socketChannel.write(ByteBuffer.wrap(
//                            "Вычислен %d-й член: %d"
//                            .formatted(argument, result)
//                            .getBytes(StandardCharsets.UTF_8))
//                    );


                }

            } catch (IOException err) {
                System.out.println(err.getMessage());
            }
        }
    }

    private static ByteBuffer echo(String msg, String answer) {
        return ByteBuffer.wrap(
                "%s – %s"
                .formatted(
                        msg.length() < CIT_LIMIT ?
                                msg : msg.substring(0, CIT_LIMIT) + "...",
                        answer)
                .getBytes(StandardCharsets.UTF_8)
        );
    }

    private static ByteBuffer transmit(String msg) {
        return ByteBuffer.wrap(msg.getBytes(StandardCharsets.UTF_8));
    }

    private static String computingResponse(int arg) {
        long res;
        long timeOn = new Date().getTime();
        try {
            res = Computer.fibonacci(arg);
            Thread.sleep(1500);
        } catch (Exception e) {
            return e.getMessage();
        }
        long time = new Date().getTime() - timeOn;
        return "%d-й член Фибоначчи вычислен за %s: %d%n"
                .formatted(arg, Computer.nanoTimeFormatter(time), res);
    }


}
