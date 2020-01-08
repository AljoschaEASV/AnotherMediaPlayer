package sample.Infrastructure;

/**
 *
 * @author tha
 */
public class DBTester {

    public static void main (String [] args){
        



        do{
           String data = DB.getDisplayData();
           if (data.equals(DB.NOMOREDATA)){
               break;
           }else{
               System.out.print(data);
           }   
        } while(true);
        

        

       
    }
    
}
