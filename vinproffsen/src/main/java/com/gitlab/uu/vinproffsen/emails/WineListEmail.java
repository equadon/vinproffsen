package com.gitlab.uu.vinproffsen.emails;

import com.gitlab.uu.email.Email;
import com.gitlab.uu.email.EmailException;
import com.gitlab.uu.vinproffsen.items.Wine;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Specialized email class that formats a list of wines.
 *
 * Only HTML emails supported for now.
 *
 * @author Niklas Persson
 * @version 2016-03-25
 */
public class WineListEmail extends Email {
    private final static Logger LOG = Logger.getLogger(WineListEmail.class.getName());

    public WineListEmail(String subject, String to, List<Wine> wines) throws EmailException {
        super(subject, to, toHtmlMessage(wines), "text/html");
    }

    private static String toHtmlMessage(List<Wine> wines) throws EmailException {
        Map<String, Object> root = new HashMap<>();
        root.put("wines", wines);

        return fromTemplate(WineListEmail.class, "/emails/", "wine_list.ftl", root);
    }
}
