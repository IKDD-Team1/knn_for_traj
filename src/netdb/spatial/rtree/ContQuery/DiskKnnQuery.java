package netdb.spatial.rtree.ContQuery;

import java.io.*;
import java.util.*;

import netdb.spatial.rtree.rtree.*;
import netdb.spatial.rtree.spatialindex.*;
import netdb.spatial.rtree.spatialindex.Point;
import netdb.spatial.rtree.spatialindex.Region;
import netdb.spatial.rtree.storagemanger.*;

public class DiskKnnQuery
{
	private ISpatialIndex tree;
	
	public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException
	{
		long startTime = System.currentTimeMillis();
		new DiskKnnQuery(args);
		long endTime   = System.currentTimeMillis();
		long totalTime = endTime - startTime;
		System.err.println("Total Run Time:"+ (double)totalTime +" (ms)");
	}

	DiskKnnQuery(String[] args) throws FileNotFoundException, UnsupportedEncodingException
	{
		/**
		 * Build a POI RTree in memory
		 */
		System.out.println("Build RTree...");
		// Create a disk based storage manager.
		try {
						// Create a disk based storage manager.
						PropertySet ps = new PropertySet();

						ps.setProperty("FileName", "data/TainanPOI_38665.txt");
							// .idx and .dat extensions will be added.

						IStorageManager diskfile = new DiskStorageManager(ps);

						IBuffer file = new RandomEvictionsBuffer(diskfile, 32, false);
							// applies a main memory random buffer on top of the persistent storage manager
							// (LRU buffer, etc can be created the same way).

						PropertySet ps2 = new PropertySet();

						// If we need to open an existing tree stored in the storage manager, we only
						// have to specify the index identifier as follows
						Integer i = new Integer(1); // INDEX_IDENTIFIER_GOES_HERE (suppose I know that in this case it is equal to 1);
						ps2.setProperty("IndexIdentifier", i);
							// this will try to locate and open an already existing r-tree index from file manager file.

						ISpatialIndex tree = new RTree(ps2, file);
					
				
						//System.out.println("Doing Region Query...");
						//queryRegion();
						
						System.out.println("Doing KNN Query...");
						ContinuousKNN(tree,"data/mt.kml", 10);
						
		} catch (SecurityException | NullPointerException | IllegalArgumentException | IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
		}
	}
	
	public void queryRegion(){
		long startTime = System.currentTimeMillis();
		double[] h = new double[]{120.178421,23.017662};
		double[] l = new double[]{120.322960,22.935479};
		Region r = new Region(h, l);
		ArrayList<POI> list = new ArrayList<POI>();
		MyVisitor v = new MyVisitor(list);
		tree.pointLocationQuery(r, v);
		
		long endTime   = System.currentTimeMillis();
		long totalTime = endTime - startTime;
		System.err.println("Region Query Run Time:"+ (double)totalTime +" (ms)");
		
//		System.out.println("Output POI list of map:");
//		for(POI kp : v.list){
//			System.out.println("("+kp.x+","+kp.y+")"+kp.name);
//		}
	}
	
	
	public void ContinuousKNN(ISpatialIndex tree, String trajFile, int k) throws FileNotFoundException, UnsupportedEncodingException {
		int pCnt = 0;
				
		/**
		 * Convert KML to point list
		 */
		MyReadKML readKML = new MyReadKML(trajFile);
		
		PrintWriter writer = new PrintWriter("disk-output.txt", "UTF-8");
		
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
			
			//knnList.add(insertPoi);
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
	

	

	// example of a Visitor pattern.
	// findes the index and leaf IO for answering the query and prints
	// the resulting data IDs to stdout.
	class MyVisitor implements IVisitor
	{
		ArrayList<POI> list;
		
		MyVisitor(ArrayList<POI> list){
			this.list = list;
		}
		public void visitNode(final INode n)
		{
			/**
			 * do nothing
			 */
		}

		public void visitData(final IData d)
		{
			String poiName;
			try {
				poiName = new String(d.getData(), "UTF-8");
				Region p = (Region)d.getShape();
		
				POI poi = new POI();
				poi.x = p.m_pLow[0];
				poi.y = p.m_pLow[1];
				poi.name = poiName;
				list.add(poi);
				
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}

}
