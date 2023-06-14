import java.util.List;

interface Stack<T> {
    static <T> Stack empty(){ return new Stack<T>(){}; }
    interface OnEmpty<R,T>{ R of(); }
    interface OnElem<R,T>{ R of(T elem, Stack tail);}
    default <R,T> R match(OnEmpty<R,T> a, OnElem<R,T> b){return a.of();}

    default <T> Stack<T> push(T top){
        Stack<T> self = (Stack<T>) this;
        return new Stack<T>() {
            public <R,T> R match(OnEmpty<R,T> a, OnElem<R,T> b) {return b.of((T) top,self);}
        };
    }
    default <T> T top(){
        return (T) match(
                ()-> {throw new Error("NO!");},
                (t,tail) -> t
        );
    }

    default int size(){
        return match(
                ()->0,
                (top,tail)-> 1 + tail.size()
        );
    }


    default String toStr(){
        return match(
                ()->"[]",
                (top,tail)->"["+ toStringAux(top,tail) + "]"
        );
    }

    private <T> String toStringAux(T top, Stack tail){
        String base = top+"";
        return (String) tail.match(
                ()->base,
                (t,tail2)-> toStringAux(t,tail2) + ", " + base
        );
    }
}

class TestStack{
    public static void main(String[] args){
        Stack s0 = Stack.empty();
        Stack s1 = s0.push(1); // [1]
        System.out.println("s1"+ s1.toString());
        Stack many = Stack.empty().push(1).push(2).push(3); // [3,2,1]
        System.out.println(many.size());

        System.out.println(s0.match(
                ()->"<Empty>",
                (top,tail)->""+top
        ));

        System.out.println(s0.toStr());
        System.out.println(s1.toStr());
        System.out.println(many.toStr());


        Stack<Person> personStack = Stack.empty();
        personStack = personStack.push(new Person("Bob",190));
        personStack = personStack.push(new Person("Alice",170));
        System.out.println((Person) personStack.top());
        personStack = personStack.push(new Person("Dany",210));
        System.out.println(personStack.toStr());


        List.of(1,2,3).stream()
                .<String>mapMulti((num,cons)->{
                    for(int i=0;i<(num*5);i++){cons.accept(" ");}
                    cons.accept(String.valueOf(num));
                })
                .forEach(System.out::print);



    }
}
