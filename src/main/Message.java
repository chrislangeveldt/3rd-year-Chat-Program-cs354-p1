import java.io.Serializable;

/** 
 * Object used to send messages to and from server
 */
public class Message implements Serializable {
    private String text, from, to;

    /** 
     * Constructor for broadcast
     * 
     * @param text The actual text to send
     * @param from Where it is sent from
     */
    public Message(String text, String from) { 
        this.text = text;
        this.from = from;
        this.to = null;
    }

    /** 
     * Constructor for direct message
     * 
     * @param text The actual text to send
     * @param from Where it is sent from
     * @param to Where it is sent to
     */
    public Message(String text, String from, String to) {
        this.text = text;
        this.from = from;
        this.to = to;
    }

    /** 
     * @return the text
     */
    public String text() { return text; }

    /** 
     * @return the from
     */
    public String from() { return from; }

    /** 
     * @return the to
     */
    public String to() { return to; }
}

