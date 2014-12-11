package netdb.spatial.rtree.ContQuery;

import java.io.File;
import java.util.List;

import de.micromata.opengis.kml.v_2_2_0.*;
import de.micromata.opengis.kml.v_2_2_0.gx.MultiTrack;
import de.micromata.opengis.kml.v_2_2_0.gx.Track;

public class MyReadKML {
	Kml kml = null;
	Document document = null;
	List<Feature> t = null;
		
	
	public MyReadKML(String file){
		System.out.println("Load KML file("+file+")...");
        kml = Kml.unmarshal(new File(file));
        document = (Document)kml.getFeature();
	}
	
	/*** Not General Case ***/
	public List<String> getPoints(){
		t = document.getFeature();
		
        for(Object o : t){
        	Placemark placemark = (Placemark)o;
        	Object ob = placemark.getGeometry();
        	Point point = null;
        	MultiTrack mulT = null;
        	if (ob instanceof Point) {
        		continue;
        	}
        	else if(ob instanceof MultiTrack){
        		mulT = (MultiTrack) ob;
        		List<Track> track = mulT.getTrack();
        		Track tra = track.get(0);
        		//for(String s :tra.getCoord() )
                //		 System.out.println(s);
        		return tra.getCoord();
        	}
        }
        return null;
	}
}
