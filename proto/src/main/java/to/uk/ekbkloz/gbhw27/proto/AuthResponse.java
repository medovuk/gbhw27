package to.uk.ekbkloz.gbhw27.proto;

import to.uk.ekbkloz.gbhw27.proto.exceptions.AuthException;

/**
 * Created by Andrey on 03.09.2016.
 */
public class AuthResponse extends ProtoObject {
    private static final long serialVersionUID = 4176626663161202305L;
    private boolean succeeded;
    private String nickname;
    private String roomName;
    private AuthException exception;

    public AuthResponse(boolean succeeded, String nickname, String roomName, AuthException exception) {
        this.succeeded = succeeded;
        this.nickname = nickname;
        this.roomName = roomName;
        this.exception = exception;
    }

    public boolean isSucceeded() {
        return succeeded;
    }

    public String getNickname() {
        return nickname;
    }

    public String getRoomName() {
        return roomName;
    }

    public AuthException getException() {
        return exception;
    }
}
