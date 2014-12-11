package netdb.spatial.rtree.ContQuery;

import java.util.ArrayList;
import java.util.Collections;

public class KNNList {
	private ArrayList<POI> knnList = new ArrayList<POI>();
	public double src_x, src_y;
	
    public KNNList(double src_x, double src_y) {
    	this.src_x = src_x;
    	this.src_y = src_y;
    }
    
    @SuppressWarnings("unchecked")
	public ArrayList<POI> getKNN() {
    	/**
    	 * Sort at first
    	 */
        Collections.sort(knnList);
    	return knnList;
    }
}
