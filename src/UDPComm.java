import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UDPComm {
    String host = "";
    int port;
    char[] msg = new char[256];
    byte[] msgByte = new byte[256];

    public boolean sendMsg() {
        try {
            InetAddress addr = InetAddress.getByName(host);
            DatagramPacket pkg = new DatagramPacket(msgByte, msgByte.length, addr, port);
            DatagramSocket ds = new DatagramSocket();

            ds.send(pkg);
            ds.close();

            return true;
        }
        catch (IOException ioe) {
            return false;
        }
    }

    public boolean receiveMsg() {
        byte[] msgByte = new byte[256];

        try {
            DatagramSocket ds = new DatagramSocket(port);
            DatagramPacket pkg = new DatagramPacket(msgByte, msgByte.length);
            ds.receive(pkg);

            this.host = pkg.getAddress().getHostName();

            setMsg(pkg.getData());
            ds.close();

            return true;

        } catch (IOException ioe) {
            System.out.println(ioe.getMessage());
            return false;
        }
    }

    public void setMsg(byte[] msgByte) {
        this.msgByte = msgByte;
        this.msg = byteToChar(msgByte);
    }

    public String getMsgStr() {
        String msgStr = "";

        for (int i = 0; i < this.msg.length; i++)
            if (this.msg[i]!=0)
                msgStr = msgStr + this.msg[i];

        return msgStr;
    }

    public char[] getJogada() {
        char[] jogada = {' ',' ',' ',' ',' ',' ',' ',' ',' '};

        for (int i = 0; i < this.msg.length; i++) {
            if (i > 8)
                break;

            if (this.msg[i] != 0)
                jogada[i] = this.msg[i];
        }

        return jogada;
    }

    public UDPComm(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public UDPComm(int port) {
        this.port = port;
    }

    public byte[] charToByte(char[] msg) {
        byte[] msgByte = new byte[msg.length];

        for (int i = 0;i<msg.length;i++)
            msgByte[i] = (byte) msg[i];

        return msgByte;
    }

    private char[] byteToChar(byte[] msgByte) {
        char[] msg = new char[msgByte.length];

        for (int i = 0; i < msgByte.length; i++)
            msg[i] = (char) msgByte[i];

        return msg;
    }
}