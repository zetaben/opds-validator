
import com.thaiopensource.util.Localizer;
import com.thaiopensource.util.OptionParser;
import com.thaiopensource.util.PropertyMapBuilder;
import com.thaiopensource.util.UriOrFile;
import com.thaiopensource.util.Version;
import com.thaiopensource.validate.Flag;
import com.thaiopensource.validate.FlagOption;
import com.thaiopensource.validate.OptionArgumentException;
import com.thaiopensource.validate.SchemaReader;
import com.thaiopensource.validate.StringOption;
import com.thaiopensource.validate.ValidateProperty;
import com.thaiopensource.validate.ValidationDriver;
import com.thaiopensource.validate.rng.CompactSchemaReader;
import com.thaiopensource.xml.sax.ErrorHandlerImpl;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.File;
import java.util.ArrayList;
import java.util.List;


class Validator {

	public static void  main(String[] args) {

		SchemaReader sr = CompactSchemaReader.getInstance();
		ErrorHandlerImpl eh = new ErrorHandlerImpl(System.out);
		PropertyMapBuilder properties = new PropertyMapBuilder();
		properties.put(ValidateProperty.ERROR_HANDLER, eh);
		boolean hadError;
		String encoding=null;
		OptionParser op = new OptionParser("v:e:", args);
		String version="1.0";

		try {
			while (op.moveToNextOption()) {
				switch (op.getOptionChar()) {
					case 'v':
						version = op.getOptionArg();
						if (!(new File("res/opds_v"+version+".rnc").exists())){
							System.err.println("invalid OPDS Version "+version);
							throw new OptionParser.InvalidOptionException();
						}

						break;
					case 'e':
						encoding = op.getOptionArg();
						break;

				}
			}	
		}
		catch (OptionParser.InvalidOptionException e) {
			eh.print("invalid_option"+ op.getOptionCharString());
			System.exit(2);
		}
		catch (OptionParser.MissingArgumentException e) {
			eh.print("option_missing_argument"+ op.getOptionCharString());
			System.exit(2);
		}
		    args = op.getRemainingArgs();



		try {
			ValidationDriver driver = new ValidationDriver(properties.toPropertyMap(), sr);
			InputSource in = ValidationDriver.uriOrFileInputSource("res/opds_v"+version+".rnc");
			if (encoding != null)
				in.setEncoding(encoding);
			if (driver.loadSchema(in)) {
				for (int i = 0; i < args.length; i++) {
					if (!driver.validate(ValidationDriver.uriOrFileInputSource(args[i])))
						hadError = true;
				}
			}
			else
				hadError = true;
		}
		catch (SAXException e) {
			hadError = true;
			eh.printException(e);
		}
		catch (IOException e) {
			hadError = true;
			eh.printException(e);
		}

	}

}
