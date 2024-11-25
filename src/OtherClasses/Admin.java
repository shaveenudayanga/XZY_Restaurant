package OtherClasses;

/**
 * @author : H V A S Udayanga
 * @index : AS2022359
 **/

public record Admin (
        int adminId,
        String username){
    public static Admin admin;


    public Admin() {
        this(0,"");
    }

}
