import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    public static void main(String[] args) throws IOException {

        final Socket socket = new Socket(GUSCI_Config.HOSTNAME, GUSCI_Config.FBC_PORT);

        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
             Scanner scanner = new Scanner(System.in))
        {
//            String serverTalks;
//            while((serverTalks = in.readLine()) != null)
//                System.out.println(serverTalks);

//            listenToServer(in, out);
            System.out.println(in.readLine());

            String input;
            while (true) {
                input = scanner.nextLine();
                if ("end".equals(input)) break;

                out.println(input);

                if ("terminate".equals(input)) break;

                System.out.println(in.readLine());
            }
        }
//        catch (InterruptedException e) {
//            e.printStackTrace();
//        }


    }

    private static void listenToServer(BufferedReader source, PrintWriter out) throws IOException {
//        int str;
//        do {
//            str = source.read();
//            System.out.print((char) str);
////            out.println("\n");
//        } while (str != -1);
        String str;
        while ((str = source.readLine()) != null)
            System.out.println(str);
        System.out.println("***");
    }

}
