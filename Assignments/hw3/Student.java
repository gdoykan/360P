import java.util.HashMap;

/**
 * Created by gydoy on 3/4/2018.
 */
public class Student {
    static String name;
    static HashMap<Integer, String> books;

    public HashMap getBooks() {
        return books;
    }
    public void addBook(int id, String book){
        books.put(id, book);
    }
    public void removeBook(String book){
        books.remove(book);
    }

    public Student(String name){
        this.name = name;
        this.books = new HashMap<>();
    }

}
