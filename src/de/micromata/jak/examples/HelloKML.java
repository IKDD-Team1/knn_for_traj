package de.micromata.jak.examples;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import de.micromata.opengis.kml.v_2_2_0.Coordinate;
import de.micromata.opengis.kml.v_2_2_0.Document;
import de.micromata.opengis.kml.v_2_2_0.Feature;
import de.micromata.opengis.kml.v_2_2_0.Folder;
import de.micromata.opengis.kml.v_2_2_0.GroundOverlay;
import de.micromata.opengis.kml.v_2_2_0.Kml;
import de.micromata.opengis.kml.v_2_2_0.LatLonBox;
import de.micromata.opengis.kml.v_2_2_0.LookAt;
import de.micromata.opengis.kml.v_2_2_0.Placemark;
import de.micromata.opengis.kml.v_2_2_0.Point;
import de.micromata.opengis.kml.v_2_2_0.gx.MultiTrack;
import de.micromata.opengis.kml.v_2_2_0.gx.Track;

public class HelloKML {
	/**
	 * HelloKML Sample project
	 */
	/*public static void main(String[] args) {
        System.out.println("This is KML test");
        final Kml kml = Kml.unmarshal(new File("data/39.kml"));
        final Placemark placemark = (Placemark) kml.getFeature();
        Point point = (Point) placemark.getGeometry();
        List<Coordinate> coordinates = point.getCoordinates();
        for (Coordinate coordinate : coordinates) {
                System.out.println(coordinate.getLatitude());
                System.out.println(coordinate.getLongitude());
                System.out.println(coordinate.getAltitude());
        }
        
	}
       */
    
	Kml kml = null;
	Document document = null;
	List<Feature> t = null;
		
	
	public HelloKML(String file){
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
	
	
	
	
    public static void main(String[] args) {
            System.out.println("This is KML test");
            final Kml kml = Kml.unmarshal(new File("data/39.kml"));
            final Document document = (Document)kml.getFeature();
            System.out.println(document.getName());
            List<Feature> t = document.getFeature();
            for(Object o : t){
            	Placemark placemark = (Placemark)o;
            	Object ob = placemark.getGeometry();
            	Point point = null;
            	MultiTrack mulT = null;
            	if (ob instanceof Point) {
            		point = (Point) ob;
            		List<Coordinate> coordinates = point.getCoordinates();
                    for (Coordinate coordinate : coordinates) {
                    	 System.out.print("Start or End: ");
                    	 System.out.print(coordinate.getLatitude() + " ");
                         System.out.print(coordinate.getLongitude() + " ");
                         System.out.print(coordinate.getAltitude() + "\n");
                    }
            	}
            	else if(ob instanceof MultiTrack){
            		mulT = (MultiTrack) ob;
            		List<Track> track = mulT.getTrack();
                    for (Track tra : track) {
                    	 for(String s :tra.getCoord() )
                    		 System.out.println(s);
                    }
            	}
            	
            }
             
     
      }
         
 
    
}
