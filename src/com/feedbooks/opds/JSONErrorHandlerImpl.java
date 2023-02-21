package com.feedbooks.opds;

import com.thaiopensource.util.UriOrFile;
import com.thaiopensource.xml.sax.ErrorHandlerImpl;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.text.MessageFormat;
import java.util.Objects;
import java.util.ResourceBundle;

public class JSONErrorHandlerImpl extends ErrorHandlerImpl {

    private final JSONArray errors;
    private ResourceBundle bundle = null;

    public JSONErrorHandlerImpl(OutputStream os) {
        super(os);
        errors = new JSONArray();
    }

    public void close() {
        String txt;
        try {
            txt = errors.toString(3);
        } catch (JSONException e) {
            txt = "Failed JSON Serialization " + e.getMessage();
        }


        super.print(txt);
    }


    public void warning(SAXParseException e) {
        printSAXParseException("warning", e);
    }

    public void error(SAXParseException e) {
        printSAXParseException("error", e);
    }

    public void fatalError(SAXParseException e) throws SAXParseException {
        throw e;
    }

    public void printException(Throwable e) {
        if (e instanceof SAXException)
            printSAXParseException("fatal", (SAXParseException) e);
        else
            print("fatal :" + formatMessage(e));
    }

    public void print(String message) {
        if (message.length() != 0) {
            errors.put(message);
        }
    }

    private String getString(String key) {
        String bundleName = "com.thaiopensource.xml.sax.resources.Messages";
        if (bundle == null)
            bundle = ResourceBundle.getBundle(bundleName);
        return bundle.getString(key);
    }

    private String format(String key, Object[] args) {
        return MessageFormat.format(getString(key), args);
    }

    private void printSAXParseException(String severity, SAXParseException e) {

        try {
            JSONObject ret = new JSONObject().put("severity", severity);

            String systemId = e.getSystemId();
            if (systemId != null) {
                ret.put("location", UriOrFile.uriToUriOrFile(systemId));
            }
            int n = e.getLineNumber();
            if (n >= 0) {
                ret.put("line", n);
            }
            n = e.getColumnNumber();
            if (n >= 0) {
                ret.put("column", n);
            }
            ret.put("message", formatMessage(e));
            errors.put(ret);

        } catch (JSONException ep) {
            print("JSON Object failed");
        }

    }

    private String formatMessage(SAXException se) {
        Exception e = se.getException();
        String detail = se.getMessage();
        if (e != null) {
            String detail2 = e.getMessage();
            // Crimson stupidity
            if (Objects.equals(detail2, detail) || e.getClass().getName().equals(detail))
                return formatMessage(e);
            else if (detail2 == null)
                return format("exception",
                        new Object[]{e.getClass().getName(), detail});
            else
                return format("tunnel_exception",
                        new Object[]{e.getClass().getName(),
                                detail,
                                detail2});
        } else {
            if (detail == null)
                detail = getString("no_detail");
            return detail;
        }
    }

    private String formatMessage(Throwable e) {
        String detail = e.getMessage();
        if (detail == null)
            detail = getString("no_detail");
        if (e instanceof FileNotFoundException)
            return format("file_not_found", new Object[]{detail});
        return format("exception",
                new Object[]{e.getClass().getName(), detail});
    }
}
