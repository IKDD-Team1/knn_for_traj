package netdb.spatial.rtree.ContQuery;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import netdb.spatial.rtree.spatialindex.IData;
import netdb.spatial.rtree.spatialindex.INode;
import netdb.spatial.rtree.spatialindex.ISpatialIndex;
import netdb.spatial.rtree.spatialindex.IVisitor;
import netdb.spatial.rtree.spatialindex.Region;

public class RegionQuery {
	ISpatialIndex tree = null;
	MyVisitor v = null;
	Region r = null;
	
	public RegionQuery(ISpatialIndex tree, Region r){
		this.tree = tree;
		this.r = r;
	}
	
	public void queryRegion(){
		if(r == null){
			System.err.println("There is no Region value.");
			System.exit(-1);
		}

		v = new MyVisitor(new ArrayList<POI>());
		
		long startTime   = System.currentTimeMillis();
		
		tree.pointLocationQuery(r, v);
		
		
		long endTime   = System.currentTimeMillis();
		long totalTime = endTime - startTime;
		System.err.println("Region Query Run Time:"+ (double)totalTime +" (ms)");

	}
	
	public void print(){
		System.out.println("Output POI list of map:");
		for(POI kp : v.list){
			System.out.println("("+kp.x+","+kp.y+")"+kp.name);
		}
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
