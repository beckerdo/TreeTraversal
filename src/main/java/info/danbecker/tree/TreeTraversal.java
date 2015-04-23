package info.danbecker.tree;

import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An app to demonstrate tree traversal and costs. 
 * @author <a href="mailto://dan@danbecker.info>Dan Becker</a>
 */
public class TreeTraversal {
	// options
	public static boolean testMode = false;
	public static boolean verbose = true;
	public static boolean debug = false;
	
    protected static Properties properties;

	protected static Logger logger = LoggerFactory.getLogger(TreeTraversal.class);
    
    static {
       loadProperties();	
    }

    /** Loads country and currency names and codes from property files. */
    public static void loadProperties() {
        properties = new Properties();
        try {
        	String name = "app.properties";
            properties.load(TreeTraversal.class.getClassLoader().getResourceAsStream( name ));
        	logger.info( "Loaded properties from " + name + ".");;
        } catch (IOException e){
            e.printStackTrace();
        }
    }    
	/** Commmand line version of this application. */
	public static void main(String[] args) throws Exception {
	    System.out.println( "TreeTraversal 1.0 by Dan Becker" );
	    long startTime = System.currentTimeMillis();
	    
	    // Parse the command line arguments
		Options cliOptions = createOptions();
		CommandLineParser cliParser = new BasicParser();
	    CommandLine line = cliParser.parse( cliOptions, args );

	    // Gather command line arguments for execution
	    if( line.hasOption( "help" ) ) {
	    	HelpFormatter formatter = new HelpFormatter();
	    	formatter.printHelp( "java -jar TreeTraversal.jar <options> info.danbecker.tree TreeTraversal", cliOptions );
	    	System.exit( 0 );
	    }
	    if( line.hasOption( "verbose" ) ) {
	    	verbose = true;	
	    	System.out.println( "   running in verbose mode");
	    }	    
	    if( line.hasOption( "debug" ) ) {
	    	debug = true;	
	    	System.out.println( "   running in debug mode");
	    }	    
	    if( line.hasOption( "test" ) ) {
	    	testMode = true;	
	    	// this.fileName = line.getOptionValue( "fileName" );
	    	if ( verbose )
	    		System.out.println( "   running in test mode");
	    }
	    
	    execute();
	    
		// conclude and end
		if (verbose) {
	       long elapsedTime = System.currentTimeMillis() - startTime;
	       System.out.println( "application elapsed time=" + format( elapsedTime ));       
		}
	}

	public static void execute() {
		System.out.println( "Key numbers=" + properties.getProperty( "number_keys"));
		System.out.println( "Node read cost=" + properties.getProperty( "node_read_cost"));
		System.out.println( "Node data size=" + properties.getProperty( "node_data_size"));
		System.out.println( "Node key size=" + properties.getProperty( "node_key_size"));
	}
	
	// For example, to convert 10 minutes to milliseconds, use: TimeUnit.MILLISECONDS.convert(10L, TimeUnit.MINUTES)
    public static String format(long durationMillis) {
        if (durationMillis == 0) return "00:00:00,000";
        long hours = TimeUnit.HOURS.convert( durationMillis, TimeUnit.MILLISECONDS );
        long mins = TimeUnit.MINUTES.convert( durationMillis, TimeUnit.MILLISECONDS );
        long secs = TimeUnit.SECONDS.convert( durationMillis, TimeUnit.MILLISECONDS );
        long millis = TimeUnit.MILLISECONDS.convert( durationMillis % 1000L, TimeUnit.MILLISECONDS );
        return String.format("%02d:%02d:%02d.%03d", hours, mins, secs, millis);        
    }
    
	/** Command line options for this application. */
	public static Options createOptions() {
		// create the Options
		Options options = new Options();
		options.addOption( "h", "help", false, "print the command line options." );
		options.addOption( "t", "test", false, "do not perform actions, just list what would happen." );
		options.addOption( "v", "verbose", false, "prints many more messages to the console than normal." );
		options.addOption( "d", "debug", false, "prints many more messages to the console than verbose." );
		return options;
	}

}