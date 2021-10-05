import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class Server {
    private static final int CIT_LIMIT = 15;
    private static final String WELCOME = ("Здравствуйте, вас приветствует %s! " +
                                           "Сегодня вам доступно вычисление N-го члена Фибоначчи! " +
                                           "Чтобы воспользоваться услугой, отправьте команду 'fib x', где 'x' – интересующий вас член.")
            .formatted(/*GUSCI_Config.WELCOME,*/ Computer.NAME);

    public static void main(String[] args) throws IOException {

        System.out.println("Сервер запускается");
        final ServerSocket serverSocket = new ServerSocket(GUSCI_Config.FBC_PORT);
        server:
        while (true) {
            try (Socket socket = serverSocket.accept();
                 PrintWriter socketOut = new PrintWriter(socket.getOutputStream(), true);
                 BufferedReader socketIn = new BufferedReader(new InputStreamReader(socket.getInputStream())))
            {
                System.out.println("Связались с " + socket.getRemoteSocketAddress());
                sendResponse(socketOut, WELCOME);

                String clientsRequest;
                while (socket.isConnected()) {
                    clientsRequest = socketIn.readLine();
                    System.out.println("Получено от клиента: " + clientsRequest);

                    if (clientsRequest == null) break;

                    if ("terminate".equals(clientsRequest)) {
                        System.out.println("От клиента получена команда остановки");
                        break server;
                    }

                    if (!clientsRequest.startsWith("fib") && !clientsRequest.startsWith("фиб")) {
                        sendResponse(socketOut,
                                askBack(clientsRequest,
                                        "это конечно хорошо, но как насчёт Фибоначчи?"));
                        continue;
                    }

                    String[] request = clientsRequest.split(" ");
                    if (request.length < 2) {
                        sendResponse(socketOut,
                                askBack(clientsRequest,
                                        "это замечательно, но нужно соблюсти формат."));
                        continue;
                    }

                    int argument;
                    try {
                        argument = Integer.parseInt(request[1]);
                    } catch (NumberFormatException e) {
                        sendResponse(socketOut,
                                askBack(clientsRequest,
                                        "это прикольно, но нужно соблюсти формат."));
                        continue;
                    }

                    sendResponse(socketOut,
                            computingResponse(argument));

                }
                System.out.println("Завершение сеанса с клиентом");
            } catch (IOException err) {
                System.out.println(err.getMessage());
            }
        }
        System.out.println("Сервер останавливается");
    }

    private static String askBack(String msg, String answer) {
        return "%s – %s".formatted(
                        msg.length() < CIT_LIMIT ?
                                msg : msg.substring(0, CIT_LIMIT) + "...",
                                answer);
    }


    private static String computingResponse(int arg) {
        long res;
        long timeOn = System.nanoTime();
        try {
            res = Computer.fibonacci(arg);
            System.out.println("Вычислен результат: " + res);
        } catch (Exception e) {
            return e.getMessage();
        }
        long time = System.nanoTime() - timeOn;
        return "%d-й член Фибоначчи: %d (вычислен за %s)%n"
                .formatted(arg, res, Computer.nanoTimeFormatter(time));
    }

    private static void sendResponse(PrintWriter stream, String msg) {
        stream.println(msg);
        System.out.println(msg.equals(WELCOME) ?
                "Отправлено приветствие" :
                "Отправлено клиенту: " + msg);
    }
}
