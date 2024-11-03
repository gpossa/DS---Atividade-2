import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UDPComm {
    String host = "";
    int port;
    char[] message = new char[256];
    byte[] messageByte = new byte[256];

    public UDPComm(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public UDPComm(int port) {
        this.port = port;
    }

    public boolean sendMessage() {
        try {
            InetAddress address = InetAddress.getByName(host);
            DatagramSocket socket = new DatagramSocket();
            DatagramPacket packet = new DatagramPacket(messageByte, messageByte.length, address, port);

            socket.send(packet);
            socket.close();

            return true;
        }
        catch (IOException ioe) {
            return false;
        }
    }

    public boolean receiveMessage() {
        byte[] messageByte = new byte[256];

        try {
            DatagramSocket socket = new DatagramSocket(port);
            DatagramPacket packet = new DatagramPacket(messageByte, messageByte.length);
            socket.receive(packet);

            this.host = packet.getAddress().getHostAddress();

            setMessage(packet.getData());
            socket.close();

            return true;

        } catch (IOException ioe) {
            System.out.println(ioe.getMessage());
            return false;
        }
    }

    public void setMessage(byte[] messageByte) {
        this.messageByte = messageByte;
        this.message = byteToChar(messageByte);
    }

    public String getMessageStr() {
        StringBuilder messageStr = new StringBuilder();

        for (char c : this.message)
            if (c != 0)
                messageStr.append(c);

        return messageStr.toString();
    }

    public char[] getJogada() {
        char[] jogada = {' ',' ',' ',' ',' ',' ',' ',' ',' '};

        for (int i = 0; i < this.message.length; i++) {
            if (i > 8)
                break;

            if (this.message[i] != 0)
                jogada[i] = this.message[i];
        }

        return jogada;
    }

    public byte[] charToByte(char[] message) {
        byte[] messageByte = new byte[message.length];

        for (int i = 0; i < message.length; i++)
            messageByte[i] = (byte) message[i];

        return messageByte;
    }

    private char[] byteToChar(byte[] messageByte) {
        char[] message = new char[messageByte.length];

        for (int i = 0; i < messageByte.length; i++)
            message[i] = (char) messageByte[i];

        return message;
    }
}