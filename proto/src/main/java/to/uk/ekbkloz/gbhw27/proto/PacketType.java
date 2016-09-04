package to.uk.ekbkloz.gbhw27.proto;

/**
 * Created by Andrey on 03.09.2016.
 */
public enum PacketType {
    AUTH_REQUEST,
    AUTH_RESPONSE,
    USERS_LIST,
    ROOMS_LIST,
    MESSAGE,
    CREATE_CHATROOM,
    REMOVE_CHATROOM,
    JOIN_CHATROOM,
    LEAVE_CHATROOM;
}
