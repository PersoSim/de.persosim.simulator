package de.persosim.simulator.perso.dscardsigner;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import javax.smartcardio.Card;
import javax.smartcardio.CardChannel;
import javax.smartcardio.CardException;
import javax.smartcardio.CardTerminal;
import javax.smartcardio.CommandAPDU;
import javax.smartcardio.ResponseAPDU;
import javax.smartcardio.TerminalFactory;

/**
 * @author tsenger
 *
 */
public class SigAnimaCardHandler {

    private static final Logger log = Logger.getLogger(SigAnimaCardHandler.class.getName());

    public static final byte PERM_FREE = 0;
    public static final byte PERM_PIN = 1;

    private static final int SW_NO_ERROR = 0x9000;

    private static final boolean debug = false;

    private final CardChannel channel;

    public SigAnimaCardHandler(int slotId, byte[] aid) throws CardException {
        channel = connect(slotId);
        selectApplet(aid);
    }

    public boolean verify(String password) throws CardException {
        return sendVerify(password);
    }

    public byte[] sign(byte keyId, byte[] input) throws CardException {
        sendMSE(keyId);
        return sendPSOSign(input);
    }

    public byte[] genKeyPair(byte keyId, byte domainParameterId) throws CardException {
        sendMSE(keyId, domainParameterId);
        return sendGenKeyPair();
    }

    public boolean createAndWriteFile(short fid, byte[] fileBytes) throws CardException {
        sendCreateFile(fid, (short)fileBytes.length, PERM_FREE);
        return writeFile(fid, fileBytes);
    }

    public boolean writeFile(short fid, byte[] fileBytes) throws CardException {
        if (!sendSelectFile(fid)) return false;
        return sendWriteBinary(fileBytes);
    }

    public boolean setPUK(String puk) throws CardException {
        if (puk.length()!=10) throw new IllegalArgumentException("PUK length must be 10 bytes!");
        return sendChangeReferenceData((byte)1, puk.getBytes());
    }

    public boolean setPIN(String puk, String pin) throws CardException {
        if (puk.length()!=10) throw new IllegalArgumentException("PUK length must be 10 bytes!");
        byte[] mergedPukPin = new byte[10+pin.length()];
        System.arraycopy(puk.getBytes(), 0, mergedPukPin, 0, 10);
        System.arraycopy(pin.getBytes(), 0, mergedPukPin, 10, pin.length());
        return sendChangeReferenceData((byte)0, mergedPukPin);
    }

    public boolean setState(byte state) throws CardException {
        CommandAPDU capdu = new CommandAPDU(0, 0xDA, 0x68, state);
        ResponseAPDU resp = transmit(capdu);
        return resp.getSW()==SW_NO_ERROR;
    }

    /**
     * Reads the content of an elementary transparent file (EF). If the file is
     * bigger then 255 byte this function uses multiply READ BINARY command to
     * get the whole file.
     *
     * @param fid contains the FID of the EF to read.
     * @return Returns the content of the EF with the given SFID
     * @throws CardException
     * @throws IOException
     */
    public byte[] getFile(short fid) throws IOException, CardException   {

        byte[] content = null;
        // synchronized (getClass()) { // select and read (sync done in host)
            if (sendSelectFile(fid)) {
                byte[] firstBytes = sendReadBinary((byte) 0, (byte) 0, (byte) 0x8);
                if (firstBytes.length > 0) {
                    int fileLength = getLength(firstBytes);
                    content = readFile(fileLength);
                }
            }
        // }
        return content;
    }

    /**
     * Establish connection to terminal and card on terminal.
     *
     * @param slotId terminal to use
     * @return channel
     * @throws CardException
     */
    private CardChannel connect(int slotId) throws CardException {

        List<CardTerminal> cardTerminals = TerminalFactory.getDefault().terminals().list();

        /* Is a Reader connected we can access? */
        if (cardTerminals.size() == 0) {
            throw new CardException ("No reader present");
        }

        /* Terminal we are working on */
        CardTerminal terminal = cardTerminals.get(slotId);

        /* Is a card present? */
        if (!terminal.isCardPresent()) {
            throw new CardException ("No Card present!");
        }

        Card card = terminal.connect("T=1");
        return card.getBasicChannel();
    }

