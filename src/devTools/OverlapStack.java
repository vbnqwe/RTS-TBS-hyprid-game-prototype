package devTools;

import java.util.ArrayList;

public class OverlapStack {
    private final ArrayList<String> namesOfObjects;

    public OverlapStack(){
        namesOfObjects = new ArrayList<>();
    }

    public void addObject(String str){
        namesOfObjects.add(str);
    }

    public void addObject(int index, String str){
        namesOfObjects.add(index, str);
    }

    public String get(int index){
        return namesOfObjects.get(index);
    }

    public void remove(String str){
        for(int i = 0; i < namesOfObjects.size(); i++){
            if(str.compareTo(namesOfObjects.get(i)) == 0){
                namesOfObjects.remove(i);
                break;
            }
        }
    }

    public void remove(int index){
        namesOfObjects.remove(index);
    }

    public ArrayList<String> getStack(){ return namesOfObjects; }
}
