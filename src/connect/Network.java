package connect;

public class Network {

    public static Listener listen() {
        return new Listener();
    }

    public static void send(int type, byte[] data, String IP) {
        new Sender((byte) type, data, IP);
    }

}
