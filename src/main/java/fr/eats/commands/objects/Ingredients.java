package fr.eats.commands.objects;

import java.util.ArrayList;

public class Ingredients {
    private final String name;
    private final ArrayList<String> conflicts;

    public Ingredients(String name){
        this.name = name;
        this.conflicts = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public ArrayList<String> getConflicts() {
        return conflicts;
    }

    public void addConflict(String conflict){
        this.conflicts.add(conflict);
    }

    public boolean haveconflict(ArrayList<String> ing) {
        for (String s : ing) {
            if (this.conflicts.contains(s)) {
                return true;
            }
        }
        return false;
    }
}
