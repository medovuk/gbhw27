package to.uk.ekbkloz.gbhw27.proto;

/**
 * Created by Andrey on 03.09.2016.
 */
public class Message extends ProtoObject {
    private static final long serialVersionUID = 2059988857025723251L;
    private String room;
    private String from;
    private String to;
    private String text;

    /**
     *
     * @param room
     * @param from
     * @param to
     * @param text
     */
    public Message(String room, String from, String to, String text) {
        this.room = room;
        this.from = from;
        this.to = to;
        this.text = text;
    }

    public String getRoom() {
        return room;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public String getText() {
        return text;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return "Message{" +
                "room='" + room + '\'' +
                ", from='" + from + '\'' +
                ", to='" + to + '\'' +
                ", text='" + text + '\'' +
                '}';
    }
}
