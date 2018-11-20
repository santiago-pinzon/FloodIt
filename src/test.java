import java.util.ArrayList;


public class test {

  public static void main(String[] args) {
    ArrayList<String> list1 = new ArrayList<>(1);
    ArrayList<String> list2 = new ArrayList<>(1);
    
    String A = "a";
    String B = "b";
    String C = "c";
    String D = "d";
    
    list1.add(A);
    list1.add(B);
    list1.add(C);
    list1.add(D);
    
    list2.add(A);
    list2.add(B);
    
   
    
    list1.removeAll(list2);
    
    System.out.println(list1);
    

  }

}
