package ensemble.samples.language.concurrency.task;
import java.util.Date;
public class DailySales {
private Integer dailySalesId;
private Integer quantity;
private Date date;
public DailySales() {
}
public DailySales(int id, int qty, Date date) {
this.dailySalesId = id;
this.quantity = qty;
this.date = date;
}
public Integer getDailySalesId() {
return dailySalesId;
}
public void setDailySalesId(Integer dailySalesId) {
this.dailySalesId = dailySalesId;
}
public Integer getQuantity() {
return quantity;
}
public void setQuantity(Integer quantity) {
this.quantity = quantity;
}
public Date getDate() {
return date;
}
public void setDate(Date date) {
this.date = date;
}
}
