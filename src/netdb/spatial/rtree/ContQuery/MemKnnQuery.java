package netdb.spatial.rtree.ContQuery;

import java.io.*;
import java.util.*;

import netdb.spatial.rtree.rtree.*;
import netdb.spatial.rtree.spatialindex.*;
import netdb.spatial.rtree.spatialindex.Point;
import netdb.spatial.rtree.spatialindex.Region;
import netdb.spatial.rtree.storagemanger.*;
import de.micromata.opengis.kml.v_2_2_0.*;

public class MemKnnQuery
{
	private ISpatialIndex tree;

	public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException
	{
		long startTime = System.currentTimeMillis();
		new MemKnnQuery(args);
		long endTime   = System.currentTimeMillis();
		long totalTime = endTime - startTime;
		System.err.println("Total Run Time:"+ (double)totalTime +" (ms)");
	}

	MemKnnQuery(String[] args) throws FileNotFoundException, UnsupportedEncodingException
	{
		/**
		 * Build a POI RTree in memory
		 */
		System.out.println("Build RTree...");
		tree = buildPoiRTree("data/TainanPOI_38665.txt");
		
		System.out.println("Doing KNN Query...");
		ContinuousKNN(tree,"data/mt.kml", 10);    //K=10
	}
	
	public ISpatialIndex buildPoiRTree(String poiFile) {

		/*create memory storage*/
		IStorageManager memStorage = new MemoryStorageManager();

		// Create a new, empty, RTree with dimensionality 2, minimum load 70%, and the RSTAR splitting policy.
		PropertySet ps = new PropertySet();

		Double f = new Double(0.7);
		ps.setProperty("FillFactor", f);
		ps.setProperty("IndexCapacity", new Integer(32));
		ps.setProperty("LeafCapacity", new Integer(32));
		
		// Index capacity and leaf capacity may be different.
		ps.setProperty("Dimension", new Integer(2));

		ISpatialIndex tree = new RTree(ps, memStorage);		
		
		
		try {
			BufferedReader reader = new BufferedReader(new FileReader(poiFile));

			int id=0;
			String line = reader.readLine();
			while (line!=null) {
				String[] poi = line.split(":");  //poi[0]:log,lat   poi[1]:data
				String[] position = poi[0].split(",");			
				line = reader.readLine();
				if(poi.length != 2)  continue;  //If data is empty, don't save
				
				/**
				 * Insert (x,y) 
				 */
				Point p = new Point(new double[]{Double.parseDouble(position[0]),Double.parseDouble(position[1])});
				String data = poi[1];
				tree.insertData(data.getBytes(), p, id++);
			}
			
			System.out.println("tree nodes: "+id);
			reader.close();
		} catch (IOException e) {
			System.err.println(e);
		} catch (ArrayIndexOutOfBoundsException e){
			System.err.println(e);
		}
		
		return tree;
	}
	public void ContinuousKNN(ISpatialIndex tree, String trajFile, int k) throws FileNotFoundException, UnsupportedEncodingException {
		int pCnt = 0;
				
		/**
		 * Convert KML to point list
		 */
		MyReadKML readKML = new MyReadKML(trajFile);
		
		PrintWriter writer = new PrintWriter("Mem-output.txt", "UTF-8");
		
		long tTime = 0;
		
		for(String coor : readKML.getPoints() ){
			String[] c = coor.split(" ");
			double[] p = new double[]{Double.parseDouble(c[0]), Double.parseDouble(c[1])};
		
			Point point = new Point( p ) ;
			pCnt++;
			
			// this will find the k nearest neighbors.
			POI insertPoi = new POI();
			insertPoi.x = p[0];
			insertPoi.y = p[1];
			insertPoi.name = "traPoint";

			KNNList list = new KNNList(p[0], p[1]);
			KNNVisitor vis = new KNNVisitor(list);
			
			long sTime = System.currentTimeMillis();
			
			tree.nearestNeighborQuery(k, point, vis);
			
			long eTime   = System.currentTimeMillis();
			tTime += eTime - sTime;
			
			
			writer.println(vis.knn.src_x+","+vis.knn.src_y+"  knn :");
			for(POI kp : vis.knn.getKNN()){
				writer.println("("+kp.x+","+kp.y+")"+kp.name+":"+kp.dist);
			}
		}
		
		
		System.err.println("\nDoing KNN Query Run Time:"+ (double)tTime +" (ms)");
		System.out.println("Traj total points :"+pCnt);
		writer.close();
		
	}
	
}
