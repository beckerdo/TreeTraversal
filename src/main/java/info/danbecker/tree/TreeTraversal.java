package info.danbecker.tree;

import java.io.IOException;
import java.util.Properties;
import java.util.Random;
import java.util.List;
import java.util.ArrayList;
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

    protected static Random random = new Random();
    
    public static int numKeys;
    public static int nodeReadCost;
    public static int nodeDataSize;
    public static int nodeKeySize;
    public static int nodeEmptySize;
    public static int treeFillPercent;
    public static int numAccesses;

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
		numKeys = Integer.parseInt( properties.getProperty( "number_keys"));
	    nodeReadCost = Integer.parseInt( properties.getProperty( "node_read_cost"));
	    nodeDataSize = Integer.parseInt( properties.getProperty( "node_data_size"));
	    nodeKeySize = Integer.parseInt( properties.getProperty( "node_key_size"));
	    nodeEmptySize = Integer.parseInt( properties.getProperty( "node_empty_size"));
	    treeFillPercent = Integer.parseInt( properties.getProperty( "tree_fill_percent"));
	    numAccesses = Integer.parseInt( properties.getProperty( "number_accesses"));

	    System.out.println( "Application inputs:");
		System.out.println( "Number or keys=" + numKeys );
		System.out.println( "Node read cost=" + nodeReadCost );
		System.out.println( "Node empty size=" + nodeEmptySize );
		System.out.println( "Node data size=" + nodeDataSize );
		System.out.println( "Node key size=" + nodeKeySize );
		System.out.println( "Tree fill percentage=" + treeFillPercent );
		System.out.println( "Number of access=" + numAccesses );

		// For this demo, every node will have a key (String) or node data (no string)
		System.out.println();
		System.out.println( "Calculated values:");
		int numNodes = 0;
		for ( int level = 0; level < numKeys; level++ ) {
			numNodes += (int) Math.pow( 2.0,  numKeys ); // 2^0 + 2^1 + ... + 2^keys 
		}
		System.out.println( "Tree number nodes=" + numNodes  );
		System.out.println( "Tree empty size=" + numNodes * nodeEmptySize );
		int popNodes = numNodes * treeFillPercent / 100;
		System.out.println( "Tree populated nodes=" + popNodes );
		
		// For each key, a * represents a  level number. 
		// A key name represents a 1.
		// So, for example, key1..key4, there can be 
		List<String> nodes = new ArrayList<String>( numNodes ); 
		
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