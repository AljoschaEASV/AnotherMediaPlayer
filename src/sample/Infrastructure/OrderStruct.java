package sample.Infrastructure;

public class OrderStruct implements Comparable{
    public String videoName;
    public String playlistName;
    public int orderNo;

    public String toString(){
        return "videoName: " + videoName + " playlistName: " + playlistName + " orderNo: " + orderNo;
    }

    @Override
    public int compareTo(Object o) {
        OrderStruct in = (OrderStruct) o;
        int result = 0;
        if (this.orderNo<in.orderNo) {
            result= -1;
        } else if (this.orderNo==in.orderNo){
            result= 0;
        } else if (this.orderNo>in.orderNo){
            result= 1;
        }
        return result;
    }
}
