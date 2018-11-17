package project.server;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import project.Config;
import project.logic.common.utils.Serializer;
import project.logic.representation.Location;

import java.io.IOException;
import java.net.Socket;
import java.util.Objects;
import java.util.Scanner;

public final class Client extends Thread {
    private final Location location;
    private final String name;

    public Client(@Nullable String name, @NotNull Location location) {
        super();
        this.name = "" + name;
        this.location = Objects.requireNonNull(location);
    }

    @Override
    public void run() {
        try (Scanner scanner = new Scanner(System.in, "utf-8")) {
            System.out.print("> ");
            while (scanner.hasNextLine()) {
                String query = scanner.nextLine().trim().toLowerCase();
                if (query.equals("exit")) break;

                try (Socket socket = new Socket(Config.HOST, Config.PORT)) {
                    Serializer.serialize(socket.getOutputStream(), new Request(name, query, location));
                    Response response = (Response) Serializer.deserialize(socket.getInputStream());
                    response.getDishes().forEach(System.out::println);
                    System.out.print("> ");
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void main(String... args) {
        new Client(null, Location.NONE).start();
    }
}