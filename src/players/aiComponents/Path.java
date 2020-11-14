package players.aiComponents;

import map.systems.SolarSystem;
import players.DefaultPlayer;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class Path {
    private ArrayList<String> path;

    public Path(SolarSystem start){
        path = new ArrayList<>();
        path.add(start.getName());
    }

    public Path(Path oldPath, SolarSystem newAddition){
        path = oldPath.getPath();
        path.add(newAddition.getName());
    }

    public Path(Path oldPath){
        path = oldPath.getPath();
    }

    public ArrayList<String> getPath(){ return path; }

    public void addToPath(SolarSystem sys){ path.add(sys.getName()); }

    public String getLastInPath(){ return path.get(path.size() - 1); }

    public SolarSystem getSysLastInPath(SolarSystem[] map) {
        for(int i = 0; i < map.length; i++){
            if(map[i].getName().compareTo(path.get(path.size() - 1)) == 0){
                return map[i];
            }
        }
        return null;
    }

    public SolarSystem getSysFirstInPath(SolarSystem[] map){
        for(int i = 0; i < map.length; i++){
            if(map[i].getName().compareTo(path.get(0)) == 0){
                return map[i];
            }
        }
        return null;
    }

    public SolarSystem getSysAtInPath(SolarSystem[] map, int index){
        for(SolarSystem sys : map){
            if(sys.getName().compareTo(path.get(index)) == 0){
                return sys;
            }
        }
        return null;
    }

    public double getDistance(SolarSystem[] map){
        ArrayList<SolarSystem> path2 = new ArrayList<>();
        for(int i = 0; i < path.size(); i++){
            for(int j = 0; j < map.length; j++){
                if(path.get(i).compareTo(map[j].getName()) == 0){
                    path2.add(map[j]);
                }
            }
        }

        //actually calculaturaes distance
        double distance = 0;
        for(int i = 0; i < path2.size() - 1; i++){
            //calc from path.get(i) to path.get(i+1)
            double tempDistance = Math.sqrt(Math.pow(path2.get(i).getX() - path2.get(i + 1).getX(), 2) + Math.pow(path2.
                    get(i).getY() - path2.get(i + 1).getY(), 2));
            distance += tempDistance;
        }
        return distance;
    }

    public boolean getIfAlreadyAdded(String sys){
        for(int i = 0; i < path.size(); i++){
            if(path.get(i).compareTo(sys) == 0){
                return true;
            }
        }
        return false;
    }
}
