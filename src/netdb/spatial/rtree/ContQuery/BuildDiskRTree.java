package netdb.spatial.rtree.ContQuery;
// NOTE: Please read README.txt before browsing this code.

import java.io.*;
import java.util.*;

import netdb.spatial.rtree.rtree.*;
import netdb.spatial.rtree.spatialindex.*;
import netdb.spatial.rtree.storagemanger.*;


public class BuildDiskRTree
{
	String inputfile = null;
	
	public static void main(String[] args)
	{
		new BuildDiskRTree(args);
	}
	
	BuildDiskRTree(String[] args)
	{
		try
		{
			BufferedReader reader = null;

			inputfile = "data/TainanPOI_38665.txt";
			
			try
			{
				reader = new BufferedReader(new FileReader( inputfile));
			}
			catch (FileNotFoundException e)
			{
				System.err.println("Cannot open data file "+inputfile+".");
				System.exit(-1);
			}

			// Create a disk based storage manager.
			PropertySet ps = new PropertySet();

			Boolean b = new Boolean(true);
			ps.setProperty("Overwrite", b);
				//overwrite the file if it exists.

			ps.setProperty("FileName", inputfile);
				// .idx and .dat extensions will be added.

			Integer i = new Integer(4096);
			ps.setProperty("PageSize", i);
				// specify the page size. Since the index may also contain user defined data
				// there is no way to know how big a single node may become. The storage manager
				// will use multiple pages per node if needed. Off course this will slow down performance.

			IStorageManager diskfile = new DiskStorageManager(ps);

			IBuffer file = new RandomEvictionsBuffer(diskfile, 32, false);
				// applies a main memory random buffer on top of the persistent storage manager
				// (LRU buffer, etc can be created the same way).

			// Create a new, empty, RTree with dimensionality 2, minimum load 70%, using "file" as
			// the StorageManager and the RSTAR splitting policy.
			PropertySet ps2 = new PropertySet();

			Double f = new Double(0.7);
			ps2.setProperty("FillFactor", f);

			
			ps2.setProperty("IndexCapacity", new Integer(32));
			ps2.setProperty("LeafCapacity", new Integer(32));
				// Index capacity and leaf capacity may be different.

			i = new Integer(2);
			ps2.setProperty("Dimension", i);

			ISpatialIndex tree = new RTree(ps2, file);

			int count = 0;
			int indexIO = 0;
			int leafIO = 0;
			int id, op;
			double x1, x2, y1, y2;
			double[] f1 = new double[2];
			double[] f2 = new double[2];

			long start = System.currentTimeMillis();
			String line = reader.readLine();
			int  nid=0;
			while (line != null)
			{
				String[] poi = line.split(":");
				String[] position = poi[0].split(",");
				
				line = reader.readLine();
				if(poi.length != 2)  continue;
				
				//insert (x,y)
				Point p = new Point(new double[]{Double.parseDouble(position[0]),Double.parseDouble(position[1])});
				String data = poi[1];
				tree.insertData(data.getBytes(), p, nid++);	
			}

			long end = System.currentTimeMillis();

			System.err.println("Operations: " + count);
			System.err.println(tree);
			System.err.println("Minutes: " + ((end - start) / 1000.0f) / 60.0f);

			// since we created a new RTree, the PropertySet that was used to initialize the structure
			// now contains the IndexIdentifier property, which can be used later to reuse the index.
			// (Remember that multiple indices may reside in the same storage manager at the same time
			//  and every one is accessed using its unique IndexIdentifier).
			Integer indexID = (Integer) ps2.getProperty("IndexIdentifier");
			System.err.println("Index ID: " + indexID);

			boolean ret = tree.isIndexValid();
			if (ret == false) System.err.println("Structure is INVALID!");

			// flush all pending changes to persistent storage (needed since Java might not call finalize when JVM exits).
			tree.flush();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