    private ResponseAPDU transmit(CommandAPDU apdu) throws CardException {
        if (debug) System.out.println("Send:\n"+HexString.bufferToHex(apdu.getBytes())+"\n");
        ResponseAPDU resp = channel.transmit(apdu);
        if (debug) System.out.println("Receive:\n"+HexString.bufferToHex(resp.getBytes())+"\n");
        return resp;
    }

    /**
     * Reads x bytes from EF which has been selected before.
     *
     * @param length
     *            Length of the file to read
     * @return file content
     * @throws CardException
     * @throws SecureMessagingException
     */
    private byte[] readFile(int length) throws CardException {
        int remainingBytes = length;
        byte[] resp = null;
        byte[] fileData = new byte[length];

        int maxReadLength = 0xFF;
        int i = 0;

        do {
            int offset = i * maxReadLength;
            byte off1 = (byte) ((offset & 0x0000FF00) >> 8);
            byte off2 = (byte) (offset & 0x000000FF);

            if (remainingBytes <= maxReadLength) {
                resp = sendReadBinary(off1, off2, remainingBytes);
                remainingBytes = 0;
            } else {
                resp = sendReadBinary(off1, off2, maxReadLength);
                remainingBytes -= maxReadLength;
            }
            System.arraycopy(resp, 0, fileData, i * maxReadLength,	resp.length);
            i++;

        } while (remainingBytes > 0);
        return fileData;
    }

    /**
     * Get the length value from a TLV coded byte array. This function is adapted
     * from bouncycastle
     *
     * @see org.bouncycastle.asn1.ASN1InputStream#readLength(InputStream s, int
     *      limit)
     *
     * @param b
     *            TLV coded byte array that contains at least the tag and the
     *            length value. The data value is not necessary.
     * @return
     * @throws IOException
     */
    private int getLength(byte[] b) throws IOException {
        ByteArrayInputStream s = new ByteArrayInputStream(b);
        int size = 0;
        s.read(); // Skip the the first byte which contains the Tag value
        int length = s.read();
        if (length < 0)
            throw new EOFException("EOF found when length expected");

        if (length == 0x80)
            return -1; // indefinite-length encoding

        if (length > 127) {
            size = length & 0x7f;

            // Note: The invalid long form "0xff" (see X.690 8.1.3.5c) will be
            // caught here
            if (size > 4)
                throw new IOException("DER length more than 4 bytes: " + size);

            length = 0;
            for (int i = 0; i < size; i++) {
                int next = s.read();
                if (next < 0)
                    throw new EOFException("EOF found reading length");
                length = (length << 8) + next;
            }

            if (length < 0)
                throw new IOException("corrupted stream - negative length found");

        }
        return length + size + 2; // +1 tag, +1 length
    }

    private boolean sendVerify(String password) throws CardException {
        log.fine("called sendVerify");
        CommandAPDU capdu = new CommandAPDU(0, 0x20, 0x00, 0x00, password.getBytes());
        ResponseAPDU resp = transmit(capdu);
        if (resp.getSW()==SW_NO_ERROR) return true;
        else throw new CardException("Verify command failed! Response was: "+HexString.bufferToHex(resp.getBytes()));
    }

    private boolean sendChangeReferenceData(byte p1, byte[] data) throws CardException {
        CommandAPDU capdu = new CommandAPDU(0, 0x24, p1, 0x00, data);
        ResponseAPDU resp = transmit(capdu);
        if (resp.getSW()==SW_NO_ERROR) return true;
        else throw new CardException("ChangeReferenceData command failed! Response was: "+HexString.bufferToHex(resp.getBytes()));
    }

    private boolean sendSelectFile(short fid) throws CardException {
        CommandAPDU capdu = new CommandAPDU(0, 0xA4, 0x00, 0x00, shortToByteArray(fid));
        ResponseAPDU resp = transmit(capdu);
        if (resp.getSW()==SW_NO_ERROR) return true;
        else throw new CardException("Select file command failed! Response was: "+HexString.bufferToHex(resp.getBytes()));
    }

