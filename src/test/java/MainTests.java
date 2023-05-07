import org.junit.jupiter.api.Test;
import org.kub0679.ActiveRecords.User;
import org.kub0679.Utility.Encoding;

public class MainTests {
    @Test
    public void TestUsers(){
        User user1 = User.findById(1);

        User user2 = User.builder().User_Id(2).build();
        user2 = user2.findById();

        if(user1 == null){
            user1 = User.builder()
                    .Firstname("Bob")
                    .Lastname("Ross")
                    .Phone("420420420420")
                    .Email("BobRoss@vsb.cz")
                    .Permission("Z")
                    .Password(Encoding.Hash("BeatTheDevil"))
                    .Address_Id(1)
                    .build();

            user1.create();
        }else{
            System.out.println("User exists!");
            System.out.println(user1);
        }
        user1.close();
        if(user2 == null){
            user2 = User.builder()
                    .Firstname("Josh")
                    .Lastname("Ridiculous")
                    .Address_Id(2)
                    .Password(Encoding.Hash("IAmAtomic"))
                    .Permission("G")
                    .Email("JoshRidiculous@gmail.com")
                    .build();

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
    public void TestGetAllUsers(){
        User[] users = User.getAll();
        assert users != null;
        for(User user : users){
            System.out.println(user);
        }
    }

}
