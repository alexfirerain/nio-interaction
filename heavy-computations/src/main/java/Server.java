import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

public class Server {
    private static final int CIT_LIMIT = 15;

    public static void main(String[] args) throws IOException {

        System.out.println("Сервер запускается");
        final ServerSocketChannel serverChannel = ServerSocketChannel.open();
        serverChannel.bind(new InetSocketAddress("localhost", 4160));
        while (true) {
            try (SocketChannel socketChannel = serverChannel.accept()) {
                System.out.println("Связались с " + socketChannel.getRemoteAddress());

                final ByteBuffer inputBuffer = ByteBuffer.allocate(2 << 10);

                socketChannel.write(
                        transmit("""
                                Здравствуйте, вас приветствует %s!
                                Сегодня вам доступно вычисление N-го члена Фибоначчи!
                                Чтобы воспользоваться услугой, отправьте команду 'fib x', где 'x' – интересующий вас член."""
                                .formatted(Computer.NAME)
                ));
                System.out.println("Отправлено приветствие");

                while (socketChannel.isConnected()) {
                    int bytesCount = socketChannel.read(inputBuffer);
                    if (bytesCount == -1) break;

                    String clientsRequest = new String(inputBuffer.array(), 0, bytesCount, StandardCharsets.UTF_8);
                    inputBuffer.clear();
                    System.out.println("Получено от клиента: " + clientsRequest);

                    if (!clientsRequest.startsWith("fib")) {
                        socketChannel.write(askBack(clientsRequest,
                                "это конечно хорошо, но как насчёт Фибоначчи?" )
                        );
                        continue;
                    }

                    String[] request = clientsRequest.split(" ");
                    if (request.length < 2) {
                        socketChannel.write(askBack(clientsRequest,
                                "это замечательно, но нужно соблюсти формат." )
                        );
                        continue;
                    }

                    int argument;
                    try {
                        argument = Integer.parseInt(request[1]);
                    } catch (NumberFormatException e) {
                        socketChannel.write(askBack(clientsRequest,
                                "это прикольно, но нужно соблюсти формат." )
                        );
                        continue;
                    }

                    socketChannel.write(transmit(computingResponse(argument)));

//                    socketChannel.write(transmit("Значение " + argument + " принято на вычисление."));
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

    private static ByteBuffer askBack(String msg, String answer) {
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
        if (arg > 93) return "Величина результат превышает 4 байта, такие вычисления ещё не реализованы!";
        long res;
        long timeOn = System.nanoTime();
        try {
            res = Computer.fibonacci(arg);
//            Thread.sleep(1500);
        } catch (Exception e) {
            return e.getMessage();
        }
        long time = System.nanoTime() - timeOn;
        return "%d-й член Фибоначчи: %d (вычислен за %s)%n"
                .formatted(arg, res, Computer.nanoTimeFormatter(time));
    }


}
