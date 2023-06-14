import java.util.stream.Stream;

/**
 * OO OOSet
 *
 * A OOSet is a OO collection of elements that does not contain duplicates.
 * It is a functional data structure that supports the following operations:
 * 1. Add an element to the OOSet.
 * 2. Check if the OOSet contains the given element.
 * 3. Remove the given element from the OOSet.
 * 4. Return the number of elements in the OOSet.
 * 5. Return a string representation of the OOSet.
 * Use match to implement the above operations.
 *
 * Author: Hamish Burke
 * Last Modified: 15/06/2023
 *
 * @param <T>
 */
interface OOSet<T>{

    /**
     * Returns an empty OOSet.
     * @param <T>
     * @return
     */
    static <T> OOSet<T> empty(){ return new OOSet<T>(){}; }

    /**
     * Returns true if the OOSet is empty.
     * @return
     */
    default boolean isEmpty(){
        return match(
            ()-> true,
            (prev,data)-> false
        );
    }

    interface OnEmpty<R>{ R of(); }
    interface OnElem<R,T>{ R of(OOSet prev, T data); }

    /**
     * 1. Empty OOSet
     * 2. Non-empty OOSet
     * Returns the state of the OOSet.
     * @param a
     * @param b
     * @return
     * @param <R>
     */
    default <R> R match(OnEmpty<R> a, OnElem<R,T> b){ return a.of(); }

    /**
     * Add an element to the OOSet. If the element is already in the OOSet, return the OOSet unchanged.
     * @param data
     * @return
     */
    default OOSet<T> add(T data){
        OOSet<T> self = this;
        if(self.contains(data)){
            return self;
        }
        return new OOSet<T>() {
            public <R> R match(OnEmpty<R> a, OnElem<R, T> b) {
                return b.of(self,data);
            }
        };
    }

    /**
     * Check if the OOSet contains the given element.
     * @param data
     * @return
     */
    default boolean contains(T data){
        return match(
                ()-> false,
                (prev,curr)-> curr.equals(data) ? true : prev.contains(data)
        );
    }

    /**
     * Remove the given element from the OOSet.
     * @param data
     * @return
     */
    default OOSet<T> remove(T data){
        OOSet<T> self = this;
        return match(
                ()-> self,
                (prev,curr)-> curr.equals(data) ? prev : prev.remove(data).add(curr)
        );
    }

    /**
     * Return the number of elements in the OOSet.
     * @return
     */
    default int size(){
        return match(
                ()-> 0,
                (prev,curr)-> 1 + prev.size()
        );
    }

    /**
     * Return a string representation of the OOSet.
     * @return
     */
    default String output(){
        return match(
                ()->"[]",
                (prev,curr)-> "[" + ouputAux(prev,curr) + "]"
        );
    }

    private String ouputAux(OOSet prev, T curr){
        return (String) prev.match(
                ()-> curr.toString(),
                (prev2,curr2)-> ouputAux(prev2, (T) curr2) + ", " + curr.toString()
        );
    }

    /**
     * Return a new OOSet that is the union of this OOSet and the other OOSet.
     * @param other
     * @return
     */
    default OOSet<T> union(OOSet<T> other){
        return match(
                ()-> other,
                (prev,curr)-> other.contains(curr) ? prev.union(other) : prev.union(other).add(curr)
        );
    }

    /**
     * Return a new OOSet that is the symmetric difference of this OOSet and the other OOSet.
     * @param other
     * @return
     */
    default boolean equal(OOSet<T> other){
        return match(
                ()-> other.isEmpty(),
                (prev,curr)-> other.contains(curr) && prev.equal(other.remove(curr))
        );
    }

    default Stream<T> stream(){
        return match(
                ()-> Stream.empty(),
                (prev,curr)-> Stream.concat(Stream.of(curr), prev.stream())
        );
    }


}

class Example{
    private record Person(String name, int age){
        @Override
        public String toString(){
            return name;
        }
    }

    public static void main(String[] args){
        // Use match to implement the above operations.
        OOSet<Integer> set = OOSet.empty();
        set = set.add(1).add(2).add(1).add(4).add(5); // [1, 2, 4, 5]
        System.out.println(set.output());

        System.out.println(set.contains(1)); // true
        System.out.println(set.contains(3)); // false

        set = set.remove(1); // [2, 4, 5]
        System.out.println(set.output());

        System.out.println(set.size()); // 3

        // Create a OOSet of Person objects
        OOSet<Person> people = OOSet.empty();
        people = people.add(new Person("Hamish", 19)).add(new Person("John", 22)).add(new Person("Jane", 23));
        System.out.println(people.output());

        // Find the youngest person
        System.out.println(findYoungest(people)); // Hamish

        // Find the youngest person using streams
        System.out.println(findYoungest2(people)); // Hamish


        // Union
        OOSet<Integer> set2 = OOSet.empty();
        set2 = set2.add(1).add(2).add(3).add(4).add(5); // [1, 2, 3, 4, 5]
        System.out.println(set.union(set2).output()); // [1, 2, 3, 4, 5]

        // Check distinct
        System.out.println(isDistinct(set));
        System.out.println(isDistinct(set2));
        System.out.println(isDistinct(people));
    }

    /**
     * Find the youngest person in the OOSet.
     * @param people
     */
    public static Person findYoungest(OOSet<Person> people){
        return people.match(
                ()-> null,
                (prev,curr)-> {
                    Person youngest = findYoungest(prev);
                    return youngest == null || curr.age() < youngest.age() ? curr : youngest;
                }
        );
    }

    /**
     * Find the youngest person in the OOSet.
     * Using streams.
     */
    public static Person findYoungest2(OOSet<Person> people) {
        return people.stream()
                .reduce((a, b) -> a.age() < b.age() ? a : b)
                .orElse(null);
    }

    /**
     * Check if the OOSet contains distinct elements.
     * @param set
     * @return
     */
    public static boolean isDistinct(OOSet<?> set){
        int disSize = (int) set.stream()
                .distinct()
                .count();

        return disSize==set.size();
    }


}
