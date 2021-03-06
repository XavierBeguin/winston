/**
 * I waive copyright and related rights in the this work worldwide through the CC0 1.0 Universal
 * public domain dedication. https://creativecommons.org/publicdomain/zero/1.0/legalcode
 */

package gov.usgs.volcanoes.winston.server.http;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Guess a mime type given a file name.
 * 
 * @author Tom Parker
 *
 */
public class MimeType {
  private static final Logger LOGGER = LoggerFactory.getLogger(MimeType.class);
    private static final Map<String, String> mimeTypes;
    
    static {
        Map<String, String> map = new HashMap<String, String>();
        map.put("png", "image/png");
        map.put("jpg", "image/jpeg");
        map.put("jpeg", "image/jpeg");
        map.put("gif", "image/gif");
        map.put("html", "text/html");
        map.put("ico", "image/x-icon");
        map.put("css", "text/css");
        map.put("js", "application/javascript");
        mimeTypes = Collections.unmodifiableMap(map);
    }
    
    /**
     * Guess mime type
     * @param fileName filename hint
     * @return mime-type string
     */
    public static String guessMimeType(String fileName) {
      LOGGER.info("guessing mime-type for {}", fileName);
        int index = fileName.indexOf('.');
        String extension = null;
        
        if (index == -1)
            extension = fileName;
        else if (fileName.length() + 2 > index)
            extension = fileName.substring(index+1);

        return mimeTypes.get(extension);
    }
}
