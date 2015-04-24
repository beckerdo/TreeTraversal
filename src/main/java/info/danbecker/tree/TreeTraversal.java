package info.danbecker.tree;

import java.io.IOException;
import java.util.Map;
import java.util.AbstractMap;
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
 * An app to demonstrate sparse tree traversal and costs.
 * Users get to set tree size, node sizes (empty, just key, and data),
 * fill percentage, and number of accesses.
 * The program then spits out some sizes and benchmarks.
 * <p>
 * For demonstration purposes, this app uses a binary tree, but
 * can be updated to work with N trees.
 * <p>
 * Implementation details are contained below.
 * <p>
 * @author <a href="mailto://dan@danbecker.info>Dan Becker</a>
 */
public class TreeTraversal {
	// options
	public static boolean testMode = false;
	public static boolean verbose = true;
	public static boolean debug = false;
	
    protected static Properties properties;
	protected static List<String> nodes; 

    protected static Random random = new Random();
    
    public static int numKeys;
    public static int numNodes;
    public static int popNodes;
    public static int nodeReadCost;
    public static int nodeDataSize;
    public static int nodeKeySize;
    public static int nodeEmptySize;
    public static int treeFillPercent;
    public static int numAccesses;

    public static final String DATA = "DATA";
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
		// if (verbose) {
	    //    long elapsedTime = System.currentTimeMillis() - startTime;
	    //    System.out.println( "application elapsed time=" + format( elapsedTime ));       
		// }
	}

	/** 
	 * This method reads properties, builds an implementation, and runs tests. 
	 * The implementation builds an array-list to contain a binary tree.
	 * The top of the tree is at array position 0.
	 * The second level of the tree is at array positions 1, 2,
	 * The third level of the tree is at array positions 3, 4, 5, 6.
	 * In other words, each level contains 2^level items.
	 */
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
		numNodes = 0;
		for ( int level = 0; level < numKeys; level++ ) {
			numNodes += (int) Math.pow( 2.0,  numKeys ); // 2^0 + 2^1 + ... + 2^keys 
		}
		System.out.println( "Tree number nodes=" + numNodes  );
		System.out.println( "Tree empty size=" + numNodes * nodeEmptySize );
		popNodes = numNodes * treeFillPercent / 100;
		System.out.println( "Tree populated nodes=" + popNodes );
		System.out.println( "Tree populated node size (popNodes*nodeDataSize)=" + popNodes * nodeDataSize );
		
		nodes = new ArrayList<String>( numNodes ); 

		System.out.println();
		sparseTreeTest();
		
		System.out.println();
		refTreeTest();
		
	}

	public static void sparseTreeTest() {
		System.out.println( "Sparse Tree values:");
		System.out.println( "Sparse tree empty node size (emptyNodes*emptyNodeSize)=" + (numNodes - popNodes) * nodeEmptySize );
		System.out.println( "Sparse tree total node size (popSize + emptySize)=" + (numNodes - popNodes) * nodeEmptySize + (popNodes * nodeDataSize) );
		
		// Empty tree
		nodes.clear();
		nodes.add( DATA ); // Always populate default node.
		for( int i = 0; i < numNodes - 1; i++ ) {
			nodes.add( "" );
		}

		// Populate random nodes. Ensure no collisions. May take a while for full trees.
		int placedData = popNodes;
		while ( placedData > 0) {
			int randomIndex = random.nextInt( numNodes );
			if ( !DATA.equals( nodes.get( randomIndex ) )) {
				nodes.set( randomIndex, DATA );
				placedData--;
			}
		}

		int numReads = 0;
		// Generate numAccesses random accesses and count reads.
		int leafPositions = (int) Math.pow( 2.0, numKeys - 1 );
		for ( int access = 0; access < numAccesses; access++ ) {
			// For each key (numKeys), check the leaf node for data. No data, access parent
			int pos = random.nextInt( leafPositions );
			int level = numKeys - 1;
			int index = levelPositionToIndex( level, pos );
			numReads += 1;
			while ( (level > 0 ) && !DATA.equals( nodes.get( index ))) {
				level -= 1;
				pos /= 2;
				index = levelPositionToIndex( level, pos );
				numReads += 1;
			}
		}
		System.out.println( "Sparse tree populated nodes=" + popNodes + "/" + numNodes + "(" + treeFillPercent + "%), data accesses=" + numAccesses + ", num reads=" + numReads + ", avgAccessCount="  + Double.toString( ((double)numReads)/((double)numAccesses) ));
		

	}

	public static void refTreeTest() {
		System.out.println( "Reference Tree values:");
		System.out.println( "Ref tree key node size (keyNodes*keyNodeSize)=" + (numNodes - popNodes) * nodeKeySize );
		System.out.println( "Ref tree total node size (popSize + keySize)=" + (numNodes - popNodes) * nodeKeySize + (popNodes * nodeDataSize) );
		System.out.println( "Ref tree populated nodes=" + popNodes + "/" + numNodes + "(" + treeFillPercent + "%), data accesses=" + numAccesses + ", num reads=" + 1.5*numAccesses + ", avgAccessCount="  + Double.toString( 1.5 ));
		
	}
	
	/** Given an index into a list, returns a pair that has level and element number in that level.
	 * By using Map.Entry, the key contains the level number, the value contains the element position on that level.
	 * This assumes a binary tree.
	 * The first level (level 0) contains item 0.
	 * The second level (level 1) contains items 1 and 2.
	 * The third level (level 2) contains items 3,4, 5,6.
	 */
	public static Map.Entry<Integer,Integer> indexToLevelPosition( int index ) {
		if ( index < 0 ) return null;
		int nodeCount = 0;
		for ( int level = 0; level < numKeys; level++ ) {
            int nodesThisLevel = (int) Math.pow( 2.0, level );
			if ( nodeCount + nodesThisLevel  > index ) {
				return new AbstractMap.SimpleImmutableEntry<Integer,Integer>( level, index - nodeCount  );
			}
			nodeCount += nodesThisLevel; 
		}
		return null;
	}

	/** Given an index into a list, returns a pair that has level and element number in that level.
	 * By using Map.Entry, the key contains the level number, the value contains the element number.
	 * This assumes a binary tree.
	 * The first level (level 0) contains item 0.
	 * The second level (level 1) contains items 1 and 2.
	 * The third level (level 2) contains items 3,4, 5,6.
	 */
	public static int levelPositionToIndex( int level, int position ) {
		if ( level < 0 ) return -1;
		if (( position < 0 ) || (position >= (int) Math.pow( 2.0, level ))) return -1;

		int index = 0;
		for ( int i = 0; i < level; i++ ) {
            index += (int) Math.pow( 2.0, i );
		}
		return index + position;
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