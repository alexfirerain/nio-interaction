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

                socketOut.println(WELCOME);
                System.out.println("Отправлено приветствие");

                String clientsRequest;
                while (socket.isConnected()) {
                    clientsRequest = socketIn.readLine();

                    System.out.println("Получено от клиента: " + clientsRequest);

                    if ("terminate".equals(clientsRequest)) {
                        System.out.println("От клиента получена команда остановки");
                        break server;
                    }

                    if (!clientsRequest.startsWith("fib") && !clientsRequest.startsWith("фиб")) {
                        String response = askBack(clientsRequest,"это конечно хорошо, но как насчёт Фибоначчи?");
                        socketOut.println(response);
                        System.out.println("Отправлено клиенту: " + response);
                        continue;
                    }

                    String[] request = clientsRequest.split(" ");
                    if (request.length < 2) {
                        String response = askBack(clientsRequest,"это замечательно, но нужно соблюсти формат.");
                        socketOut.println(response);
                        System.out.println("Отправлено клиенту: " + response);
                        continue;
                    }

                    int argument;
                    try {
                        argument = Integer.parseInt(request[1]);
                    } catch (NumberFormatException e) {
                        String response = askBack(clientsRequest,"это прикольно, но нужно соблюсти формат.");
                        socketOut.println(response);
                        System.out.println("Отправлено клиенту: " + response);
                        continue;
                    }

                    String response = computingResponse(argument);
                    socketOut.println(response);
                    System.out.println("Отправлено клиенту: " + response);

//                    out.write("Значение " + argument + " принято на вычисление."));
//                    long result = Computer.fibonacci(argument);
//                    System.out.println("Вычислен результат: " + result);
//                    out.write("Вычислен %d-й член: %d".formatted(argument, result));

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
//            Thread.sleep(1500);
            System.out.println("Вычислен результат: " + res);
        } catch (Exception e) {
            return e.getMessage();
        }
        long time = System.nanoTime() - timeOn;
        return "%d-й член Фибоначчи: %d (вычислен за %s)%n"
                .formatted(arg, res, Computer.nanoTimeFormatter(time));
    }
}
