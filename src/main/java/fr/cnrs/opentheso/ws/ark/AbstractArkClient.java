package fr.cnrs.opentheso.ws.ark;
import javax.json.JsonObject;
import java.lang.reflect.Constructor;
import java.util.Properties;
import java.util.ResourceBundle;

import java.util.logging.Level;
import java.util.logging.Logger;


public abstract class AbstractArkClient {

    protected Properties propertiesArk;
    protected String idArk;
    protected String Uri;
    protected String jsonArk;
    protected JsonObject loginJson;
    protected String token;
    protected String message;

    private static String arkClientClass;

    private static final String ARK_CLIENT_CLASS_PROPERTY = "arkClientClass";
    private static final String DEFAULT_ARK_CLIENT_CLASS = ArkClientRest.class.toString();


    static {
        try {
            ResourceBundle rd
                    = ResourceBundle.getBundle("preferences");
            if (rd.containsKey(ARK_CLIENT_CLASS_PROPERTY))
                arkClientClass = rd.getString("arkClientClass");
            else
                arkClientClass = DEFAULT_ARK_CLIENT_CLASS;
            Logger.getLogger(AbstractArkClient.class.getName()).log(Level.INFO, "ARK CLIENT CLASS = " + arkClientClass);
        } catch (Exception e) {
            arkClientClass = DEFAULT_ARK_CLIENT_CLASS;
        }

    }


    public AbstractArkClient() {
        this.propertiesArk = null;
    }

    public AbstractArkClient(Properties props) {
        this.propertiesArk = props;
    }

    public void setPropertiesArk(Properties propertiesArk) {
        this.propertiesArk = propertiesArk;
    }

    public abstract boolean login();

    public abstract boolean getArk(String ark);

    public abstract boolean addArk(String arkString);

    public abstract boolean isArkExist(String ark);

    public abstract boolean updateArk(String jsonString);

    public abstract boolean updateUriArk(String jsonString);

    public String getIdArk() {
        return idArk;
    }

    public String getUri() {
        return Uri;
    }

    public String getMessage() {
        return message;
    }

    public JsonObject getLoginJson() {
        return loginJson;
    }

    public void setLoginJson(JsonObject loginJson) {
        this.loginJson = loginJson;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }


    //@todo: to externalize ?
    public abstract boolean deleteHandle(String s);

    public abstract boolean isHandleExist(String idHandle);

    public abstract String getIdHandle();


    public static class ArkClientFactory {

        /**
         * ArkClient Factory. Returns first AbstractArkClient subclass (different than the default one = ArkClientRest) if exists.
         * The default one (ArkClientRest) is instanciated and returned if such client does not exist.
         *
         * @return
         */
        public static AbstractArkClient buildArkClient() {
            try {

                Class clientClass = Class.forName(arkClientClass);
                Constructor[] ctors = clientClass.getDeclaredConstructors();
                Constructor<AbstractArkClient> ctor = null;
                for (int i = 0; i < ctors.length; i++) {
                    ctor = ctors[i];
                    if (ctor.getGenericParameterTypes().length == 0)
                        break;
                }
                AbstractArkClient clientInstance = ctor.newInstance();
                Logger.getLogger(AbstractArkClient.class.getName()).log(Level.SEVERE, "clientInstance - " + clientInstance.getClass().getName());
                return clientInstance;
            } catch (Exception e) {
                 Logger.getLogger(AbstractArkClient.class.getName()).log(Level.SEVERE, e.getMessage());
                return new ArkClientRest();
            }
        }
    }


}
