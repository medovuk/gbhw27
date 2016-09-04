package to.uk.ekbkloz.gbhw27.proto;

/**
 * Created by Andrey on 03.09.2016.
 */
public class Credential extends ProtoObject {
    private static final long serialVersionUID = 910594534150357487L;
    /**
     * Login
     */
    private String login;
    /**
     * Password
     */
    private String password;

    /**
     *
     * @param login
     * @param password
     */
    public Credential(String login, String password) {
        this.login = login;
        this.password = password;
    }

    /**
     *
     * @return login
     */
    public String getLogin() {
        return login;
    }

    /**
     *
     * @return password
     */
    public String getPassword() {
        return password;
    }
}
