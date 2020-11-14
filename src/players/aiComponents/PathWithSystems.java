package players.aiComponents;

import map.systems.SolarSystem;

import java.util.ArrayList;

public class PathWithSystems extends Path {

    private SolarSystem[] path;

    public PathWithSystems(Path oldPath, SolarSystem[] map){
        super(oldPath);
        path = new SolarSystem[super.getPath().size()];
        for(int i = 0; i < super.getPath().size(); i++){
            for(SolarSystem sys : map){
                if(sys.getName().compareTo(super.getPath().get(i)) == 0){
                    path[i] = sys;
                }
            }
        }
    }

    public PathWithSystems(SolarSystem[] newPath){
        super(newPath[0]);
        this.path = newPath;
    }

    public SolarSystem[] getSystems(){ return path; }

    public SolarSystem getPathAtIndex(int index){ return path[index]; }

    public int getPathLength(){ return path.length; }

    public SolarSystem getLastSystemInPath(){
        return path[path.length - 1];
    }
}
