package info.danbecker.tree;

import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNull;

import java.util.Map;

public class TreeTraversalTest {
	
	@Test
    public void testLevelPosition() {		
		TreeTraversal.numKeys = 4;
		
		assertNull( "index", TreeTraversal.indexToLevelPosition( -20 )  );
		
		Map.Entry<Integer,Integer> info = TreeTraversal.indexToLevelPosition( 0 );
		assertTrue( "level 0,0", 0 == info.getKey() );
		assertTrue( "level 0,0", 0 == info.getValue() );

		info = TreeTraversal.indexToLevelPosition( 1 );
		assertTrue( "level 1,0", 1 == info.getKey() );
		assertTrue( "level 1,0", 0 == info.getValue() );

		info = TreeTraversal.indexToLevelPosition( 2 );
		assertTrue( "level 1,1", 1 == info.getKey() );
		assertTrue( "level 1,1", 1 == info.getValue() );

		info = TreeTraversal.indexToLevelPosition( 3 );
		assertTrue( "level 2,0", 2 == info.getKey() );
		assertTrue( "level 2,0", 0 == info.getValue() );

		info = TreeTraversal.indexToLevelPosition( 4 );
		assertTrue( "level 2,1", 2 == info.getKey() );
		assertTrue( "level 2,1", 1 == info.getValue() );

		info = TreeTraversal.indexToLevelPosition( 5 );
		assertTrue( "level 2,2", 2 == info.getKey() );
		assertTrue( "level 2,2", 2 == info.getValue() );

		info = TreeTraversal.indexToLevelPosition( 6 );
		assertTrue( "level 2,3", 2 == info.getKey() );
		assertTrue( "level 2,3", 3 == info.getValue() );

		info = TreeTraversal.indexToLevelPosition( 7 );
		assertTrue( "level 3,0", 3 == info.getKey() );
		assertTrue( "level 3,0", 0 == info.getValue() );

		info = TreeTraversal.indexToLevelPosition( 14 );
		assertTrue( "level 3,0", 3 == info.getKey() );
		assertTrue( "level 3,0", 7 == info.getValue() );	
	}
	
	@Test
    public void testIndex() {		
		TreeTraversal.numKeys = 4;
		
		assertTrue( "negative", -1 == TreeTraversal.levelPositionToIndex( -1, 0 )  );
		assertTrue( "negative", -1 == TreeTraversal.levelPositionToIndex( 0, 1 )  );
		assertTrue( "negative", -1 == TreeTraversal.levelPositionToIndex( 1, 2 )  );

		assertTrue( "index", 0 == TreeTraversal.levelPositionToIndex( 0, 0 )  );
		assertTrue( "index", 1 == TreeTraversal.levelPositionToIndex( 1, 0 )  );
		assertTrue( "index", 2 == TreeTraversal.levelPositionToIndex( 1, 1 )  );
		assertTrue( "index", 3 == TreeTraversal.levelPositionToIndex( 2, 0 )  );
		assertTrue( "index", 4 == TreeTraversal.levelPositionToIndex( 2, 1 )  );
		assertTrue( "index", 5 == TreeTraversal.levelPositionToIndex( 2, 2 )  );
		assertTrue( "index", 6 == TreeTraversal.levelPositionToIndex( 2, 3 )  );
		assertTrue( "index", 7 == TreeTraversal.levelPositionToIndex( 3, 0 )  );
		assertTrue( "index", 14 == TreeTraversal.levelPositionToIndex( 3, 7 )  );
	}
}