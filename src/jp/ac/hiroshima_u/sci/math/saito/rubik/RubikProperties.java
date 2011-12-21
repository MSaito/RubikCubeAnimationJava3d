package jp.ac.hiroshima_u.sci.math.saito.rubik;

import java.io.IOException;
import java.net.URL;
import java.util.Properties;
import java.util.logging.Logger;

public class RubikProperties extends Properties {
    private static final Logger logger = Logger.getLogger(RubikProperties.class.getCanonicalName());
    private static final long serialVersionUID = 1L;
    private static RubikProperties me;
    private RubikProperties() {
    }
    
    private RubikProperties(String fileName) throws IOException  {
        URL url = RubikProperties.class.getClassLoader().getResource(fileName);
        load(url.openStream());
    }   
    
    private static synchronized void setMe() {
        if (me == null) {
            try {
                me = new RubikProperties("rubik.properties");
                logger.info("properties read success");
            } catch (IOException e) {
                me = new RubikProperties();
                me.setProperty("speed", "1000");
                me.setProperty("maxCounter", "500");
                logger.info("properties fail, use default settings");
            }
        }
    }
    
    public static String get(String key) {
        setMe();
        return me.getProperty(key);
    }

    public static int getInt(String key) {
        setMe();
        String value = me.getProperty(key);
        int x = Integer.valueOf(value);
        return x;
    }
    
    public static URL getURL(String fileName) {
        setMe();
        return RubikProperties.class.getClassLoader().getResource(fileName);
    }
}
