import java.util.*;
import java.util.function.*;
import java.util.stream.Collectors;

class Person{
    String name;
    double height;

    public Person(String n, double h){
        this.name = n;
        this.height = h;
    }

    public String name(){return name;}
    public double height(){return height;}
}

class Student extends Person{
    double gpa;
    public Student(String name, double height, double gpa){
        super(name,height);
        this.gpa = gpa;
    }

    public double gpa(){return gpa;}
}

class ListUtil {
    public <T> String format(List<T> list){
        return list.stream()
                .map(Object::toString)
                .collect(Collectors.joining(", ", "[", "]"));
    }
}



public class Exercise{
    static String shortestPerson(List<Person> ps) {
        // comparator
        Comparator<Person> personComparator = new Comparator<Person>(){
            @Override
            public int compare(Person p1, Person p2){
                return (int) (p1.height()-p2.height());
            }
        };


        // supplier
        Supplier<IllegalArgumentException> exceptionSupplier = new Supplier<IllegalArgumentException>() {
            @Override
            public IllegalArgumentException get() {
                return new IllegalArgumentException();
            }
        };


        return ps.stream()
                .min(personComparator)
                .orElseThrow(exceptionSupplier)
                .name();
    }

    /**
     * 'averageHeight' that calculates the average height of people in the list.
     * If the list is empty, throw an IllegalArgumentException.
     * use the 'mapToInt' and 'average' methods of the stream.
     * @param ps
     * @return
     */
    static double averageHeight(List<Person> ps){
        return ps.stream()
                .mapToDouble(x -> x.height())
                .average()
                .orElseThrow(()->new IllegalArgumentException());
    }

    /**
     *
     */
    static double averageGPA(List<Student> ps){
        Supplier<IllegalArgumentException> NoStudentError = new Supplier<IllegalArgumentException>(){
            @Override
            public IllegalArgumentException get(){
                return new IllegalArgumentException();
            }
        };

        return ps.stream()
                .mapMultiToDouble((num,cons)->{
                    if(num.gpa!=0){
                        cons.accept(num.gpa);
                    }
                })
                .average()
                .orElseThrow(NoStudentError);
    }


    /**
     * 'firstAlphabetical' that finds the person whose name comes first in alphabetical order in the list.
     * If the list is empty, throw an IllegalArgumentException.
     * compare the person names using String's compareTo method in your comparator.
     * @param ps
     * @return
     */
    static String firstAlphabetical(List<Person> ps){
        Supplier<IllegalArgumentException> illegalArgumentExceptionSupplier = new Supplier<IllegalArgumentException>(){
            @Override
            public IllegalArgumentException get(){
                return new IllegalArgumentException();
            }
        };


        return ps.stream()
                .map(p -> p.name())
                .sorted(Comparator.comparing(String::valueOf))
                .findFirst().orElseThrow(illegalArgumentExceptionSupplier);
    }


    record Node(double weight, String city, Set<Node> links){}

    static Map<Double,String> flatten(List<Node> nodes, Consumer<>) {
        return nodes.stream()
                .<Map.Entry<Double,String>>mapMulti((node,cons)->{
                    cons.accept(Map.entry(node.weight,node.city));
                    node.links.stream()
                            .map(x -> Map.entry(node.weight,node.city))
                            .forEach(cons);

                })
                .collect(Collectors.toMap(Map.Entry::getKey,Map.Entry::getValue)); // .toMap take key,val (two suppliers)

        /*
        eg
            .collect(Collectors.toMap(x -> x.valforkey,y-> y.valforval));
         */
    }



        /**
         * 'findMostPopulated' method to find the city with the least population.
         * If the map is empty, throw a NoSuchElementException.
         * @param that
         * @return
         */
    static String findLeastPopulated(HashMap<String,Integer> that){
        return that.entrySet()
                .stream()
                .min(Comparator.comparing(Map.Entry::getValue))
                .orElseThrow(()-> new NoSuchElementException())
                .getKey();
    }


    /**
     * Find the average height out of all the classrooms
     * With grades higher than 80
     * @return
     */
    static double findAverageHeight(Set<List<Student>> rooms,int gpa){
        Supplier<UnknownError> unknownErrorSupplier = new Supplier<UnknownError>(){
            @Override
            public UnknownError get(){
                return new UnknownError();
            }
        };


        return rooms.stream()
                .<Double>mapMulti((rs,cons)->{
                    rs.stream()
                            .filter(s -> s.gpa()>gpa) // grades higher than 80
                            .map(Student::height)
                            .forEach(cons::accept);
                })
                .mapToDouble(x -> x.doubleValue())
                .average()
                .orElseThrow(unknownErrorSupplier);
    }

    static List<Student> enrollPeopleIntoSchool(List<Person> ps){
        return ps.stream()
                .map(p -> new Student(p.name(),p.height(),0.0))
                .toList();
    }


    static Person p(String name,int height){ return new Person(name,height); }
    static void test(String expected, List<Person> input){
        String res=shortestPerson(input);
        if(!res.equals(expected)){System.out.println("For input "+input+" we get "+res+" instead of "+expected);}
    }
    public static void main(String[] args){
        // Enable assertions


        try{ shortestPerson(List.of()); assert false; }
        catch(IllegalArgumentException iae){}

        test("Bob", List.of(p("Bob",190)));
        test("Adam", List.of(p("Adam",220)));
        test("Alice", List.of(p("Bob",190),p("Alice",170)));
        test("Bob", List.of(p("Bob",170),p("Alice",190)));
        test("Bob", List.of(p("Bob",1),p("Alice",2),p("Dany",3)));
        test("Dany", List.of(p("Bob",3),p("Alice",2),p("Dany",1)));
        test("Alice", List.of(p("Bob",3),p("Alice",2),p("Dany",5)));
    }
}