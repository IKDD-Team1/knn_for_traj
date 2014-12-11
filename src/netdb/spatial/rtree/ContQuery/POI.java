package netdb.spatial.rtree.ContQuery;

public class POI implements Comparable{
	public double x;
	public double y;
	public String name;
	public double dist;
	@Override
	public int compareTo(Object o) {
		return (((POI)o).dist < this.dist) ? 1 : -1;
				
	}
}