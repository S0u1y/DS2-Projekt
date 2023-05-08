package org.kub0679.ActiveRecords;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.kub0679.DatabaseGateway;
import org.kub0679.Utility.DBField;
import org.kub0679.Utility.ReflectiveCloner;

import java.lang.reflect.Field;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

@Getter
@Setter
@ToString
public class Payment extends DatabaseGateway {

    private static final String FIND_BY_ID = "SELECT * FROM payment WHERE payment_id = ?";

    @DBField(strategy = DBField.Strategy.Id)
    private Integer payment_id;
    @DBField
    private Integer user_id;
    @DBField
    private Float amount;
    @DBField
    private Date paymentDate;

    public Payment(){
        super();
    }

    public Payment(Integer payment_id, Integer user_id, Float amount, Date paymentDate) {
        this();
        this.payment_id = payment_id;
        this.user_id = user_id;
        this.amount = amount;
        this.paymentDate = paymentDate;
    }

    public Payment(ResultSet resultSet) {
        super(resultSet);
    }



    public static Payment[] getAll() {
        try (
                Payment payment = new Payment();
                ResultSet rs = payment.executeQuery(payment.SELECT);
                ResultSet sizeResult = payment.executeQuery("Select count(*) FROM Payment");
        ){
            if (rs == null) return null;
            sizeResult.next();
            int size = sizeResult.getInt(1);
            Payment[] output = new Payment[size];

            for (int i = 0; rs.next() && i < size; i++) {
                output[i] = new Payment(rs);
            }

            rs.close();
            sizeResult.close();

            return output;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static Payment findById(int payment_id){
        try(
                Payment payment = new Payment();
                ResultSet rs = payment.executeQuery(FIND_BY_ID, payment_id)
        ){
            if (rs == null) return null;
            if (!rs.next()) return null;

            return new Payment(rs);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Payment findById(){
        return findById(payment_id);
    }

    public boolean load(){
        if(!this.isPersistent()) return false;

        Payment payment = findById();

        ReflectiveCloner.clone(payment, this);

        return true;
    }
    public boolean isPersistent(){
        return payment_id != 0;
    }


}



