package il.cshaifasweng.OCSFMediatorExample.server;

import java.io.IOException;

public class App 
{
    private static SimpleServer server;
    public static void main( String[] args ) throws IOException
    {
        server = new SimpleServer(3000);
        try {
            server.listen();
            System.out.println("Server is running");
        }catch(IOException e){
            e.printStackTrace();
            System.err.println("ERROR! Server could not be started : " + e.getMessage());
        }
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutting down server...");
            try {
                if (server != null) {
                    server.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }));

    }
}
