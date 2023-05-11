import org.junit.jupiter.api.Test;
import org.kub0679.ActiveRecords.Comment;
import org.kub0679.ActiveRecords.Document;
import org.kub0679.ActiveRecords.Payment;
import org.kub0679.ActiveRecords.User;
import org.kub0679.Searcher;
import org.kub0679.Utility.Encoding;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

public class MainTests {
    @Test
    public void TestUsers(){
        User user1 = User.findById(1);

        User user2 = new User();
        user2.setUser_Id(2);
        user2 = user2.findById();

        if(user1 == null){
            user1 = new User();
            user1.setFirstname("Bob");
            user1.setLastname("Ross");
            user1.setPhone("420420420420");
            user1.setEmail("BobRoss@vsb.cz");
            user1.setPermission("Z");
            user1.setPassword(Encoding.Hash("BeatTheDevil"));
            user1.setAddress_Id(1);

            user1.create();
        }else{
            System.out.println("User exists!");
            System.out.println(user1);
        }
        user1.close();
        if(user2 == null){
            user2 = new User();
            user2.setFirstname("Josh");
            user2.setLastname("Ridiculous");
            user2.setAddress_Id(2);
            user2.setPassword(Encoding.Hash("IAmAtomic"));
            user2.setPermission("G");
            user2.setEmail("JoshRidiculous@gmail.com");

            user2.create();
        }else{
            System.out.println("User exists!");
            System.out.println(user2);
        }
        user2.close();
    }

    @Test
    public void TestUserConstruction(){

        User user = new User();

    }

    @Test
    public void TestGetAll(){
        User[] users = User.getAll();
        assert users != null;

        for(User user : users){
            System.out.println(user);
        }

        Comment[] comments = Comment.getAll();

        for(Comment comment : comments){
            System.out.println(comment);
        }

    }

    @Test
    public void TestSearchUsers(){
        List<User> users = Searcher.searchUsers("Bob", "last-name");

        for (User user: users) {
            System.out.println(user);
        }

        users = Searcher.searchUsers("Bob", "membership-last");

        for (User user: users) {
            System.out.println(user);
        }

    }

    @Test
    public void TestSearchDocuments(){
        List<Document> documents = Searcher.searchDocuments("", "title");

        for (var document: documents) {
            System.out.println(document);
        }

    }

    @Test
    public void TestEditUser(){
        User user = User.findById(2);

        assert user != null;
        user.setPhone("+81750286037");

        user.edit();
        //maybe call edit on getAll() ?
    }

    @Test
    public void TestRefund(){
        Payment payment = Payment.findById(3);
        assert payment != null;

        System.out.println(payment);

        payment.refund();

        System.out.println(payment);

    }

    @Test
    public void TestChangeRole(){
        User josh = User.findById(2);
        assert josh != null;

        josh.changeRole("Z");

    }


}
