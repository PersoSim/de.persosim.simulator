package de.persosim.simulator.perso.dscardsigner;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.util.List;

import javax.smartcardio.Card;
import javax.smartcardio.CardChannel;
import javax.smartcardio.CardException;
import javax.smartcardio.CardTerminal;
import javax.smartcardio.CommandAPDU;
import javax.smartcardio.ResponseAPDU;
import javax.smartcardio.TerminalFactory;

import de.persosim.simulator.utils.HexString;
import static de.persosim.simulator.utils.PersoSimLogger.TRACE;
import static de.persosim.simulator.utils.PersoSimLogger.log;

/**
 * Class for communication with smartcards containing an SigAnima JavaCard applet (https://github.com/tsenger/SigAnima)
 *  
 * SigAnima provided ECDSA plain signature function.  
 * This class provides all necessary methods to use all functions of this applet.
 * All implemented commands with all parameters are described in the APDU reference document of SigAnima on the project repository. 
 * 
 * @author tsenger
 *
 */
public class SigAnimaCardHandler {

    public static final byte PERM_FREE = 0;
    public static final byte PERM_PIN = 1;

    private static final int SW_NO_ERROR = 0x9000;


    private final CardChannel channel;

    public SigAnimaCardHandler(int slotId, byte[] aid) throws CardException {
        channel = connect(slotId);
        selectApplet(aid);
    }

    /**
     * @param password The data field should contain the ASCII bytes of the PIN (i.e. from the range 0x30 .. 0x39). 
     * @return Returns true if the PIN was correct. Otherwise it return false. The retry counter has a initial value of 3.
     * @throws CardException
     */
    public boolean verify(String password) throws CardException {
        return sendVerify(password);
    }

    /**
     * Signs the given plain data and return the signature. The input field may contain the hash value of the data to be signed. 
     * The sign command will sign what ever it gets in the input field. 
     * The signing function will pad data with leading zero up to the size of the public key if the data is shorter then the public key size. 
     * If the data is bigger then the public key, the data will be truncated to the size of the public key (most significant bytes will be cut off).
     * 
     * @param keyId Contains the key identifier to the key to sign with. There three slots for key pair. The key identifier is simply the index of the key pair and must be a value between 0x00 and 0x02.
     * @param input Input data to sign. It may contain the hash value of the data to be signed. 
     * @return Signature bytes
     * @throws CardException
     */
    public byte[] sign(byte keyId, byte[] input) throws CardException {
        sendMSE(keyId);
        return sendPSOSign(input);
    }

    /**
     * Generates a ECDSA key pair and return the public key
     * 
     * @param keyId Contains the key identifier. There three slots for key pair. The key identifier is simply the index of the key pair and must be a value between 0x00 and 0x02.
     * @param domainParameterId  Contains the ID of the standardized domain parameters. Valid values are: 0x0A for secp224r1, 0x0B for BrainpoolP224r1, 0x0C for secp256r1, 0x0D for BrainpoolP256r1, 0x0E for BrainpoolP320r1
     * @return The response is a simple TLV structures with tag 0x86 which contains the uncompressed EC public key as value.
     * @throws CardException
     */
    public byte[] genKeyPair(byte keyId, byte domainParameterId) throws CardException {
        sendMSE(keyId, domainParameterId);
        return sendGenKeyPair();
    }

    /**
     * Generates a new EF and fill it with the given data
     * 
     * @param fid FID of the EF to create
     * @param fileBytes The content of the EF
     * @return success
     * @throws CardException
     */
    public boolean createAndWriteFile(short fid, byte[] fileBytes) throws CardException {
        sendCreateFile(fid, (short)fileBytes.length, PERM_FREE);
        return writeFile(fid, fileBytes);
    }

    /**
     * Select existing EF and fill it with the given data
     * @param fid EF to select
     * @param fileBytes The content of the EF
     * @return success
     * @throws CardException
     */
    public boolean writeFile(short fid, byte[] fileBytes) throws CardException {
        if (!sendSelectFile(fid)) return false;
        return sendWriteBinary(fileBytes);
    }

    /**
     * Sets the PUK of the applet.
     * This command is only available when the applet is in the initial state. 
     * 
     * @param puk The field should contain the ASCII bytes of the PUK (i.e. from the range 0x30 .. 0x39).  The PUK length should always be 10.
     * @return success
     * @throws CardException
     */
    public boolean setPUK(String puk) throws CardException {
        if (puk.length()!=10) throw new IllegalArgumentException("PUK length must be 10 bytes!");
        return sendChangeReferenceData((byte)1, puk.getBytes());
    }

    /**
     * Change the PIN of the applet
     * 
     * @param puk The field should contain the ASCII bytes of the PUK (i.e. from the range 0x30 .. 0x39). The PUK length should always be 10.
     * @param pin The field should contain the ASCII bytes of the PUK (i.e. from the range 0x30 .. 0x39). The PIN length should be between 4 and 10.
     * @return success
     * @throws CardException
     */
    public boolean setPIN(String puk, String pin) throws CardException {
        if (puk.length()!=10) throw new IllegalArgumentException("PUK length must be 10 bytes!");
        byte[] mergedPukPin = new byte[10+pin.length()];
        System.arraycopy(puk.getBytes(), 0, mergedPukPin, 0, 10);
        System.arraycopy(pin.getBytes(), 0, mergedPukPin, 10, pin.length());
        return sendChangeReferenceData((byte)0, mergedPukPin);
    }

