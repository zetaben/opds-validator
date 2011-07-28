
import java.util.ResourceBundle;
import java.text.MessageFormat;
import java.io.PrintWriter;
import java.io.Writer;
import java.io.OutputStream;
import java.io.FileNotFoundException;
import com.thaiopensource.xml.sax.ErrorHandlerImpl;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXParseException;
import org.xml.sax.SAXException;
import com.thaiopensource.util.UriOrFile;

import org.json.*;

public class JSONErrorHandlerImpl extends ErrorHandlerImpl {
  private final String bundleName
    = "com.thaiopensource.xml.sax.resources.Messages";

  private ResourceBundle bundle = null;
  private JSONArray errors;

  public JSONErrorHandlerImpl() {
    this(System.err);
  }

  public JSONErrorHandlerImpl(OutputStream os) {
    super(os);
    errors=new JSONArray();
  }

  public JSONErrorHandlerImpl(Writer w) {
    super(w);
    errors=new JSONArray();
  }

  public void close() {
  String txt="";
  try {
  txt=errors.toString(3);
  }catch(JSONException e){
  txt="Failed JSON Serialization "+e.getMessage();
  }


  super.print(txt); 	
  }


  public void warning(SAXParseException e) throws SAXParseException {
    print(format("warning",
		 new Object[] { formatMessage(e), formatLocation(e) }));
  }

  public void error(SAXParseException e) {
    print(format("error",
		 new Object[] { formatMessage(e), formatLocation(e) }));
  }

  public void fatalError(SAXParseException e) throws SAXParseException {
    throw e;
  }

  public void printException(Throwable e) {
    String loc;
    if (e instanceof SAXParseException)
      loc = formatLocation((SAXParseException)e);
    else
      loc = "";
    String message;
    if (e instanceof SAXException)
      message = formatMessage((SAXException)e);
    else
      message = formatMessage(e);
    print(format("fatal", new Object[] { message, loc }));
  }

  public void print(String message) {
    if (message.length() != 0) {
      errors.put(message);
    }
  }

  private String getString(String key) {
    if (bundle == null)
      bundle = ResourceBundle.getBundle(bundleName);
    return bundle.getString(key);
  }

  private String format(String key, Object[] args) {
    return MessageFormat.format(getString(key), args);
  }
  private String formatLocation(SAXParseException e) {
    String systemId = e.getSystemId();
    int n = e.getLineNumber();
    Integer lineNumber = n >= 0 ? new Integer(n) : null;
    n = e.getColumnNumber();
    Integer columnNumber = n >= 0 ? new Integer(n) : null;
    if (systemId != null) {
      systemId = UriOrFile.uriToUriOrFile(systemId);
      if (lineNumber != null) {
	if (columnNumber != null)
	  return format("locator_system_id_line_number_column_number",
			new Object[] { systemId, lineNumber, columnNumber });
	else
	  return format("locator_system_id_line_number",
			new Object[] { systemId, lineNumber });
      }
      else
	return format("locator_system_id",
		      new Object[] { systemId });
    }
    else if (lineNumber != null) {
      if (columnNumber != null)
	return format("locator_line_number_column_number",
		      new Object[] { lineNumber, columnNumber });
      else
	return format("locator_line_number",
		      new Object[] { lineNumber });
    }
    else
      return "";
  }

  private String formatMessage(SAXException se) {
    Exception e = se.getException();
    String detail = se.getMessage();
    if (e != null) {
      String detail2 = e.getMessage();
      // Crimson stupidity
      if (detail2 == detail || e.getClass().getName().equals(detail))
	return formatMessage(e);
      else if (detail2 == null)
	return format("exception",
		      new Object[]{ e.getClass().getName(), detail });
      else
	return format("tunnel_exception",
		      new Object[] { e.getClass().getName(),
				     detail,
				     detail2 });
    }
    else {
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
      return format("file_not_found", new Object[] { detail });
    return format("exception",
		  new Object[] { e.getClass().getName(), detail });
  }
}
