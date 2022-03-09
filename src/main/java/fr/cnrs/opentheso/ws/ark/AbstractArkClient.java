package fr.cnrs.opentheso.ws.ark;

import org.reflections.Reflections;

import javax.json.JsonObject;
import java.lang.reflect.Constructor;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class AbstractArkClient {

    public static Class DEFAULT_CLIENT_CLASS = ArkClientRest.class;
    protected Properties propertiesArk;
    protected String idArk;
//    private String idHandle;
    protected String Uri;
//
//    // prefix MOM
//    private String prefixHandle = "20.500.11859";

//    private String urlHandle = "http://193.48.137.68:8000/api/handles/";
    protected String jsonArk;

    protected JsonObject loginJson;

    protected String token;

    protected String message;


    public AbstractArkClient(){
        this.propertiesArk = null;
    }

    public AbstractArkClient(Properties props){
        this.propertiesArk = props;
    }


    public void setPropertiesArk(Properties propertiesArk) {
        this.propertiesArk = propertiesArk;
    }

    public abstract boolean login();

    //protected abstract void getTokenFromString(String tokenString);

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


    //@todo: à externaliser
    public abstract boolean deleteHandle(String s);
    public abstract boolean isHandleExist(String idHandle);
    public abstract String getIdHandle();


    public static class ArkClientFactory {

        /**
         * ArkClient Factory. Returns first AbstractArkClient subclass (different than the default one = ArkClientRest) if exists.
         * The default one (ArkClientRest) is instanciated and returned if such client does not exist.
         * @return
         */
        public static AbstractArkClient buildArkClient(){
            Reflections reflections = new Reflections("fr.cnrs.opentheso.ws.ark");
            Set<Class<? extends AbstractArkClient>> allClasses = reflections.getSubTypesOf(AbstractArkClient.class);
            Set<Class<? extends AbstractArkClient>> filteredClasses =
                    allClasses.stream().filter(client -> !client.getClass().equals(DEFAULT_CLIENT_CLASS)).collect(Collectors.toSet());
            Class clientClass = filteredClasses.toArray(new Class[0])[0];
            Constructor[] ctors  = clientClass.getDeclaredConstructors();
            Constructor<AbstractArkClient> ctor = null;
            for (int i = 0; i < ctors.length; i++) {
                ctor = ctors[i];
                if (ctor.getGenericParameterTypes().length == 0)
                    break;
            }
            try {
                AbstractArkClient clientInstance = ctor.newInstance();
                return clientInstance;
            }
            catch(Exception e){
                return new ArkClientRest();
            }
        }


        //    /**
//     * Gets Available Ark client Types list
//     * @return
//     */
//    public List<AbstractArkClient.ArkServerType> getArkServerClasses(){
//        if(this.arkServerClasses == null){
//            arkServerClasses = new ArrayList<>();
//            Reflections reflections = new Reflections("fr.cnrs.opentheso.ws.ark");
//            Set<Class<? extends AbstractArkClient>> allClasses =
//                    reflections.getSubTypesOf(AbstractArkClient.class);
//            for(Class c : allClasses){
//                this.arkServerClasses.add(new AbstractArkClient.ArkServerType(c));
//            }
//            this.arkServerClasses.sort((a, b) ->
//                    a.getServerClientClass().equals(AbstractArkClient.ArkServerType.DEFAULT_TYPE) ? -1 : 1);
//        }
//        return this.arkServerClasses;
//    }
    }


//    /**
//     * Ark server client wrapper
//     */
//    public static class ArkServerType{
//        public static Class DEFAULT_TYPE = ArkClientRest.class;
//        private Class serverClientClass;
//        private String prettyString;
//
//        public ArkServerType(Class c){
//            this.serverClientClass = c;
//            this.prettyString = this.findPrettyString();
//        }
//
//        public String toString(){
//            return this.prettyString;
//        }
//
//        public Class getServerClientClass(){
//            return this.serverClientClass;
//        }
//
//        private String findPrettyString(){
//            Constructor[] ctors  = this.serverClientClass.getDeclaredConstructors();
//            Constructor ctor = null;
//            for (int i = 0; i < ctors.length; i++) {
//                ctor = ctors[i];
//                if (ctor.getGenericParameterTypes().length == 0)
//                    break;
//            }
//            try {
//                Object o = ctor.newInstance();
//                return o.toString();
//            }
//            catch(Exception e){
//                return null;
//            }
//        }
//    }
}