    private boolean sendCreateFile(short fid, short length, byte permission) throws CardException {
        byte[] fidBytes = shortToByteArray(fid);
        byte[] lengthBytes = shortToByteArray(length);
        byte data[] = new byte[]{fidBytes[0],fidBytes[1], lengthBytes[0],lengthBytes[1],permission};

        CommandAPDU capdu = new CommandAPDU(0, 0xE0, 0x00, 0x00, data);
        ResponseAPDU resp = transmit(capdu);
        if (resp.getSW()==SW_NO_ERROR) return true;
        else throw new CardException("CreateFile command failed! Response was: "+HexString.bufferToHex(resp.getBytes()));
    }

    private boolean sendWriteBinary(byte[] data) throws CardException {

        int blockSize = 128;
        short offset = 0;
        ByteArrayOutputStream apduData = new ByteArrayOutputStream();

        while (offset < data.length) {
            if (offset + blockSize > data.length) {
                blockSize = data.length - offset;
            }
            apduData.reset();
            apduData.write(data, offset, blockSize);
            CommandAPDU capdu = new CommandAPDU(0, 0xD0, (byte) (offset >> 8), (byte) (offset & 0xFF), apduData.toByteArray());
            ResponseAPDU resp = transmit(capdu);
            if (!(resp.getSW()==SW_NO_ERROR)) throw new CardException("WriteBinary command failed! Response was: "+HexString.bufferToHex(resp.getBytes()));
            offset += blockSize;
        }
        return true;
    }

    private byte[] sendReadBinary(byte high_offset, byte low_offset, int le) throws CardException {
        CommandAPDU capdu = new CommandAPDU((byte) 0x00, (byte) 0xB0, high_offset, low_offset, le);
        ResponseAPDU resp = transmit(capdu);
        if (resp.getSW()!=SW_NO_ERROR) throw new CardException("ReadBinary command failed! Response was: "+HexString.bufferToHex(resp.getBytes()));
        return resp.getData();
    }

    private byte[] sendGenKeyPair() throws CardException {
        CommandAPDU capdu = new CommandAPDU(0, 0x46, 0x80, 0x00);
        ResponseAPDU resp = transmit(capdu);
        if (resp.getSW()!=SW_NO_ERROR) throw new CardException("Generate KeyPair command failed! Response was:  "+HexString.bufferToHex(resp.getBytes()));
        return resp.getData();
    }

    private byte[] sendPSOSign(byte[] tosign) throws CardException {
        log.fine("called sendPSOSign");
        CommandAPDU capdu = new CommandAPDU(0, 0x2A, 0x9E, 0x9A, tosign);
        ResponseAPDU resp = transmit(capdu);
        if (resp.getSW()!=SW_NO_ERROR) throw new CardException("PSOSign command failed! Response was: "+HexString.bufferToHex(resp.getBytes()));
        return resp.getData();
    }

    private boolean sendMSE(byte keyId) throws CardException {
        log.fine("called sendMSE");
        CommandAPDU capdu = new CommandAPDU(0, 0x22, 0x41, 0xB6, new byte[]{(byte)0x84, 0x01, keyId});
        ResponseAPDU resp = transmit(capdu);
        if (resp.getSW()==SW_NO_ERROR) return true;
        else throw new CardException("MSE:SetAT failed! Response was: "+HexString.bufferToHex(resp.getBytes()));
    }

    private boolean sendMSE(byte keyId, byte domainParameterId) throws CardException {
        CommandAPDU capdu = new CommandAPDU(0, 0x22, 0x41, 0xB6, new byte[]{(byte)0x84, 0x01, keyId, (byte)0x80, 0x01, domainParameterId});
        ResponseAPDU resp = transmit(capdu);
        if (resp.getSW()==SW_NO_ERROR) return true;
        else throw new CardException("MSE:SetAT failed! Response was: "+HexString.bufferToHex(resp.getBytes()));
    }

    private boolean selectApplet(byte[] aid) throws CardException {
        CommandAPDU capdu = new CommandAPDU(0, 0xA4, 0x04, 0, aid);
        ResponseAPDU resp = transmit(capdu);
        if (resp.getSW()==SW_NO_ERROR) return true;
        else throw new CardException("Select Applet failed! Response was: "+HexString.bufferToHex(resp.getBytes()));
    }

    private static byte[] shortToByteArray(short sh) {
        byte[] ret = new byte[2];
        ret[0] = (byte)((sh >>> 8) & 0xff);
        ret[1] = (byte)(sh & 0xff);
        return ret;
    }

}