    /**
     * Sets the internal state of the applet.
     * 
     * @param state 0x01 initial, 0x02 prepersonalized. The state is set to personalized (0x03) implicitly by the change reference data command when setting the user PIN. 
     * @return success
     * @throws CardException
     */
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
            if (sendSelectFile(fid)) {
                byte[] firstBytes = sendReadBinary((byte) 0, (byte) 0, (byte) 0x8);
                if (firstBytes.length > 0) {
                    int fileLength = getLength(firstBytes);
                    content = readFile(fileLength);
                }
            }
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
    	log(SigAnimaCardHandler.class, "Send:\n"+HexString.dump(apdu.getBytes())+"\n", TRACE);
        ResponseAPDU resp = channel.transmit(apdu);
        log(SigAnimaCardHandler.class, "Receive:\n"+HexString.dump(resp.getBytes())+"\n", TRACE);
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
        CommandAPDU capdu = new CommandAPDU(0, 0x20, 0x00, 0x00, password.getBytes());
        ResponseAPDU resp = transmit(capdu);
        if (resp.getSW()==SW_NO_ERROR) return true;
        else throw new CardException("Verify command failed! Response was: "+HexString.dump(resp.getBytes()));
    }

    private boolean sendChangeReferenceData(byte p1, byte[] data) throws CardException {
        CommandAPDU capdu = new CommandAPDU(0, 0x24, p1, 0x00, data);
        ResponseAPDU resp = transmit(capdu);
        if (resp.getSW()==SW_NO_ERROR) return true;
        else throw new CardException("ChangeReferenceData command failed! Response was: "+HexString.dump(resp.getBytes()));
    }

    private boolean sendSelectFile(short fid) throws CardException {
        CommandAPDU capdu = new CommandAPDU(0, 0xA4, 0x00, 0x00, shortToByteArray(fid));
        ResponseAPDU resp = transmit(capdu);
        if (resp.getSW()==SW_NO_ERROR) return true;
        else throw new CardException("Select file command failed! Response was: "+HexString.dump(resp.getBytes()));
    }

    private boolean sendCreateFile(short fid, short length, byte permission) throws CardException {
        byte[] fidBytes = shortToByteArray(fid);
        byte[] lengthBytes = shortToByteArray(length);
        byte data[] = new byte[]{fidBytes[0],fidBytes[1], lengthBytes[0],lengthBytes[1],permission};

        CommandAPDU capdu = new CommandAPDU(0, 0xE0, 0x00, 0x00, data);
        ResponseAPDU resp = transmit(capdu);
        if (resp.getSW()==SW_NO_ERROR) return true;
        else throw new CardException("CreateFile command failed! Response was: "+HexString.dump(resp.getBytes()));
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
            if (!(resp.getSW()==SW_NO_ERROR)) throw new CardException("WriteBinary command failed! Response was: "+HexString.dump(resp.getBytes()));
            offset += blockSize;
        }
        return true;
    }

    private byte[] sendReadBinary(byte high_offset, byte low_offset, int le) throws CardException {
        CommandAPDU capdu = new CommandAPDU((byte) 0x00, (byte) 0xB0, high_offset, low_offset, le);
        ResponseAPDU resp = transmit(capdu);
        if (resp.getSW()!=SW_NO_ERROR) throw new CardException("ReadBinary command failed! Response was: "+HexString.dump(resp.getBytes()));
        return resp.getData();
    }

    private byte[] sendGenKeyPair() throws CardException {
        CommandAPDU capdu = new CommandAPDU(0, 0x46, 0x80, 0x00);
        ResponseAPDU resp = transmit(capdu);
        if (resp.getSW()!=SW_NO_ERROR) throw new CardException("Generate KeyPair command failed! Response was:  "+HexString.dump(resp.getBytes()));
        return resp.getData();
    }

    private byte[] sendPSOSign(byte[] tosign) throws CardException {
        CommandAPDU capdu = new CommandAPDU(0, 0x2A, 0x9E, 0x9A, tosign);
        ResponseAPDU resp = transmit(capdu);
        if (resp.getSW()!=SW_NO_ERROR) throw new CardException("PSOSign command failed! Response was: "+HexString.dump(resp.getBytes()));
        return resp.getData();
    }

    private boolean sendMSE(byte keyId) throws CardException {
        CommandAPDU capdu = new CommandAPDU(0, 0x22, 0x41, 0xB6, new byte[]{(byte)0x84, 0x01, keyId});
        ResponseAPDU resp = transmit(capdu);
        if (resp.getSW()==SW_NO_ERROR) return true;
        else throw new CardException("MSE:SetAT failed! Response was: "+HexString.dump(resp.getBytes()));
    }

    private boolean sendMSE(byte keyId, byte domainParameterId) throws CardException {
        CommandAPDU capdu = new CommandAPDU(0, 0x22, 0x41, 0xB6, new byte[]{(byte)0x84, 0x01, keyId, (byte)0x80, 0x01, domainParameterId});
        ResponseAPDU resp = transmit(capdu);
        if (resp.getSW()==SW_NO_ERROR) return true;
        else throw new CardException("MSE:SetAT failed! Response was: "+HexString.dump(resp.getBytes()));
    }

    private boolean selectApplet(byte[] aid) throws CardException {
        CommandAPDU capdu = new CommandAPDU(0, 0xA4, 0x04, 0, aid);
        ResponseAPDU resp = transmit(capdu);
        if (resp.getSW()==SW_NO_ERROR) return true;
        else throw new CardException("Select Applet failed! Response was: "+HexString.dump(resp.getBytes()));
    }

    private static byte[] shortToByteArray(short sh) {
        byte[] ret = new byte[2];
        ret[0] = (byte)((sh >>> 8) & 0xff);
        ret[1] = (byte)(sh & 0xff);
        return ret;
    }

}