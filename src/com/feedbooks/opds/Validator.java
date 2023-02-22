package com.feedbooks.opds;

import com.thaiopensource.util.OptionParser;
import com.thaiopensource.util.PropertyMapBuilder;
import com.thaiopensource.validate.SchemaReader;
import com.thaiopensource.validate.ValidateProperty;
import com.thaiopensource.validate.ValidationDriver;
import com.thaiopensource.validate.rng.CompactSchemaReader;
import com.thaiopensource.xml.sax.ErrorHandlerImpl;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.*;


public class Validator {

    private String opds_version = "1.2";
    private String encoding;
    private ErrorHandlerImpl eh;

    public static void main(String[] args) {

        String encoding = null;
        OptionParser op = new OptionParser("hv:e:f:", args);
        String opds_version = "1.1";
        ErrorHandlerImpl eh = new ErrorHandlerImpl(System.out);

        try {
            while (op.moveToNextOption()) {
                switch (op.getOptionChar()) {
                    case 'f' -> {
                        String fmt = op.getOptionArg();
                        if (fmt.equalsIgnoreCase("json")) {
                            eh = new JSONErrorHandlerImpl(System.out);
                        }
                    }
                    case 'v' -> {
                        opds_version = op.getOptionArg();
                        if (!(new File("res/opds_v" + opds_version + ".rnc").exists())) {
                            System.err.println("invalid OPDS Version " + opds_version);
                            throw new OptionParser.InvalidOptionException();
                        }
                    }
                    case 'e' -> encoding = op.getOptionArg();
                    case 'h' -> {
                        eh.print("OPDSValidator usage: java -jar OPDSValidator [options] file");
                        eh.print("Options:");
                        eh.print("-h\t\tThis help message");
                        eh.print("-v opds_version\tOPDSVersion to use (default 1.2)");
                        eh.print("-e encoding\tFile encoding (passed to jing)");
                        eh.print("-f format\tError output format (default text, avail : json)");
                    }
                }
            }
        } catch (OptionParser.InvalidOptionException e) {
            eh.print("invalid_option" + op.getOptionCharString());
            System.exit(2);
        } catch (OptionParser.MissingArgumentException e) {
            eh.print("option_missing_argument" + op.getOptionCharString());
            System.exit(2);
        }
        args = op.getRemainingArgs();


        Validator v = new Validator();
        if (encoding != null) {
            v.SetEncoding(encoding);
        }
        v.SetOPDSVersion(opds_version);
        v.SetErrorHandler(eh);
        if (args.length != 0) {
            v.validate(args);
        } else {
            Reader reader = new InputStreamReader(System.in);
            final char[] buffer = new char[0x1000];
            StringBuilder out = new StringBuilder();
            int read = 0;
            try {
                while (read >= 0) {
                    read = reader.read(buffer, 0, buffer.length);
                    if (read > 0) {
                        out.append(buffer, 0, read);
                    }
                }
                reader.close();
            } catch (IOException e) {
                e.printStackTrace(System.err);
            }
            String[] names = {"stdin://"};
            String[] in = {out.toString()};
            v.validate(in, names);
        }
        eh.close();
    }

    public void SetErrorHandler(ErrorHandlerImpl er) {
        this.eh = er;
    }

    public void SetEncoding(String e) {
        this.encoding = e;
    }

    public void SetOPDSVersion(String e) {
        this.opds_version = e;
    }

    private void validateByDirectOrFilenames(String[] args, String[] names) {
        SchemaReader sr = CompactSchemaReader.getInstance();
        PropertyMapBuilder properties = new PropertyMapBuilder();
        properties.put(ValidateProperty.ERROR_HANDLER, eh);

        OPDSRequirementsValidator opds_val = new OPDSRequirementsValidator();
        opds_val.setErrorHandler(eh);
        opds_val.setOPDSVersion(opds_version);


        try {
            ValidationDriver driver = new ValidationDriver(properties.toPropertyMap(), sr);
            InputSource in = ValidationDriver.uriOrFileInputSource("res/opds_v" + opds_version + ".rnc");
            if (encoding != null)
                in.setEncoding(encoding);
            if (driver.loadSchema(in)) {
                for (int i = 0; i < args.length; i++) {
                    InputSource source1, source2;
                    if (names.length == 0) {
                        source2 = source1 = ValidationDriver.uriOrFileInputSource(args[i]);
                    } else {
                        source1 = new InputSource(new StringReader(args[i]));
                        source2 = new InputSource(new StringReader(args[i]));
                        source1.setSystemId(names[i]);
                        source2.setSystemId(names[i]);
                        source1.setPublicId(names[i]);
                        source2.setPublicId(names[i]);
                    }
                    if (driver.validate(source1)) {
                        opds_val.validate(source2);
                    }
                }
            }
        } catch (SAXException | IOException e) {
            eh.printException(e);
        }
    }

    /**
     * Validate an OPDS Stream
     *
     * @params args Files to be validated
     */
    public void validate(String[] args) {
        validateByDirectOrFilenames(args, new String[0]);
    }

    /**
     * Validate an OPDS Stream by direct input
     *
     * @params args content to be validated
     * @params names content idendifier (i.e. filenames )
     */
    public void validate(String[] args, String[] names) {
        validateByDirectOrFilenames(args, names);
    }

}
