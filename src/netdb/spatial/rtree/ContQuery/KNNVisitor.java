package netdb.spatial.rtree.ContQuery;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import netdb.spatial.rtree.spatialindex.IData;
import netdb.spatial.rtree.spatialindex.INode;
import netdb.spatial.rtree.spatialindex.IVisitor;
import netdb.spatial.rtree.spatialindex.Region;

public class KNNVisitor implements IVisitor
{
	public KNNList knn;
	ArrayList<POI> list;
	
	public KNNVisitor(KNNList knnList) {
		this.knn = knnList;
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
	
			//System.out.println(d.getIdentifier()+":("+ p.m_pLow[0]+","+p.m_pLow[1]+")"+poiName);
			POI poi = new POI();
			poi.x = p.m_pLow[0];
			poi.y = p.m_pLow[1];
			poi.name = poiName;
			poi.dist = dist(knn.src_x, knn.src_y, poi.x, poi.y);
			knn.getKNN().add(poi);
			
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	private double dist(double x1, double y1, double x2, double y2){
		return Math.sqrt( (x1-x2)*(x1-x2)+(y1-y2)*(y1-y2) );
	}
}
