import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.function.Supplier;

public class TowerExercise{
    static void invalidC(Supplier<Castle> cs) {
        try{ cs.get(); }
        catch(AssertionError ae){ return; }
        assert false;
    }
    public static Castle.Tower t(int height){ return new Castle.Tower(height); }
    public static void main(String[] a){
        Castle.Tower t10 = t(10);
        Castle.Tower t11 = t(11);

        new Castle(List.of(t10,t(9),t(7),t(9)));
        new Castle(List.of(t10,t(9),t(7),t(9),t11));
        new Castle(List.of(t(5),t(5),t(11),t(10),t10,t(5)));
        new Castle(List.of(t(5),t(11),t(11),t(10),t10,t(25)));

        //invalid castles!

        //TestsGroup1 not enough towers
        invalidC(()->new Castle(List.of()));
        invalidC(()->new Castle(List.of(t(10))));
        invalidC(()->new Castle(List.of(t(10),t(11))));
        invalidC(()->new Castle(List.of(t(10),t(11),t(12))));

        //TestsGroup2 at least one tower is too short
        invalidC(()->new Castle(List.of(t(4),t(11),t(10),t(12))));
        invalidC(()->new Castle(List.of(t(4),t(3),t(2),t(12))));
        invalidC(()->new Castle(List.of(t(20),t(4),t(11),t(10),t(12))));
        invalidC(()->new Castle(List.of(t(11),t10,t11,t(4),t(11),t(10),t(12))));

        //TestsGroup3 at least two towers are ==
        invalidC(()->new Castle(List.of(t10,t11,t10,t(12))));
        invalidC(()->new Castle(List.of(t11,t11,t10,t(12))));
        invalidC(()->new Castle(List.of(t(26),t11,t11,t10,t(12),t(7))));
        invalidC(()->new Castle(List.of(t(16),t11,t11,t10,t(12),t10,t(7))));

        //TestsGroup4 at least two towers with same max height
        invalidC(()->new Castle(List.of(t(10),t(11),t(12),t(12))));
        invalidC(()->new Castle(List.of(t(10),t(10),t(10),t(10),t(10))));
        invalidC(()->new Castle(List.of(t(6),t(7),t(8),t(9),t(9))));
        invalidC(()->new Castle(List.of(t(10),t(7),t(10),t(9),t(9),t(6))));
    }
}


// valid castles:
//at least 4 towers
//all towers are at least 5 meters tall
//no tower is == to another tower
//exactly one tower is the tallest
record Castle(List<Tower> towers){
    record Tower(int height){}

    Castle{
        assert towers.size()>=4;
        assert towers.stream()
                .allMatch(x -> x.height>=5);

        assert towers.stream()
                .distinct()
                .count()==towers.size();

        int tallest = towers.stream().max(Comparator.comparing(Tower::height)).get().height;

        assert towers.stream()
                .filter(tow -> tow.height==tallest)
                .count()==1;


    }
}